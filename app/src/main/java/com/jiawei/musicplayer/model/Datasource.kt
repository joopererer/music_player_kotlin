package com.jiawei.musicplayer.model

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

class Datasource {

    val list: MutableList<String> = mutableListOf()
    val musicLiveData = MutableLiveData<List<String>>()

    fun setObserver(owner: LifecycleOwner, observer: Observer<List<String>>) {
        musicLiveData.observe(owner, observer)
    }

    fun scanFile() {
        MusicScanner.getMusicScanner()?.findMusicFile{
            Log.d("dx", "musicdata $it")
//            musicModel.findPosition(0, it)
//            list.clear()
//            list.addAll(it)
            musicLiveData.postValue(it)
        }
    }

    fun toMusicFile(file: String): MusicFile {
        val index_folder = file.lastIndexOf('/')
        val index_suffix = file.lastIndexOf('.')
        val dir = file.substring(0, index_folder)
        val type = file.substring(index_suffix)
        val name = file.substring(index_folder+1, index_suffix)
        return MusicFile(file, name, dir, type)
    }

    fun loadMusicFiles(): List<MusicFile> {
//        val dir = "file://folder/my_music/"
//        val type = "mp3"
        val filesList = mutableListOf<MusicFile>()
//        for (id in 1..20) {
//            filesList.add(MusicFile("music_{$id}", dir, type))
//        }
        for(file: String in musicLiveData.value!!) {
            filesList.add(toMusicFile(file))
        }
        return filesList.toList()
    }

}