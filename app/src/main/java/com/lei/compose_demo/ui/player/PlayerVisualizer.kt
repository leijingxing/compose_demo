package com.lei.compose_demo.ui.player

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.math.max

// 频谱条主渐变色。
private val VisualizerMainGradient = listOf(
    Color(0xFF7DD3FC),
    Color(0xFF38BDF8),
    Color(0xFF0EA5E9)
)
// 频谱条高亮渐变色。
private val VisualizerHighlightGradient = listOf(
    Color(0xFFE2F7FF),
    Color(0xFFA5F3FC),
    Color(0xFF67E8F9)
)

/**
 * 播放页 FFT 背景频谱组件。
 *
 * @param isPlaying 是否正在播放。
 * @param fftBands FFT 频带强度数据。
 * @param modifier 修饰符。
 */
@Composable
fun BackgroundVisualizer(
    isPlaying: Boolean,
    fftBands: List<Float>,
    modifier: Modifier = Modifier
) {
    // 安全频带数据，避免空列表导致布局异常。
    val safeBands = remember(fftBands) {
        if (fftBands.isEmpty()) List(size = 48) { 0f } else fftBands
    }
    // 绘制频带数据（聚合后更稳定）。
    val displayBands = remember(safeBands) {
        aggregateBands(sourceBands = safeBands, targetCount = 26)
    }
    // 峰值保持数组（与 displayBands 一一对应）。
    val peakBands = remember(displayBands.size) { FloatArray(size = displayBands.size) { 0f } }
    // 峰值保持剩余帧数数组。
    val peakHoldFrames = remember(displayBands.size) { IntArray(size = displayBands.size) { 0 } }
    // 峰值保持帧数上限。
    val peakHoldMaxFrames = 7
    // 峰值下降速度。
    val peakFallSpeed = 0.035f

    // 更新峰值保持数据。
    displayBands.forEachIndexed { bandIndex, bandValue ->
        // 当前频带值。
        val current = bandValue.coerceIn(0f, 1f)
        // 当前峰值。
        val peak = peakBands[bandIndex]
        if (current >= peak) {
            peakBands[bandIndex] = current
            peakHoldFrames[bandIndex] = peakHoldMaxFrames
        } else {
            // 当前峰值剩余保持帧数。
            val hold = peakHoldFrames[bandIndex]
            if (hold > 0) {
                peakHoldFrames[bandIndex] = hold - 1
            } else {
                peakBands[bandIndex] = max(0f, peak - peakFallSpeed)
            }
        }
    }
    // 播放状态透明度动画。
    val visualizerAlpha by animateFloatAsState(
        targetValue = if (isPlaying) 0.92f else 0.48f,
        animationSpec = tween(durationMillis = 450, easing = LinearEasing),
        label = "visualizerAlpha"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .alpha(visualizerAlpha)
    ) {
        // 背景模糊底光层，增强氛围感。
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(56.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0x4D22D3EE),
                            Color(0x1A2BA6F7),
                            Color.Transparent
                        )
                    )
                )
        )
        // 轻微暗色蒙层，避免底光抢夺主体频谱。
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x33060B15))
        )
        // 底部重点暗化层，提升前景文字可读性。
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0x22040A12),
                            Color(0x66030911)
                        )
                    )
                )
        )
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .blur(6.dp)
                .padding(horizontal = 18.dp, vertical = 20.dp)
        ) {
            // 频谱条数量。
            val barCount = displayBands.size
            if (barCount <= 0) {
                return@Canvas
            }
            // 每条柱子的宽度。
            val barWidth = size.width / (barCount * 1.34f)
            // 柱子间距。
            val barGap = barWidth * 0.34f
            // 底部对齐基线 Y。
            val baseLineY = size.height * 0.82f
            // 柱子起始 X。
            val startX = (size.width - (barCount * barWidth + (barCount - 1) * barGap)) / 2f
            // 最高柱子高度。
            val maxBarHeight = size.height * 0.48f

            displayBands.forEachIndexed { bandIndex, bandValue ->
                // 当前频带强度。
                val clampedBand = bandValue.coerceIn(0f, 1f)
                // 当前频带峰值。
                val peakValue = peakBands[bandIndex].coerceIn(0f, 1f)
                // 低状态下保持一点呼吸感，不让 UI 完全静止。
                val baseEnergy = if (isPlaying) 0.04f else 0.02f
                // 当前柱子的基础能量。
                val energy = baseEnergy + clampedBand * if (isPlaying) 0.96f else 0.30f
                // 实际柱子高度。
                val barHeight = (maxBarHeight * energy).coerceAtLeast(8f)
                // 峰值标记高度。
                val peakHeight = (maxBarHeight * (0.04f + peakValue * 0.96f)).coerceAtLeast(8f)
                // 当前柱子左上角 X。
                val left = startX + bandIndex * (barWidth + barGap)
                // 当前柱子左上角 Y。
                val top = baseLineY - barHeight
                // 峰值标记 Y。
                val peakTop = baseLineY - peakHeight
                // 柱子圆角半径。
                val cornerRadius = CornerRadius(x = barWidth / 2f, y = barWidth / 2f)
                // 高频高亮渐变色（带透明度）。
                val highlightColors = listOf(
                    VisualizerHighlightGradient[0].copy(alpha = 0.42f),
                    VisualizerHighlightGradient[1].copy(alpha = 0.42f),
                    VisualizerHighlightGradient[2].copy(alpha = 0.42f)
                )

                // 主体频谱柱。
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = VisualizerMainGradient,
                        startY = top,
                        endY = baseLineY
                    ),
                    topLeft = Offset(left, top),
                    size = Size(width = barWidth, height = barHeight),
                    cornerRadius = cornerRadius
                )

                // 高频高亮层，提升质感。
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = highlightColors,
                        startY = top,
                        endY = top + barHeight * 0.62f
                    ),
                    topLeft = Offset(left, top),
                    size = Size(width = barWidth, height = barHeight * 0.62f),
                    cornerRadius = cornerRadius,
                    blendMode = BlendMode.Screen
                )

                // 底部反射，营造层次。
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0x40AAB4C2),
                            Color.Transparent
                        ),
                        startY = baseLineY,
                        endY = baseLineY + barHeight * 0.22f
                    ),
                    topLeft = Offset(left, baseLineY),
                    size = Size(width = barWidth, height = barHeight * 0.22f),
                    cornerRadius = cornerRadius
                )

                // 峰值保持线（细条）。
                drawRoundRect(
                    color = Color(0xCCE8F7FF),
                    topLeft = Offset(left, peakTop),
                    size = Size(width = barWidth, height = 2.2f),
                    cornerRadius = CornerRadius(x = 1.2f, y = 1.2f),
                    blendMode = BlendMode.Screen
                )
            }
        }
    }
}

