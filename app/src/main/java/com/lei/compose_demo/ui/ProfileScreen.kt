package com.lei.compose_demo.ui

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 个人中心页面。
 */
@Composable
fun ProfileScreen() {
    // 背景色
    val backgroundColor = Color(0xFF0F1115)
    // 滚动状态
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = backgroundColor,
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
        ) {
            // 顶部用户信息区域
            UserProfileHeader()

            Spacer(modifier = Modifier.height(24.dp))

            // 统计数据区域
            UserStatsSection()

            Spacer(modifier = Modifier.height(24.dp))

            // 菜单列表
            MenuSection()
            
            Spacer(modifier = Modifier.height(100.dp)) // 底部留白给播放条
        }
    }
}

/**
 * 用户信息头部。
 */
@Composable
private fun UserProfileHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp, bottom = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 头像
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color(0xFF2A2F3A)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                tint = Color(0xFFA1A1AA),
                modifier = Modifier.size(64.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        // 用户名
        Text(
            text = "Music Lover",
            color = Color.White,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(4.dp))
        // VIP 标识 (模拟)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(Color(0xFFFACC15).copy(alpha = 0.2f), CircleShape)
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Text(
                text = "VIP 会员",
                color = Color(0xFFFACC15),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

/**
 * 用户统计数据区域。
 */
@Composable
private fun UserStatsSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatItem(count = "128", label = "收藏")
        StatItem(count = "45", label = "关注")
        StatItem(count = "12", label = "歌单")
        StatItem(count = "2k", label = "粉丝")
    }
}

/**
 * 单个统计项。
 */
@Composable
private fun StatItem(count: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count,
            color = Color.White,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text = label,
            color = Color(0xFFA1A1AA),
            style = MaterialTheme.typography.bodySmall
        )
    }
}

/**
 * 菜单列表区域。
 */
@Composable
private fun MenuSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(Color(0xFF171A21), MaterialTheme.shapes.large)
            .padding(vertical = 8.dp)
    ) {
        MenuItem(
            icon = Icons.Default.Favorite,
            title = "我的喜欢",
            subtitle = "128 首歌曲"
        )
        DividerItem()
        MenuItem(
            icon = Icons.Default.History,
            title = "最近播放",
            subtitle = "查看播放历史"
        )
        DividerItem()
        MenuItem(
            icon = Icons.Default.Settings,
            title = "设置",
            subtitle = "应用偏好设置"
        )
        DividerItem()
        MenuItem(
            icon = Icons.Outlined.Info,
            title = "关于",
            subtitle = "版本 1.0.0"
        )
    }
}

/**
 * 分割线。
 */
@Composable
private fun DividerItem() {
    HorizontalDivider(
        modifier = Modifier.padding(start = 56.dp, end = 16.dp),
        thickness = 0.5.dp,
        color = Color(0xFF2A2F3A)
    )
}

/**
 * 菜单项。
 */
@Composable
private fun MenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFFA1A1AA),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    color = Color(0xFF52525B),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = Color(0xFF52525B),
            modifier = Modifier.size(14.dp)
        )
    }
}
