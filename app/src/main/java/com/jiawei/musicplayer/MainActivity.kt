package com.jiawei.musicplayer

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import com.jiawei.musicplayer.data.AppDataContainer
import com.jiawei.musicplayer.model.MusicFile
import com.jiawei.musicplayer.model.MusicListViewModel
import com.jiawei.musicplayer.model.MusicListViewModelFactory
import com.jiawei.musicplayer.ui.MainScreen
import com.jiawei.musicplayer.ui.theme.MusicPlayerTheme

class MainActivity : BaseActivity() {

    val container = AppDataContainer(MusicApp.getAppContext())
    val data: MusicListViewModel by viewModels{ MusicListViewModelFactory(container.musicFilesRepository) }
    val musicData = mutableStateListOf<MusicFile>()
//    lateinit var container: AppContainer

    override fun onCreate(savedInstanceState: Bundle?) {
//        container =
//        data = MusicListViewModel(container.musicFilesRepository)
        super.onCreate(savedInstanceState)
        data.setObserver(this) {
            musicData.clear()
            musicData.addAll(data.loadMusicFiles() ?: listOf())
        }

        setContent {
            val isShowLoading by data.showLoading.collectAsState()
            MusicPlayerTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    MainScreen(Modifier.fillMaxSize(), musicData, isShowLoading)
                }
            }
        }
    }

    override fun hasFilePermission(hasPerssion: Boolean) {
        if(hasPerssion){
            data.scanFile()
        }
    }
}
