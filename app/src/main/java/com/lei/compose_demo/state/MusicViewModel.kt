package com.lei.compose_demo.state

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.lei.compose_demo.data.FakeMusicRepository
import com.lei.compose_demo.data.PlayerState
import com.lei.compose_demo.data.Track
import com.lei.compose_demo.data.local.LocalMusicRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 音乐页面 ViewModel，负责管理 UI 状态。
 */
class MusicViewModel(
    // 应用对象。
    application: Application,
) : AndroidViewModel(application) {
    // 数据仓库，用于提供歌曲列表。
    private val repository = FakeMusicRepository()
    // 本地音乐仓库。
    private val localMusicRepository = LocalMusicRepository(application.applicationContext)
    // 播放器实例。
    private val player = ExoPlayer.Builder(application.applicationContext).build()
    // 播放进度定时任务。
    private var progressJob: Job? = null
    // 进度更新间隔（毫秒）。
    private val progressIntervalMs: Long = 1000L
    // 默认歌曲时长（秒）。
    private val defaultDurationSeconds: Int = 240

    // 当前 UI 状态。
    var uiState by mutableStateOf(createInitialState())
        private set

    init {
        // 播放结束后自动切歌。
        player.addListener(
            object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_ENDED) {
                        selectNextTrack()
                    }
                }
            }
        )
        // 初始化时加载本地缓存。
        loadCachedTracks()
    }

    /**
     * 处理页面事件。
     *
     * @param event 触发的页面事件。
     */
    fun onEvent(event: MusicEvent) {
        when (event) {
            MusicEvent.TogglePlay -> togglePlay()
            MusicEvent.Next -> selectNextTrack()
            MusicEvent.Previous -> selectPreviousTrack()
            is MusicEvent.SeekTo -> seekToProgress(event.progress)
            MusicEvent.ScanLocalMusic -> scanLocalMusic()
            is MusicEvent.SelectTrack -> selectTrackById(event.trackId)
        }
    }

    /**
     * 创建初始状态。
     */
    private fun createInitialState(): MusicUiState {
        // 获取歌曲列表。
        val tracks = repository.getTracks()
        // 选中第一首作为默认歌曲。
        val currentTrack = tracks.firstOrNull()
        // 解析歌曲时长（秒）。
        val durationSeconds = parseDurationSeconds(currentTrack?.durationText)
        // 初始播放器状态。
        val playerState = PlayerState(
            isPlaying = false,
            progress = 0f,
            positionSeconds = 0,
            durationSeconds = durationSeconds,
            currentTrackId = currentTrack?.id,
        )
        return MusicUiState(
            tracks = tracks,
            currentTrack = currentTrack,
            playerState = playerState,
        )
    }

    /**
     * 切换播放/暂停状态。
     */
    private fun togglePlay() {
        // 当前歌曲。
        val currentTrack = uiState.currentTrack
        // 是否有可播放的 Uri。
        val canPlay = !currentTrack?.uri.isNullOrBlank()
        if (!canPlay) {
            return
        }
        if (player.isPlaying) {
            // 暂停播放。
            player.pause()
        } else {
            // 若没有媒体项则先加载当前歌曲。
            if (player.mediaItemCount == 0) {
                playTrack(currentTrack, true)
            } else {
                player.play()
            }
        }
        // 复制当前播放器状态。
        val currentPlayerState = uiState.playerState
        // 切换播放状态。
        val newPlayerState = currentPlayerState.copy(isPlaying = player.isPlaying)
        uiState = uiState.copy(playerState = newPlayerState)
        if (newPlayerState.isPlaying) {
            startProgressTicker()
        } else {
            stopProgressTicker()
        }
    }

    /**
     * 选择下一首歌曲。
     */
    private fun selectNextTrack() {
        // 当前歌曲列表。
        val tracks = uiState.tracks
        // 当前歌曲对象。
        val currentTrack = uiState.currentTrack
        // 当前歌曲索引。
        val currentIndex = tracks.indexOf(currentTrack)
        // 下一个索引，超出则回到 0。
        val nextIndex = if (currentIndex >= 0) (currentIndex + 1) % tracks.size else 0
        // 下一个歌曲对象。
        val nextTrack = tracks.getOrNull(nextIndex)
        // 是否可播放。
        val canPlay = !nextTrack?.uri.isNullOrBlank()
        // 解析歌曲时长（秒）。
        val durationSeconds = parseDurationSeconds(nextTrack?.durationText)
        // 更新 UI 状态。
        uiState = uiState.copy(
            currentTrack = nextTrack,
            playerState = uiState.playerState.copy(
                isPlaying = canPlay,
                progress = 0f,
                positionSeconds = 0,
                durationSeconds = durationSeconds,
                currentTrackId = nextTrack?.id,
            )
        )
        if (canPlay) {
            playTrack(nextTrack, true)
            startProgressTicker()
        } else {
            stopProgressTicker()
        }
    }

    /**
     * 选择上一首歌曲。
     */
    private fun selectPreviousTrack() {
        // 当前歌曲列表。
        val tracks = uiState.tracks
        // 当前歌曲对象。
        val currentTrack = uiState.currentTrack
        // 当前歌曲索引。
        val currentIndex = tracks.indexOf(currentTrack)
        // 上一个索引，超出则回到最后。
        val previousIndex = if (currentIndex > 0) currentIndex - 1 else tracks.lastIndex
        // 上一个歌曲对象。
        val previousTrack = tracks.getOrNull(previousIndex)
        // 是否可播放。
        val canPlay = !previousTrack?.uri.isNullOrBlank()
        // 解析歌曲时长（秒）。
        val durationSeconds = parseDurationSeconds(previousTrack?.durationText)
        // 更新 UI 状态。
        uiState = uiState.copy(
            currentTrack = previousTrack,
            playerState = uiState.playerState.copy(
                isPlaying = canPlay,
                progress = 0f,
                positionSeconds = 0,
                durationSeconds = durationSeconds,
                currentTrackId = previousTrack?.id,
            )
        )
        if (canPlay) {
            playTrack(previousTrack, true)
            startProgressTicker()
        } else {
            stopProgressTicker()
        }
    }

    /**
     * 根据歌曲 ID 选择歌曲。
     *
     * @param trackId 目标歌曲 ID。
     */
    private fun selectTrackById(trackId: String) {
        // 当前歌曲列表。
        val tracks = uiState.tracks
        // 目标歌曲对象。
        val selectedTrack = tracks.firstOrNull { track ->
            // 当前遍历的歌曲。
            track.id == trackId
        }
        if (selectedTrack == null) {
            return
        }
        // 是否可播放。
        val canPlay = !selectedTrack.uri.isNullOrBlank()
        // 解析歌曲时长（秒）。
        val durationSeconds = parseDurationSeconds(selectedTrack.durationText)
        // 更新 UI 状态。
        uiState = uiState.copy(
            currentTrack = selectedTrack,
            playerState = uiState.playerState.copy(
                isPlaying = canPlay,
                progress = 0f,
                positionSeconds = 0,
                durationSeconds = durationSeconds,
                currentTrackId = selectedTrack.id,
            )
        )
        if (canPlay) {
            playTrack(selectedTrack, true)
            startProgressTicker()
        } else {
            stopProgressTicker()
        }
    }

    /**
     * 启动播放进度计时。
     */
    private fun startProgressTicker() {
        // 停止旧任务。
        stopProgressTicker()
        progressJob = viewModelScope.launch {
            while (uiState.playerState.isPlaying) {
                delay(progressIntervalMs)
                updateProgressTick()
            }
        }
    }

    /**
     * 停止播放进度计时。
     */
    private fun stopProgressTicker() {
        progressJob?.cancel()
        progressJob = null
    }

    /**
     * 单次进度更新。
     */
    private fun updateProgressTick() {
        // 当前播放器状态。
        val currentState = uiState.playerState
        // 播放器时长（毫秒）。
        val durationMs = player.duration
        // 播放器当前进度（毫秒）。
        val positionMs = player.currentPosition
        // 当前总时长（秒）。
        val durationSeconds = if (durationMs > 0) (durationMs / 1000L).toInt() else currentState.durationSeconds
        // 当前播放位置（秒）。
        val positionSeconds = if (durationMs > 0) (positionMs / 1000L).toInt() else currentState.positionSeconds + 1
        if (durationSeconds <= 0) {
            return
        }
        if (positionSeconds >= durationSeconds) {
            selectNextTrack()
            return
        }
        // 计算进度。
        val progress = positionSeconds.toFloat() / durationSeconds.toFloat()
        // 更新 UI 状态。
        uiState = uiState.copy(
            playerState = currentState.copy(
                positionSeconds = positionSeconds,
                durationSeconds = durationSeconds,
                progress = progress,
            )
        )
    }

    /**
     * 拖拽进度到指定位置。
     *
     * @param progress 目标进度（0.0 ~ 1.0）。
     */
    private fun seekToProgress(progress: Float) {
        // 当前播放器状态。
        val currentState = uiState.playerState
        // 当前总时长。
        val durationSeconds = currentState.durationSeconds
        if (durationSeconds <= 0) {
            return
        }
        // 目标进度（安全范围）。
        val safeProgress = progress.coerceIn(0f, 1f)
        // 播放器时长（毫秒）。
        val durationMs = if (player.duration > 0) player.duration else durationSeconds * 1000L
        // 目标播放位置（毫秒）。
        val targetPositionMs = (durationMs * safeProgress).toLong()
        // 目标播放位置（秒）。
        val targetPosition = (targetPositionMs / 1000L).toInt()
        // 实际执行 seek。
        if (player.duration > 0) {
            player.seekTo(targetPositionMs)
        }
        // 更新 UI 状态。
        uiState = uiState.copy(
            playerState = currentState.copy(
                positionSeconds = targetPosition,
                progress = safeProgress,
            )
        )
    }

    /**
     * 播放指定歌曲。
     *
     * @param track 目标歌曲。
     * @param autoPlay 是否自动播放。
     */
    private fun playTrack(track: Track?, autoPlay: Boolean) {
        if (track == null) {
            return
        }
        // 音频 Uri。
        val uri = track.uri
        if (uri.isNullOrBlank()) {
            return
        }
        // 媒体项。
        val mediaItem = MediaItem.fromUri(uri)
        player.setMediaItem(mediaItem)
        player.prepare()
        if (autoPlay) {
            player.play()
        } else {
            player.pause()
        }
    }

    /**
     * 扫描本地音乐并刷新列表。
     */
    private fun scanLocalMusic() {
        viewModelScope.launch {
            // 扫描并缓存。
            val tracks = localMusicRepository.scanAndCache()
            if (tracks.isNotEmpty()) {
                applyTrackList(tracks)
            }
        }
    }

    /**
     * 加载本地缓存的音乐列表。
     */
    private fun loadCachedTracks() {
        viewModelScope.launch {
            // 读取缓存列表。
            val tracks = localMusicRepository.loadCachedTracks()
            if (tracks.isNotEmpty()) {
                applyTrackList(tracks)
            }
        }
    }

    /**
     * 应用新的歌曲列表。
     *
     * @param tracks 新的歌曲列表。
     */
    private fun applyTrackList(tracks: List<Track>) {
        // 选中第一首作为默认歌曲。
        val currentTrack = tracks.firstOrNull()
        // 解析歌曲时长（秒）。
        val durationSeconds = parseDurationSeconds(currentTrack?.durationText)
        // 停止当前播放并清空媒体。
        player.stop()
        player.clearMediaItems()
        uiState = uiState.copy(
            tracks = tracks,
            currentTrack = currentTrack,
            playerState = uiState.playerState.copy(
                isPlaying = false,
                progress = 0f,
                positionSeconds = 0,
                durationSeconds = durationSeconds,
                currentTrackId = currentTrack?.id,
            )
        )
        stopProgressTicker()
    }

    /**
     * 解析歌曲时长文本为秒。
     *
     * @param durationText 时长文本（例如 3:45）。
     */
    private fun parseDurationSeconds(durationText: String?): Int {
        if (durationText.isNullOrBlank()) {
            return defaultDurationSeconds
        }
        // 拆分分钟与秒数。
        val parts = durationText.split(":")
        if (parts.size != 2) {
            return defaultDurationSeconds
        }
        // 分钟数字。
        val minutes = parts[0].toIntOrNull() ?: return defaultDurationSeconds
        // 秒数数字。
        val seconds = parts[1].toIntOrNull() ?: return defaultDurationSeconds
        // 计算总秒数。
        val totalSeconds = minutes * 60 + seconds
        return if (totalSeconds > 0) totalSeconds else defaultDurationSeconds
    }

    override fun onCleared() {
        // 释放进度任务。
        stopProgressTicker()
        // 释放播放器资源。
        player.release()
        super.onCleared()
    }
}
