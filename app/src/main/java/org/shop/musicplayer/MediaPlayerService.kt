package org.shop.musicplayer

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder

class MediaPlayerService : Service() {
    private var mediaPlayer: MediaPlayer? = null

    override fun onBind(intent: Intent): IBinder? {
        // Bind 서비스가 아닌 포그라운드 서비스이기에 onBind는 null을 반환한다
        return null
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
}