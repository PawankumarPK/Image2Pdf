package com.technocom.imagetopdf.dto

import android.graphics.Bitmap

/*
Created by Pawan kumar
 */


data class FileExplorerDTO(
        var bitmap: String? = null,
        var modificationTime: Long? = null,
        var selected:Boolean = false

)
