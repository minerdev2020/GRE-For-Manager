package com.minerdev.greformanager

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Database(entities = [House::class, Image::class], version = 1, exportSchema = false)
abstract class GreDatabase : RoomDatabase() {
    abstract fun houseDao(): HouseDao
    abstract fun imageDao(): ImageDao

    companion object {
        private const val NUMBER_OF_THREADS = 4
        val databaseWriteExecutor: ExecutorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS)

        @Volatile
        private var INSTANCE: GreDatabase? = null
        private val roomDatabaseCallback: Callback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                databaseWriteExecutor.execute {
                    INSTANCE?.let {
                        it.houseDao().deleteAll()
                        it.imageDao().deleteAll()
                    }
                }
            }
        }

        fun getDatabase(context: Context): GreDatabase? {
            if (INSTANCE == null) {
                synchronized(GreDatabase::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(context.applicationContext,
                                GreDatabase::class.java, "gre_db")
                                .addCallback(roomDatabaseCallback)
                                .build()
                    }
                }
            }

            return INSTANCE
        }
    }
}