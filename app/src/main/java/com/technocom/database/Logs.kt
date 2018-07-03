package com.technocom.database

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.technocom.dtos.Dto
import io.reactivex.Maybe


/*
Created by Pawan kumar
 */

@Dao
interface Logs  {

    @Query("SELECT * FROM tablename")
    fun getAll(): Maybe<List<Dto>>

    @Insert
    fun insertAll(users: Dto)

    @Delete
    fun delete(user: Dto)
}