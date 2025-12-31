package com.lei.compose_demo.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.lei.compose_demo.data.FakeMusicRepository
import com.lei.compose_demo.data.PlayerState
import com.lei.compose_demo.data.Track

/**
 * 音乐页面 ViewModel，负责管理 UI 状态。
 */
class MusicViewModel : ViewModel() {
    // 数据仓库，用于提供歌曲列表。
    private val repository = FakeMusicRepository()

    // 当前 UI 状态。
    var uiState by mutableStateOf(createInitialState())
        private set

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
        // 初始播放器状态。
        val playerState = PlayerState(
            isPlaying = false,
            progress = 0f,
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
        // 复制当前播放器状态。
        val currentPlayerState = uiState.playerState
        // 切换播放状态。
        val newPlayerState = currentPlayerState.copy(isPlaying = !currentPlayerState.isPlaying)
        uiState = uiState.copy(playerState = newPlayerState)
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
        // 更新 UI 状态。
        uiState = uiState.copy(
            currentTrack = nextTrack,
            playerState = uiState.playerState.copy(
                isPlaying = true,
                progress = 0f,
                currentTrackId = nextTrack?.id,
            )
        )
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
        // 更新 UI 状态。
        uiState = uiState.copy(
            currentTrack = previousTrack,
            playerState = uiState.playerState.copy(
                isPlaying = true,
                progress = 0f,
                currentTrackId = previousTrack?.id,
            )
        )
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
        // 更新 UI 状态。
        uiState = uiState.copy(
            currentTrack = selectedTrack,
            playerState = uiState.playerState.copy(
                isPlaying = true,
                progress = 0f,
                currentTrackId = selectedTrack.id,
            )
        )
    }
}
