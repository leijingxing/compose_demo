package com.lei.compose_demo.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.lei.compose_demo.state.MusicEvent
import com.lei.compose_demo.state.MusicViewModel

/**
 * 应用导航路由定义。
 */
object AppRoute {
    // 音乐主页路由。
    const val MUSIC = "music"
    // 播放详情页路由。
    const val PLAYER = "player"
}

/**
 * 应用导航入口。
 */
@Composable
fun ComposeDemoNav() {
    // 导航控制器。
    val navController = rememberNavController()
    // 共享的音乐页面 ViewModel。
    val musicViewModel: MusicViewModel = viewModel()

    ComposeDemoNavGraph(
        navController = navController,
        musicViewModel = musicViewModel,
    )
}

/**
 * 应用导航图。
 *
 * @param navController 导航控制器。
 * @param musicViewModel 共享的音乐页面 ViewModel。
 */
@Composable
fun ComposeDemoNavGraph(
    navController: NavHostController,
    musicViewModel: MusicViewModel,
) {
    NavHost(
        navController = navController,
        startDestination = AppRoute.MUSIC
    ) {
        composable(AppRoute.MUSIC) {
            MusicScreen(
                viewModel = musicViewModel,
                onOpenDetail = { navController.navigate(AppRoute.PLAYER) },
            )
        }
        composable(AppRoute.PLAYER) {
            PlayerDetailScreen(
                currentTrack = musicViewModel.uiState.currentTrack,
                playerState = musicViewModel.uiState.playerState,
                onBack = { navController.popBackStack() },
                onTogglePlay = { musicViewModel.onEvent(MusicEvent.TogglePlay) },
                onPrevious = { musicViewModel.onEvent(MusicEvent.Previous) },
                onNext = { musicViewModel.onEvent(MusicEvent.Next) },
            )
        }
    }
}
