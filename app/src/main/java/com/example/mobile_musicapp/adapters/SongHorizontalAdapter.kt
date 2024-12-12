package com.example.mobile_musicapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile_musicapp.R
import com.example.mobile_musicapp.models.Song
import com.example.mobile_musicapp.helpers.ImageHelper

class SongHorizontalAdapter(
    private val songs: List<Song>,
    private val onItemClick: (Song) -> Unit
) : RecyclerView.Adapter<SongHorizontalAdapter.ViewHolder>() {

    private val imageHelper = ImageHelper()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val songThumbnail: ImageView = itemView.findViewById(R.id.songThumbnail)
        val songName: TextView = itemView.findViewById(R.id.songName)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(songs[position])
                }
            }
        }

        fun bind(song: Song) {
            songName.text = song.title
            imageHelper.loadImage(song.thumbnail, songThumbnail) // Using ImageHelper to load image
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_song_horizontal, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(songs[position])
    }

    override fun getItemCount(): Int = songs.size
}
