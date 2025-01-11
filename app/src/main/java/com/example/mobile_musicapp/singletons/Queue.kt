package com.example.mobile_musicapp.singletons

import com.example.mobile_musicapp.helpers.RandomHelper
import com.example.mobile_musicapp.models.Playlist
import com.example.mobile_musicapp.models.Song
import com.example.mobile_musicapp.services.PlayerManager
import com.example.mobile_musicapp.services.SongDao
import com.example.mobile_musicapp.viewModels.PlayerBarViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

val RandomHelper = RandomHelper()

// This object uses to manage the list of songs have been played, current song and the song being played
object Queue {
    private var songs : MutableList<Song> = mutableListOf()
    private var originalSongs : MutableList<Song> = mutableListOf()
    private var currentSongIndex = 0
    private var viewModel = PlayerBarViewModel()
    private var item = 0
    private const val perPage = 10
    private var page = 1
    private var nextSongs : MutableList<Song> = mutableListOf()

    fun initialize(viewModel: PlayerBarViewModel) {
        this.viewModel = viewModel
        CoroutineScope(Dispatchers.IO).launch {
            getNextSongs()
        }
    }

    fun addSong(song: Song) {
        if (!songs.contains(song)) {
            songs.add(song)
            if (viewModel.shuffleMode.value == true) {
                originalSongs.add(song)
            }
        }
    }

    fun removeSong(song: Song) {
        if (songs.contains(song)) {
            songs.remove(song)
        }

        if (originalSongs.contains(song)) {
            originalSongs.remove(song)
        }
    }

    fun getSongs(): MutableList<Song> {
        return songs
    }

    fun getCurrentSong(): Song? {
        if (songs.isNotEmpty() && currentSongIndex < songs.size) {
            return songs[currentSongIndex]
        }
        return null
    }

    fun toggleShuffleMode() {
        if (viewModel.shuffleMode.value == true) {
            // Get shuffled songs and assign to songs
            val shuffledSongs = RandomHelper.getRandomSongs(songs, currentSongIndex)
            songs = shuffledSongs
        }
        else {
            // Get original songs and assign to songs
            songs = originalSongs
        }
    }

    fun isExisted(song: Song): Boolean {
        return songs.contains(song)
    }

    // Get 10 songs from database for the next songs of queue
    private suspend fun getNextSongs() {
        nextSongs = SongDao.getPopularSongs(page, perPage).toMutableList()
        page += 1
        item = 0
    }

    // Get next song from nextSongs
    private fun getNextSong() : Song? {
        while (item < nextSongs.size) {
            if (isExisted(nextSongs[item])) {
                item += 1
                continue
            }
            break
        }
        if (item == nextSongs.size) {
            return null
        }
        return nextSongs[item]
    }

    fun nextSong() {
        if (songs.isNotEmpty()) {
            currentSongIndex += 1

            if (currentSongIndex >= songs.size) { // This song is the last song of the queue, get the next song
                CoroutineScope(Dispatchers.IO).launch {
                    var nextSong : Song?
                    var attempt = 0
                    val maxAttempts = 10

                    do {
                        nextSong = getNextSong()
                        if (nextSong == null) {
                            getNextSongs()
                        }
                        attempt += 1
                    } while (nextSong == null && attempt < maxAttempts)

                    if (nextSong != null) {
                        withContext(Dispatchers.Main) {
                            addSong(nextSong)
                            if (viewModel.waiting.value == true) {
                                viewModel.updateSong(getCurrentSong()!!)
                                PlayerManager.prepare()
                            }
                        }
                    }
                    else {
                        return@launch
                    }
                }
            }
        }
    }

    fun previousSong() {
        if (songs.isNotEmpty()) {
            currentSongIndex -= 1
            if (currentSongIndex < 0) { // This song is the first song of the queue, restart this song
                currentSongIndex = 0
            }
        }
    }

    private fun clearQueue() {
        originalSongs.clear()
        songs.clear()
        currentSongIndex = 0
    }

    fun openPlaylist(playlist: MutableList<Song>, index: Int) {
        clearQueue()
        currentSongIndex = index
        originalSongs.addAll(playlist)

        if (viewModel.shuffleMode.value == true) {
            val shuffledSongs = RandomHelper.getRandomSongs(playlist, index)
            songs = shuffledSongs
        }
        else{
            songs = originalSongs
        }
    }

    fun openSong(song: Song) {
        if (songs.contains(song)) {
            currentSongIndex = songs.indexOf(song)
        }
    }

    fun openPlaylist(playlist: Playlist) {
        clearQueue()
        originalSongs.addAll(playlist.songs)

        if (viewModel.shuffleMode.value == true) {
            val shuffledSongs = RandomHelper.getRandomSongs(playlist.songs, 0)
            songs = shuffledSongs
        }
        else{
            songs = originalSongs
        }
    }
}