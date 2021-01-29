package com.minerdev.greformanager;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;

public class OneSideDrawerLayout extends DrawerLayout {
    private Boolean isSwipeOpenEnabled = true;
    private int drawerGravity;

    public OneSideDrawerLayout(@NonNull Context context) {
        super(context);
    }

    public OneSideDrawerLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public OneSideDrawerLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!isSwipeOpenEnabled && !isDrawerVisible(drawerGravity)) {
            return false;
        }

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!isSwipeOpenEnabled && !isDrawerVisible(drawerGravity)) {
            return false;
        }

        return super.onTouchEvent(ev);
    }

    public void lockSwipe(int drawerGravity) {
        isSwipeOpenEnabled = false;
        this.drawerGravity = drawerGravity;
    }

    public void unLockSwipe() {
        isSwipeOpenEnabled = true;
    }
}
