package com.technocom.dtos

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/*
Created by Pawan kumar
 */

@Entity(tableName = "tablename")
data class Dto(@PrimaryKey(autoGenerate = true) var id: Long,
               @ColumnInfo(name = "name") val name: String,
               @ColumnInfo(name = "time") val time: Long,
               @ColumnInfo(name = "items") val itemOfImages: Int,
               @ColumnInfo(name = "path") val path: String)



