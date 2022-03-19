package com.beyzaakkuzu.musicplayer.view

import android.graphics.Rect
import android.view.View
import android.view.WindowInsets
import androidx.recyclerview.widget.RecyclerView
import me.zhanghai.android.fastscroll.FastScroller

class ScrollingViewOnApplyWindow(rvSongs: RecyclerView, fastScroller: FastScroller?) :View.OnApplyWindowInsetsListener {

    val mPadding = Rect()
    var mFastScroller: FastScroller? = null

    fun ScrollingViewOnApplyWindowInsetsListener(
        view: View?,
        fastScroller: FastScroller?
    ) {
        if (view != null) {
            mPadding[view.paddingLeft, view.paddingTop, view.paddingRight] = view.paddingBottom
        }
        mFastScroller = fastScroller
    }




    override fun onApplyWindowInsets(view: View, insets: WindowInsets): WindowInsets {
        view.setPadding(
            mPadding.left + insets.systemWindowInsetLeft, mPadding.top,
            mPadding.right + insets.systemWindowInsetRight,
            mPadding.bottom + insets.systemWindowInsetBottom
        )
        mFastScroller?.setPadding(
            insets.systemWindowInsetLeft, 0,
            insets.systemWindowInsetRight, insets.systemWindowInsetBottom
        )
        return insets
    }
}