package com.jiawei.musicplayer.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.jiawei.musicplayer.model.MusicFile

@Database(entities = [MusicFile::class], version = 1, exportSchema = false)
abstract class MusicPlayerDatabase : RoomDatabase() {
    abstract fun musicFileDao(): MusicFileDao

    companion object {
        @Volatile
        private var Instance: MusicPlayerDatabase? = null
        fun getDatabase(context: Context): MusicPlayerDatabase{
            return Instance ?: synchronized(this){
                Room.databaseBuilder(context, MusicPlayerDatabase::class.java, "music_file_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }

}