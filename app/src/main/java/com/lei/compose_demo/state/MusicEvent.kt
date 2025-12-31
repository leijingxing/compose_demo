package com.lei.compose_demo.state

/**
 * 音乐页面事件。
 */
sealed class MusicEvent {
    /**
     * 播放/暂停切换事件。
     */
    data object TogglePlay : MusicEvent()

    /**
     * 下一首事件。
     */
    data object Next : MusicEvent()

    /**
     * 上一首事件。
     */
    data object Previous : MusicEvent()

    /**
     * 选择指定歌曲事件。
     *
     * @param trackId 被选中的歌曲 ID。
     */
    data class SelectTrack(
        // 被选中的歌曲 ID。
        val trackId: String,
    ) : MusicEvent()
}
