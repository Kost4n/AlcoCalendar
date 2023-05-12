package com.kost4n.alcocalendar

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    version = 1,
    entities = [
        Record::class
    ]
)
abstract class MyDatabase: RoomDatabase() {
    abstract fun getRecordDao(): RecordDao

    companion object {
        @Volatile
        private var INSTANCE: MyDatabase? = null

        fun getMyDatabase(context: Context): MyDatabase {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE = buildDatabase(context)
                }
            }
            return INSTANCE!!
        }

        private fun buildDatabase(context: Context): MyDatabase {
            return Room.databaseBuilder(context,
            MyDatabase::class.java,
            "rec_database").build()
        }
    }
}