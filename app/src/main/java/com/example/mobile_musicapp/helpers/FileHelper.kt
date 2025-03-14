package com.example.mobile_musicapp.helpers

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import java.io.File
import java.io.FileOutputStream


class FileHelper {

    companion object {

        fun getFileFromUri(uri: Uri, contentResolver: ContentResolver, context: Context): File? {
            Log.d("UploadSong", "Trying to get file from Uri: $uri")

            if (uri.scheme == "file") {
                Log.d("UploadSong", "File path: ${uri.path}")
                return File(uri.path!!)
            }

            // Nếu URI là kiểu "content", từ MediaStore
            if (uri.scheme == "content") {
                // Lấy tên file từ URI
                val fileName = getFileNameFromUri(uri, contentResolver)
                if (fileName == null) {
                    Log.e("UploadSong", "Failed to get file name from URI: $uri")
                    return null
                }

                // Lấy nội dung từ MediaStore
                val cursor = contentResolver.query(uri, null, null, null, null)
                cursor?.use {
                    if (it.moveToFirst()) {
                        val columnIndex = it.getColumnIndex(MediaStore.MediaColumns.DATA)
                        if (columnIndex != -1) {
                            val filePath = it.getString(columnIndex)
                            Log.d("UploadSong", "File path from MediaStore: $filePath")
                            return File(filePath)
                        }
                    }
                }

                // Nếu không tìm thấy tệp, tạo tệp tạm từ InputStream với tên file gốc
                contentResolver.openInputStream(uri)?.use { inputStream ->
                    // Đảm bảo rằng bạn gọi context được truyền vào để lấy cacheDir
                    val tempFile = File(context.cacheDir, fileName)

                    // Sử dụng FileOutputStream đúng cách
                    FileOutputStream(tempFile).use { outputStream ->
                        inputStream.copyTo(outputStream)
                        Log.d("UploadSong", "Temp file created: ${tempFile.absolutePath}")
                    }

                    return tempFile
                }
            }

            Log.e("UploadSong", "Failed to get file for uri: $uri")
            return null
        }

        fun getFileNameFromUri(uri: Uri, contentResolver: ContentResolver): String? {
            val cursor = contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val columnIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    return it.getString(columnIndex)
                }
            }
            return null // Return null if no name is found
        }

    }
}