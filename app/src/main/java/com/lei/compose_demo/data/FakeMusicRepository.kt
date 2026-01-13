package com.lei.compose_demo.data

/**
 * 假数据仓库，用于提供演示歌曲列表。
 */
class FakeMusicRepository {

    // 完整的演示歌曲列表。
    private val allTracks = listOf(
        Track(id = "t1", title = "Neon Skyline", artist = "Luna Vale", durationText = "3:45"),
        Track(id = "t2", title = "Midnight Drive", artist = "Echo City", durationText = "4:12"),
        Track(id = "t3", title = "Golden Hour", artist = "Mira", durationText = "3:58"),
        Track(id = "t4", title = "Soft Signals", artist = "Atlas Nine", durationText = "4:03"),
        Track(id = "t5", title = "Afterglow", artist = "Nova Lane", durationText = "3:36"),
        Track(id = "t6", title = "Deep Focus", artist = "Mindset", durationText = "5:10"),
        Track(id = "t7", title = "Business Class", artist = "Prestige", durationText = "3:15"),
        Track(id = "t8", title = "Strategic Vision", artist = "Executive", durationText = "4:20"),
        Track(id = "t9", title = "Brainstorm", artist = "Creative Flow", durationText = "3:55"),
        Track(id = "t10", title = "Future Trends", artist = "Market Watch", durationText = "4:05"),
        Track(id = "t11", title = "Morning Briefing", artist = "Daily News", durationText = "2:45"),
        Track(id = "t12", title = "Late Night Code", artist = "Dev Mode", durationText = "6:30"),
        Track(id = "t13", title = "Success Story", artist = "Inspiration", durationText = "3:50"),
        Track(id = "t14", title = "Urban Rhythm", artist = "City Beats", durationText = "4:15"),
        Track(id = "t15", title = "Cloud Computing", artist = "Server Side", durationText = "3:22"),
    )

    /**
     * 获取演示歌曲列表。
     */
    fun getTracks(): List<Track> {
        return allTracks
    }

    /**
     * 搜索歌曲。
     * @param query 搜索关键词。
     */
    fun search(query: String): List<Track> {
        if (query.isBlank()) return emptyList()
        return allTracks.filter {
            it.title.contains(query, ignoreCase = true) ||
            it.artist.contains(query, ignoreCase = true)
        }
    }
}
