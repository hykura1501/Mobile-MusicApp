package com.example.mobile_musicapp.helpers

class RandomHelper {
    private fun getRandomNumber(min: Int, max: Int): Int {
        require(min < max) { "Invalid range" }
        return (min..max).random()
    }

    fun getRandomSongIndex(playedSong : MutableList<Int>, size : Int) : Int {
        val list = mutableListOf<Int>()
        for (i in 0..size) {
            if (!playedSong.contains(i)) {
                list.add(i)
            }
        }
        return list[getRandomNumber(0, list.size - 1)]
    }
}