package com.lei.compose_demo.data.local

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.lei.compose_demo.data.Track
import kotlinx.coroutines.flow.first
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * 本地音乐列表缓存。
 *
 * @param context 应用上下文。
 */
class LocalMusicCacheStore(
    // 应用上下文。
    private val context: Context,
) {
    // JSON 解析配置。
    private val json = Json { ignoreUnknownKeys = true }

    /**
     * 保存音乐列表到本地缓存。
     *
     * @param tracks 音乐列表。
     */
    suspend fun saveTracks(tracks: List<Track>) {
        // 序列化后的 JSON。
        val jsonText = json.encodeToString(tracks)
        context.localMusicDataStore.edit { preferences ->
            // 保存 JSON 文本。
            preferences[TRACKS_KEY] = jsonText
        }
    }

    /**
     * 读取本地缓存的音乐列表。
     */
    suspend fun getTracks(): List<Track> {
        // 当前偏好数据。
        val preferences = context.localMusicDataStore.data.first()
        // JSON 文本内容。
        val jsonText = preferences[TRACKS_KEY]
        if (jsonText.isNullOrBlank()) {
            return emptyList()
        }
        // 反序列化为列表。
        val tracks = json.decodeFromString<List<Track>>(jsonText)
        return tracks
    }

    /**
     * 清空本地音乐缓存。
     */
    suspend fun clear() {
        context.localMusicDataStore.edit { preferences ->
            // 移除缓存键。
            preferences.remove(TRACKS_KEY)
        }
    }

    private companion object {
        // 本地缓存 Key。
        val TRACKS_KEY: Preferences.Key<String> = stringPreferencesKey("local_tracks")
    }
}

// 本地音乐缓存 DataStore 实例。
private val Context.localMusicDataStore by preferencesDataStore(name = "local_music_cache")
