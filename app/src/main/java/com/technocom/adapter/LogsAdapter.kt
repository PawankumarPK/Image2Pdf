package com.technocom.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.technocom.dtos.Dto
import com.technocom.imagetopdf.R
import kotlinx.android.synthetic.main.history.view.*
import kotlinx.android.synthetic.main.historyviewholder.view.*

/*
Created by Pawan kumar
 */


class LogsAdapter(val context: Context,val itemList: MutableList<Dto>) : RecyclerView.Adapter<LogsAdapter.LogViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):LogViewHolder {

        return LogViewHolder(LayoutInflater.from(context).inflate(R.layout.historyviewholder,parent,false))

    }
    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        holder.onBind(position)

    }


    override fun getItemCount(): Int {
        return itemList.size
    }


    inner class LogViewHolder(val view : View) : RecyclerView.ViewHolder(view)
    {

         fun onBind(position: Int){
             itemView.mName.text = itemList[position].name
             itemView.mTime.text = itemList[position].time.toString()
             itemView.mItems.text = itemList[position].itemOfImages.toString()
         }
    }
}