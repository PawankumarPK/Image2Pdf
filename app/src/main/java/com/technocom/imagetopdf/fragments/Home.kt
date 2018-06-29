package com.technocom.imagetopdf.fragments


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.technocom.camera.CameraActivity
import com.technocom.imagetopdf.R
import com.technocom.imagetopdf.activity.FileExplorer
import com.technocom.imagetopdf.activity.MainActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*


/*
Created by Pawan kumar
 */

class Home : BaseFragment() {

    private lateinit var fabOpen: Animation
    private lateinit var fabClose: Animation
    private lateinit var rotateForward: Animation
    private lateinit var rotateBackward: Animation
    private var isOpen = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        (activity as MainActivity).backArrow.visibility = View.GONE

        fabOpen = AnimationUtils.loadAnimation(context, R.anim.fab_open)
        fabClose = AnimationUtils.loadAnimation(context, R.anim.fab_close)

        rotateForward = AnimationUtils.loadAnimation(context, R.anim.rotate_forward)
        rotateBackward = AnimationUtils.loadAnimation(context, R.anim.rotate_backward)
        plusFab.setOnClickListener { animateFab() }

        galleryLayout.setOnClickListener {
            startActivityForResult(Intent(context, FileExplorer::class.java), 1001)
        }
        cameraLayout.setOnClickListener {
            startActivityForResult(Intent(context, CameraActivity::class.java), 1002)
        }


        galleryFab.setOnClickListener { startActivityForResult(Intent(context, FileExplorer::class.java), 1001) }

        cameraFab.setOnClickListener {
            animateFab()
            startActivityForResult(Intent(activity, CameraActivity::class.java), 1002)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            1001 -> {
                fragmentManager!!.beginTransaction().replace(R.id.frame_container, PdfView()).addToBackStack(null).commit()
            }
            1002 -> {
                fragmentManager!!.beginTransaction().replace(R.id.frame_container, PdfView()).addToBackStack(null).commit()
            }
        }
    }

    private fun animateFab() = if (isOpen) {
        plusFab.startAnimation(rotateBackward)
        cameraFab.startAnimation(fabClose)
        galleryFab.startAnimation(fabClose)
        galleryText.startAnimation(fabClose)
        cameraText.startAnimation(fabClose)
        cameraFab.isClickable = false
        galleryFab.isClickable = false
        isOpen = false
    } else {
        plusFab.startAnimation(rotateForward)
        cameraFab.startAnimation(fabOpen)
        galleryFab.startAnimation(fabOpen)
        galleryText.startAnimation(fabOpen)
        cameraText.startAnimation(fabOpen)
        cameraFab.isClickable = true
        galleryFab.isClickable = true
        isOpen = true
    }

    override fun onDestroyView() {
        (activity as MainActivity).backArrow.visibility = View.VISIBLE
        super.onDestroyView()
    }
}

