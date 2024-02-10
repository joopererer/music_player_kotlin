package com.jiawei.musicplayer.data

import com.jiawei.musicplayer.model.MusicFile
import kotlinx.coroutines.flow.Flow

interface MusicFilesRepository {

    fun getAllMusicFileStream(): Flow<List<MusicFile>>

    fun getMusicFileStream(path:String): Flow<MusicFile>

    suspend fun insertMusicFile(music: MusicFile)

    suspend fun deleteMusicFile(music: MusicFile)

    suspend fun updateMusicFile(music: MusicFile)

    suspend fun clearMusicFile()

}