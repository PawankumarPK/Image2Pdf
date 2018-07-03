package com.technocom.imagetopdf.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.technocom.adapter.LogsAdapter
import com.technocom.imagetopdf.R

/*
Created by Pawan kumar
 */


class History : BaseFragment() {

    var logsAdapter : LogsAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initRecyclerView(){

        mRecyclerView.ad
    }


}