package com.technocom.imagetopdf.activity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import com.technocom.imagetopdf.R
import kotlinx.android.synthetic.main.activity_cropped_image.*
import android.provider.MediaStore
import android.os.Environment.getExternalStorageDirectory
import com.technocom.imagetopdf.adapter.RecyclerAdapter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


class CroppedImage : BaseActivity() {

    private var croppedImage: Bitmap? = null
    private var position = -1
    private lateinit var adapter: RecyclerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cropped_image)
        position = intent.getIntExtra("position", position)
        croppedImage = BitmapFactory.decodeFile(MainActivity.filesListDataResult[position])
        cropImage.setImageBitmap(croppedImage)
        cropImageToSize.setOnClickListener { cropImage() }
        rotateLeft.setOnClickListener { rotate(true) }
        rotateRight.setOnClickListener { rotate(false) }
        save.setOnClickListener { saveAction() }

    }

    private fun cropImage() {
        hideCrop()
        croppedImage = cropImage.croppedImage
        previewImage.setImageBitmap(croppedImage)
        Toast.makeText(this, "Image Cropped", Toast.LENGTH_LONG).show()


    }

    private fun rotate(value: Boolean) {
        if (value)
            cropImage.rotateImage(cropImage.rotation.toInt() + 90)
        else
            cropImage.rotateImage(cropImage.rotation.toInt() - 90)
    }

    private fun showCrop() {
        cropImage.visibility = View.VISIBLE
        rotateLeft.visibility = View.VISIBLE
        cropImageToSize.visibility = View.VISIBLE
        rotateRight.visibility = View.VISIBLE
        preview.visibility = View.GONE

    }

    private fun hideCrop() {
        cropImage.visibility = View.GONE
        rotateLeft.visibility = View.GONE
        cropImageToSize.visibility = View.GONE
        rotateRight.visibility = View.GONE
        preview.visibility = View.VISIBLE
    }

    override fun onBackPressed() {
        if (preview.visibility == View.VISIBLE)
            showCrop()
        else
            super.onBackPressed()
    }

    private fun saveAction() {
        val fOut: OutputStream?
       // val file = File(MainActivity.filesListDataResult[position]) // the File to save , append increasing numeric counter to prevent files from getting overwritten.
        val file = File(this.externalCacheDir.path,"cache_${System.currentTimeMillis()}") // the File to save , append increasing numeric counter to prevent files from getting overwritten.
        fOut = FileOutputStream(file)
        croppedImage!!.compress(Bitmap.CompressFormat.JPEG, 85, fOut) // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
        fOut.flush() // Not really required
        fOut.close() // do not forget to close the stream
        MainActivity.filesListDataResult[position] = file.absolutePath
        finish()



    }
}