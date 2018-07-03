package com.technocom.imagetopdf.adapter

import android.app.Dialog
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.technocom.camera.SquareTransformation
import com.technocom.imagetopdf.R
import com.technocom.imagetopdf.fragments.PdfView
import kotlinx.android.synthetic.main.view_holder.view.*
import com.bumptech.glide.load.resource.bitmap.RoundedCorners




/*
Created by Pawan kumar
 */

class RecyclerAdapter(private val context: Context, private val list: MutableList<String>, private val itemClick: PdfView) : RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        return RecyclerViewHolder(LayoutInflater.from(context).inflate(R.layout.view_holder, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(viewholder: RecyclerViewHolder, position: Int) {
        viewholder.onBind(position)
    }

    inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dialog = Dialog(context)
        var delete: TextView
        var crop: TextView

        init {
            dialog.setContentView(R.layout.custom_dialog)
            delete = dialog.findViewById(R.id.delete) as TextView
            crop = dialog.findViewById(R.id.crop) as TextView
        }

        fun onBind(position: Int) {
//            Picasso.get()
//                    .load("file://" + list[position])
//                    .resize(360,640)
//                    .centerCrop()
//                    .transform(CropSquareTransformation())
//                    .into(itemView.spacecraftImg)

            val requestOptions = RequestOptions()
            requestOptions.override(360,640)
            requestOptions.centerCrop()
            requestOptions.transform(SquareTransformation())
            Glide.with(context).load("file://" + list[position]).apply(requestOptions).into(itemView.spacecraftImg)
            itemView.spacecraftImg.setOnClickListener {
                dialog.show()
            }

            delete.setOnClickListener {
                itemClick.deleteImages(position)
                notifyDataSetChanged()
                dialog.dismiss()
            }
            crop.setOnClickListener {
                itemClick.cropImage(position)
                dialog.dismiss()

            }
        }

    }

    interface ItemClick {
        fun deleteImages(position: Int)
        fun cropImage(position: Int)
    }
}