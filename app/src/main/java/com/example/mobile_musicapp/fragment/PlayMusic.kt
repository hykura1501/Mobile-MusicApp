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
import com.example.mobile_musicapp.R
import com.example.mobile_musicapp.models.SongModel
import com.example.mobile_musicapp.services.CurrentPlaylist
import com.example.mobile_musicapp.services.MockDao
import com.example.mobile_musicapp.services.IDao
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
    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying = false
    private lateinit var seekBar: SeekBar
    private lateinit var currentTime: TextView
    private lateinit var totalTime: TextView
    private lateinit var song: SongModel

    private var dao : IDao = MockDao()

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
        playButton = view.findViewById<ImageButton>(R.id.playButton) as ImageButton
        seekBar = view.findViewById<SeekBar>(R.id.seekBar) as SeekBar
        currentTime = view.findViewById<TextView>(R.id.currentTime) as TextView
        totalTime = view.findViewById<TextView>(R.id.totalTime) as TextView
        nextButton = view.findViewById<ImageButton>(R.id.nextButton) as ImageButton
        previousButton = view.findViewById<ImageButton>(R.id.previousButton) as ImageButton

        // Khởi tạo MediaPlayer với file nhạc trong res/raw
        song = SongModel(id = "1", title = "Forget Me Now", artist = "Fishy, Trí Dũng", duration = 210, url = "http://example.com/song1")
        dao.openSong(song)
        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.forget_me_now)
        seekBar.isEnabled = false // chặn người dùng thay đổi thời gian

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

        nextButton.setOnClickListener {
            pauseMusic()
            mediaPlayer?.reset()
            CurrentPlaylist.nextSong()
            dao.openSong(song)
            //mediaPlayer = MediaPlayer.create(requireContext(), R.raw.forget_me_now)

        }

        previousButton.setOnClickListener {
            pauseMusic()
            mediaPlayer?.reset()
            CurrentPlaylist.previousSong()
            dao.openSong(song)
            //mediaPlayer = MediaPlayer.create(requireContext(), R.raw.forget_me_now)
        }
    }

    private fun prepareMusic(song : SongModel) {
        //val filename = dao.openSong(song)
        //val songFile = File(context.cacheDir, fileName)

//        if (songFile.exists()) {
//            mediaPlayer?.reset()
//            mediaPlayer?.setDataSource(songFile.absolutePath)
//            mediaPlayer?.prepare()
//        }
    }

    @SuppressLint("DefaultLocale")
    private fun playMusic() {
        mediaPlayer?.start()
        playButton.setImageResource(R.drawable.ic_pause_black)
        isPlaying = true

        // Thiết lập SeekBar max với duration (tổng thời gian bài hát)
        mediaPlayer?.let {
            seekBar.max = it.duration
            val minutes = (it.duration / 1000) / 60
            val seconds = (it.duration / 1000) % 60
            val time = String.format("%02d:%02d", minutes, seconds)
            totalTime.text = time
        }

        // Cập nhật SeekBar theo thời gian phát nhạc
        val handler = Handler(Looper.getMainLooper())
        handler.post(object : Runnable {
            @SuppressLint("DefaultLocale")
            override fun run() {
                mediaPlayer?.let {
                    // Cập nhật tiến trình SeekBar
                    seekBar.progress = it.currentPosition

                    // Cập nhật thời gian hiện tại
                    val minutes = (it.currentPosition / 1000) / 60
                    val seconds = (it.currentPosition / 1000) % 60
                    val time = String.format("%02d:%02d", minutes, seconds)
                    currentTime.text = time

                    // Lặp lại mỗi giây
                    if (isPlaying) {
                        handler.postDelayed(this, 1000)
                    }
                }
            }
        })
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