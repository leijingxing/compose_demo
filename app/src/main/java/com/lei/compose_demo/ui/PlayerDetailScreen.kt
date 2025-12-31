package com.lei.compose_demo.ui

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lei.compose_demo.data.PlayerState
import com.lei.compose_demo.data.Track

/**
 * 播放详情页面。
 *
 * @param currentTrack 当前播放歌曲。
 * @param playerState 播放器状态。
 * @param onBack 返回事件。
 * @param onTogglePlay 播放/暂停事件。
 * @param onSeekTo 拖拽进度事件。
 * @param onPrevious 上一首事件。
 * @param onNext 下一首事件。
 */
@Composable
fun PlayerDetailScreen(
    currentTrack: Track?,
    playerState: PlayerState,
    onBack: () -> Unit,
    onTogglePlay: () -> Unit,
    onSeekTo: (Float) -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
) {
    // 系统返回键处理，返回到列表页。
    BackHandler(onBack = onBack)

    // 页面背景色。
    val backgroundColor = Color(0xFF0F1115)
    // 主标题颜色。
    val titleColor = Color.White
    // 副标题颜色。
    val subtitleColor = Color(0xFFA1A1AA)
    // 进度条主色。
    val progressColor = Color(0xFF3B82F6)
    // 封面渐变起始色。
    val coverStartColor = Color(0xFF1F2937)
    // 封面渐变结束色。
    val coverEndColor = Color(0xFF111827)
    // 已播放时间文本。
    val elapsedText = formatTime(playerState.positionSeconds)
    // 总时长文本。
    val totalText = formatTime(playerState.durationSeconds)

    // 页面进入动画状态。
    val visibleState = remember {
        MutableTransitionState(false).apply { targetState = true }
    }

    AnimatedVisibility(
        visibleState = visibleState,
        enter = fadeIn() + slideInVertically(initialOffsetY = { offset ->
            // 进入时的位移基准。
            offset / 3
        }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { offset ->
            // 退出时的位移基准。
            offset / 3
        })
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "返回",
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "正在播放",
                color = titleColor,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .background(
                    Brush.linearGradient(
                        colors = listOf(coverStartColor, coverEndColor)
                    ),
                    shape = MaterialTheme.shapes.large
                )
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = currentTrack?.title ?: "未选择歌曲",
            color = titleColor,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text = currentTrack?.artist ?: "请选择一首歌曲",
            color = subtitleColor,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(20.dp))
        Slider(
            value = playerState.progress,
            onValueChange = onSeekTo,
            valueRange = 0f..1f,
            colors = SliderDefaults.colors(
                thumbColor = progressColor,
                activeTrackColor = progressColor,
                inactiveTrackColor = progressColor.copy(alpha = 0.2f)
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
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
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = onPrevious) {
                Icon(
                    imageVector = Icons.Default.SkipPrevious,
                    contentDescription = "上一首",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            IconButton(onClick = onTogglePlay) {
                Icon(
                    imageVector = if (playerState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "播放控制",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
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
