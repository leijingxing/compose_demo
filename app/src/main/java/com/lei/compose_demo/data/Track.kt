package com.lei.compose_demo.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 歌曲数据模型。
 *
 * @param id 歌曲唯一标识。
 * @param title 歌曲标题。
 * @param artist 歌手名称。
 * @param durationText 时长文本（例如 3:45）。
 * @param uri 音频文件 Uri 字符串。
 */
@Serializable
data class Track(
    // 歌曲唯一标识。
    @SerialName("id") val id: String,
    // 歌曲标题。
    @SerialName("title") val title: String,
    // 歌手名称。
    @SerialName("artist") val artist: String,
    // 时长文本。
    @SerialName("durationText") val durationText: String,
    // 音频文件 Uri 字符串。
    @SerialName("uri") val uri: String? = null,
)
