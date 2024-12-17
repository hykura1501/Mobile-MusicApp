package com.example.mobile_musicapp.helpers

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import androidx.core.graphics.ColorUtils
import androidx.palette.graphics.Palette
import com.example.mobile_musicapp.R

object BackgroundHelper {

    fun updateBackgroundWithImageColor(context: Context, songThumbnailUrl: String, targetView: View, cornerRadius: Float = 0f) {
        val imageHelper = ImageHelper()
        imageHelper.downloadImage(songThumbnailUrl) { bitmap ->
            if (bitmap != null) {
                Palette.from(bitmap).generate { palette ->
                    val dominantColor = palette?.getDominantColor(context.getColor(R.color.defaultBackgroundColor)) ?: return@generate
                    applyGradientBackground(dominantColor, targetView, cornerRadius)
                }
            }
        }
    }

    private fun applyGradientBackground(dominantColor: Int, targetView: View, cornerRadius: Float) {
        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(dominantColor, ColorUtils.blendARGB(dominantColor, Color.BLACK, 0.7f))
        )
        gradientDrawable.cornerRadius = cornerRadius
        targetView.background = gradientDrawable
    }
}
