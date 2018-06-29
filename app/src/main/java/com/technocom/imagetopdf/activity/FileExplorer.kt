package com.technocom.imagetopdf.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v7.widget.GridLayoutManager
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import com.technocom.imagetopdf.R
import com.technocom.imagetopdf.adapter.FileExplorerAdapter
import com.technocom.imagetopdf.dto.FileExplorerDTO
import com.technocom.imagetopdf.utils.ItemOffsetDecoration
import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_file_explorer.*
import org.apache.commons.io.FileUtils
import java.io.File
import java.util.*


/*
Created by Pawan kumar
 */


class FileExplorer : BaseActivity(), FileExplorerAdapter.ItemClickListener {
    override fun adapterInterface(boolean: Boolean, position: Int) {
        filesListDto[position].selected = boolean
        filesListResult.add(filesListDto[position].bitmap!!)
        explorerAdapter.notifyItemChanged(position)
        doneVisibility()
    }

    private var percentage = 0
    private val filesList = ArrayList<File>()
    private lateinit var explorerAdapter: FileExplorerAdapter
    private val fileTypes = arrayOf("jpeg", "jpg")
    private val filesListDto = ArrayList<FileExplorerDTO>()
    private val fileExplorerDTO = FileExplorerDTO()
    private val filesListResult = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_explorer)
        done.setOnClickListener { doneClick() }
        initRecyclerView()

        backArrowFileExplorer.setOnClickListener { finish() }
        no_external_card.progress = percentage

        Completable.fromAction { getFiles() }.observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.newThread())
                .subscribe(object : CompletableObserver {
                    override fun onComplete() {
                        notifyAdapter()
                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()

                    }
                })
    }


    private fun initRecyclerView() {
        files_recycler_view!!.layoutManager = GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false)
        files_recycler_view.addItemDecoration(ItemOffsetDecoration(5))
        explorerAdapter = FileExplorerAdapter(this, filesListDto, this)
        files_recycler_view!!.adapter = explorerAdapter

    }

    private fun getFiles() {
        try {
            filesList.addAll(FileUtils.listFiles(Environment.getExternalStorageDirectory(), fileTypes, true))
        } catch (ignored: Exception) {
        }
        for (i in 0 until filesList.size) {
            percentage = (i.toFloat().div(filesList.size.toFloat()).times(100f)).toInt()
            no_external_card.progress = percentage
            fileExplorerDTO.bitmap = filesList[i].path
            fileExplorerDTO.selected = false
            fileExplorerDTO.modificationTime = filesList[i].lastModified()
            filesListDto.add(fileExplorerDTO.copy())
        }
        percentage = 100

    }

    private fun notifyAdapter() {
        if (filesListDto.size > 0) {
            no_external_card!!.visibility = View.GONE
            files_recycler_view!!.visibility = View.VISIBLE
        } else {
            files_recycler_view!!.visibility = View.GONE
            no_external_card!!.visibility = View.VISIBLE

        }
        filesListDto.sortWith(Comparator { o1, o2 -> o2.modificationTime!!.compareTo(o1.modificationTime!!) })
        explorerAdapter.notifyDataSetChanged()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        showData()
    }

    override fun onItemClick(file: File) {
        val resultIntent = Intent()
        resultIntent.data = Uri.fromFile(file)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    private fun doneClick() {
        if (filesListResult.size <= 0) {
            Toast.makeText(this@FileExplorer, "No Item Selected", Toast.LENGTH_SHORT).show()
            return
        }
        showData()
    }

    private fun showData() {
        MainActivity.filesListDataResult.addAll(filesListResult)
        finish()
    }


    private fun doneVisibility() {
        if (filesListResult.size > 0)
            done.visibility = VISIBLE
        else
            done.visibility = GONE
    }

}