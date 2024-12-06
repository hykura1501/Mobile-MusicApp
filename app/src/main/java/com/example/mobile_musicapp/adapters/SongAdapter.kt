package com.example.mobile_musicapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile_musicapp.R
import com.example.mobile_musicapp.models.Song


class SongAdapter(private val songs : List<Song>) : RecyclerView.Adapter<SongAdapter.ViewHolder>() {

    var onItemClick: ((Song) -> Unit)? = null

    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        val songNameTextView: TextView = listItemView.findViewById(R.id.songNameTextView)
        val songImageView: ImageView = listItemView.findViewById(R.id.songImageView)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick?.invoke(songs[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : SongAdapter.ViewHolder {
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
        val songNameTextView = holder.songNameTextView
        songNameTextView.text = song.title
        val songImageView = holder.songImageView
        val imageResId = R.drawable.song
        songImageView.setImageResource(imageResId)
    }

    override fun getItemCount(): Int = songs.size
}