package com.lei.compose_demo.state

/**
 * 音乐页面 UI 事件。
 */
sealed class MusicEvent {
    // 选中歌曲。
    data class SelectTrack(val trackId: String) : MusicEvent()
    // 扫描本地音乐（假装扫描）。
    object ScanLocalMusic : MusicEvent()
    // 切换播放/暂停。
    object TogglePlay : MusicEvent()
    // 拖拽进度。
    data class SeekTo(val progress: Float) : MusicEvent()
    // 上一首。
    object Previous : MusicEvent()
    // 下一首。
    object Next : MusicEvent()
    // 搜索歌曲。
    data class SearchTracks(val query: String) : MusicEvent()
}
