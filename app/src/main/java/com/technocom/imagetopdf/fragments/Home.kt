package com.technocom.imagetopdf.fragments

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.google.android.gms.ads.AdRequest
import com.technocom.camera.CameraActivity
import com.technocom.imagetopdf.R
import com.technocom.imagetopdf.activity.MainActivity
import com.technocom.util.IabHelper
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*
import java.util.regex.Matcher
import java.util.regex.Pattern

/*
Created by Pawan kumar
 */

class Home : BaseFragment() {

    private lateinit var fabOpen: Animation
    private lateinit var fabClose: Animation
    private lateinit var rotateForward: Animation
    private lateinit var rotateBackward: Animation
    private var isOpen = false
    private var regex = "([^\\s]+(\\.(?i)(jpg|png|jpeg))$)"
    private var pattern: Pattern? = null
    private var matcher: Matcher? = null
    private val tagInApp = "InAppPurchase"
    private lateinit var mHelper: IabHelper


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        (activity as MainActivity).backArrow.visibility = View.GONE
        (activity as MainActivity).mReset.visibility = View.GONE
       // (activity as Settings).mReset.visibility = View.GONE

        val base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAu+oqx2e1/PuZj+m44V3VESbhyMuuu1SV88K1aVshQBqjHbZexTtIeyzWnerztUp6fFMzDL4aYeuKo4xCZMqGee+eWjLkGf31vET+ZOPvWbgxGCj5/xK1/GJLZ7H8B6utPVqgdWusK30rOVTCjA6Iw9GevbFWwHgrPzs+N42PzqAOMkL0Y/DPVZBJBUKamgYGRLrLAYiKVJ73d6ONTQToIqqFqg/TmO0T7ixsO6jrKbTzI7/lZqtJbylkHQqrLnQy4m1QWGlJi1yPYtD+wSFKCC03tonAtN9CWTXEfymdU1JT7QizFK07+ShwPYIEGmrTQu1jLlFfTPSqIBNF49sR+wIDAQAB"
        mHelper = IabHelper(activity, base64EncodedPublicKey)
        mHelper.startSetup { result ->
            if (!result.isSuccess) {
                Log.d(tagInApp, "In-app Billing setup failed: $result")
            } else {
                Log.d(tagInApp, "In-app Billing is set up OK")
                mHelper.queryInventoryAsync(mGotInventoryListener)
            }
        }

        if (!pref.isPremium)
            addmob()

        pattern = Pattern.compile(regex)

        fabOpen = AnimationUtils.loadAnimation(context, R.anim.fab_open)
        fabClose = AnimationUtils.loadAnimation(context, R.anim.fab_close)

        rotateForward = AnimationUtils.loadAnimation(context, R.anim.rotate_forward)
        rotateBackward = AnimationUtils.loadAnimation(context, R.anim.rotate_backward)
        plusFab.setOnClickListener { animateFab() }

        galleryLayout.setOnClickListener { galleryIntent() }

        galleryFab.setOnClickListener { galleryIntent() }

        cameraLayout.setOnClickListener { startActivityForResult(Intent(context, CameraActivity::class.java), 1002) }

        cameraFab.setOnClickListener { animateFab()
            startActivityForResult(Intent(activity, CameraActivity::class.java), 1002)
        }
    }

    private fun galleryIntent() {
        val photoPickerIntent = Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        photoPickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, 1001)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            1001 -> {
                if (data == null) {
                    toastLong("No Item Selected")
                    return
                }
                for (i in 0 until data.clipData.itemCount) {
                    val imagePath = getRealPathFromURI(data.clipData.getItemAt(i).uri)
                    val fileName = imagePath.split("/")
                    if (!validate(fileName[fileName.lastIndex])) {
                        unknownFileFormat()
                        continue
                    }
                    MainActivity.filesListDataResult.add(imagePath)
                }

                fragmentManager!!.beginTransaction().replace(R.id.frame_container, PdfView()).addToBackStack(null).commit()
            }
            1002 -> {
                fragmentManager!!.beginTransaction().replace(R.id.frame_container, PdfView()).addToBackStack(null).commit()
            }
        }
    }

    private fun validate(image: String): Boolean {
        matcher = pattern!!.matcher(image.replace("\\s", ""))
        return matcher!!.matches()

    }

    private fun getRealPathFromURI(contentUri: Uri): String {
        var cursor: Cursor? = null
        try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = activity!!.contentResolver.query(contentUri, proj, null, null, null)
            val columnIndex = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            return cursor.getString(columnIndex)
        } finally {
            cursor?.close()
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
        (activity as MainActivity).mReset.visibility = View.VISIBLE
       // (activity as Settings).mReset.visibility = View.VISIBLE
        super.onDestroyView()
    }

    private fun unknownFileFormat() {
        Snackbar.make(view!!, "Unkown File Format", 3000).show()
    }

    private fun addmob() {
        adView.visibility = View.VISIBLE
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    private val mGotInventoryListener = IabHelper.QueryInventoryFinishedListener { result, inventory ->
        if (result.isFailure)
            Log.d(tagInApp, "isFailure")
        else
            pref.isPremium = inventory.hasPurchase(activity!!.packageName)
    }

}

