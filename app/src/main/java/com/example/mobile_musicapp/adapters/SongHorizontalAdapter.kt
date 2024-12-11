package com.example.mobile_musicapp.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mobile_musicapp.R
import com.example.mobile_musicapp.models.Song

class SongHorizontalAdapter constructor(private val context : Context, private val songs : List<Song>) : RecyclerView.Adapter<SongHorizontalAdapter.ViewHolder>() {

    var onItemClick: ((Song) -> Unit)? = null
    var onOptionClick: ((Song) -> Unit)? = null

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val songName: TextView = view.findViewById(R.id.songNameTextView)
        val songThumbnail: ImageView = view.findViewById(R.id.songImageView)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick?.invoke(songs[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : SongHorizontalAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val contactView = inflater.inflate(R.layout.song_item_horizontal_layout, parent, false)
        return ViewHolder(contactView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Get the data model based on position
        val song: Song = songs[position]
        // Set item views based on your views and data model
        val songNameTextView = holder.songName
        songNameTextView.text = song.title
        val songThumbnailView = holder.songThumbnail
        val imageResId = R.drawable.song

        Glide.with(context).load(song.url).error(imageResId).into(songThumbnailView)
//        songThumbnailView.setImageResource(imageResId)
    }

    override fun getItemCount(): Int = songs.size
}