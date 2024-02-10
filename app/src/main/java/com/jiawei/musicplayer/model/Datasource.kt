package com.jiawei.musicplayer.model

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.jiawei.musicplayer.data.MusicFilesRepository
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class Datasource(private val musicFilesRepository: MusicFilesRepository) : ViewModel() {

    val list: MutableList<MusicFile> = mutableListOf()
    val musicLiveData = MutableLiveData<List<MusicFile>>()
    val mainScope = MainScope()

    fun setObserver(owner: LifecycleOwner, observer: Observer<List<MusicFile>>) {
        musicLiveData.observe(owner, observer)
    }

    fun scanFile() {
        loadData { files ->
            if (files.isEmpty()) {
                // scan
                MusicScanner.getMusicScanner()?.findMusicFile {
                    Log.d("dx", "musicdata $it")
                    val musicFiles = toMusicFiles(it)
                    saveData(musicFiles)
                    musicLiveData.postValue(musicFiles)
                }
            }else{
                // load direct
                musicLiveData.postValue(files)
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
            filesList.add(toMusicFile(file))
        }
        return filesList.toList()
    }

    fun toMusicFile(file: String): MusicFile {
        val index_folder = file.lastIndexOf('/')
        val index_suffix = file.lastIndexOf('.')
        val dir = file.substring(0, index_folder)
        val type = file.substring(index_suffix+1)
        val name = file.substring(index_folder+1, index_suffix)
        return MusicFile(file, name, dir, type)
    }

    fun loadMusicFiles(): List<MusicFile>? {
        return musicLiveData.value
    }

}