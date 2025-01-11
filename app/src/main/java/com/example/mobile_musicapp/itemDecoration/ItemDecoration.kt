package com.example.mobile_musicapp.itemDecoration

import android.graphics.Canvas
import androidx.recyclerview.widget.RecyclerView

class FadeEdgeItemDecoration(private val fadeHeight: Int) : RecyclerView.ItemDecoration() {

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val recyclerViewHeight = parent.height

        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            val itemHeight = child.height
            val halfItemHeight = itemHeight / 2

            val itemTop = child.top
            val itemBottom = child.bottom

            val alphaTop = calculateAlpha(itemTop + halfItemHeight, fadeHeight)
            val alphaBottom = calculateAlpha(recyclerViewHeight - (itemBottom - halfItemHeight), fadeHeight)

            val alpha = minOf(alphaTop, alphaBottom)
            child.alpha = alpha
        }
    }

    private fun calculateAlpha(distanceToEdge: Int, fadeHeight: Int): Float {
        return when {
            distanceToEdge <= 0 -> 0f
            distanceToEdge < fadeHeight -> distanceToEdge.toFloat() / fadeHeight // Mờ dần
            else -> 1f
        }
    }
}
