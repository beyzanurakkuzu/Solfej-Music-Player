package com.beyzaakkuzu.musicplayer.util

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.preference.PreferenceManager
import androidx.annotation.CheckResult
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.beyzaakkuzu.musicplayer.R
import com.beyzaakkuzu.musicplayer.util.Utils.isWindowBackgroundDark
import com.beyzaakkuzu.musicplayer.util.Utils.resolveColor

class ThemeStore {
    companion object {
        const val CONFIG_PREFS_KEY_DEFAULT = "[[kabouzeid_app-theme-helper]]"
        const val KEY_MATERIAL_YOU = "material_you"
        const val KEY_ACCENT_COLOR = "accent_color"
        const val KEY_WALLPAPER_COLOR = "wallpaper_color"

        @CheckResult
        @ColorInt
        fun accentColor(context: Context): Int {
            // Set MD3 accent if MD3 is enabled or in-app accent otherwise
            if (isMD3Enabled(context) && VersionUtils.hasS()) {
                return ContextCompat.getColor(context, R.color.system_accent1_600)
            }
            val desaturatedColor = prefs(context).getBoolean("desaturated_color", false)
            val color = if (isWallpaperAccentEnabled(context)) {
                wallpaperColor(context)
            } else {
                prefs(context).getInt(
                    KEY_ACCENT_COLOR,
                    resolveColor(context, R.attr.colorAccent, Color.parseColor("#263238"))
                )
            }
            return if (isWindowBackgroundDark(context) && desaturatedColor) Utils.desaturateColor(
                color,
                0.4f
            ) else color
        }

        private fun isMD3Enabled(context: Context): Boolean {
            return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(KEY_MATERIAL_YOU, VersionUtils.hasS())
        }
        @CheckResult
        fun prefs(context: Context): SharedPreferences {
            return context.getSharedPreferences(
                CONFIG_PREFS_KEY_DEFAULT,
                Context.MODE_PRIVATE
            )
        }
        private fun isWallpaperAccentEnabled(context: Context): Boolean {
            return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean("wallpaper_accent", VersionUtils.hasOreoMR1() && !VersionUtils.hasS())
        }
        @CheckResult
        @ColorInt
        fun wallpaperColor(context: Context): Int {
            return prefs(context).getInt(
                KEY_WALLPAPER_COLOR,
                resolveColor(context, R.attr.colorAccent, Color.parseColor("#263238"))
            )
        }
    }
}