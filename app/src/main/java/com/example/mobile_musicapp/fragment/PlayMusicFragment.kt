package com.example.mobile_musicapp.fragment
import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.mobile_musicapp.R
import com.example.mobile_musicapp.services.MockDao
import com.example.mobile_musicapp.singletons.Favorite
import com.example.mobile_musicapp.singletons.Queue
import com.google.android.material.snackbar.Snackbar
import kotlin.text.*

@Suppress("RemoveExplicitTypeArguments")
class PlayMusicFragment : Fragment() {

    private lateinit var playButton: ImageButton
    private lateinit var nextButton: ImageButton
    private lateinit var previousButton: ImageButton
    private lateinit var addToFavoritesButton: ImageButton
    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying = false
    private lateinit var seekBar: SeekBar
    private lateinit var currentTime: TextView
    private lateinit var totalTime: TextView
    private lateinit var artist: TextView
    private lateinit var songName: TextView
    private lateinit var songThumbnail: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_play_music, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Connect UI components
        val mockDao = MockDao()
        Queue.openPlaylist(mockDao.getSamplePlaylist())

        connectUI(view)
        prepareMusic()

        // Xử lý khi nhấn nút Play/Pause
        playButton.setOnClickListener {
            isPlaying = !isPlaying
            if (isPlaying) {
                playMusic()
            } else {
                pauseMusic()
            }
        }

        nextButton.setOnClickListener {
            nextSong()
        }

        previousButton.setOnClickListener {
            previousSong()
        }

        addToFavoritesButton.setOnClickListener {
            val song = Queue.getCurrentSong()!!
            Favorite.addToFavorites(song)

            // Hiển thị thông báo
            Snackbar.make(view, "Added to Favorites!", Snackbar.LENGTH_SHORT)
                .setAction("UNDO") {
                    Favorite.removeFromFavorites(song)
                    Toast.makeText(context, "Removed from Favorites!", Toast.LENGTH_SHORT).show()
                }
                .show()
        }
    }

    private fun prepareMusic() {
        val song = Queue.getCurrentSong()!!
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer()
        }
        playButton.setImageResource(R.drawable.ic_pause_black)
        isPlaying = true

        try {
            mediaPlayer?.reset()
            mediaPlayer?.setDataSource(song.path)
            mediaPlayer?.prepareAsync()

            mediaPlayer?.setOnPreparedListener {
                it.start()
                updateUI()
                updateSeekBarAndTime()
            }

            mediaPlayer?.setOnCompletionListener {
                nextSong()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun playMusic() {
        mediaPlayer?.start()
        playButton.setImageResource(R.drawable.ic_pause_black)

        // Thiết lập SeekBar max với duration (tổng thời gian bài hát)
        updateSeekBarAndTime()
    }

    private fun pauseMusic() {
        mediaPlayer?.pause()
        playButton.setImageResource(R.drawable.ic_play_black)
    }

    private fun nextSong() {
        pauseMusic()
        mediaPlayer?.reset()
        Queue.nextSong()
        prepareMusic()
    }

    private fun previousSong() {
        pauseMusic()
        mediaPlayer?.reset()
        Queue.previousSong()
        prepareMusic()
    }

    @SuppressLint("DefaultLocale")
    private fun updateUI() {
        val song = Queue.getCurrentSong()!!
        // TODO update thumbnail
        Glide.with(this)
            .load(song.thumbnail)
            .placeholder(R.drawable.song)
            .error(R.drawable.song)
            .into(songThumbnail)

        artist.text = song.artistName
        songName.text = song.title

        mediaPlayer?.let {
            seekBar.max = it.duration
            val minutes = (it.duration / 1000) / 60
            val seconds = (it.duration / 1000) % 60
            val time = String.format("%02d:%02d", minutes, seconds)
            totalTime.text = time
        }
    }

    private fun updateSeekBarAndTime() {
        val handler = Handler(Looper.getMainLooper())
        handler.post(object : Runnable {
            @SuppressLint("DefaultLocale")
            override fun run() {
                mediaPlayer?.let {
                    // Cập nhật SeekBar
                    seekBar.progress = it.currentPosition

                    // Cập nhật thời gian hiện tại
                    val minutes = (it.currentPosition / 1000) / 60
                    val seconds = (it.currentPosition / 1000) % 60
                    val time = String.format("%02d:%02d", minutes, seconds)
                    currentTime.text = time

                    // Lặp lại mỗi giây nếu vẫn đang phát
                    if (isPlaying) {
                        handler.postDelayed(this, 1000)
                    }
                }
            }
        })
    }

    private fun connectUI(view: View) {
        playButton = view.findViewById<ImageButton>(R.id.playButton) as ImageButton
        seekBar = view.findViewById<SeekBar>(R.id.seekBar) as SeekBar
        currentTime = view.findViewById<TextView>(R.id.currentTime) as TextView
        totalTime = view.findViewById<TextView>(R.id.totalTime) as TextView
        nextButton = view.findViewById<ImageButton>(R.id.nextButton) as ImageButton
        previousButton = view.findViewById<ImageButton>(R.id.previousButton) as ImageButton
        addToFavoritesButton = view.findViewById<ImageButton>(R.id.addToFavoritesButton) as ImageButton
        artist = view.findViewById<TextView>(R.id.artist) as TextView
        songName = view.findViewById<TextView>(R.id.songName) as TextView
        songThumbnail = view.findViewById<ImageView>(R.id.songThumbnail) as ImageView

        seekBar.isEnabled = false
    }

    override fun onDestroy() {
        super.onDestroy()
        // Release MediaPlayer when Fragment was destroyed
        mediaPlayer?.release()
        mediaPlayer = null
    }
}