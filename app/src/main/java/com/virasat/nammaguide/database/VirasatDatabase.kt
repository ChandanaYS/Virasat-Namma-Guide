package com.virasat.nammaguide.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [VisitedPlaceEntity::class],
    version = 1,
    exportSchema = false
)
abstract class VirasatDatabase : RoomDatabase() {
    abstract fun visitedPlaceDao(): VisitedPlaceDao

    companion object {
        @Volatile
        private var instance: VirasatDatabase? = null

        fun getInstance(context: Context): VirasatDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    VirasatDatabase::class.java,
                    "virasat_namma_guide.db"
                ).build().also { instance = it }
            }
        }
    }
}
