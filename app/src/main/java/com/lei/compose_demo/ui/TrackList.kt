package com.lei.compose_demo.ui

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lei.compose_demo.data.Track

/**
 * 歌曲列表。
 *
 * @param tracks 歌曲列表。
 * @param currentTrack 当前选中歌曲。
 * @param onTrackClick 点击事件。
 */
@Composable
fun TrackList(
    tracks: List<Track>,
    currentTrack: Track?,
    onTrackClick: (Track) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(tracks, key = { track ->
            // 列表项歌曲，用于生成稳定 key。
            track.id
        }) { track ->
            // 当前列表项歌曲。
            TrackItem(
                track = track,
                isSelected = track.id == currentTrack?.id,
                onClick = { onTrackClick(track) }
            )
        }
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

/**
 * 单个歌曲项。
 *
 * @param track 歌曲数据。
 * @param isSelected 是否选中。
 * @param onClick 点击事件。
 */
@Composable
fun TrackItem(
    track: Track,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    // 选中背景颜色。
    val selectedColor = Color(0xFF1F2937)
    // 未选中背景颜色。
    val normalColor = Color(0xFF151922)
    // 封面占位背景颜色。
    val coverColor = Color(0xFF2A2F3A)
    // 文字主色。
    val titleColor = Color.White
    // 次级文字颜色。
    val subtitleColor = Color(0xFFA1A1AA)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        colors = CardDefaults.cardColors(containerColor = if (isSelected) selectedColor else normalColor),
        shape = MaterialTheme.shapes.medium,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(coverColor)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = track.title,
                    color = titleColor,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Text(
                    text = track.artist,
                    color = subtitleColor,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Text(
                text = track.durationText,
                color = subtitleColor,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
