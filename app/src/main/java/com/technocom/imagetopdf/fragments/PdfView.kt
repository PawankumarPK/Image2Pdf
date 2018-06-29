package com.technocom.imagetopdf.fragments


import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.support.annotation.RequiresApi
import android.support.design.widget.Snackbar
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.technocom.camera.CameraActivity
import com.technocom.imagetopdf.R
import com.technocom.imagetopdf.activity.CroppedImage
import com.technocom.imagetopdf.activity.FileExplorer
import com.technocom.imagetopdf.activity.MainActivity
import com.technocom.imagetopdf.activity.MainActivity.Companion.filesListDataResult
import com.technocom.imagetopdf.adapter.RecyclerAdapter
import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_pdf_view.*
import java.io.*


/*
Created by Pawan kumar
 */

class PdfView : BaseFragment(), RecyclerAdapter.ItemClick {

    private lateinit var fabOpen: Animation
    private lateinit var fabClose: Animation
    private lateinit var rotateForward: Animation
    private lateinit var rotateBackward: Animation
    private lateinit var adapter: RecyclerAdapter
    private lateinit var pdfFile: File
    private var pageNo = 0
    private lateinit var pageInfo: PdfDocument.PageInfo
    private var canClick = true
    private var progress = 0
    private lateinit var progressDialog: ProgressDialog


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_pdf_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        gallery.setOnClickListener { startActivityForResult(Intent(activity, FileExplorer::class.java), 1001) }

        camera.setOnClickListener { startActivityForResult(Intent(activity, CameraActivity::class.java), 1002) }

        createPDF.setOnClickListener { progressView() }

        pdfFile = File(activity!!.externalCacheDir.path + "/image2pdf.pdf")

        if (pdfFile.createNewFile()) {
            Log.e("Filecreated", "Filecreated")
        } else {
            Log.e("Filecreated", "Filenotcreated")
        }

        fabOpen = AnimationUtils.loadAnimation(activity, R.anim.fab_open)
        fabClose = AnimationUtils.loadAnimation(activity, R.anim.fab_close)

        rotateForward = AnimationUtils.loadAnimation(activity, R.anim.rotate_forward)
        rotateBackward = AnimationUtils.loadAnimation(activity, R.anim.rotate_backward)
        initRecyclerView()

    }

    private fun createPDF() {
        if (MainActivity.filesListDataResult.size == 0)
            return
        val document = PdfDocument()
        createPdf(document, MainActivity.filesListDataResult[pageNo])
    }

    private fun initRecyclerView() {
        rv.layoutManager = GridLayoutManager(activity, 2, GridLayoutManager.VERTICAL, false)
        adapter = RecyclerAdapter(activity!!, MainActivity.filesListDataResult, this)
        rv.adapter = adapter

    }

    override fun deleteImages(position: Int) {
        MainActivity.filesListDataResult.removeAt(position)
    }

    override fun cropImage(position: Int) {
        val intent = Intent(activity, CroppedImage::class.java)
        intent.putExtra("position", position)
        adapter.notifyDataSetChanged()
        startActivityForResult(intent, 1003)

    }


    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        adapter.notifyDataSetChanged()

    }


    private fun createPdf(document: PdfDocument, bitmapPath: String) {
        progress = (pageNo.toFloat().div(filesListDataResult.size).times(100f)).toInt()
        activity!!.runOnUiThread {
            if (!::progressDialog.isInitialized)
                progressDialog = ProgressDialog.show(activity, "$progress%", "Please Wait", true)
            progressDialog.setTitle("$progress%")
        }

        val stream = ByteArrayOutputStream()
        var bitmap = BitmapFactory.decodeFile(bitmapPath)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        bitmap = getResizedBitmap(bitmap, 535, 758)
        pageInfo = PdfDocument.PageInfo.Builder(595, 842, pageNo).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint()
        paint.color = Color.RED
        canvas.drawBitmap(bitmap, 30f, 42f, paint)
        document.finishPage(page)
        bitmap.recycle()
        try {
            pageNo++
            if (pageNo == MainActivity.filesListDataResult.size) {
                document.writeTo(FileOutputStream(pdfFile))
                makePdf()
                document.close()
                return
            }
            createPdf(document, MainActivity.filesListDataResult[pageNo])
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(activity, "Something wrong:" + e.toString(),
                    Toast.LENGTH_LONG).show()
        }

    }

    fun getResizedBitmap(bm: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
        val width = bm.width
        val height = bm.height
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height
        // CREATE A MATRIX FOR THE MANIPULATION
        val matrix = Matrix()
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight)

        // "RECREATE" THE NEW BITMAP
        val resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false)
        bm.recycle()
        return resizedBitmap
    }

    private fun openPdf(file: File) {
        val target = Intent(Intent.ACTION_VIEW)
        target.setDataAndType(Uri.fromFile(file), "application/pdf")
        target.flags = Intent.FLAG_ACTIVITY_NO_HISTORY

        val intent = Intent.createChooser(target, "Open File")
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Snackbar.make(view!!, "No Application Found to open Pdf", 3).show()
            // progressView.visibility = View.GONE
        }
    }

    private fun makePdf() {
        val pdfFile = File(Environment.getExternalStorageDirectory().absolutePath, "Image2Pdf")
        if (!pdfFile.exists())
            pdfFile.mkdir()
        val createdPdf = File(pdfFile.path + "/Pdf_${System.currentTimeMillis()}.pdf")
        try {

            val instream = FileInputStream(this.pdfFile)
            val outstream = FileOutputStream(createdPdf)

            val buffer = ByteArray(1024)

            var length: Int
            do {
                length = instream.read(buffer)
                if (length > 0)
                    outstream.write(buffer, 0, length)
                else
                    break

            } while (true)

            createdPdf.createNewFile()
            instream.close()
            outstream.close()
            this.pdfFile.delete()
        } catch (ioe: IOException) {
            ioe.printStackTrace()
        }
        activity!!.runOnUiThread({
            val snackbar = Snackbar.make(view!!, "Pdf Converted", 5000)
            snackbar.setAction("Open Pdf") { openPdf(createdPdf) }
            snackbar.setActionTextColor(Color.RED)
            snackbar.show()
        })
    }

    private fun progressView() {
        if (!canClick)
            return
        progressBar.visibility = VISIBLE
        canClick = false
        Completable.fromAction { createPDF() }.observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.newThread())
                .subscribe(object : CompletableObserver {
                    override fun onComplete() {
                       // progressDialog.progress = 100

                        if (::progressDialog.isInitialized && progressDialog.isShowing)
                            progressDialog.dismiss()
                        canClick = true
                        progressBar.visibility = GONE
                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()

                    }
                })
    }
}