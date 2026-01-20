package com.lei.compose_demo.ui

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.util.copy
import com.lei.compose_demo.state.MusicEvent
import com.lei.compose_demo.state.MusicViewModel

/**
 * 音乐主页面。
 *
 * @param viewModel 页面 ViewModel。
 * @param onOpenDetail 打开播放详情页事件。
 * @param sharedTransitionScope 共享元素转场作用域。
 * @param animatedVisibilityScope 动画可见性作用域。
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MusicScreen(
    viewModel: MusicViewModel = viewModel(),
    onOpenDetail: () -> Unit,
    onOpenSearch: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    // 当前 UI 状态。
    val uiState = viewModel.uiState
    // 背景主色。
    val backgroundColor = Color(0xFF0F1115)
    // 卡片背景色。
    val cardColor = Color(0xFF171A21)

    // 需要申请的权限名称。
    val storagePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_AUDIO
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }
    // 权限申请回调。
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        // 是否授予读取音频权限。
        if (granted) {
            viewModel.onEvent(MusicEvent.ScanLocalMusic)
        }
    }

    Scaffold(
        containerColor = backgroundColor,
        bottomBar = {
            PlayerBar(
                currentTrack = uiState.currentTrack,
                playerState = uiState.playerState,
                onTogglePlay = { viewModel.onEvent(MusicEvent.TogglePlay) },
                onNext = { viewModel.onEvent(MusicEvent.Next) },
                onOpenDetail = onOpenDetail,
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = animatedVisibilityScope
            )
        },
        contentWindowInsets = WindowInsets.statusBars
    ) { innerPadding ->
        // 使用 LazyColumn 让整个页面可滚动
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 顶部间隔
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            // 头部区域
            item {
                HeaderSection(onSearchClick = onOpenSearch)
            }

            // Hero 卡片
            item {
                HeroCard(
                    cardColor = cardColor,
                    onOpenDetail = onOpenDetail,
                )
            }

            // 功能操作区域 (如扫描音乐)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SectionTitle(title = "我的音乐")
                    
                    FilledTonalButton(
                        onClick = { permissionLauncher.launch(storagePermission) },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color(0xFF2A2F3A),
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.GraphicEq,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "扫描本地", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }

            // 歌曲列表
            if (uiState.tracks.isEmpty()) {
                item {
                    EmptyState()
                }
            } else {
                items(uiState.tracks, key = { it.id }) { track ->
                    TrackItem(
                        track = track,
                        isSelected = track.id == uiState.currentTrack?.id,
                        onClick = {
                            viewModel.onEvent(MusicEvent.SelectTrack(track.id))
                        }
                    )
                }
            }
            
            // 底部留白，防止被播放条遮挡
            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

/**
 * 顶部标题区域。
 */
@Composable
private fun HeaderSection(
    onSearchClick: () -> Unit,
) {
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
                text = "发现音乐",
                color = subtitleColor,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Orbit Music",
                color = titleColor,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp
                )
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            IconButton(
                onClick = onSearchClick,
                modifier = Modifier
                    .background(Color(0xFF2A2F3A), CircleShape)
                    .size(44.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "搜索",
                    tint = Color.White
                )
            }
            IconButton(
                onClick = { /* TODO: 通知点击 */ },
                modifier = Modifier
                    .background(Color(0xFF2A2F3A), CircleShape)
                    .size(44.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.NotificationsNone,
                    contentDescription = "通知",
                    tint = Color.White
                )
            }
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
    // 更加高级的蓝绿渐变
    val gradientStart = Color(0xFF4F46E5) // Indigo
    val gradientEnd = Color(0xFF06B6D4)   // Cyan

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpenDetail),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = MaterialTheme.shapes.extraLarge,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            // 背景渐变
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(gradientStart, gradientEnd),
                            start = androidx.compose.ui.geometry.Offset(0f, 0f),
                            end = androidx.compose.ui.geometry.Offset(1000f, 1000f)
                        )
                    )
            )
            
            // 装饰性圆圈 (可选)
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 20.dp, end = 20.dp)
                    .size(100.dp)
                    .background(Color.White.copy(alpha = 0.1f), CircleShape)
            )
            
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(bottom = 0.dp, start = 0.dp)
                    .size(140.dp)
                    .background(Color.Black.copy(alpha = 0.05f), CircleShape)
            )

            // 内容
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.2f), MaterialTheme.shapes.small)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "每日精选",
                            color = Color.White,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }

                Column {
                    Text(
                        text = "专注时刻",
                        color = Color.White,
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "提升效率的精选歌单",
                        color = Color.White.copy(alpha = 0.9f),
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
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
    )
}

/**
 * 空状态显示。
 */
@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Audiotrack,
            contentDescription = null,
            tint = Color(0xFF2A2F3A),
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "暂无音乐，点击扫描添加",
            color = Color(0xFF52525B),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}