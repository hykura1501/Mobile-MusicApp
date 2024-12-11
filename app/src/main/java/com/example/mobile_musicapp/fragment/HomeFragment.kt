package com.example.mobile_musicapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile_musicapp.R
import com.example.mobile_musicapp.adapters.SongAdapter
import com.example.mobile_musicapp.adapters.SongHorizontalAdapter
import com.example.mobile_musicapp.models.Song


class HomeFragment : Fragment() {

    private lateinit var rcvSongData1 : RecyclerView
    private lateinit var rcvSongData2 : RecyclerView
    private lateinit var rcvSongData3 : RecyclerView
    private val listDataSong1  = mutableListOf<Song>()
    private val listDataSong2  = mutableListOf<Song>()
    private val listDataSong3  = mutableListOf<Song>()
//    private val listDataSong1  = mutableListOf(
//        Song("1","Lyricist Who Made…","Lyricist Who Made…", 120, "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQd9Qr9zHhflTHMB2RkIkqyp_Q8oJbr4GVDcVBkJhbQVmWXmDtCSezjjtXY0IO4C6DJDNA&usqp=CAU"),
//        Song("2","Lyricist Who Made 1 …","Lyricist Who Made…", 120, "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQd9Qr9zHhflTHMB2RkIkqyp_Q8oJbr4GVDcVBkJhbQVmWXmDtCSezjjtXY0IO4C6DJDNA&usqp=CAU"),
//        Song("3","Lyricist Who Made 2 …","Lyricist Who Made…", 120, "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQd9Qr9zHhflTHMB2RkIkqyp_Q8oJbr4GVDcVBkJhbQVmWXmDtCSezjjtXY0IO4C6DJDNA&usqp=CAU"),
//        Song("4","Lyricist Who Made 2 …","Lyricist Who Made…", 120, "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQd9Qr9zHhflTHMB2RkIkqyp_Q8oJbr4GVDcVBkJhbQVmWXmDtCSezjjtXY0IO4C6DJDNA&usqp=CAU"),
//    )
//    private val listDataSong2  = mutableListOf<Song>(
//        Song("1","Lyricist Who Made…","Lyricist Who Made…", 120, "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQd9Qr9zHhflTHMB2RkIkqyp_Q8oJbr4GVDcVBkJhbQVmWXmDtCSezjjtXY0IO4C6DJDNA&usqp=CAU"),
//        Song("2","Lyricist Who Made 1 …","Lyricist Who Made…", 120, "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQd9Qr9zHhflTHMB2RkIkqyp_Q8oJbr4GVDcVBkJhbQVmWXmDtCSezjjtXY0IO4C6DJDNA&usqp=CAU"),
//        Song("3","Lyricist Who Made 2 …","Lyricist Who Made…", 120, "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQd9Qr9zHhflTHMB2RkIkqyp_Q8oJbr4GVDcVBkJhbQVmWXmDtCSezjjtXY0IO4C6DJDNA&usqp=CAU"),
//        Song("4","Lyricist Who Made 2 …","Lyricist Who Made…", 120, "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQd9Qr9zHhflTHMB2RkIkqyp_Q8oJbr4GVDcVBkJhbQVmWXmDtCSezjjtXY0IO4C6DJDNA&usqp=CAU"),
//
//        )
//    private val listDataSong3  = mutableListOf<Song>(
//        Song("1","Lyricist Who Made…","Lyricist Who Made…", 120, "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQd9Qr9zHhflTHMB2RkIkqyp_Q8oJbr4GVDcVBkJhbQVmWXmDtCSezjjtXY0IO4C6DJDNA&usqp=CAU"),
//        Song("2","Lyricist Who Made 1 …","Lyricist Who Made…", 120, "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQd9Qr9zHhflTHMB2RkIkqyp_Q8oJbr4GVDcVBkJhbQVmWXmDtCSezjjtXY0IO4C6DJDNA&usqp=CAU"),
//        Song("3","Lyricist Who Made 2 …","Lyricist Who Made…", 120, "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQd9Qr9zHhflTHMB2RkIkqyp_Q8oJbr4GVDcVBkJhbQVmWXmDtCSezjjtXY0IO4C6DJDNA&usqp=CAU"),
//        Song("4","Lyricist Who Made 2 …","Lyricist Who Made…", 120, "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQd9Qr9zHhflTHMB2RkIkqyp_Q8oJbr4GVDcVBkJhbQVmWXmDtCSezjjtXY0IO4C6DJDNA&usqp=CAU"),
//
//        )

    private val adapterSong1 by lazy {
        SongHorizontalAdapter(requireContext(),listDataSong1)
    }
    private val adapterSong2 by lazy {
        SongHorizontalAdapter(requireContext(),listDataSong2)
    }
    private val adapterSong3 by lazy {
        SongHorizontalAdapter(requireContext(),listDataSong3)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        connectUI(view)
        setUpRecyclerview()
    }

    private fun setUpRecyclerview(){
        rcvSongData1.apply {
            adapter = adapterSong1
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            hasFixedSize()
        }

        rcvSongData2.apply {
            adapter = adapterSong2
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            hasFixedSize()
        }
        rcvSongData3.apply {
            adapter = adapterSong3
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            hasFixedSize()
        }

    }

    private fun connectUI(view: View) {
        rcvSongData1 = view.findViewById(R.id.rcv_data_song_1)
        rcvSongData2 = view.findViewById(R.id.rcv_data_song_2)
        rcvSongData3 = view.findViewById(R.id.rcv_data_song_3)
    }
}