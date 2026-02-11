package com.lei.compose_demo.service

import android.media.audiofx.Visualizer
import android.os.SystemClock
import kotlin.math.hypot
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

/**
 * 基于系统 Visualizer 的 FFT 频谱采集器。
 *
 * @param bandCount 输出频带数量。
 * @param onBandsUpdate 频带更新回调。
 */
class AudioFftAnalyzer(
    // 输出频带数量。
    private val bandCount: Int,
    // 频带更新回调。
    private val onBandsUpdate: (List<Float>) -> Unit,
) {
    // 当前 Visualizer 实例。
    private var visualizer: Visualizer? = null
    // 当前平滑后的频带缓存。
    private var smoothedBands: FloatArray = FloatArray(size = bandCount) { 0f }
    // 每个频带的噪声底线。
    private var bandNoiseFloor: FloatArray = FloatArray(size = bandCount) { 0.02f }
    // AGC 短时包络（快响应）。
    private var shortEnvelope: Float = 0.30f
    // AGC 长时包络（慢响应）。
    private var longEnvelope: Float = 0.30f
    // AGC 组合包络。
    private var mixEnvelope: Float = 0.30f
    // 上次向 UI 输出的时间戳（毫秒）。
    private var lastEmitAtMs: Long = 0L
    // 最小输出间隔（毫秒），用于降低抖动。
    private val minEmitIntervalMs: Long = 40L

    /**
     * 绑定 ExoPlayer 的音频 Session。
     *
     * @param audioSessionId 音频会话 ID。
     */
    fun attach(audioSessionId: Int) {
        if (audioSessionId <= 0) {
            return
        }
        release()
        try {
            // 创建 Visualizer 并绑定到指定 Session。
            val createdVisualizer = Visualizer(audioSessionId)
            // 使用设备支持的最大 FFT 缓冲区，提升频谱分辨率。
            val maxCaptureSize = Visualizer.getCaptureSizeRange()[1]
            createdVisualizer.captureSize = maxCaptureSize
            createdVisualizer.setDataCaptureListener(
                object : Visualizer.OnDataCaptureListener {
                    override fun onWaveFormDataCapture(
                        visualizer: Visualizer?,
                        waveform: ByteArray?,
                        samplingRate: Int
                    ) = Unit

                    override fun onFftDataCapture(
                        visualizer: Visualizer?,
                        fft: ByteArray?,
                        samplingRate: Int
                    ) {
                        if (fft == null || fft.size < 4) {
                            return
                        }
                        // 当前回调时间戳。
                        val nowMs = SystemClock.uptimeMillis()
                        // 与上次输出的时间差。
                        val deltaMs = nowMs - lastEmitAtMs
                        if (deltaMs < minEmitIntervalMs) {
                            return
                        }
                        lastEmitAtMs = nowMs
                        // 转换 FFT 原始字节为可绘制频带。
                        val nextBands = buildBandsFromFft(
                            fft = fft,
                            samplingRateMilliHz = samplingRate
                        )
                        onBandsUpdate(nextBands)
                    }
                },
                // 采样率略降，减少高频抖动感。
                Visualizer.getMaxCaptureRate() / 2,
                false,
                true
            )
            createdVisualizer.enabled = true
            visualizer = createdVisualizer
        } catch (_: Throwable) {
            // 某些机型可能不支持或无权限，失败时保持静默避免崩溃。
            release()
        }
    }

    /**
     * 清空当前频带（用于暂停或切换状态）。
     */
    fun clear() {
        // 重置平滑频带缓存。
        smoothedBands = FloatArray(size = bandCount) { 0f }
        // 重置噪声底线缓存。
        bandNoiseFloor = FloatArray(size = bandCount) { 0.02f }
        // 重置 AGC 短时包络。
        shortEnvelope = 0.30f
        // 重置 AGC 长时包络。
        longEnvelope = 0.30f
        // 重置 AGC 组合包络。
        mixEnvelope = 0.30f
        // 重置输出时间戳。
        lastEmitAtMs = 0L
        // 输出全 0 频带，驱动 UI 回落。
        onBandsUpdate(List(size = bandCount) { 0f })
    }

    /**
     * 释放采集资源。
     */
    fun release() {
        // 当前 Visualizer 引用。
        val currentVisualizer = visualizer
        if (currentVisualizer != null) {
            try {
                currentVisualizer.enabled = false
                currentVisualizer.release()
            } catch (_: Throwable) {
                // release 失败时忽略，避免影响主流程。
            }
        }
        visualizer = null
    }

    /**
     * 将 FFT 字节转换为平滑后的频带强度。
     *
     * @param fft FFT 原始数据。
     * @param samplingRateMilliHz 采样率（毫赫兹）。
     */
    private fun buildBandsFromFft(fft: ByteArray, samplingRateMilliHz: Int): List<Float> {
        // FFT 可用复数对数量（除去 DC/奈奎斯特特殊位）。
        val complexCount = fft.size / 2
        if (complexCount <= 2) {
            return List(size = bandCount) { 0f }
        }
        // 采样率（Hz）。
        val samplingRateHz = (samplingRateMilliHz / 1000f).coerceAtLeast(8000f)
        // FFT 长度。
        val fftSize = fft.size
        // 单个频点对应频率步进（Hz）。
        val frequencyStep = samplingRateHz / fftSize.toFloat()
        // 频带最小频率（Hz）。
        val minFrequencyHz = 32f
        // 频带最大频率（Hz）。
        val maxFrequencyHz = min(16000f, samplingRateHz * 0.48f)
        // 对数频带边界数组（Hz）。
        val bandEdges = buildLogBandEdges(
            bandCount = bandCount,
            minFrequencyHz = minFrequencyHz,
            maxFrequencyHz = maxFrequencyHz
        )

        // 每个频点的归一化振幅。
        val magnitudes = FloatArray(size = complexCount) { 0f }
        // 遍历复数频点，计算模长。
        for (index in 1 until complexCount - 1) {
            // 实部字节值。
            val real = fft[index * 2].toFloat()
            // 虚部字节值。
            val imaginary = fft[index * 2 + 1].toFloat()
            // 频点振幅。
            val amplitude = hypot(real, imaginary)
            // 对数压缩，减少高振幅主导。
            val compressed = ln(1f + amplitude) / ln(129f)
            // 基础增益提升，增强中低能量可见度。
            val boosted = (compressed * 2.2f).coerceIn(0f, 1f)
            // Gamma 修正，提升弱信号细节。
            val gammaCorrected = boosted.toDouble().pow(0.78).toFloat()
            // 当前频点频率（Hz）。
            val frequencyHz = index.toFloat() * frequencyStep
            // 听感加权与平坦响应混合，避免低频被过度压制。
            val hearingWeight = aWeightingGain(frequencyHz = frequencyHz)
            // 混合后的频点权重（低频更容易保留）。
            val mixedWeight = 0.78f + hearingWeight * 0.22f
            // 当前频点加权结果。
            val hearingWeighted = gammaCorrected * mixedWeight
            magnitudes[index] = hearingWeighted.coerceIn(0f, 1f)
        }

        // 本帧频带输出。
        val frameBands = FloatArray(size = bandCount) { 0f }
        // 按真实频率边界聚合频带。
        for (bandIndex in 0 until bandCount) {
            // 当前频带起始频率。
            val bandStartHz = bandEdges[bandIndex]
            // 当前频带结束频率。
            val bandEndHz = bandEdges[bandIndex + 1]
            // 起始频点下标。
            val startIndex = frequencyToBinIndex(
                frequencyHz = bandStartHz,
                frequencyStep = frequencyStep,
                maxIndex = complexCount - 1
            )
            // 结束频点下标（开区间）。
            val endIndex = max(
                startIndex + 1,
                frequencyToBinIndex(
                    frequencyHz = bandEndHz,
                    frequencyStep = frequencyStep,
                    maxIndex = complexCount - 1
                )
            )
            // 当前频带能量累积值。
            var energySum = 0f
            // 当前频带样本数量。
            var sampleCount = 0
            for (freqIndex in startIndex until endIndex.coerceAtMost(complexCount)) {
                energySum += magnitudes[freqIndex]
                sampleCount += 1
            }
            // 当前频带平均能量。
            val averageEnergy = if (sampleCount > 0) energySum / sampleCount else 0f
            frameBands[bandIndex] = averageEnergy
        }

        // 空间平滑频带，减少相邻柱子的锯齿突刺。
        val spatialBands = FloatArray(size = bandCount) { 0f }
        for (bandIndex in 0 until bandCount) {
            // 左邻频带值。
            val leftValue = frameBands[(bandIndex - 1).coerceAtLeast(0)]
            // 中心频带值。
            val centerValue = frameBands[bandIndex]
            // 右邻频带值。
            val rightValue = frameBands[(bandIndex + 1).coerceAtMost(bandCount - 1)]
            // 三点加权平滑值。
            val smoothedValue = leftValue * 0.22f + centerValue * 0.56f + rightValue * 0.22f
            spatialBands[bandIndex] = smoothedValue
        }

        // 本帧峰值能量。
        val currentPeak = spatialBands.maxOrNull() ?: 0f
        // 短时包络上升系数。
        val shortRise = 0.42f
        // 短时包络下降系数。
        val shortFall = 0.14f
        // 长时包络上升系数。
        val longRise = 0.06f
        // 长时包络下降系数。
        val longFall = 0.018f
        // 更新短时包络。
        shortEnvelope = if (currentPeak >= shortEnvelope) {
            shortEnvelope + (currentPeak - shortEnvelope) * shortRise
        } else {
            shortEnvelope + (currentPeak - shortEnvelope) * shortFall
        }
        // 更新长时包络。
        longEnvelope = if (currentPeak >= longEnvelope) {
            longEnvelope + (currentPeak - longEnvelope) * longRise
        } else {
            longEnvelope + (currentPeak - longEnvelope) * longFall
        }
        // 组合 AGC 包络。
        mixEnvelope = (shortEnvelope * 0.72f + longEnvelope * 0.28f).coerceIn(0.06f, 1f)

        // 时间平滑 attack（上升）系数。
        val attackFactor = 0.36f
        // 时间平滑 release（下降）系数。
        val releaseFactor = 0.07f
        for (bandIndex in 0 until bandCount) {
            // 当前频带原始值。
            val rawBand = spatialBands[bandIndex]
            // 当前频带旧噪声底线。
            val oldFloor = bandNoiseFloor[bandIndex]
            // 噪声底线上升系数。
            val floorRise = 0.03f
            // 噪声底线下降系数。
            val floorFall = 0.12f
            // 噪声底线更新系数。
            val floorAlpha = if (rawBand < oldFloor) floorFall else floorRise
            // 新噪声底线值。
            val newFloor = oldFloor + (rawBand - oldFloor) * floorAlpha
            bandNoiseFloor[bandIndex] = newFloor.coerceIn(0.005f, 0.25f)
            // 去噪后的有效能量。
            val denoised = (rawBand - bandNoiseFloor[bandIndex]).coerceAtLeast(0f)
            // AGC 归一化结果。
            val agcNormalized = (denoised / mixEnvelope).coerceIn(0f, 1f)
            // 当前频带位置比例。
            val positionRatio =
                if (bandCount > 1) bandIndex.toFloat() / (bandCount - 1).toFloat() else 1f
            // 频段权重（轻度低频增强，避免左侧过弱）。
            val lowCutWeight = 1.08f - positionRatio * 0.14f
            // 频带加权后目标值。
            val weightedTarget = (agcNormalized * lowCutWeight).coerceIn(0f, 1f)
            // 低频段柔性压缩，避免左侧前几根柱子过高。
            val lowBandCompression = if (positionRatio < 0.35f) {
                // 压缩强度：越靠左压缩越明显。
                val strength = (0.35f - positionRatio) / 0.35f
                // 目标指数：1.0~1.45，指数越大压缩越明显。
                val exponent = 1f + strength * 0.45f
                weightedTarget.toDouble().pow(exponent.toDouble()).toFloat()
            } else {
                weightedTarget
            }
            // 门限以下直接归零，避免底噪常亮。
            val target = if (lowBandCompression < 0.012f) 0f else lowBandCompression
            // 旧平滑值。
            val previous = smoothedBands[bandIndex]
            // attack/release 选择系数。
            val alpha = if (target >= previous) attackFactor else releaseFactor
            // 新平滑值。
            val smoothed = previous + (target - previous) * alpha
            smoothedBands[bandIndex] = smoothed.coerceIn(0f, 1f)
        }
        return smoothedBands.toList()
    }

    /**
     * 构建对数分布的频带边界。
     *
     * @param bandCount 频带数量。
     * @param minFrequencyHz 最小频率。
     * @param maxFrequencyHz 最大频率。
     */
    private fun buildLogBandEdges(
        bandCount: Int,
        minFrequencyHz: Float,
        maxFrequencyHz: Float
    ): FloatArray {
        // 有效最小频率。
        val safeMinHz = minFrequencyHz.coerceAtLeast(20f)
        // 有效最大频率。
        val safeMaxHz = max(maxFrequencyHz, safeMinHz + 1f)
        // 对数边界结果数组。
        val edges = FloatArray(size = bandCount + 1) { safeMinHz }
        // 总频比值。
        val ratio = safeMaxHz / safeMinHz
        for (index in 0..bandCount) {
            // 当前边界插值比例。
            val t = index.toFloat() / bandCount.toFloat()
            // 当前边界频率值。
            val edgeHz = safeMinHz * ratio.toDouble().pow(t.toDouble()).toFloat()
            edges[index] = edgeHz
        }
        return edges
    }

    /**
     * 将频率值映射为 FFT 频点下标。
     *
     * @param frequencyHz 频率值（Hz）。
     * @param frequencyStep 单频点频率步进（Hz）。
     * @param maxIndex 可用最大下标。
     */
    private fun frequencyToBinIndex(
        frequencyHz: Float,
        frequencyStep: Float,
        maxIndex: Int
    ): Int {
        // 安全步进值。
        val safeStep = frequencyStep.coerceAtLeast(1f)
        // 频率映射结果。
        val mapped = (frequencyHz / safeStep).roundToInt()
        return mapped.coerceIn(1, maxIndex)
    }

    /**
     * A-weighting 近似增益（线性值）。
     *
     * @param frequencyHz 频率值（Hz）。
     */
    private fun aWeightingGain(frequencyHz: Float): Float {
        // 安全频率值。
        val frequency = frequencyHz.coerceAtLeast(20f).toDouble()
        // 频率平方。
        val frequencySquare = frequency * frequency
        // A-weighting 分子项。
        val numerator = (12200.0 * 12200.0) * (frequencySquare * frequencySquare)
        // A-weighting 分母项。
        val denominator =
            (frequencySquare + 20.6 * 20.6) *
                sqrt((frequencySquare + 107.7 * 107.7) * (frequencySquare + 737.9 * 737.9)) *
                (frequencySquare + 12200.0 * 12200.0)
        // 频率 A-weighting dB 值。
        val aWeightDb = 2.0 + 20.0 * log10(numerator / denominator)
        // dB 转线性增益。
        val linearGain = 10.0.pow(aWeightDb / 20.0).toFloat()
        return linearGain.coerceIn(0.45f, 1.35f)
    }
}
