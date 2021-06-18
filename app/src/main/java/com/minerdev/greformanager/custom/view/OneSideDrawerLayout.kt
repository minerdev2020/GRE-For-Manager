package com.minerdev.greformanager.custom.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.drawerlayout.widget.DrawerLayout

class OneSideDrawerLayout : DrawerLayout {
    private var isSwipeOpenEnabled = true
    private var drawerGravity = 0

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        // 잠겨있으면서 보이지 않을때 작동하지않는다, 반대의 경우 정상작동
        return if (!isSwipeOpenEnabled && !isDrawerVisible(drawerGravity)) {
            false
        } else
            super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        // 잠겨있으면서 보이지 않을때 작동하지않는다, 반대의 경우 정상작동
        return if (!isSwipeOpenEnabled && !isDrawerVisible(drawerGravity)) {
            false
        } else
            super.onTouchEvent(ev)
    }

    override fun performClick(): Boolean {
        // 잠겨있으면서 보이지 않을때 작동하지않는다, 반대의 경우 정상작동
        return if (!isSwipeOpenEnabled && !isDrawerVisible(drawerGravity)) {
            false
        } else
            super.performClick()
    }

    fun lockSwipe(drawerGravity: Int) {
        isSwipeOpenEnabled = false
        this.drawerGravity = drawerGravity
    }

    fun unLockSwipe() {
        isSwipeOpenEnabled = true
    }
}