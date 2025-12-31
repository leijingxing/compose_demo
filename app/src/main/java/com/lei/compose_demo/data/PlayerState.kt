package com.lei.compose_demo.data

/**
 * 播放器状态模型。
 *
 * @param isPlaying 是否正在播放。
 * @param progress 播放进度（0.0 ~ 1.0）。
 * @param positionSeconds 当前播放位置（秒）。
 * @param durationSeconds 当前歌曲总时长（秒）。
 * @param currentTrackId 当前播放的歌曲 ID。
 */
data class PlayerState(
    // 是否正在播放。
    val isPlaying: Boolean,
    // 播放进度（0.0 ~ 1.0）。
    val progress: Float,
    // 当前播放位置（秒）。
    val positionSeconds: Int,
    // 当前歌曲总时长（秒）。
    val durationSeconds: Int,
    // 当前播放的歌曲 ID。
    val currentTrackId: String?,
)
