package com.dermomedic

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.dermomedic.database.AnalyseData
import com.dermomedic.database.AnalyseDatabase
import com.dermomedic.database.AnalyseDatabaseDao
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class AnalyseDatabaseTest {

    private lateinit var analyseDao: AnalyseDatabaseDao
    private lateinit var db: AnalyseDatabase

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        db = Room.inMemoryDatabaseBuilder(context, AnalyseDatabase::class.java)
                // Allowing main thread queries, just for testing.
                .allowMainThreadQueries()
                .build()
        analyseDao = db.analyseDatabaseDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetAnalyse() = runBlocking<Unit> {
        val analyse = AnalyseData(fileName="test.png")
        val idx = analyseDao.insert(analyse)
        assertNotNull(idx)
        val newAnalyse = analyseDao.get(idx)
        assertNotNull(newAnalyse)
        val findByNameAnalyse = analyseDao.findByName("test.png")
        assertNotNull(findByNameAnalyse)
    }
}

