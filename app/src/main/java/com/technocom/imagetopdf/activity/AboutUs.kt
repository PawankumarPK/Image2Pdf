package com.technocom.imagetopdf.activity

import android.os.Bundle
import com.technocom.imagetopdf.R
import kotlinx.android.synthetic.main.activity_about_us.*


class AboutUs : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)
      /*  version.text = "v.${BuildConfig.VERSION_NAME}"
        close.setOnClickListener { finish() }*/
    }
}