/**
 * 将原始频带聚合到目标数量，降低视觉噪声。
 *
 * @param sourceBands 原始频带列表。
 * @param targetCount 目标频带数量。
 */
private fun aggregateBands(sourceBands: List<Float>, targetCount: Int): List<Float> {
    // 安全目标数量。
    val safeTargetCount = targetCount.coerceAtLeast(1)
    if (sourceBands.isEmpty()) {
        return List(size = safeTargetCount) { 0f }
    }
    // 原始频带数量。
    val sourceCount = sourceBands.size
    // 聚合后频带结果。
    val resultBands = MutableList(size = safeTargetCount) { 0f }
    for (targetIndex in 0 until safeTargetCount) {
        // 当前目标桶起始下标。
        val start = (targetIndex * sourceCount) / safeTargetCount
        // 当前目标桶结束下标（开区间）。
        val end = ((targetIndex + 1) * sourceCount) / safeTargetCount
        // 当前桶内能量和。
        var bucketSum = 0f
        // 当前桶样本数。
        var bucketCount = 0
        for (sourceIndex in start until end.coerceAtMost(sourceCount)) {
            bucketSum += sourceBands[sourceIndex]
            bucketCount += 1
        }
        // 当前桶平均值。
        val average = if (bucketCount > 0) bucketSum / bucketCount else 0f
        resultBands[targetIndex] = average
    }
    return resultBands
}
