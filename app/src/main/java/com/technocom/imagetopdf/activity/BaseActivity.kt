package com.technocom.imagetopdf.activity

import android.support.v7.app.AppCompatActivity
import android.widget.Toast


abstract class BaseActivity : AppCompatActivity() {
    fun toastLong(value: Any) {
        Toast.makeText(this, value.toString(), Toast.LENGTH_LONG).show()
    }
}