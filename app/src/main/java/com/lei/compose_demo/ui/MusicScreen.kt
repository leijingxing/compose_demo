package com.lei.compose_demo.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lei.compose_demo.state.MusicEvent
import com.lei.compose_demo.state.MusicViewModel

/**
 * 音乐主页面。
 *
 * @param viewModel 页面 ViewModel。
 * @param onOpenDetail 打开播放详情页事件。
 */
@Composable
fun MusicScreen(
    viewModel: MusicViewModel = viewModel(),
    // 打开播放详情页事件。
    onOpenDetail: () -> Unit,
) {
    // 当前 UI 状态。
    val uiState = viewModel.uiState
    // 背景主色。
    val backgroundColor = Color(0xFF0F1115)
    // 卡片背景色。
    val cardColor = Color(0xFF171A21)

    Scaffold(
        containerColor = backgroundColor,
        bottomBar = {
            PlayerBar(
                currentTrack = uiState.currentTrack,
                playerState = uiState.playerState,
                onTogglePlay = { viewModel.onEvent(MusicEvent.TogglePlay) },    
                onNext = { viewModel.onEvent(MusicEvent.Next) },
                onOpenDetail = onOpenDetail,
            )
        }
    ) { innerPadding ->
        // 页面内容容器。
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            HeaderSection()
            Spacer(modifier = Modifier.height(16.dp))
            HeroCard(
                cardColor = cardColor,
                onOpenDetail = onOpenDetail,
            )
            Spacer(modifier = Modifier.height(20.dp))
            SectionTitle(title = "热门歌曲")
            Spacer(modifier = Modifier.height(12.dp))
            TrackList(
                tracks = uiState.tracks,
                currentTrack = uiState.currentTrack,
                onTrackClick = { clickedTrack ->
                    // 被点击的歌曲。
                    viewModel.onEvent(MusicEvent.SelectTrack(clickedTrack.id))
                }
            )
        }
    }
}

/**
 * 顶部标题区域。
 */
@Composable
private fun HeaderSection() {
    // 标题颜色。
    val titleColor = Color.White
    // 副标题颜色。
    val subtitleColor = Color(0xFFA1A1AA)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "Orbit Music",
                color = titleColor,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
            )
            Text(
                text = "商业风音乐播放器",
                color = subtitleColor,
                style = MaterialTheme.typography.bodySmall
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "搜索",
                tint = Color.White
            )
            Icon(
                imageVector = Icons.Default.NotificationsNone,
                contentDescription = "通知",
                tint = Color.White
            )
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(Color(0xFF2A2F3A))
            )
        }
    }
}

/**
 * 推荐大卡片区域。
 *
 * @param cardColor 卡片背景色。
 * @param onOpenDetail 打开播放详情页事件。
 */
@Composable
private fun HeroCard(
    cardColor: Color,
    onOpenDetail: () -> Unit,
) {
    // 渐变起始色。
    val gradientStart = Color(0xFF3B82F6)
    // 渐变结束色。
    val gradientEnd = Color(0xFF22C55E)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpenDetail),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = MaterialTheme.shapes.large
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(gradientStart, gradientEnd)
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "本周精选",
                    color = Color.White,
                    style = MaterialTheme.typography.labelLarge
                )
                Column {
                    Text(
                        text = "New Business Vibes",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "提升专注与效率",
                        color = Color.White.copy(alpha = 0.85f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

/**
 * 区域标题。
 *
 * @param title 标题文本。
 */
@Composable
private fun SectionTitle(
    title: String,
) {
    Text(
        text = title,
        color = Color.White,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
    )
}
