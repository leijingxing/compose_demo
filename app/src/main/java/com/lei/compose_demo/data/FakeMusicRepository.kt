package com.lei.compose_demo.data

/**
 * 假数据仓库，用于提供演示歌曲列表。
 */
class FakeMusicRepository {
    /**
     * 获取演示歌曲列表。
     */
    fun getTracks(): List<Track> {
        // 演示歌曲列表。
        val tracks = listOf(
            Track(id = "t1", title = "Neon Skyline", artist = "Luna Vale", durationText = "3:45"),
            Track(id = "t2", title = "Midnight Drive", artist = "Echo City", durationText = "4:12"),
            Track(id = "t3", title = "Golden Hour", artist = "Mira", durationText = "3:58"),
            Track(id = "t4", title = "Soft Signals", artist = "Atlas Nine", durationText = "4:03"),
            Track(id = "t5", title = "Afterglow", artist = "Nova Lane", durationText = "3:36"),
        )
        return tracks
    }
}
