package com.technocom.imagetopdf.utils

import android.content.Context
import android.content.SharedPreferences
import com.technocom.imagetopdf.R

/**
 * Created by Technocom on 3/5/2018.
 */

class Preferences {

    private lateinit var sharedPreferences: SharedPreferences
    private var instance: Preferences? = null


    var isPremium: Boolean
        get() = sharedPreferences.getBoolean(ISPREMIUM, false)
        set(value) = sharedPreferences.edit().putBoolean(ISPREMIUM, value).apply()

    var isAutoFilename: Boolean
        get() = sharedPreferences.getBoolean(ISNAME, true)
        set(value) = sharedPreferences.edit().putBoolean(ISNAME, value).apply()


    var isFitPage: Boolean
        get() = sharedPreferences.getBoolean(ISFITSIZE, true)
        set(value) = sharedPreferences.edit().putBoolean(ISFITSIZE, value).apply()

    var isCompression: Int
        get() = sharedPreferences.getInt(ISCOMPRESSION, 100)
        set(value) = sharedPreferences.edit().putInt(ISCOMPRESSION,value).apply()


    private fun initialize(context: Context): Preferences {
        this.sharedPreferences = context.getSharedPreferences(context.resources.getString(R.string.app_name), Context.MODE_PRIVATE)
        return this
    }

    fun getInstance(context: Context): Preferences {
        if (instance == null)
            instance = initialize(context)
        return instance!!
    }

    companion object {
        private const val ISPREMIUM = "ISPREMIUM"
        private const val ISNAME = "ISNAME"
        private const val ISFITSIZE = "ISFITSIZE"
        private const val ISCOMPRESSION = "ISCOMPRESSION"
    }


}
