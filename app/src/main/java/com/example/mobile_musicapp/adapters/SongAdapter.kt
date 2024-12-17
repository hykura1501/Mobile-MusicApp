package com.example.mobile_musicapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mobile_musicapp.R
import com.example.mobile_musicapp.models.Song


class SongAdapter(private val songs : List<Song>) : RecyclerView.Adapter<SongAdapter.ViewHolder>() {

    private val songsList = mutableListOf<Song>()
    var onItemClick: ((Song) -> Unit)? = null
    var onOptionClick: ((Song) -> Unit)? = null

    init {
        songsList.clear()
        songsList.addAll(songs)
    }
    @SuppressLint("NotifyDataSetChanged")
    fun submitList(data : List<Song>) {
        songsList.clear()
        songsList.addAll(data)
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val songName: TextView = view.findViewById(R.id.songNameTextView)
        val songThumbnail: ImageView = view.findViewById(R.id.songImageView)
        val songArtist: TextView = view.findViewById(R.id.artistNameTextView)
        private val optionsButton: ImageView = view.findViewById(R.id.optionsButton)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick?.invoke(songsList[position])
                }
            }
            optionsButton.setOnClickListener {
                onOptionClick?.invoke(songsList[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : SongAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val contactView = inflater.inflate(R.layout.song_item_vertical_layout, parent, false)
        return ViewHolder(contactView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Get the data model based on position
        val song: Song = songsList[position]
        // Set item views based on your views and data model
        val songTitle = holder.songName
        songTitle.text = song.title
        val songArtist = holder.songArtist
        songArtist.text = song.artistName

        Glide.with(holder.itemView.context)
            .load(song.thumbnail)
            .error(R.drawable.song)
            .into(holder.songThumbnail)
    }

    override fun getItemCount(): Int = songs.size
}