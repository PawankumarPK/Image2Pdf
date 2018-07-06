package com.technocom.imagetopdf.activity

import android.app.Application
import android.os.Bundle
import android.os.PersistableBundle
import android.preference.Preference
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.technocom.ApplicationClass
import com.technocom.database.AppDatabase
import com.technocom.imagetopdf.utils.Preferences


abstract class BaseActivity : AppCompatActivity() {

    lateinit var pref: Preferences
    lateinit var db: AppDatabase
    lateinit var mainActivity: MainActivity


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = ApplicationClass.database
        pref = Preferences().getInstance(this)


    }

    fun toastLong(value: Any) {
        Toast.makeText(this, value.toString(), Toast.LENGTH_LONG).show()
    }
}