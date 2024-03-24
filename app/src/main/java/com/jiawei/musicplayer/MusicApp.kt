package com.jiawei.musicplayer

import android.app.Application
import android.content.Context

class MusicApp : Application() {
    companion object {
        private lateinit var instance: MusicApp
        fun getAppContext(): Context {
            return instance.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}