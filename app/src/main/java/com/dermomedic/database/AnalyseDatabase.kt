package com.dermomedic.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [AnalyseData::class], version = 1, exportSchema = false)
abstract class AnalyseDatabase : RoomDatabase() {

    abstract val analyseDatabaseDao: AnalyseDatabaseDao

    companion object {
        @Volatile
        private var INSTANCE: AnalyseDatabase? = null
        fun getInstance(context: Context): AnalyseDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.applicationContext,
                            AnalyseDatabase::class.java,
                            "sleep_history_database"
                    )
                            .fallbackToDestructiveMigration()
                            .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}
