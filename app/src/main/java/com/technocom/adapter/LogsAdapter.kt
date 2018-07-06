package com.technocom.adapter

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.design.widget.Snackbar
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.technocom.dtos.Dto
import com.technocom.imagetopdf.R
import kotlinx.android.synthetic.main.historyviewholder.view.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/*
Created by Pawan kumar
 */


class LogsAdapter(val context: Context, val itemList: MutableList<Dto>) : RecyclerView.Adapter<LogsAdapter.LogViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {

        return LogViewHolder(LayoutInflater.from(context).inflate(R.layout.historyviewholder, parent, false))

    }

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        holder.onBind(position)

    }


    override fun getItemCount(): Int {
        return itemList.size
    }


    inner class LogViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        private val formattedDate = SimpleDateFormat("dd/MMM/yy  hh:mm:ss aaa", Locale.getDefault())
        fun onBind(position: Int) {
            itemView.mName.text = itemList[position].name
            itemView.mTime.text = formattedDate.format(itemList[position].time)
            itemView.mItems.text = itemList[position].itemOfImages.toString()
            itemView.mOpenPdf.setOnClickListener { openPdf(File(itemList[position].path)) }


        }
        private fun openPdf(file: File) {
            val target = Intent(Intent.ACTION_VIEW)
            target.setDataAndType(Uri.fromFile(file), "application/pdf")
            target.flags = Intent.FLAG_ACTIVITY_NO_HISTORY

            val intent = Intent.createChooser(target, "Open File")
            try {
                context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Snackbar.make(view!!, "No Application Found to open Pdf", 3).show()
                // progressView.visibility = View.GONE
            }
        }
    }


}