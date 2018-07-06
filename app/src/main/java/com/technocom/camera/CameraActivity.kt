package com.technocom.camera

import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.hardware.Camera
import android.hardware.Camera.PictureCallback
import android.os.Bundle
import android.os.Environment.DIRECTORY_PICTURES
import android.os.Environment.getExternalStoragePublicDirectory
import android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
import android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import com.technocom.imagetopdf.R
import com.technocom.imagetopdf.activity.BaseActivity
import com.technocom.imagetopdf.activity.MainActivity
import kotlinx.android.synthetic.main.activity_camera.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class CameraActivity : BaseActivity() {
    private val TAG = "CameraActivity"
    private var mCamera: Camera? = null
    private var mPreview: CameraPreview? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        initListeners()
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH))
            camera_preview.visibility = GONE
    }

    override fun onStart() {
        mCamera = getCameraInstance()
        if (mCamera == null) {
            toastLong("Unable to Open Camera")
            return
        }
        mPreview = CameraPreview(this, mCamera)
        camera_preview.addView(mPreview)
        super.onStart()
    }
    private fun initListeners() {
        capture.setOnClickListener { mCamera?.takePicture(null, null, mPicture) }
        flash.setOnClickListener { flashRadioHideShow() }
        thumb.setOnClickListener { finish() }
        flashRadio.setOnCheckedChangeListener { group, checkedId -> mPreview?.setFlash(checkedId);flashRadioHideShow() }
    }

    private fun flashRadioHideShow() {
        val animation: Animation
        if (flashRadio.visibility == VISIBLE) {
            flashRadio.visibility = GONE
            animation = TranslateAnimation(0f, flash.x, 0f, 0f)
        } else {
            flashRadio.visibility = VISIBLE
            animation = TranslateAnimation(flash.x, 0f, 0f, 0f)
        }
        animation.duration = 300
        flashRadio.startAnimation(animation)
    }

    private val mPicture = PictureCallback { data, _ ->
        val pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE)
        if (pictureFile == null) {
            Log.d(TAG, "Error creating media file, check storage permissions")
            return@PictureCallback
        }

        try {
            val fos = FileOutputStream(pictureFile)
            fos.write(data)
            fos.close()
//            Picasso.get()
//                    .load(pictureFile)
//                    .transform(CircleTransform())
//                    .into(thumb)
            Glide.with(this).load(pictureFile).apply(RequestOptions.circleCropTransform()).into(thumb)

            MainActivity.filesListDataResult.add(pictureFile.absolutePath)
            mCamera?.startPreview()
        } catch (e: FileNotFoundException) {
            Log.d(TAG, "File not found: " + e.message)
        } catch (e: IOException) {
            Log.d(TAG, "Error accessing file: " + e.message)
        }
    }

    private fun getCameraInstance(): Camera? {
        var c: Camera? = null
        try {
            c = Camera.open()
        } catch (e: Exception) {
        }
        return c
    }

    private fun getOutputMediaFile(type: Int): File? {
        val mediaStorageDir = File(getExternalStoragePublicDirectory(
                DIRECTORY_PICTURES), resources.getString(R.string.app_name))
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory")
                return null
            }
        }
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val mediaFile: File
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = File(mediaStorageDir.path + File.separator +
                    "IMG_" + timeStamp + ".jpg")
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = File(mediaStorageDir.path + File.separator +
                    "VID_" + timeStamp + ".mp4")
        } else {
            return null
        }

        return mediaFile
    }

    override fun onStop() {
        mCamera?.stopPreview()
        super.onStop()
    }
    override fun onDestroy() {
        mCamera?.release()
        super.onDestroy()
    }
}
