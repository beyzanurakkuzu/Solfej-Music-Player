package com.beyzaakkuzu.musicplayer.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.beyzaakkuzu.musicplayer.service.ACTION_NEXT
import com.beyzaakkuzu.musicplayer.service.ACTION_PLAY_PAUSE
import com.beyzaakkuzu.musicplayer.service.ACTION_PREVIOUS
import com.beyzaakkuzu.musicplayer.service.PlayerService
import com.beyzaakkuzu.musicplayer.util.MusicPlayerRemote
import java.lang.IllegalStateException

class NotificationActionBroadcast:BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action != null) {
            when (intent.action) {
                ACTION_PREVIOUS -> {
                    startService(context!!, ACTION_PREVIOUS)
                    MusicPlayerRemote.playPreviousSong()
                }

                ACTION_PLAY_PAUSE -> {
                    startService(context!!, ACTION_PLAY_PAUSE)
                    MusicPlayerRemote.playPause()
                }

                ACTION_NEXT -> {
                    startService(context!!, ACTION_NEXT)
                    MusicPlayerRemote.playNextSong()
                }

                else -> Unit
            }
        }
    }

    private fun startService(context: Context, command: String?) {
        if (MusicPlayerRemote.playerService == null) {
            val intent = Intent(context, PlayerService::class.java)
            intent.action = command
            try {
                // IMPORTANT NOTE: (kind of a hack)
                // on Android O and above the following crashes when the app is not running
                // there is no good way to check whether the app is running so we catch the exception
                // we do not always want to use startForegroundService() because then one gets an ANR
                // if no notification is displayed via startForeground()
                // according to Play analytics this happens a lot, I suppose for example if command = PAUSE
                context.startService(intent)
            } catch (ignored: IllegalStateException) {
                ContextCompat.startForegroundService(context, intent)
            }
        }
    }
}