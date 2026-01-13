package com.lei.compose_demo.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.lei.compose_demo.data.PlayerState
import com.lei.compose_demo.data.Track
import com.lei.compose_demo.ui.player.PlayerLyricsPage
import com.lei.compose_demo.ui.player.PlayerMainPage
import com.lei.compose_demo.ui.player.PlayerTopBar

// 背景颜色常量
private val BackgroundColors = listOf(Color(0xFF0B0F14), Color(0xFF0F172A), Color(0xFF0B0F14))

/**
 * 播放详情页面入口。
 * 使用 HorizontalPager 管理 [PlayerMainPage] 和 [PlayerLyricsPage]。
 *
 * @param currentTrack 当前播放歌曲。
 * @param playerState 播放器状态。
 * @param onBack 返回事件。
 * @param onTogglePlay 播放/暂停事件。
 * @param onSeekTo 拖拽进度事件。
 * @param onPrevious 上一首事件。
 * @param onNext 下一首事件。
 * @param sharedTransitionScope 共享元素转场作用域。
 * @param animatedVisibilityScope 动画可见性作用域。
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun PlayerDetailScreen(
    currentTrack: Track?,
    playerState: PlayerState,
    onBack: () -> Unit,
    onTogglePlay: () -> Unit,
    onSeekTo: (Float) -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    // 系统返回键处理
    BackHandler(onBack = onBack)

    // 页面进入动画状态
    val visibleState = remember {
        MutableTransitionState(false).apply { targetState = true }
    }

    // 页面背景渐变
    val backgroundBrush = remember { Brush.verticalGradient(colors = BackgroundColors) }

    // Pager 状态：2页（0: 主页, 1: 歌词）
    val pagerState = rememberPagerState(pageCount = { 2 })

    AnimatedVisibility(
        visibleState = visibleState,
        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 3 }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 3 })
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush)
                .systemBarsPadding() // 适配系统状态栏
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. 公共顶部导航栏 (Padding 移到这里处理，保证 Pager 全宽或者统一 Padding)
            Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                PlayerTopBar(onBack = onBack)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. 页面切换区域
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { page ->
                when (page) {
                    0 -> PlayerMainPage(
                        currentTrack = currentTrack,
                        playerState = playerState,
                        onTogglePlay = onTogglePlay,
                        onSeekTo = onSeekTo,
                        onPrevious = onPrevious,
                        onNext = onNext,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                    1 -> PlayerLyricsPage(
                        playerState = playerState
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. 页面指示器 (Dots)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(2) { iteration ->
                    val color = if (pagerState.currentPage == iteration) Color.White else Color.White.copy(alpha = 0.2f)
                    val size = if (pagerState.currentPage == iteration) 8.dp else 6.dp
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .clip(CircleShape)
                            .background(color)
                            .size(size)
                    )
                }
            }
        }
    }
}
