package org.shop.musicplayer

import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.shop.musicplayer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var mediaPlayer: MediaPlayer? = null

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
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.cheer).apply {
                isLooping = true
            }
        }
        mediaPlayer?.start()
    }

    private fun mediaPlayerPause() {
        mediaPlayer?.pause()
    }

    private fun mediaPlayerStop() {
        mediaPlayer?.stop()

        // mediaPlayer 더이상 사용하지 않을 때 메모리 해제를 위해 release 후 null 로 초기화
        mediaPlayer?.release()
        mediaPlayer = null
    }
}