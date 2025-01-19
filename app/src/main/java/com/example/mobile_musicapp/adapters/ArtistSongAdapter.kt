package com.example.mobile_musicapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mobile_musicapp.R
import com.example.mobile_musicapp.models.Song

class ArtistSongAdapter(private val songs: List<Song>) : RecyclerView.Adapter<ArtistSongAdapter.SongViewHolder>() {

    var onItemClick: ((Song) -> Unit)? = null
    var onOptionClick: ((Song) -> Unit)? = null

    inner class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val songTitle: TextView = itemView.findViewById(R.id.songTitle)
        val songThumbnail: ImageView = itemView.findViewById(R.id.songThumbnail)

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(songs[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_artist_song, parent, false)
        return SongViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songs[position]
        holder.songTitle.text = song.title
        Glide.with(holder.itemView.context).load(song.thumbnail).into(holder.songThumbnail)
    }

    override fun getItemCount(): Int = songs.size
}