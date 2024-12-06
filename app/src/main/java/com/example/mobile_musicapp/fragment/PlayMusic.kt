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
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.example.mobile_musicapp.R
import com.example.mobile_musicapp.services.MockDao
import com.example.mobile_musicapp.singletons.Favorite
import com.example.mobile_musicapp.singletons.Queue
import com.google.android.material.snackbar.Snackbar
import kotlin.text.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PlayMusic.newInstance] factory method to
 * create an instance of this fragment.
 */
@Suppress("RemoveExplicitTypeArguments")
class PlayMusic : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_play_music, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PlayMusic.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PlayMusic().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Kết nối giao diện
        val mockDao = MockDao()
        Queue.songs = mockDao.openPlaylist("1")

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
            mediaPlayer?.setDataSource(song.url)
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
        artist.text = song.artist
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

        seekBar.isEnabled = false
    }

    override fun onDestroy() {
        super.onDestroy()
        // Giải phóng MediaPlayer khi Fragment bị hủy
        mediaPlayer?.release()
        mediaPlayer = null
    }
}