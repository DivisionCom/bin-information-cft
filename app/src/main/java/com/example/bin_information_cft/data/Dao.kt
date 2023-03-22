package com.example.bin_information_cft.data

import androidx.lifecycle.LiveData
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@androidx.room.Dao
interface Dao {
    @Insert (onConflict = OnConflictStrategy.IGNORE)
    fun insertItem(item: DbItem)

    @Query("SELECT * FROM requests")
    fun getAllItems(): Flow<List<DbItem>>

    @Query("SELECT * FROM requests WHERE name LIKE :name")
    fun getItem(name: String) : LiveData<DbItem>
}