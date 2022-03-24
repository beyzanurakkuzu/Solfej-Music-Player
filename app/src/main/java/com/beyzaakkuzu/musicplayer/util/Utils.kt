package com.beyzaakkuzu.musicplayer.util

import android.content.Context
import android.graphics.Color
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.content.res.use
import java.lang.Exception

object Utils{
fun isWindowBackgroundDark(context: Context): Boolean {
    return isColorLight(resolveColor(context, android.R.attr.windowBackground))
}
    fun isColorLight(@ColorInt color: Int): Boolean {
        val darkness =
            1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
        return darkness < 0.4
    }
    @JvmOverloads
    fun resolveColor(context: Context, @AttrRes attr: Int, fallback: Int = 0): Int {
        context.theme.obtainStyledAttributes(intArrayOf(attr)).use {
            return try {
                it.getColor(0, fallback);
            } catch (e: Exception) {
                Color.BLACK
            }
        }
    }
    fun desaturateColor(color: Int, ratio: Float): Int {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)

        hsv[1] = hsv[1] / 1 * ratio + 0.2f * (1.0f - ratio)

        return Color.HSVToColor(hsv)
    }
}