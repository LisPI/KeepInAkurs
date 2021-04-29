package com.develop.room530.lis.akursnotify.features.home

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.view.marginBottom
import com.google.android.material.floatingactionbutton.FloatingActionButton

const val FAB_ANIM_HIDE_SHOW_DURATION = 500L

class FABBehavior(context: Context, attributes: AttributeSet) : FloatingActionButton.Behavior(
    context,
    attributes
) {
    override fun onStopNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: FloatingActionButton,
        target: View,
        type: Int
    ) {
        super.onStopNestedScroll(coordinatorLayout, child, target, type)
        Handler(Looper.getMainLooper()).postDelayed({
            child.animate().translationY(0F).setDuration(FAB_ANIM_HIDE_SHOW_DURATION).start()
        }, 500)
    }

    override fun onNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: FloatingActionButton,
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray
    ) {
        super.onNestedScroll(
            coordinatorLayout,
            child,
            target,
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            type,
            consumed
        )

        child.animate().translationY((child.height + child.marginBottom).toFloat())
            .setDuration(FAB_ANIM_HIDE_SHOW_DURATION).start()
    }

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: FloatingActionButton,
        directTargetChild: View,
        target: View,
        nestedScrollAxes: Int
    ): Boolean {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL
    }
}