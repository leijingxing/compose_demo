package com.lei.compose_demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import com.lei.compose_demo.ui.theme.Compose_demoTheme

/**
 * 应用主界面入口 Activity。
 */
class MainActivity : ComponentActivity() {
    /**
     * Activity 创建回调。
     *
     * @param savedInstanceState 用于恢复状态的 Bundle。
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Compose_demoTheme {
                Compose_demoApp()
            }
        }
    }
}

/**
 * 应用主界面内容入口。
 */
@PreviewScreenSizes
@Composable
fun Compose_demoApp() {
    // 当前选中的导航目的地。
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            // 遍历所有导航目的地。
            AppDestinations.entries.forEach { destination ->
                item(
                    icon = {
                        Icon(
                            destination.icon,
                            contentDescription = destination.label
                        )
                    },
                    label = { Text(destination.label) },
                    selected = destination == currentDestination,
                    onClick = { currentDestination = destination }
                )
            }
        }
    ) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            // Scaffold 提供的内容内边距。
            Greeting(
                name = "Android",
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

/**
 * 应用导航目的地枚举。
 *
 * @param label 导航标签文本。
 * @param icon 导航图标资源。
 */
enum class AppDestinations(
    // 导航标签文本。
    val label: String,
    // 导航图标资源。
    val icon: ImageVector,
) {
    // 首页目的地。
    HOME("Home", Icons.Default.Home),
    // 收藏目的地。
    FAVORITES("Favorites", Icons.Default.Favorite),
    // 个人资料目的地。
    PROFILE("Profile", Icons.Default.AccountBox),
}

/**
 * 问候语文本组件。
 *
 * @param name 显示在问候语中的名称。
 * @param modifier 传入的界面修饰符。
 */
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

/**
 * 预览问候语组件。
 */
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Compose_demoTheme {
        Greeting("Android")
    }
}
