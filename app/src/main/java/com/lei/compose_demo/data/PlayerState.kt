package com.lei.compose_demo.data

/**
 * 播放器状态模型。
 *
 * @param isPlaying 是否正在播放。
 * @param progress 播放进度（0.0 ~ 1.0）。
 * @param currentTrackId 当前播放的歌曲 ID。
 */
data class PlayerState(
    // 是否正在播放。
    val isPlaying: Boolean,
    // 播放进度（0.0 ~ 1.0）。
    val progress: Float,
    // 当前播放的歌曲 ID。
    val currentTrackId: String?,
)
