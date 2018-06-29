package com.technocom.imagetopdf.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.technocom.imagetopdf.R
import kotlinx.android.synthetic.main.activity_splash_screen.*


class SplashScreen : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
    }

    override fun onStart() {
        super.onStart()
        val path = "android.resource://" + packageName + "/" + R.raw.splash
        splash_video.setVideoURI(Uri.parse(path))
        splash_video.setOnPreparedListener { mp -> splash_video.start();mp.setVolume(0f, 0f) }
        splash_video.setOnCompletionListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    override fun onStop() {
        splash_video.stopPlayback()
        super.onStop()
    }
}
