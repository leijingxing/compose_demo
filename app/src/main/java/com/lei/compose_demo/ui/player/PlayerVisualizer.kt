package com.lei.compose_demo.ui.player

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * 播放页背景模拟波形组件。
 *
 * @param isPlaying 是否正在播放。
 * @param modifier 修饰符。
 */
@Composable
fun BackgroundVisualizer(
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    // 柱子数量
    val barCount = 20

    Box(
        modifier = modifier
            .fillMaxSize()
            .blur(8.dp) // 适度的模糊
            .alpha(0.5f) // 适中的透明度
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .align(Alignment.BottomCenter)
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            repeat(barCount) { index ->
                VisualizerBar(
                    isPlaying = isPlaying,
                    // 使用纯白，但是间隔调整透明度，营造层次感
                    color = Color.White.copy(alpha = if (index % 2 == 0) 0.8f else 0.4f),
                    animDelay = index * 40
                )
            }
        }
    }
}

/**
 * 单个波形条。
 */
@Composable
private fun RowScope.VisualizerBar(
    isPlaying: Boolean,
    color: Color,
    animDelay: Int
) {
    // 目标高度比例
    val heightScale = remember { Animatable(0.1f) }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            delay(animDelay.toLong())
            while (true) {
                // 随机高度 0.2 到 1.0
                val nextHeight = Random.nextFloat() * 0.8f + 0.2f
                val duration = Random.nextInt(400, 800)
                
                heightScale.animateTo(
                    targetValue = nextHeight,
                    animationSpec = tween(durationMillis = duration, easing = LinearEasing)
                )
            }
        } else {
            // 停止播放时缓缓降下
            heightScale.animateTo(
                targetValue = 0.1f,
                animationSpec = tween(durationMillis = 500)
            )
        }
    }

    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight(heightScale.value)
            .clip(CircleShape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        color.copy(alpha = 0.8f),
                        color.copy(alpha = 0.1f)
                    )
                )
            )
    )
}
