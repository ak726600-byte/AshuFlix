package com.ashu.flix

import android.app.Activity
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.MediaController
import android.widget.Toast
import android.widget.VideoView

/** Simple fullscreen video player (built-in VideoView, no extra libraries). */
class PlayerActivity : Activity() {
    private lateinit var video: VideoView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val frame = FrameLayout(this)
        frame.setBackgroundColor(Color.BLACK)
        video = VideoView(this)
        frame.addView(video, FrameLayout.LayoutParams(-1, -1, Gravity.CENTER))
        setContentView(frame)
        hideSystemUi()

        val controller = MediaController(this)
        controller.setAnchorView(video)
        video.setMediaController(controller)

        val uri = intent.getStringExtra("uri")
        if (uri == null) {
            finish()
            return
        }
        video.setVideoURI(Uri.parse(uri))
        video.setOnPreparedListener { it.start() }
        video.setOnErrorListener { _, _, _ ->
            Toast.makeText(this, "Ye video chal nahi paya", Toast.LENGTH_LONG).show()
            finish()
            true
        }
    }

    @Suppress("DEPRECATION")
    private fun hideSystemUi() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUi()
    }

    override fun onPause() {
        video.pause()
        super.onPause()
    }
}
