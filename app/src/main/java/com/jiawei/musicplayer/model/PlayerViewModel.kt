package com.jiawei.musicplayer.model

import androidx.lifecycle.ViewModel
import com.jiawei.musicplayer.core.MusicPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class PlayerViewModel : ViewModel() {

    private val _playerState = MutableStateFlow(PlayerState())
    val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

    private val player by lazy { MusicPlayer.getInstance() }
    private var playbackJob: Job? = null

    fun updateCurrentMusic(curMusic: MusicFile) {
        _playerState.value = _playerState.value.copy(currentMusic = curMusic)
    }

    fun updateProgress(progress: Long) {
        _playerState.value = _playerState.value.copy(progress = progress)
    }

    fun updateIsPlaying(isPlaying: Boolean) {
        _playerState.value = _playerState.value.copy(isPlaying = isPlaying)
    }

    fun playMusic(music: MusicFile) {
        MusicPlayer.getInstance().play(music.path)
        updateCurrentMusic(music)
        startPlaybackUpdate()
    }

    fun toggleMusic() {
        if(player.isPlaying()) {
            player.pause()
        }else{
            player.resume()
        }
        updateIsPlaying(player.isPlaying())
    }

    fun seekMusic(position: Long) {
        _playerState.value = _playerState.value.copy(isPlaying = true, progress = position)
        player.seek(position)
        player.resume()
    }

    fun getDuration(): Long {
        return player.duration()
    }

    private fun startPlaybackUpdate() {
        if (playbackJob?.isActive == true) return
        playbackJob = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                delay(500)
                _playerState.value = _playerState.value.copy(isPlaying = player.isPlaying(), progress = player.position())
            }
        }
    }

    private fun stopPlaybackUpdate() {
        playbackJob?.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        stopPlaybackUpdate()
        player.stop()
        player.release()
    }

}