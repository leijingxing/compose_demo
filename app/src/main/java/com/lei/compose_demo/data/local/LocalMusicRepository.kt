package com.lei.compose_demo.data.local

import android.content.Context
import com.lei.compose_demo.data.Track

/**
 * 本地音乐仓库。
 *
 * @param context 应用上下文。
 */
class LocalMusicRepository(
    // 应用上下文。
    private val context: Context,
) {
    // 本地扫描器。
    private val scanner = LocalMusicScanner()
    // 缓存存储。
    private val cacheStore = LocalMusicCacheStore(context)

    /**
     * 扫描本地音乐并缓存。
     */
    suspend fun scanAndCache(): List<Track> {
        // 扫描结果。
        val tracks = scanner.scan(context)
        // 保存到缓存。
        cacheStore.saveTracks(tracks)
        return tracks
    }

    /**
     * 读取缓存中的本地音乐。
     */
    suspend fun loadCachedTracks(): List<Track> {
        return cacheStore.getTracks()
    }

    /**
     * 清空本地音乐缓存。
     */
    suspend fun clearCache() {
        cacheStore.clear()
    }
}
