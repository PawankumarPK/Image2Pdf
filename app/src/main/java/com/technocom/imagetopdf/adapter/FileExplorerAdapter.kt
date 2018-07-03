package com.technocom.imagetopdf.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.technocom.camera.SquareTransformation
import com.technocom.imagetopdf.R
import com.technocom.imagetopdf.dto.FileExplorerDTO
import kotlinx.android.synthetic.main.file_explorer_adapter.view.*
import java.io.File


/*
 * Created by pawan kumar
 */

class FileExplorerAdapter(private val context: Context, private val filesList: List<FileExplorerDTO>, var intefaceListner: ItemClickListener) : RecyclerView.Adapter<FileExplorerAdapter.RowViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowViewHolder {
        return RowViewHolder(LayoutInflater.from(context).inflate(R.layout.file_explorer_adapter, parent, false))
    }

    override fun onBindViewHolder(holder: RowViewHolder, position: Int) {
        holder.onBindUi(position)
    }

    override fun getItemCount(): Int {
        return filesList.size
    }

    interface ItemClickListener {
        fun onItemClick(file: File)

        fun adapterInterface(boolean: Boolean, position: Int)

    }


    inner class RowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun onBindUi(pos: Int) {
//            Picasso.get()
//                    .load("file://" + filesList[pos].bitmap)
//                    .transform(CropSquareTransformation())
//                    .into(itemView.file_type_icon)

            val requestOptions = RequestOptions()
            requestOptions.override(360, 640)
            requestOptions.centerCrop()
            requestOptions.transform(SquareTransformation())
            Glide.with(context).load("file://" + filesList[pos].bitmap).apply(requestOptions).into(itemView.file_type_icon)

            itemView.setOnClickListener {
                filesList[pos].selected = !filesList[pos].selected
                intefaceListner.adapterInterface(filesList[pos].selected, pos)
            }

            itemView.checkBox.isChecked = filesList[pos].selected

            itemView.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                if (buttonView.isPressed) {
                    intefaceListner.adapterInterface(isChecked, pos)
                }
            }
        }
    }

}

