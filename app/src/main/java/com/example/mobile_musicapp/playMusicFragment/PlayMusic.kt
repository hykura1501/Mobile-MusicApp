package com.example.mobile_musicapp.playMusicFragment
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
import com.example.mobile_musicapp.R
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
    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying = false
    private lateinit var seekBar: SeekBar
    private lateinit var currentTime: TextView
    private lateinit var totalTime: TextView

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

    @SuppressLint("DefaultLocale")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Kết nối giao diện
        playButton = view.findViewById<ImageButton>(R.id.playButton) as ImageButton
        seekBar = view.findViewById<SeekBar>(R.id.seekBar) as SeekBar
        currentTime = view.findViewById<TextView>(R.id.currentTime) as TextView
        totalTime = view.findViewById<TextView>(R.id.totalTime) as TextView

        // Khởi tạo MediaPlayer với file nhạc trong res/raw
        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.forget_me_now_7085475)
        seekBar.isEnabled = false // chặn người dùng thay đổi thời gian

        // Cập nhật SeekBar theo thời gian phát nhạc
        mediaPlayer?.setOnPreparedListener {
            seekBar.max = it.duration
            val minutes = (it.duration / 1000) / 60
            val seconds = (it.duration / 1000) % 60
            val time = String.format("%02d:%02d", minutes, seconds)
            totalTime.text = time
        }

        mediaPlayer?.setOnCompletionListener {
            isPlaying = false
        }

        // Xử lý khi nhấn nút Play/Pause
        playButton.setOnClickListener {
            if (isPlaying) {
                pauseMusic()
            } else {
                playMusic()
            }
        }

        // Cập nhật SeekBar theo thời gian
        val handler = Handler(Looper.getMainLooper())
        handler.post(object : Runnable {
            @SuppressLint("DefaultLocale")
            override fun run() {
                mediaPlayer?.let {
                    seekBar.progress = it.currentPosition

                    val minutes = (it.currentPosition / 1000) / 60
                    val seconds = (it.currentPosition / 1000) % 60
                    val time = String.format("%02d:%02d", minutes, seconds)

                    // Cập nhật TextView với thời gian
                    currentTime.text = time

                    handler.postDelayed(this, 1000)
                }
            }
        })
    }

    private fun playMusic() {
        mediaPlayer?.start()
        playButton.setImageResource(R.drawable.ic_pause_black)
        isPlaying = true
    }

    private fun pauseMusic() {
        mediaPlayer?.pause()
        playButton.setImageResource(R.drawable.ic_play_black)
        isPlaying = false
    }

    override fun onDestroy() {
        super.onDestroy()
        // Giải phóng MediaPlayer khi Fragment bị hủy
        mediaPlayer?.release()
        mediaPlayer = null
    }
}