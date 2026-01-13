package com.lei.compose_demo.ui.player

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lei.compose_demo.data.PlayerState

/**
 * 歌词行数据模型。
 *
 * @property timeSeconds 该行歌词开始的时间（秒）。
 * @property text 歌词文本。
 */
data class LyricLine(val timeSeconds: Int, val text: String)

// 模拟的演示歌词数据
private val MockLyrics = listOf(
    LyricLine(0, "Music Starting..."),
    LyricLine(2, "欢迎收听商务精选系列"),
    LyricLine(5, "Welcome to Business Selection"),
    LyricLine(10, "专注每一个细节"),
    LyricLine(14, "Focus on every detail"),
    LyricLine(18, "成就卓越未来"),
    LyricLine(22, "Achieving excellence for the future"),
    LyricLine(26, "让灵感在旋律中流淌"),
    LyricLine(30, "Let inspiration flow in the melody"),
    LyricLine(35, "高效 · 专注 · 创新"),
    LyricLine(40, "Efficiency · Focus · Innovation"),
    LyricLine(45, "这是属于您的时刻"),
    LyricLine(50, "This is your moment"),
    LyricLine(55, "感受节奏的律动"),
    LyricLine(60, "Feel the rhythm"),
    LyricLine(65, "无论是会议还是思考"),
    LyricLine(70, "Whether meeting or thinking"),
    LyricLine(75, "音乐伴您前行"),
    LyricLine(80, "Music accompanies you"),
    LyricLine(85, "Compose Demo 展示专用"),
    LyricLine(90, "Compose Demo Showcase Only"),
    LyricLine(95, "享受编程的乐趣"),
    LyricLine(100, "Enjoy the joy of coding"),
    LyricLine(110, "感谢您的使用"),
    LyricLine(120, "Thank you for using"),
)

/**
 * 歌词页面。
 * 支持自动滚动和高亮显示。
 *
 * @param playerState 播放器状态，用于同步歌词进度。
 */
@Composable
fun PlayerLyricsPage(
    playerState: PlayerState
) {
    val listState = rememberLazyListState()

    // 计算当前应该高亮的歌词行索引
    val currentLineIndex = remember(playerState.positionSeconds) {
        val index = MockLyrics.indexOfLast { it.timeSeconds <= playerState.positionSeconds }
        if (index >= 0) index else 0
    }

    // 当索引变化时，自动滚动到对应位置
    LaunchedEffect(currentLineIndex) {
        // 滚动到屏幕中间偏上的位置，体验更好
        listState.animateScrollToItem(
            index = if (currentLineIndex > 1) currentLineIndex - 1 else 0,
            scrollOffset = 0
        )
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (MockLyrics.isEmpty()) {
            Text(
                text = "暂无歌词",
                color = SubtitleColor,
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(vertical = 50.dp, horizontal = 20.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(MockLyrics) { index, lyric ->
                    val isCurrentLine = index == currentLineIndex
                    
                    // 简单的文本大小和透明度动画
                    val targetAlpha = if (isCurrentLine) 1f else 0.4f
                    val targetScale = if (isCurrentLine) 1.2f else 1f
                    
                    val alpha by animateFloatAsState(targetValue = targetAlpha, label = "alpha")
                    val scale by animateFloatAsState(targetValue = targetScale, label = "scale")

                    Text(
                        text = lyric.text,
                        color = if (isCurrentLine) Color.White else SubtitleColor,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = if (isCurrentLine) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 18.sp * scale
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                            .alpha(alpha)
                    )
                }
            }
        }
    }
}
