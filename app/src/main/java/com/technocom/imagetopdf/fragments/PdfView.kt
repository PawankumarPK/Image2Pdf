package com.technocom.imagetopdf.fragments


import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.database.Cursor
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.support.annotation.RequiresApi
import android.support.design.widget.Snackbar
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.technocom.camera.CameraActivity
import com.technocom.dtos.Dto
import com.technocom.imagetopdf.R
import com.technocom.imagetopdf.activity.CroppedImage
import com.technocom.imagetopdf.activity.MainActivity
import com.technocom.imagetopdf.activity.MainActivity.Companion.filesListDataResult
import com.technocom.imagetopdf.adapter.RecyclerAdapter
import com.technocom.imagetopdf.utils.helper.OnStartDragListener
import com.technocom.imagetopdf.utils.helper.SimpleItemTouchHelperCallback
import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.custom_progress_dialog.*
import kotlinx.android.synthetic.main.fragment_pdf_view.*
import kotlinx.android.synthetic.main.getfilename_dialog.*
import kotlinx.android.synthetic.main.pdfcreate_dialog.*
import java.io.*
import java.util.regex.Matcher
import java.util.regex.Pattern


/*
Created by Pawan kumar
 */

class PdfView : BaseFragment(), RecyclerAdapter.ItemClick, MainActivity.ResetButton, OnStartDragListener {


