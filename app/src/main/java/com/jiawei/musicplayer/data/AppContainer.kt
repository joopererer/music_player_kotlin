package com.jiawei.musicplayer.data

import android.content.Context

interface AppContainer {
    val musicFilesRepository: MusicFilesRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val musicFilesRepository: MusicFilesRepository by lazy {
        OfflineMusicFilesRepository(MusicPlayerDatabase.getDatabase(context).musicFileDao())
    }
}