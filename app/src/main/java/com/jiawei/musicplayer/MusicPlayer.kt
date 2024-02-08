package com.jiawei.musicplayer

import android.content.Context
import androidx.media3.common.C.TIME_UNSET
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

class MusicPlayer {

    private lateinit var player: ExoPlayer

    /***
     * Singleton
     */
    companion object {
        var instance: MusicPlayer? = null
        fun getMusicPlayer(): MusicPlayer? {
            if (instance == null) {
                instance = MusicPlayer()
            }
            return instance
        }
    }

    fun init(context: Context) {
        player = ExoPlayer.Builder(context).build()
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