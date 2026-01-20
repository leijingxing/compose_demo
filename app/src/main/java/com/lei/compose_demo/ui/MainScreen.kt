package com.lei.compose_demo.ui

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.lei.compose_demo.state.MusicEvent
import com.lei.compose_demo.state.MusicViewModel

/**
 * 主页面容器，包含底部导航栏和播放条。
 *
 * @param musicViewModel 音乐 ViewModel。
 * @param onOpenDetail 打开播放详情页。
 * @param onOpenSearch 打开搜索页。
 * @param sharedTransitionScope 共享元素转场作用域。
 * @param animatedVisibilityScope 动画可见性作用域。
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MainScreen(
    musicViewModel: MusicViewModel,
    onOpenDetail: () -> Unit,
    onOpenSearch: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    // 当前选中的 Tab 索引 (0: 首页, 1: 个人)
    var selectedTab by remember { mutableIntStateOf(0) }
    
    // UI 状态
    val uiState = musicViewModel.uiState
    
    // 背景色
    val backgroundColor = Color(0xFF0F1115)
    // 底部导航栏背景色
    val navBarColor = Color(0xFF171A21)
    // 选中颜色
    val selectedColor = Color.White
    // 未选中颜色
    val unselectedColor = Color(0xFFA1A1AA)

    Scaffold(
        containerColor = backgroundColor,
        contentWindowInsets = WindowInsets.statusBars,
        bottomBar = {
            Column {
                // 播放条 (如果当前有歌曲)
                if (uiState.currentTrack != null) {
                    PlayerBar(
                        currentTrack = uiState.currentTrack,
                        playerState = uiState.playerState,
                        onTogglePlay = { musicViewModel.onEvent(MusicEvent.TogglePlay) },
                        onNext = { musicViewModel.onEvent(MusicEvent.Next) },
                        onOpenDetail = onOpenDetail,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                }
                
                // 底部导航栏
                NavigationBar(
                    containerColor = navBarColor,
                    contentColor = unselectedColor,
                    tonalElevation = 0.dp
                ) {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        icon = { Icon(Icons.Default.Home, contentDescription = "首页") },
                        label = { Text("首页") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = selectedColor,
                            selectedTextColor = selectedColor,
                            unselectedIconColor = unselectedColor,
                            unselectedTextColor = unselectedColor,
                            indicatorColor = Color.Transparent // 移除选中背景指示器
                        )
                    )
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        icon = { Icon(Icons.Default.Person, contentDescription = "我的") },
                        label = { Text("我的") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = selectedColor,
                            selectedTextColor = selectedColor,
                            unselectedIconColor = unselectedColor,
                            unselectedTextColor = unselectedColor,
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        // 内容区域
        Box(modifier = Modifier.fillMaxSize()) {
            when (selectedTab) {
                0 -> {
                    MusicScreen(
                        viewModel = musicViewModel,
                        onOpenDetail = onOpenDetail,
                        onOpenSearch = onOpenSearch,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope,
                        contentPadding = innerPadding
                    )
                }
                1 -> {
                    // 个人中心
                    // 这里的 padding 需要传递或者应用，防止被 bottomBar 遮挡
                    // ProfileScreen 内部使用了 Scaffold，这可能导致嵌套滚动问题或 padding 重复。
                    // 理想情况是 ProfileScreen 也改为只接受 contentPadding 的普通 Composable。
                    // 暂时我们可以用 Box 包裹并应用 padding，或者修改 ProfileScreen。
                    // 为了统一，我们把 innerPadding 传递给子 View 处理，或者在这里处理底部 padding。
                    // 注意：innerPadding 包含了 bottomBar 的高度。
                    
                    // 由于 ProfileScreen 目前有自己的 Scaffold，我们暂时用 Box 包裹，
                    // 但更好的做法是重构 ProfileScreen 去掉 Scaffold。
                    // 鉴于我刚刚创建了 ProfileScreen 带有 Scaffold，
                    // 我将在 Box 中调整 modifier padding bottom = innerPadding.calculateBottomPadding()
                    // 但是 ProfileScreen 的 Scaffold 会再次处理 WindowInsets。
                    
                    // 简单处理：ProfileScreen 是全屏的，我们只应用底部的 padding。
                    Box(modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())) {
                        ProfileScreen()
                    }
                }
            }
        }
    }
}
