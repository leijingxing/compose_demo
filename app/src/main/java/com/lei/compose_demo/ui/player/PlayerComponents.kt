package com.lei.compose_demo.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lei.compose_demo.data.PlayerState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.MoreVert
// --- 常量定义 ---
val TitleColor = Color(0xFFF8FAFC)
val SubtitleColor = Color(0xFF94A3B8)
val ProgressColor = Color(0xFFD4AF37)
val ControlSurfaceColor = Color(0xFF111827)
val ControlBorderColor = Color(0xFF1F2937)
val AccentButtonColor = Color(0xFFD4AF37)
val AccentIconColor = Color(0xFF0B0F14)
val TagBackgroundColor = Color(0xFF1E293B)
val TagTextColor = Color(0xFFE2E8F0)



/**
 * 顶部导航栏。
 *
 * @param onBack 返回回调。
 */
@Composable
fun PlayerTopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // 下拉返回按钮
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .background(Color.White.copy(alpha = 0.05f), CircleShape)
                .size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ExpandMore,
                contentDescription = "收起",
                tint = TitleColor,
                modifier = Modifier.size(24.dp)
            )
        }

        // 中间标题
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "PLAYING NOW",
                color = SubtitleColor.copy(alpha = 0.7f),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
            )
        }

        // 更多选项按钮
        IconButton(
            onClick = { /* TODO: Show menu */ },
            modifier = Modifier
                .background(Color.White.copy(alpha = 0.05f), CircleShape)
                .size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "更多",
                tint = TitleColor,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * 播放进度条区域。
 * 包含拖拽交互逻辑优化。
 *
 * @param playerState 播放器状态。
 * @param onSeekTo 进度跳转回调。
 */
@Composable
fun PlayerProgress(
    playerState: PlayerState,
    onSeekTo: (Float) -> Unit
) {
    // 是否正在拖拽中。
    var isDragging by remember { mutableStateOf(false) }
    // 拖拽时的临时进度值。
    var dragProgress by remember { mutableFloatStateOf(0f) }

    // 计算当前显示的值：拖拽中显示临时值，否则显示真实播放进度。
    val sliderValue = if (isDragging) dragProgress else playerState.progress

    // 计算当前显示的时间：拖拽中基于临时比例计算，否则基于真实时间。
    val displaySeconds = if (isDragging) {
        (dragProgress * playerState.durationSeconds).toInt()
    } else {
        playerState.positionSeconds
    }

    val elapsedText = formatTime(displaySeconds)
    val totalText = formatTime(playerState.durationSeconds)

    // 监听外部进度更新，如果播放器切歌了或重置了，可能需要重置拖拽状态。
    LaunchedEffect(playerState.progress) {
        // 如果进度突变（例如切歌），可以在这里强制结束拖拽状态（视需求而定）
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Slider(
            value = sliderValue,
            onValueChange = { newValue ->
                isDragging = true
                dragProgress = newValue
            },
            onValueChangeFinished = {
                // 拖拽结束，提交变更。
                onSeekTo(dragProgress)
                isDragging = false
            },
            valueRange = 0f..1f,
            colors = SliderDefaults.colors(
                thumbColor = ProgressColor,
                activeTrackColor = ProgressColor,
                inactiveTrackColor = ProgressColor.copy(alpha = 0.2f)
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = elapsedText,
                color = SubtitleColor,
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = totalText,
                color = SubtitleColor,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

/**
 * 底部控制按钮区域。
 *
 * @param isPlaying 是否正在播放。
 * @param onTogglePlay 播放/暂停回调。
 * @param onPrevious 上一首。
 * @param onNext 下一首。
 */
@Composable
fun PlayerControls(
    isPlaying: Boolean,
    onTogglePlay: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(ControlSurfaceColor, shape = RoundedCornerShape(24.dp))
            .border(1.dp, ControlBorderColor, RoundedCornerShape(24.dp))
            .padding(horizontal = 24.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(
                onClick = onPrevious,
                modifier = Modifier
                    .size(48.dp)
                    .background(ControlBorderColor, shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.SkipPrevious,
                    contentDescription = "上一首",
                    tint = TitleColor,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            IconButton(
                onClick = onTogglePlay,
                modifier = Modifier
                    .size(64.dp)
                    .background(AccentButtonColor, shape = CircleShape)
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "播放控制",
                    tint = AccentIconColor,
                    modifier = Modifier.size(30.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            IconButton(
                onClick = onNext,
                modifier = Modifier
                    .size(48.dp)
                    .background(ControlBorderColor, shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = "下一首",
                    tint = TitleColor,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

/**
 * 简单的标签胶囊。
 *
 * @param text 标签文本。
 */
@Composable
fun TagChip(text: String) {
    Box(
        modifier = Modifier
            .background(TagBackgroundColor, shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            color = TagTextColor,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

/**
 * 格式化时间为 mm:ss。
 *
 * @param seconds 总秒数。
 */
fun formatTime(seconds: Int): String {
    // 安全的秒数。
    val safeSeconds = if (seconds < 0) 0 else seconds
    // 分钟数。
    val minutes = safeSeconds / 60
    // 剩余秒数。
    val remainSeconds = safeSeconds % 60
    return "%d:%02d".format(minutes, remainSeconds)
}
