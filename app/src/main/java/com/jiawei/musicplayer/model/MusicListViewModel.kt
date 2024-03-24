package com.jiawei.musicplayer.model

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.jiawei.musicplayer.core.MusicScanner
import com.jiawei.musicplayer.data.MusicFilesRepository
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MusicListViewModel(private val musicFilesRepository: MusicFilesRepository) : ViewModel() {

    val musicLiveData = MutableLiveData<List<MusicFile>>()
    val mainScope = MainScope()
    private val _showLoading = MutableStateFlow(false)
    val showLoading: StateFlow<Boolean> get() = _showLoading

    fun setObserver(owner: LifecycleOwner, observer: Observer<List<MusicFile>>) {
        musicLiveData.observe(owner, observer)
    }

    fun setShowLoading(isShow: Boolean){
        _showLoading.value = isShow
    }

    fun scanFile() {
        setShowLoading(true)
        loadData { files ->
            if (files.isEmpty()) {
                // scan
                MusicScanner.getMusicScanner()?.findMusicFile {
                    Log.d("dx", "musicdata $it")
                    val musicFiles = toMusicFiles(it)
                    saveData(musicFiles)
                    musicLiveData.postValue(musicFiles)
                    setShowLoading(false)
                }
            }else{
                // load direct
                musicLiveData.postValue(files)
                setShowLoading(false)
            }
        }
    }

    private fun loadData(onLoaded: (List<MusicFile>) -> Unit) {
        mainScope.launch {
            val list = musicFilesRepository.getAllMusicFileStream().first()
            onLoaded(list)
        }
    }

    private fun saveData(musicFiles: List<MusicFile>, onSaved: () -> Unit = {}) {
        mainScope.launch {
            musicFilesRepository.clearMusicFile()
            for (music in musicFiles) {
                musicFilesRepository.insertMusicFile(music)
            }
        }
    }

    private fun toMusicFiles(files: List<String>): List<MusicFile> {
        val filesList = mutableListOf<MusicFile>()
        for(file: String in files) {
            filesList.add(MusicFile(file))
        }
        return filesList.toList()
    }

    fun loadMusicFiles(): List<MusicFile>? {
        return musicLiveData.value
    }

}