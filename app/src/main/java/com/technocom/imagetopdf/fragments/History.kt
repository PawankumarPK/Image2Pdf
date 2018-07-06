package com.technocom.imagetopdf.fragments

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.technocom.adapter.LogsAdapter
import com.technocom.dtos.Dto
import com.technocom.imagetopdf.R
import com.technocom.imagetopdf.activity.MainActivity
import io.reactivex.MaybeObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_feedback.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.history.*

/*
Created by Pawan kumar
 */


class History : BaseFragment() {

    var logsAdapter : LogsAdapter? = null
    private val contactList: MutableList<Dto> = ArrayList()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.history, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity.mReset.visibility = View.GONE
        mainActivity.mTitle.text = resources.getString(R.string.logs)
        initRecyclerView()
    }

   private fun initRecyclerView(){

       contactList.clear()
       mRecyclerView.layoutManager = LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false)
       logsAdapter = LogsAdapter(activity!!,contactList)
       mRecyclerView.adapter = logsAdapter
       getDbList()
   }

    private fun getDbList(){
        db.logsDao().getAll().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : MaybeObserver<List<Dto>> {
                    override fun onSuccess(t: List<Dto>) {
                        contactList.addAll(t)
                        logsAdapter!!.notifyDataSetChanged()
                    }

                    override fun onComplete() {

                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onError(e: Throwable) {

                    }
                })
    }

    override fun onDestroyView() {
        (activity as MainActivity).mReset.visibility =View.VISIBLE
        super.onDestroyView()
    }

}