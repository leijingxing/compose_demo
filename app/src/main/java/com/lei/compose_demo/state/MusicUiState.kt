package com.lei.compose_demo.state

import com.lei.compose_demo.data.PlayerState
import com.lei.compose_demo.data.Track

/**
 * 音乐页面 UI 状态。
 *
 * @param tracks 页面展示的歌曲列表。
 * @param currentTrack 当前选中的歌曲。
 * @param playerState 播放器状态。
 */
data class MusicUiState(
    // 页面展示的歌曲列表。
    val tracks: List<Track>,
    // 当前选中的歌曲。
    val currentTrack: Track?,
    // 播放器状态。
    val playerState: PlayerState,
)
