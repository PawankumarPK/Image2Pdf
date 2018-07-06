package com.technocom.imagetopdf.adapter

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.technocom.camera.SquareTransformation
import com.technocom.imagetopdf.R
import com.technocom.imagetopdf.fragments.PdfView
import com.technocom.imagetopdf.utils.helper.ItemTouchHelperAdapter
import com.technocom.imagetopdf.utils.helper.OnStartDragListener
import kotlinx.android.synthetic.main.view_holder.view.*
import java.util.*


/*
Created by Pawan kumar
 */

class RecyclerAdapter(private val context: Context, private val list: MutableList<String>, private val itemClick: PdfView, private val drag: OnStartDragListener) : RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder>(), ItemTouchHelperAdapter {

    private lateinit var vibratevar : Vibrator
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {

        vibratevar = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        return RecyclerViewHolder(LayoutInflater.from(context).inflate(R.layout.view_holder, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(viewholder: RecyclerViewHolder, position: Int) {
        viewholder.onBind(position)
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        Collections.swap(list, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
        return true
    }

    override fun onItemDismiss(position: Int) {

    }

    inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dialog = Dialog(context)
        var delete: TextView
        var crop: TextView
        var select: TextView


        init {
            dialog.setContentView(R.layout.custom_dialog)
            delete = dialog.findViewById(R.id.delete) as TextView
            crop = dialog.findViewById(R.id.crop) as TextView
            select = dialog.findViewById(R.id.mSelect)as TextView

        }

        fun onBind(position: Int) {

            val requestOptions = RequestOptions()
            requestOptions.override(360, 640)
            requestOptions.centerCrop()
            requestOptions.transform(SquareTransformation())
            Glide.with(context).load("file://" + list[position]).apply(requestOptions).into(itemView.spacecraftImg)
            itemView.setOnLongClickListener{  vibrate(); drag.onStartDrag(this@RecyclerViewHolder)
                drag.onStartDrag(this@RecyclerViewHolder)
                false
            }
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
            select.setOnClickListener {

               // list[position].sele

            }

        }

        private fun vibrate() {
            // Vibrate for 500 milliseconds
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibratevar.vibrate(VibrationEffect.createOneShot(2000, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                //deprecated in API 26
                vibratevar.vibrate(2000)
            }
        }

    }


    interface ItemClick {
        fun deleteImages(position: Int)
        fun cropImage(position: Int)
    }
}