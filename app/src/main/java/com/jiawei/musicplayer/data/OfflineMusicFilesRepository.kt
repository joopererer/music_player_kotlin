package com.jiawei.musicplayer.data

import com.jiawei.musicplayer.model.MusicFile
import kotlinx.coroutines.flow.Flow

class OfflineMusicFilesRepository(private val musicFileDao: MusicFileDao) : MusicFilesRepository {
    override fun getAllMusicFileStream(): Flow<List<MusicFile>> =
        musicFileDao.getAllMusicFiles()

    override fun getMusicFileStream(path: String): Flow<MusicFile> =
        musicFileDao.getMusicFile(path)

    override suspend fun insertMusicFile(music: MusicFile) =
        musicFileDao.insert(music)

    override suspend fun deleteMusicFile(music: MusicFile) =
        musicFileDao.delete(music)

    override suspend fun updateMusicFile(music: MusicFile) =
        musicFileDao.update(music)

    override suspend fun clearMusicFile() =
        musicFileDao.clear()

}