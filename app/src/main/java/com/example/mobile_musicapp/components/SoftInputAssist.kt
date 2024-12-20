package com.example.mobile_musicapp.components

import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout

class SoftInputAssist(activity: Activity) {

    private var rootView: View? = null
    private var contentContainer: ViewGroup? = null
    private var viewTreeObserver: ViewTreeObserver? = null
    private val contentAreaOfWindowBounds = Rect()
    private var rootViewLayout: FrameLayout.LayoutParams? = null
    private var usableHeightPrevious = 0

    private val listener = ViewTreeObserver.OnGlobalLayoutListener {
        possiblyResizeChildOfContent()
    }

    init {
        contentContainer = activity.findViewById(android.R.id.content) as ViewGroup
        rootView = contentContainer?.getChildAt(0)
        rootViewLayout = rootView?.layoutParams as? FrameLayout.LayoutParams
    }

    fun onPause() {
        viewTreeObserver?.takeIf { it.isAlive }?.removeOnGlobalLayoutListener(listener)
    }

    fun onResume() {
        if (viewTreeObserver == null || viewTreeObserver?.isAlive == false) {
            viewTreeObserver = rootView?.viewTreeObserver
        }
        viewTreeObserver?.addOnGlobalLayoutListener(listener)
    }

    fun onDestroy() {
        rootView = null
        contentContainer = null
        viewTreeObserver = null
    }

    private fun possiblyResizeChildOfContent() {
        contentContainer?.getWindowVisibleDisplayFrame(contentAreaOfWindowBounds)
        val usableHeightNow = contentAreaOfWindowBounds.height()

        if (usableHeightNow != usableHeightPrevious) {
            rootViewLayout?.height = usableHeightNow
            rootView?.layout(
                contentAreaOfWindowBounds.left,
                contentAreaOfWindowBounds.top,
                contentAreaOfWindowBounds.right,
                contentAreaOfWindowBounds.bottom
            )
            rootView?.requestLayout()

            usableHeightPrevious = usableHeightNow
        }
    }
}
