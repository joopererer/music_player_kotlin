package com.jiawei.musicplayer.core

import androidx.media3.common.C.TIME_UNSET
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.jiawei.musicplayer.MusicApp

class MusicPlayer {

    private val player: ExoPlayer by lazy {
        ExoPlayer.Builder(MusicApp.getAppContext()).build()
    }

    /***
     * Singleton
     */
    companion object {
        fun getInstance() = Helper.instance
    }
    private object Helper {
        val instance = MusicPlayer()
    }

    fun release() {
        player.release()
    }

    fun play(file: String) {
        val mediaItem = MediaItem.fromUri(file)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
    }

    fun seek(position: Long) {
        player.seekTo(position)
    }

    fun pause() {
        player.pause()
    }

    fun resume() {
        player.play()
    }

    fun stop() {
        player.stop()
    }

    fun isPlaying(): Boolean {
        return player.isPlaying
    }

    fun duration(): Long {
        if (player.duration==TIME_UNSET)
            return 0L
        return player.duration
    }

    fun position(): Long {
        return player.currentPosition
    }

}