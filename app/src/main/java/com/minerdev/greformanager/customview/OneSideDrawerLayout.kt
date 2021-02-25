package com.minerdev.greformanager.customview

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.drawerlayout.widget.DrawerLayout

class OneSideDrawerLayout : DrawerLayout {
    private var isSwipeOpenEnabled = true
    private var drawerGravity = 0

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return if (!isSwipeOpenEnabled && !isDrawerVisible(drawerGravity)) {
            false
        } else
            super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return if (!isSwipeOpenEnabled && !isDrawerVisible(drawerGravity)) {
            false
        } else
            super.onTouchEvent(ev)
    }

    fun lockSwipe(drawerGravity: Int) {
        isSwipeOpenEnabled = false
        this.drawerGravity = drawerGravity
    }

    fun unLockSwipe() {
        isSwipeOpenEnabled = true
    }
}