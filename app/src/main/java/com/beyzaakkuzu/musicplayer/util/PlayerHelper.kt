package com.beyzaakkuzu.musicplayer.util

import android.content.SharedPreferences
import android.media.MediaMetadataRetriever
import com.beyzaakkuzu.musicplayer.model.SongModel

object PlayerHelper {
    fun getCurrentSong(sharedPreferences: SharedPreferences): SongModel? {
            return if (MusicPlayerRemote.playerService?.mediaPlayer != null && MusicPlayerRemote.playerService?.currentSong != null) {
                MusicPlayerRemote.playerService?.currentSong
            } else {
                SharedPreference.getCurrentSong(sharedPreferences)
            }
    }
}

fun getSongThumbnail(songPath: String): ByteArray? {
    var imgByte: ByteArray?
    MediaMetadataRetriever().also {
        try {
            it.setDataSource(songPath)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        imgByte = it.embeddedPicture
        it.release()
    }
    return imgByte
}


