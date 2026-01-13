package com.lei.compose_demo.ui.player

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lei.compose_demo.data.PlayerState
import com.lei.compose_demo.data.Track

private val CoverGradientColors = listOf(Color(0xFF1E293B), Color(0xFF0F172A))
private val CoverBorderColor = Color(0xFF1F2937)

/**
 * 播放器主页内容（封面 + 信息 + 进度 + 控制）。
 *
 * @param currentTrack 当前歌曲。
 * @param playerState 播放器状态。
 * @param onTogglePlay 播放/暂停回调。
 * @param onSeekTo 进度跳转回调。
 * @param onPrevious 上一首。
 * @param onNext 下一首。
 * @param sharedTransitionScope 共享元素转场作用域。
 * @param animatedVisibilityScope 动画可见性作用域。
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun PlayerMainPage(
    currentTrack: Track?,
    playerState: PlayerState,
    onTogglePlay: () -> Unit,
    onSeekTo: (Float) -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // 1. 封面区域 (自适应高度权重)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            AlbumCover(
                currentTrack = currentTrack,
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = animatedVisibilityScope
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 2. 歌曲信息
        TrackInfo(currentTrack = currentTrack)

        Spacer(modifier = Modifier.height(32.dp))

        // 3. 进度条
        PlayerProgress(
            playerState = playerState,
            onSeekTo = onSeekTo
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 4. 底部控制栏
        PlayerControls(
            isPlaying = playerState.isPlaying,
            onTogglePlay = onTogglePlay,
            onPrevious = onPrevious,
            onNext = onNext
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

/**
 * 专辑封面区域。
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun AlbumCover(
    currentTrack: Track?,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .aspectRatio(1f)
            .let { modifier ->
                if (currentTrack != null) {
                    with(sharedTransitionScope) {
                        modifier.sharedElement(
                            state = rememberSharedContentState(key = "cover-${currentTrack.id}"),
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                    }
                } else modifier
            }
            .background(
                Brush.linearGradient(colors = CoverGradientColors),
                shape = RoundedCornerShape(28.dp)
            )
            .border(1.dp, CoverBorderColor, RoundedCornerShape(28.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TagChip(text = "HI-RES")
                TagChip(text = "DOLBY")
                TagChip(text = "BUSINESS")
            }
        }
    }
}

/**
 * 歌曲信息区域。
 *
 * @param currentTrack 当前歌曲数据。
 */
@Composable
private fun TrackInfo(currentTrack: Track?) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = currentTrack?.title ?: "未选择歌曲",
            color = TitleColor,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text = currentTrack?.artist ?: "请选择一首歌曲",
            color = SubtitleColor,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "商务精选 · 会议模式",
                color = SubtitleColor,
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                text = "HQ 24bit",
                color = TagTextColor,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier
                    .background(TagBackgroundColor, shape = RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}
