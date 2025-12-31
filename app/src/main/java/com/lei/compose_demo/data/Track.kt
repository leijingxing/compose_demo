package com.lei.compose_demo.data

/**
 * 歌曲数据模型。
 *
 * @param id 歌曲唯一标识。
 * @param title 歌曲标题。
 * @param artist 歌手名称。
 * @param durationText 时长文本（例如 3:45）。
 */
data class Track(
    // 歌曲唯一标识。
    val id: String,
    // 歌曲标题。
    val title: String,
    // 歌手名称。
    val artist: String,
    // 时长文本。
    val durationText: String,
)
