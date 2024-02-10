package com.jiawei.musicplayer.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.jiawei.musicplayer.model.MusicFile
import kotlinx.coroutines.flow.Flow

@Dao
interface MusicFileDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(music: MusicFile)

    @Update
    suspend fun update(music: MusicFile)

    @Delete
    suspend fun delete(music: MusicFile)

    @Query("DELETE from music_files")
    suspend fun clear()

    @Query("SELECT * from music_files WHERE path = :path")
    fun getMusicFile(path: String): Flow<MusicFile>

    @Query("SELECT * from music_files ORDER BY filename ASC")
    fun getAllMusicFiles(): Flow<List<MusicFile>>

}