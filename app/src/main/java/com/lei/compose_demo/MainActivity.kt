package com.lei.compose_demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.lei.compose_demo.ui.ComposeDemoNav
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
@Composable
fun Compose_demoApp() {
    ComposeDemoNav()
}

/**
 * 主界面预览。
 */
@Preview(showBackground = true)
@Composable
fun ComposeDemoPreview() {
    Compose_demoTheme {
        Compose_demoApp()
    }
}
