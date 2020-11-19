package com.dermomedic.database

import androidx.annotation.Nullable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "analyse_table")
data class AnalyseData(

        @PrimaryKey(autoGenerate = true)
        var idx: Long = 0L,

        @ColumnInfo(name = "file_name")
        val fileName: String,

        @Nullable
        @ColumnInfo(name = "malingant_evaluation")
        var malingantEvaluation: Float? = null
)
