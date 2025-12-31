package com.lei.compose_demo.data.remote.dto

import com.lei.compose_demo.data.Track
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 歌曲 DTO。
 *
 * @param id 歌曲 ID。
 * @param title 歌曲标题。
 * @param artist 歌手名称。
 * @param durationText 时长文本。
 */
@Serializable
data class TrackDto(
    // 歌曲 ID。
    @SerialName("id") val id: String,
    // 歌曲标题。
    @SerialName("title") val title: String,
    // 歌手名称。
    @SerialName("artist") val artist: String,
    // 时长文本。
    @SerialName("duration") val durationText: String,
)

/**
 * DTO 转领域模型。
 */
fun TrackDto.toDomain(): Track {
    // 转换后的领域模型。
    val track = Track(
        id = id,
        title = title,
        artist = artist,
        durationText = durationText,
    )
    return track
}
