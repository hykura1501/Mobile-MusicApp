package com.example.mobile_musicapp.models

import java.util.concurrent.TimeUnit

// Data model for a single line of lyrics with timestamp
data class LyricLine(
    val timestamp: Long,  // milliseconds
    val text: String
)


fun parseLrcContent(lrcContent: String): List<LyricLine> {
    val lyricLines = mutableListOf<LyricLine>()

    lrcContent.split("\n").forEach { line ->
        if (line.isNotBlank() && line.startsWith("[") && line.contains("]")) {
            val timestampEndIndex = line.indexOf("]")
            val timestampStr = line.substring(1, timestampEndIndex)
            val text = line.substring(timestampEndIndex + 1).trim()

            val timestamp = parseTimestampToMillis(timestampStr)
            lyricLines.add(LyricLine(timestamp, text))
        }
    }

    return lyricLines
}

fun parseTimestampToMillis(timestamp: String): Long {
    val parts = timestamp.split(":")
    val minutes = parts[0].toLong()
    val secondsAndMillis = parts[1].split(".")
    val seconds = secondsAndMillis[0].toLong()
    val millis = if (secondsAndMillis.size > 1) secondsAndMillis[1].toLong() else 0

    return TimeUnit.MINUTES.toMillis(minutes) + TimeUnit.SECONDS.toMillis(seconds) + millis
}