    private lateinit var touchHelper: ItemTouchHelper
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
    lateinit var dialog: Dialog
    private var isPause = false
    private var isStop = false
    private var regex = "([^\\s]+(\\.(?i)(jpg|png|jpeg))$)"
    private var pattern: Pattern? = null
    private var matcher: Matcher? = null
    private val textPaint = Paint()
    private lateinit var mInterstitialAd: InterstitialAd
    private var fileName: String = ""
    private var isButtonPressed = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_pdf_view, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as MainActivity).resetInterface = this
        dialog = Dialog(activity)
        pattern = Pattern.compile(regex)

        mInterstitialAd = InterstitialAd(activity)
        textPaint.isAntiAlias = true
        textPaint.style = Paint.Style.FILL
        textPaint.textSize = 14f
        textPaint.color = Color.parseColor("#FFFA0202")

        if (!pref.isPremium)
            InterstitialAdd()

        gallery.setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            photoPickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, 1001)
        }

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
        mCounting.text = "Total Images : ${MainActivity.filesListDataResult.size}"
    }

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder?) {
        touchHelper.startDrag(viewHolder)
    }

    @SuppressLint("SetTextI18n")
    override fun reset() {

        MainActivity.filesListDataResult.clear()
        adapter.notifyDataSetChanged()
        mCounting.text = "Total Images : ${MainActivity.filesListDataResult.size}"
    }

    private fun createPDF() {
        val document = PdfDocument()
        createPdf(document, MainActivity.filesListDataResult[pageNo])
    }

    private fun initRecyclerView() {
        rv.layoutManager = GridLayoutManager(activity, 2, GridLayoutManager.VERTICAL, false)
        adapter = RecyclerAdapter(activity!!, MainActivity.filesListDataResult, this, this)
        rv.adapter = adapter

        val callback = SimpleItemTouchHelperCallback(adapter);
        touchHelper = ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(rv);

    }

    @SuppressLint("SetTextI18n")
    override fun deleteImages(position: Int) {
        MainActivity.filesListDataResult.removeAt(position)
        adapter.notifyDataSetChanged()
        mCounting.text = "Total Images : ${MainActivity.filesListDataResult.size}"

    }

    override fun cropImage(position: Int) {
        val intent = Intent(activity, CroppedImage::class.java)
        intent.putExtra("position", position)
        startActivityForResult(intent, 1003)
    }


    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            1001 -> {
                if (data == null) {
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

            }
        }
        adapter.notifyDataSetChanged()
        mCounting.text = "Total Images : ${MainActivity.filesListDataResult.size}"
    }

    private fun validate(image: String): Boolean {
        matcher = pattern!!.matcher(image)
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

    private fun unknownFileFormat() {
        Snackbar.make(view!!, "Unkown File Format", 3000).show()
    }


    @SuppressLint("SetTextI18n")
    private tailrec fun createPdf(document: PdfDocument, bitmapPath: String) {
        if (isPause) {
            createPdf(document, MainActivity.filesListDataResult[pageNo])
            return
        }
        if (isStop)
            return
        activity!!.runOnUiThread {
            progress = (pageNo.toFloat().div(filesListDataResult.size).times(100f)).toInt()
            if (dialog.mProgressPercentage != null)
                dialog.mProgressPercentage.text = progress.toString() + "%"
        }

        val stream = ByteArrayOutputStream()
        var bitmap = decodeSampledBitmapFromResource(bitmapPath)

        if (pref.isFitPage)
            bitmap = getResizedBitmap(bitmap, 535, 758)

        var compressBitmap: Bitmap
        compressBitmap = bitmap

        if (pref.isCompression < 100) {
            Log.e("isCompression", "${pref.isCompression}")
            bitmap.compress(Bitmap.CompressFormat.JPEG, pref.isCompression, stream)
            val streamBytes = stream.toByteArray()
            compressBitmap = BitmapFactory.decodeByteArray(streamBytes, 0, streamBytes.size)
        }
        pageInfo = PdfDocument.PageInfo.Builder(595, 842, pageNo).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint()
        paint.color = Color.RED
        if (pref.isFitPage)
            canvas.drawBitmap(compressBitmap, 30f, 42f, paint)
        else
            canvas.drawBitmap(compressBitmap, 0f, 0f, paint)
        canvas.drawText("TechnocomSolution.com", 32f, canvas.height - 22f, textPaint)
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


    private fun getResizedBitmap(bm: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
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
        val createdPdf = File(pdfFile.path + "/$fileName.pdf")
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
        activity!!.runOnUiThread {
            if (mInterstitialAd.isLoaded)
                mInterstitialAd.show()

            dialog.dismiss()
            dialog.setContentView(R.layout.pdfcreate_dialog)
            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(false)
            //  dialog.mFilenameTextview.setText(fileName)
            dialog.mView.setOnClickListener {
                openPdf(createdPdf)
                dialog.dismiss()
            }
            dialog.mCancelForCreatePdf.setOnClickListener { dialog.dismiss() }
            dialog.show()

/*
            val snackbar = Snackbar.make(view!!, "Pdf Converted", 5000)
            snackbar.setAction("Open Pdf") { openPdf(createdPdf) }
            snackbar.setActionTextColor(Color.RED)
            snackbar.show()*/
        }
        db.logsDao().insertAll(Dto(0, fileName, System.currentTimeMillis(), filesListDataResult.size, createdPdf.absolutePath))


    }

    private fun nameDialog(): String {

        val handler = @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message?) {
                throw RuntimeException()
            }
        }
        dialog.setContentView(R.layout.getfilename_dialog)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.mFilenameEditext.setText(fileName)
        dialog.nameok.setOnClickListener {
            fileName = dialog.mFilenameEditext.text.toString()
            if (fileName.trim().isEmpty()) {
                dialog.mFilenameEditext.error = "Field Empty"
            }
            handler.sendMessage(handler.obtainMessage())
            isButtonPressed = true
            dialog.dismiss()
        }

        dialog.nameCancel.setOnClickListener { handler.sendMessage(handler.obtainMessage());isButtonPressed = true;dialog.dismiss() }
        dialog.show()

        try {
            Looper.loop()
        } catch (e2: RuntimeException) {

        }
        isButtonPressed = false
        return fileName
    }

    private fun progressView() {
        if (MainActivity.filesListDataResult.size == 0) {
            toastLong("list empty")
            return
        }
        pageNo = 0
        isStop = false
        isPause = false
        fileName = if (pref.isAutoFilename)
            "Pdf_${System.currentTimeMillis()}"
        else
            nameDialog()

        if (fileName.isEmpty()) {
            snackbar("Filename Empty")
            return
        }
        playAndPause()
        Completable.fromAction { createPDF() }.observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.newThread())
                .subscribe(object : CompletableObserver {
                    override fun onComplete() {
                        canClick = true
                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()

                    }
                })
    }


    private fun decodeSampledBitmapFromResource(res: String): Bitmap {
        val bitmapFactory = BitmapFactory.Options()
        bitmapFactory.inJustDecodeBounds = true
        BitmapFactory.decodeFile(res, bitmapFactory)
        bitmapFactory.inSampleSize = calculateInSampleSize(bitmapFactory)
        // Decode bitmap with inSampleSize set
        bitmapFactory.inJustDecodeBounds = false
        bitmapFactory.inPreferredConfig = Bitmap.Config.RGB_565
        return BitmapFactory.decodeFile(res, bitmapFactory)
    }

    private fun calculateInSampleSize(bitmapFactory: BitmapFactory.Options): Int {
        // Raw height and width of image
        val height = bitmapFactory.outHeight
        val width = bitmapFactory.outWidth
        var inSampleSize = 1

        if (height > 758 || width > 535) {

            val halfHeight = height / 2
            val halfWidth = width / 2

            while ((halfHeight / inSampleSize) >= 758
                    && (halfWidth / inSampleSize) >= 535) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    private fun playAndPause() {
        dialog.setContentView(R.layout.custom_progress_dialog)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
        val anim = AnimationUtils.loadAnimation(activity, R.anim.spinning_stop)
        dialog.mProgressDialog.startAnimation(anim)

        dialog.mPlayPause.setOnCheckedChangeListener { _, isChecked ->
            isPause = isChecked
            if (isPause)
                anim.cancel()
            else
                dialog.mProgressDialog.startAnimation(anim)

        }

        dialog.mStop.setOnClickListener {
            isStop = true
            dialog.dismiss()
        }
    }

    private fun InterstitialAdd() {
        mInterstitialAd = InterstitialAd(activity)
        mInterstitialAd.adUnitId = "ca-app-pub-9110061456871098/7756318281"
        mInterstitialAd.loadAd(AdRequest.Builder().build())
    }
}