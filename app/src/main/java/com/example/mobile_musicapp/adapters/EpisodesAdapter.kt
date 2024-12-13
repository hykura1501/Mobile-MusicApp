package com.example.mobile_musicapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile_musicapp.R
import com.example.mobile_musicapp.models.EpisodesModel
import com.example.mobile_musicapp.models.Song

class EpisodesAdapter (private val songs : List<EpisodesModel>) : RecyclerView.Adapter<EpisodesAdapter.ViewHolder>() {

    var onItemClick: ((EpisodesModel) -> Unit)? = null
    var onOptionClick: ((EpisodesModel) -> Unit)? = null

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val songName: TextView = view.findViewById(R.id.songNameTextView)
        val songThumbnail: ImageView = view.findViewById(R.id.songImageView)
        val songArtist: TextView = view.findViewById(R.id.artistNameTextView)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick?.invoke(songs[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : EpisodesAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val contactView = inflater.inflate(R.layout.item_episodes, parent, false)
        return ViewHolder(contactView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Get the data model based on position
        val song: EpisodesModel = songs[position]
        // Set item views based on your views and data model
        val songNameTextView = holder.songName
        songNameTextView.text = song.title
        val songArtistTextView = holder.songArtist
        songArtistTextView.text = song.artist
        val songThumbnailView = holder.songThumbnail
        val imageResId = R.drawable.song
        songThumbnailView.setImageResource(imageResId)
    }

    override fun getItemCount(): Int = songs.size
}