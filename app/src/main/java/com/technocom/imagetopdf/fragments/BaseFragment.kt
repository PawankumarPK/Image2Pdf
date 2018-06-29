package com.technocom.imagetopdf.fragments

import android.support.v4.app.Fragment
import android.widget.Toast

/*
Created by Pawan kumar
 */


abstract class BaseFragment : Fragment() {
    fun toastLong(value: Any) {
        Toast.makeText(activity, value.toString(), Toast.LENGTH_LONG).show()
    }
}