package com.jiawei.musicplayer.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jiawei.musicplayer.data.MusicFilesRepository

class MusicListViewModelFactory(private val musicFilesRepository: MusicFilesRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MusicListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MusicListViewModel(musicFilesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
