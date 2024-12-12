package com.example.mobile_musicapp.helpers

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class ImageHelper {
    fun loadImage(url: String, imageView: ImageView) {
        CoroutineScope(Dispatchers.IO).launch {
            val bitmap = downloadImage(url)
            withContext(Dispatchers.Main) {
                imageView.setImageBitmap(bitmap)
            }
        }
    }

    fun downloadImage(url: String): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input: InputStream = connection.inputStream
            bitmap = BitmapFactory.decodeStream(input)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return bitmap
    }
}