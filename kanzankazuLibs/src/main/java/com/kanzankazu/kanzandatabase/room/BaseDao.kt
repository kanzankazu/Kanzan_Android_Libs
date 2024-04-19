package com.kanzankazu.kanzandatabase.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import io.reactivex.Completable

/**
 * Created by Faisal Bahri on 2020-02-11.
 */
@Dao
interface BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(data: T): Completable

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(data: ArrayList<T>): Completable

    @Update
    fun update(data: T): Completable

    @Update
    fun updates(data: ArrayList<T>): Completable

    @Delete
    fun delete(data: T): Completable
}
