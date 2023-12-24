package org.shop.musicplayer

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.shop.musicplayer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }

        binding.playButton.setOnClickListener {
            mediaPlayerPlay()
        }

        binding.pauseButton.setOnClickListener {
            mediaPlayerPause()
        }

        binding.stopButton.setOnClickListener {
            mediaPlayerStop()
        }
    }

    private fun mediaPlayerPlay() {
        // 서비스 시작 방법 1. 인텐트를 선언한다
        val intent = Intent(this, MediaPlayerService::class.java).apply {
            action = MEDIA_PLAYER_PLAY
        }
        // 서비스 시작 방법 2. 서비스 시작
        startService(intent)
    }

    private fun mediaPlayerPause() {
        val intent = Intent(this, MediaPlayerService::class.java).apply {
            action = MEDIA_PLAYER_PAUSE
        }
        startService(intent)
    }

    private fun mediaPlayerStop() {
        val intent = Intent(this, MediaPlayerService::class.java).apply {
            action = MEDIA_PLAYER_STOP
        }
        startService(intent)
    }

    override fun onDestroy() {
        stopService(Intent(this, MediaPlayerService::class.java))
        super.onDestroy()
    }
}