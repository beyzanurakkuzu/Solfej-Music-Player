package com.beyzaakkuzu.musicplayer.service

import android.content.Context
import android.support.v4.media.session.MediaSessionCompat

class MediaSessionCallback(applicationContext: Context, private val service: PlayerService) :
    MediaSessionCompat.Callback() {
    override fun onSeekTo(pos: Long) {
        super.onSeekTo(pos)
        service.seekTo(pos.toInt())
    }
}