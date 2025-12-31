package com.lei.compose_demo.data.local

import android.content.Context
import android.content.ContentUris
import android.provider.MediaStore
import com.lei.compose_demo.data.Track

/**
 * 本地音乐扫描器。
 */
class LocalMusicScanner {
    /**
     * 扫描设备本地音乐。
     *
     * @param context 应用上下文。
     */
    fun scan(context: Context): List<Track> {
        // 结果列表。
        val tracks = mutableListOf<Track>()
        // 查询字段。
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
        )
        // 过滤条件。
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        // 排序规则。
        val sortOrder = "${MediaStore.Audio.Media.DATE_ADDED} DESC"
        // 查询游标。
        val cursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            sortOrder,
        )
        cursor?.use { result ->
            // 字段索引。
            val idIndex = result.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleIndex = result.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistIndex = result.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val durationIndex = result.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

            while (result.moveToNext()) {
                // 音乐 ID。
                val idLong = result.getLong(idIndex)
                // 音乐 ID。
                val id = idLong.toString()
                // 标题。
                val title = result.getString(titleIndex) ?: "未知标题"
                // 歌手。
                val artist = result.getString(artistIndex) ?: "未知歌手"
                // 时长（毫秒）。
                val durationMs = result.getLong(durationIndex)
                // 音频文件 Uri。
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    idLong,
                )
                // 时长文本。
                val durationText = formatDuration(durationMs)
                // 添加到列表。
                tracks.add(
                    Track(
                        id = id,
                        title = title,
                        artist = artist,
                        durationText = durationText,
                        uri = contentUri.toString(),
                    )
                )
            }
        }
        return tracks
    }

    /**
     * 将毫秒时长格式化为 mm:ss。
     *
     * @param durationMs 时长（毫秒）。
     */
    private fun formatDuration(durationMs: Long): String {
        // 安全的时长。
        val safeDurationMs = if (durationMs < 0) 0 else durationMs
        // 总秒数。
        val totalSeconds = (safeDurationMs / 1000L).toInt()
        // 分钟数。
        val minutes = totalSeconds / 60
        // 剩余秒数。
        val seconds = totalSeconds % 60
        return "%d:%02d".format(minutes, seconds)
    }
}
