package com.jiawei.musicplayer.model

data class PlayerState (
    val currentMusic: MusicFile = MusicFile(""),
    val isPlaying: Boolean = false,
    val progress: Long = 0L,
)