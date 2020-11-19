package com.dermomedic.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface AnalyseDatabaseDao {

    @Insert
    suspend fun insert(analyse: AnalyseData): Long

    @Update
    suspend fun update(analyse: AnalyseData)

    @Query("SELECT * from analyse_table WHERE idx = :key")
    suspend fun get(key: Long): AnalyseData?

    @Query("SELECT * from analyse_table WHERE file_name = :fileName")
    suspend fun findByName(fileName: String): AnalyseData?

    @Query("DELETE FROM analyse_table")
    suspend fun clear()

}

