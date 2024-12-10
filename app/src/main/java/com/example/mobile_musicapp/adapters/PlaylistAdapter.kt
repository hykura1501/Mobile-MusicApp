package com.example.mobile_musicapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile_musicapp.R
import com.example.mobile_musicapp.models.Playlist

class PlaylistAdapter (private val playlists : List<Playlist>) : RecyclerView.Adapter<PlaylistAdapter.ViewHolder>() {
    var onItemClick: ((Playlist) -> Unit)? = null
    var onItemLongClick: ((Playlist) -> Unit)? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val playlistName: TextView = itemView.findViewById(R.id.playlistNameTextView)
        val playlistImage: ImageView = itemView.findViewById(R.id.playlistImageView)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick?.invoke(playlists[position])
                }
            }
        }

        init {
            itemView.setOnLongClickListener {
                onItemLongClick?.invoke(playlists[adapterPosition])
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : PlaylistAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val contactView = inflater.inflate(R.layout.playlist_item_vertical_layout, parent, false)
        return ViewHolder(contactView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Get the data model based on position
        val playlist: Playlist = playlists[position]
        // Set item views based on your views and data model
        val playlistNameTextView = holder.playlistName
        playlistNameTextView.text = playlist.name
        val playlistImageView = holder.playlistImage
        val imageResId = R.drawable.song
        playlistImageView.setImageResource(imageResId)
    }

    override fun getItemCount(): Int = playlists.size
}