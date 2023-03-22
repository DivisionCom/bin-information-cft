package com.example.bin_information_cft.data

import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@androidx.room.Dao
interface Dao {
    @Insert
    fun insertItem(item: DbItem)

    @Query("SELECT * FROM requests")
    fun getAllItems(): Flow<List<DbItem>>
}