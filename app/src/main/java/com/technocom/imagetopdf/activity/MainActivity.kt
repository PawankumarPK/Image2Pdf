package com.technocom.imagetopdf.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.Menu
import android.view.MenuItem
import com.technocom.imagetopdf.R
import com.technocom.imagetopdf.fragments.Home
import com.technocom.imagetopdf.fragments.Settings
import kotlinx.android.synthetic.main.activity_main.*


/*
Created by Pawan kumar
 */

class MainActivity : BaseActivity() {

    lateinit var resetInterface : ResetButton
    private val PERMISSIONS_MULTIPLE_REQUEST = 123


    companion object {
        val filesListDataResult = java.util.ArrayList<String>()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar);

        mReset.setOnClickListener{
            if (::resetInterface.isInitialized)
                resetInterface.reset()
        }
        backArrow.setOnClickListener {
            onBackPressed()
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission()

        } else {
            // write your logic here
        }
        showFragementHome()
    }

    private fun showFragementHome() {
        val transaction = supportFragmentManager.beginTransaction()
        val fragment = Home()
        transaction.replace(R.id.frame_container, fragment)
        transaction.commit()
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) + ContextCompat
                        .checkSelfPermission(this,
                                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {


            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {

                Snackbar.make(this.findViewById(android.R.id.content),
                        "Please Grant Permissions to access your phone media",
                        Snackbar.LENGTH_INDEFINITE).setAction("ENABLE"
                ) {
                    requestPermissions(
                            arrayOf(Manifest.permission
                                    .READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA),
                            PERMISSIONS_MULTIPLE_REQUEST)
                }.show()
            } else {
                requestPermissions(
                        arrayOf(Manifest.permission
                                .READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA),
                        PERMISSIONS_MULTIPLE_REQUEST)
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

        when (requestCode) {
            PERMISSIONS_MULTIPLE_REQUEST -> if (grantResults.size > 0) {
                val cameraPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED
                val readExternalFile = grantResults[0] == PackageManager.PERMISSION_GRANTED

                if (cameraPermission && readExternalFile) {
                    // write your logic here
                } else {
                    Snackbar.make(this.findViewById(android.R.id.content),
                            "Please Grant Permissions to upload profile photo",
                            Snackbar.LENGTH_INDEFINITE).setAction("ENABLE"
                    ) {
                        requestPermissions(
                                arrayOf(Manifest.permission
                                        .READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA),
                                PERMISSIONS_MULTIPLE_REQUEST)
                    }.show()
                }
            }
        }
    }

    override fun onBackPressed() {
        filesListDataResult.clear()
        super.onBackPressed()
    }

    interface ResetButton {
        fun reset()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_items, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mAboutUs -> startActivity(Intent(applicationContext, AboutUs::class.java))
            R.id.mSettings -> supportFragmentManager.beginTransaction().addToBackStack(null).replace(R.id.frame_container,Settings()).commit()

        }
        return super.onOptionsItemSelected(item);
    }


}
