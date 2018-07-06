package com.technocom.imagetopdf.fragments

import android.os.Bundle
import android.support.annotation.Nullable
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import com.technocom.imagetopdf.R
import com.technocom.imagetopdf.activity.MainActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.settings.*


/*
Created by Pawan kumar
 */


class Settings : BaseFragment() {

    var name: Boolean = true
    var size: Boolean = true
    var compressed: Int = 100
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity.mReset.visibility = View.GONE
        mainActivity.mTitle.text = resources.getString(R.string.settings)
        setDefaultSetting()
        mRadioGroupName.setOnCheckedChangeListener(radioListener)
        mRadioGroup.setOnCheckedChangeListener(radioListener)
        checkedChangeList()


        /* mEyePassword.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->

             if (!isChecked) {
                 mEnterPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()

             } else {
                 mEnterPassword.transformationMethod = PasswordTransformationMethod.getInstance()

             }
         })
 */

        //    mEnterPassword.setSelection(mEnterPassword.text.length);


        mSave.setOnClickListener {
            pref.isAutoFilename = name
            pref.isFitPage = size
            pref.isCompression = compressed

            toastLong("Saved")
            fragmentManager!!.popBackStack()
        }
        mCancel.setOnClickListener {

            fragmentManager!!.popBackStack()
        }


    }

    private fun checkedChangeList() {
        mCompressionRadio.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.mNoCompression -> compressed = 100
                R.id.mLow -> compressed= 90
                R.id.mMedium -> compressed = 50
                R.id.mHigh -> compressed = 20

            }
        }
    }

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.clear()
    }

    private val radioListener = RadioGroup.OnCheckedChangeListener { group, checkedId ->
        when (group) {
            mRadioGroupName -> changeName(checkedId)
            mRadioGroup -> changeSize(checkedId)
        }
    }

    private fun setDefaultSetting() {
        name = pref.isAutoFilename
        size = pref.isFitPage
        compressed = pref.isCompression
        if (name)
            mAutomaticGenerat.isChecked = true
        else
            mAlwaysAsk.isChecked = true

        if (size)
            mFitToPageSize.isChecked = true
        else
            mNeverResize.isChecked = true

        when (compressed) {
            100 -> mNoCompression.isChecked = true
            90 -> mLow.isChecked = true
            50 -> mMedium.isChecked = true
            20 -> mHigh.isChecked = true
        }

    }

    private fun changeName(checkedId: Int) {
        name = checkedId == R.id.mAutomaticGenerat
    }

    private fun changeSize(checkedId: Int) {
        size = checkedId == R.id.mFitToPageSize
    }

    override fun onDestroyView() {
        (activity as MainActivity).mReset.visibility = View.VISIBLE
        super.onDestroyView()
    }
}