package com.jiawei.musicplayer.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "music_files")
data class MusicFile(
    @PrimaryKey
    val path: String
) {
    var filename: String
    var dir: String
    var type: String
    init {
        val index_folder = path.lastIndexOf('/')
        val index_suffix = path.lastIndexOf('.')
        dir = path.substring(0, index_folder)
        type = path.substring(index_suffix+1)
        filename = path.substring(index_folder+1, index_suffix)
    }
}
