package com.lei.compose_demo.ui

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.lei.compose_demo.state.MusicEvent
import com.lei.compose_demo.state.MusicViewModel
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.lei.compose_demo.service.MusicService

/**
 * 应用导航路由定义。
 */
object AppRoute {
    // 主页面（包含底部导航）。
    const val MAIN = "main"

    // 播放详情页路由。
    const val PLAYER = "player"

    // 搜索页面路由。
    const val SEARCH = "search"
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
    
    // 注册广播接收器监听通知栏操作
    val context = LocalContext.current
    DisposableEffect(context) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    MusicService.BROADCAST_ACTION_PLAY_PAUSE -> musicViewModel.onEvent(MusicEvent.TogglePlay)
                    MusicService.BROADCAST_ACTION_NEXT -> musicViewModel.onEvent(MusicEvent.Next)
                    MusicService.BROADCAST_ACTION_PREVIOUS -> musicViewModel.onEvent(MusicEvent.Previous)
                }
            }
        }
        val filter = IntentFilter().apply {
            addAction(MusicService.BROADCAST_ACTION_PLAY_PAUSE)
            addAction(MusicService.BROADCAST_ACTION_NEXT)
            addAction(MusicService.BROADCAST_ACTION_PREVIOUS)
        }
        // 由于通知栏点击事件实际上是由系统（NotificationManager）代理发送的，
        // 在某些 Android 版本或 ROM 上，可能被视为外部广播。
        // 为了确保能接收到点击事件，改为 RECEIVER_EXPORTED。
        // 同时我们在 Service 中通过 setPackage 限制了包名，已经有了一定的安全性。
        ContextCompat.registerReceiver(context, receiver, filter, ContextCompat.RECEIVER_EXPORTED)

        onDispose {
            context.unregisterReceiver(receiver)
        }
    }

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
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ComposeDemoNavGraph(
    navController: NavHostController,
    musicViewModel: MusicViewModel,
) {
    SharedTransitionLayout {
        NavHost(
            navController = navController,
            startDestination = AppRoute.MAIN,
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() },
            popEnterTransition = { fadeIn() },
            popExitTransition = { fadeOut() }
        ) {
            composable(AppRoute.MAIN) {
                MainScreen(
                    musicViewModel = musicViewModel,
                    onOpenDetail = { navController.navigate(AppRoute.PLAYER) },
                    onOpenSearch = { navController.navigate(AppRoute.SEARCH) },
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this@composable
                )
            }
            composable(AppRoute.PLAYER) {
                PlayerDetailScreen(
                    currentTrack = musicViewModel.uiState.currentTrack,
                    playerState = musicViewModel.uiState.playerState,
                    onBack = { navController.popBackStack() },
                    onTogglePlay = { musicViewModel.onEvent(MusicEvent.TogglePlay) },
                    onSeekTo = { progress ->
                        // 拖拽得到的进度值。
                        musicViewModel.onEvent(MusicEvent.SeekTo(progress))
                    },
                    onPrevious = { musicViewModel.onEvent(MusicEvent.Previous) },
                    onNext = { musicViewModel.onEvent(MusicEvent.Next) },
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this@composable
                )
            }
            composable(AppRoute.SEARCH) {
                SearchScreen(
                    viewModel = musicViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
