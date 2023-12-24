package org.shop.musicplayer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.Icon
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi

class MediaPlayerService : Service() {
    private var mediaPlayer: MediaPlayer? = null
    private val receiver = LowBatteryReceiver()

    override fun onBind(intent: Intent): IBinder? {
        // Bind 서비스가 아닌 포그라운드 서비스이기에 onBind는 null을 반환한다
        return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()
        initReceiver()

        // 아이콘 생성
        val playIcon = Icon.createWithResource(baseContext, R.drawable.baseline_play_arrow_24)
        val pauseIcon = Icon.createWithResource(baseContext, R.drawable.baseline_pause_24)
        val stopIcon = Icon.createWithResource(baseContext, R.drawable.baseline_stop_24)

        val mainPendingIntent = PendingIntent.getActivity(
            baseContext,
            0,
            Intent(
                baseContext,
                MainActivity::class.java
            ).apply { flags = Intent.FLAG_ACTIVITY_SINGLE_TOP },
            PendingIntent.FLAG_IMMUTABLE
        )

        val pausePendingIntent = PendingIntent.getService(
            baseContext,
            0,
            Intent(baseContext, MediaPlayerService::class.java).apply {
                action = MEDIA_PLAYER_PAUSE
            },
            PendingIntent.FLAG_IMMUTABLE
        )

        val playPendingIntent = PendingIntent.getService(
            baseContext,
            0,
            Intent(baseContext, MediaPlayerService::class.java).apply {
                action = MEDIA_PLAYER_PLAY
            },
            PendingIntent.FLAG_IMMUTABLE
        )

        val stopPendingIntent = PendingIntent.getService(
            baseContext,
            0,
            Intent(baseContext, MediaPlayerService::class.java).apply {
                action = MEDIA_PLAYER_STOP
            },
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = Notification.Builder(baseContext, CHANNEL_ID)
            .setStyle(
                Notification.MediaStyle()
                    .setShowActionsInCompactView(0, 1, 2)
            )
            .setVisibility(Notification.VISIBILITY_PUBLIC)
            .setSmallIcon(R.drawable.baseline_star_24)
            .addAction(
                Notification.Action.Builder(
                    pauseIcon, "Pause", pausePendingIntent
                ).build()
            )
            .addAction(
                Notification.Action.Builder(
                    playIcon, "Play", playPendingIntent
                ).build()
            )
            .addAction(
                Notification.Action.Builder(
                    stopIcon, "Stop", stopPendingIntent
                ).build()
            )
            .setContentIntent(mainPendingIntent)
            .setContentTitle("음악재생")
            .setContentText("음원이 재생 중입니다...")
            .build()

        startForeground(100, notification)
    }

    private fun initReceiver() {
        // 이 때 intent-filter를 등록할 수 있다. Manifest에서도 가능
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_BATTERY_LOW)
        }
        registerReceiver(receiver, filter)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channel =
            NotificationChannel(CHANNEL_ID, "MEDIA_PLAYER", NotificationManager.IMPORTANCE_DEFAULT)

        val notificationManager = baseContext.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    // 중요 CallBack: 서비스가 실행되면 onStartCommand가 실행된다. Service가 onCreate되고 바로 onStartCommand가 실행된다
    // intent 값이 들어오고 그에 따른 값을 리턴
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        /** @see
         *  intent에서 어떠한 action을 취했는지에 따라 시작, 일시정지, 정지 3가지 구현
         *  단순히 시작했던 것 외에도 기존 MainActivity에서 구현했던 것들을 모두 이곳으로 이동
         *  제어 자체도 Service에서 실행
         */
        when (intent?.action) {
            MEDIA_PLAYER_PLAY -> {
                if (mediaPlayer == null) {
                    mediaPlayer = MediaPlayer.create(baseContext, R.raw.cheer).apply {
                        isLooping = true
                    }
                }
                mediaPlayer?.start()
            }

            MEDIA_PLAYER_PAUSE -> {
                mediaPlayer?.pause()
            }

            MEDIA_PLAYER_STOP -> {
                mediaPlayer?.stop()
                mediaPlayer?.release()
                mediaPlayer = null
                stopSelf()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        mediaPlayer?.apply {
            start()
            release()
        }
        mediaPlayer = null
        unregisterReceiver(receiver)
        super.onDestroy()
    }
}