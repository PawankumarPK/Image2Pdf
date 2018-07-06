package com.technocom.imagetopdf.fragments

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.widget.Toast
import com.technocom.ApplicationClass
import com.technocom.database.AppDatabase
import com.technocom.imagetopdf.activity.MainActivity
import com.technocom.imagetopdf.utils.Preferences

/*
Created by Pawan kumar
 */


abstract class BaseFragment : Fragment() {

    lateinit var pref: Preferences
    lateinit var db: AppDatabase
    lateinit var mainActivity: MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity = activity as MainActivity

        db = ApplicationClass.database

        pref = Preferences().getInstance(activity!!)
    }

    fun toastLong(value: Any) {
        Toast.makeText(activity, value.toString(), Toast.LENGTH_LONG).show()
    }

    fun snackbar(value: Any) {
        Snackbar.make(view!!, value as String, 5000).show()
    }
}