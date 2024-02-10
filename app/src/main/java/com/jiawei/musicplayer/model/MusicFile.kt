package com.jiawei.musicplayer.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "music_files")
data class MusicFile(
    @PrimaryKey
    val path: String,
    val filename: String,
    val dir: String,
    val type: String
)
