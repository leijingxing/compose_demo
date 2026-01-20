package com.lei.compose_demo.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import com.lei.compose_demo.MainActivity
import com.lei.compose_demo.R
import com.lei.compose_demo.data.Track

class MusicService : Service() {

    private lateinit var mediaSession: MediaSessionCompat

    companion object {
        const val ACTION_UPDATE_STATE = "action_update_state"
        const val ACTION_STOP = "action_stop"
        
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_ARTIST = "extra_artist"
        const val EXTRA_IS_PLAYING = "extra_is_playing"
        
        // 发送给 ViewModel 的广播 Action
        const val BROADCAST_ACTION_PLAY_PAUSE = "com.lei.compose_demo.PLAY_PAUSE"
        const val BROADCAST_ACTION_NEXT = "com.lei.compose_demo.NEXT"
        const val BROADCAST_ACTION_PREVIOUS = "com.lei.compose_demo.PREVIOUS"
    }

    override fun onCreate() {
        super.onCreate()
        
        // 初始化 MediaSession
        mediaSession = MediaSessionCompat(this, "MusicService").apply {
            // 设置回调，处理耳机按键等系统媒体事件
            setCallback(object : MediaSessionCompat.Callback() {
                override fun onPlay() { sendBroadcast(Intent(BROADCAST_ACTION_PLAY_PAUSE)) }
                override fun onPause() { sendBroadcast(Intent(BROADCAST_ACTION_PLAY_PAUSE)) }
                override fun onSkipToNext() { sendBroadcast(Intent(BROADCAST_ACTION_NEXT)) }
                override fun onSkipToPrevious() { sendBroadcast(Intent(BROADCAST_ACTION_PREVIOUS)) }
            })
            isActive = true
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_UPDATE_STATE -> {
                val title = intent.getStringExtra(EXTRA_TITLE) ?: "Unknown"
                val artist = intent.getStringExtra(EXTRA_ARTIST) ?: "Unknown"
                val isPlaying = intent.getBooleanExtra(EXTRA_IS_PLAYING, false)
                updateNotification(title, artist, isPlaying)
            }
            ACTION_STOP -> {
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }

    private fun updateNotification(title: String, artist: String, isPlaying: Boolean) {
        // 更新 MediaSession 元数据
        mediaSession.setMetadata(
            MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
                .build()
        )

        // 更新 MediaSession 播放状态
        mediaSession.setPlaybackState(
            PlaybackStateCompat.Builder()
                .setState(
                    if (isPlaying) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED,
                    PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN,
                    1f
                )
                .setActions(
                    PlaybackStateCompat.ACTION_PLAY or
                    PlaybackStateCompat.ACTION_PAUSE or
                    PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                    PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                )
                .build()
        )

        // 点击通知跳转回 App
        val contentIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val contentPendingIntent = PendingIntent.getActivity(
            this, 0, contentIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // 构建通知
        val notification = NotificationCompat.Builder(this, "music_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground) // 暂时使用默认图标，建议替换为音乐图标
            .setContentTitle(title)
            .setContentText(artist)
            .setSubText("Compose Demo")
            .setContentIntent(contentPendingIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession.sessionToken)
                    .setShowActionsInCompactView(0, 1, 2) // 显示前3个按钮
            )
            .addAction(createAction(android.R.drawable.ic_media_previous, "Previous", BROADCAST_ACTION_PREVIOUS))
            .addAction(
                if (isPlaying) createAction(android.R.drawable.ic_media_pause, "Pause", BROADCAST_ACTION_PLAY_PAUSE)
                else createAction(android.R.drawable.ic_media_play, "Play", BROADCAST_ACTION_PLAY_PAUSE)
            )
            .addAction(createAction(android.R.drawable.ic_media_next, "Next", BROADCAST_ACTION_NEXT))
            .setOngoing(isPlaying) // 播放时常驻，暂停时可侧滑清除
            .build()

        startForeground(1, notification)
    }

    private fun createAction(iconResId: Int, title: String, action: String): NotificationCompat.Action {
        val intent = Intent(action)
        intent.setPackage(packageName) // 限制只能应用内广播
        val pendingIntent = PendingIntent.getBroadcast(
            this, action.hashCode(), intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        return NotificationCompat.Action(iconResId, title, pendingIntent)
    }

    override fun onDestroy() {
        mediaSession.release()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
