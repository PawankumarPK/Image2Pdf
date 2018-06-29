package com.technocom.imagetopdf.interfaces

import android.graphics.Bitmap



/*
Created by Pawan kumar
 */


interface ImagefileInterface {

    /**
     * Get the underlying [Bitmap] object.
     * NEVER call Bitmap.recycle() on this object.
     */
    val bitmap: Bitmap

    /**
     * Decrease the reference counter and recycle the underlying Bitmap
     * if there are no more references.
     */
    fun recycle()

    /**
     * Increase the reference counter.
     * @return self
     */
    fun retain(): ImagefileInterface
}