package com.technocom.database

import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.Database
import com.technocom.dtos.Dto


/*
Created by Pawan kumar
 */


@Database(entities = [(Dto::class)], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun logsDao(): Logs

}