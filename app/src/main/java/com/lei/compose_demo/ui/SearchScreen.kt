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
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lei.compose_demo.state.MusicEvent
import com.lei.compose_demo.state.MusicViewModel

/**
 * 搜索页面。
 *
 * @param viewModel 音乐 ViewModel。
 * @param onBack 返回回调。
 */
@Composable
fun SearchScreen(
    viewModel: MusicViewModel = viewModel(),
    onBack: () -> Unit,
) {
    // 搜索关键词状态（本地维护，输入时更新）。
    var query by remember { mutableStateOf("") }
    // 焦点请求器，用于自动聚焦搜索框。
    val focusRequester = remember { FocusRequester() }

    // 监听关键词变化并触发搜索。
    LaunchedEffect(query) {
        viewModel.onEvent(MusicEvent.SearchTracks(query))
    }

    // 自动聚焦。
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    val uiState = viewModel.uiState
    val backgroundColor = Color(0xFF0F1115)
    val searchBarColor = Color(0xFF1F2937)
    val textColor = Color.White
    val placeholderColor = Color(0xFFA1A1AA)

    Scaffold(
        containerColor = backgroundColor,
        modifier = Modifier.systemBarsPadding()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // 搜索栏。
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "返回",
                        tint = textColor
                    )
                }

                TextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(focusRequester),
                    placeholder = {
                        Text(text = "搜索歌曲或歌手...", color = placeholderColor)
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = placeholderColor
                        )
                    },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { query = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "清除",
                                    tint = placeholderColor
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = searchBarColor,
                        unfocusedContainerColor = searchBarColor,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = Color(0xFF3B82F6),
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "搜索结果",
                color = textColor,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 搜索结果列表。
            if (uiState.searchResults.isEmpty() && query.isNotEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "未找到相关歌曲",
                        color = placeholderColor,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.searchResults) { track ->
                        TrackItem(
                            track = track,
                            isSelected = track.id == uiState.currentTrack?.id,
                            onClick = {
                                viewModel.onEvent(MusicEvent.SelectTrack(track.id))
                            }
                        )
                    }
                }
            }
        }
    }
}
