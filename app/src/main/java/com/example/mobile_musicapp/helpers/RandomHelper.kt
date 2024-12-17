package com.example.mobile_musicapp.helpers

import com.example.mobile_musicapp.models.Song

class RandomHelper {
    fun getRandomNumber(min: Int, max: Int): Int {
        return (min..max).random()
    }

    fun getRandomSongs(songs: MutableList<Song>, currentSongIndex: Int): MutableList<Song> {
        val shuffleList = mutableListOf<Song>()

        if (currentSongIndex + 1 < songs.size) {
            shuffleList.addAll(songs.subList(currentSongIndex + 1, songs.size))
        }

        shuffleList.shuffle()

        val resultList = mutableListOf<Song>()
        resultList.addAll(songs.subList(0, currentSongIndex + 1))
        resultList.addAll(shuffleList)

        return resultList
    }
}