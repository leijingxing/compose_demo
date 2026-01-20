package com.lei.compose_demo.ui

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lei.compose_demo.data.PlayerState
import com.lei.compose_demo.data.Track

/**
 * 底部播放条。
 *
 * @param currentTrack 当前播放歌曲。
 * @param playerState 播放器状态。
 * @param onTogglePlay 点击播放/暂停。
 * @param onNext 点击下一首。
 * @param onOpenDetail 打开播放详情页。
 * @param sharedTransitionScope 共享元素转场作用域。
 * @param animatedVisibilityScope 动画可见性作用域。
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun PlayerBar(
    currentTrack: Track?,
    playerState: PlayerState,
    onTogglePlay: () -> Unit,
    onNext: () -> Unit,
    onOpenDetail: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    // 播放条背景色。
    val barColor = Color(0xFF151922)
    // 文字主色。
    val titleColor = Color.White
    // 次级文字颜色。
    val subtitleColor = Color(0xFFA1A1AA)
    // 进度条主色。
    val progressColor = Color(0xFF3B82F6)
    // 封面占位背景色。
    val coverColor = Color(0xFF2A2F3A)
    // 已播放时间文本。
    val elapsedText = formatTime(playerState.positionSeconds)
    // 总时长文本。
    val totalText = formatTime(playerState.durationSeconds)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(barColor)
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onOpenDetail),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 封面 Box
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(MaterialTheme.shapes.small)
                        .background(coverColor)
                        .let { modifier ->
                            if (currentTrack != null) {
                                with(sharedTransitionScope) {
                                    modifier.sharedElement(
                                        rememberSharedContentState(key = "cover-${currentTrack.id}"),
                                        animatedVisibilityScope = animatedVisibilityScope
                                    )
                                }
                            } else modifier
                        }
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = currentTrack?.title ?: "未选择歌曲",
                        color = titleColor,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Text(
                        text = currentTrack?.artist ?: "请选择一首歌曲",
                        color = subtitleColor,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = onTogglePlay) {
                    Icon(
                        imageVector = if (playerState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "播放控制",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
                IconButton(onClick = onNext) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "下一首",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = playerState.progress,
            color = progressColor,
            trackColor = progressColor.copy(alpha = 0.2f)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = elapsedText,
                color = subtitleColor,
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = totalText,
                color = subtitleColor,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

/**
 * 格式化时间为 mm:ss。
 *
 * @param seconds 总秒数。
 */
private fun formatTime(seconds: Int): String {
    // 安全的秒数。
    val safeSeconds = if (seconds < 0) 0 else seconds
    // 分钟数。
    val minutes = safeSeconds / 60
    // 剩余秒数。
    val remainSeconds = safeSeconds % 60
    return "%d:%02d".format(minutes, remainSeconds)
}