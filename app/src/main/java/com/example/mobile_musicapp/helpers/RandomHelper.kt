package com.example.mobile_musicapp.helpers

import com.example.mobile_musicapp.models.Song

class RandomHelper {
    fun getRandomNumber(min: Int, max: Int): Int {
        require(min < max) { "Invalid range" }
        return (min..max).random()
    }

    fun getRandomSong()  {

    }
}