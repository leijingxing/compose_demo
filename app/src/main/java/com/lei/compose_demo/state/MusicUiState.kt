package com.lei.compose_demo.state

import com.lei.compose_demo.data.PlayerState
import com.lei.compose_demo.data.Track

/**
 * 音乐页面 UI 状态。
 *
 * @property tracks 歌曲列表。
 * @property currentTrack 当前播放歌曲。
 * @property playerState 播放器状态。
 * @property searchResults 搜索结果列表。
 */
data class MusicUiState(
    val tracks: List<Track> = emptyList(),
    val currentTrack: Track? = null,
    val playerState: PlayerState = PlayerState(),
    val searchResults: List<Track> = emptyList(),
)
