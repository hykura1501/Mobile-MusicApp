package com.example.mobile_musicapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile_musicapp.R
import com.example.mobile_musicapp.models.Playlist

class PlaylistAdapter : ListAdapter<Playlist, PlaylistAdapter.ViewHolder>(PlaylistDiffCallback()) {

    var onItemClick: ((Playlist) -> Unit)? = null
    var onItemLongClick: ((Playlist) -> Unit)? = null

    class PlaylistDiffCallback : DiffUtil.ItemCallback<Playlist>() {
        override fun areItemsTheSame(oldItem: Playlist, newItem: Playlist): Boolean {
            return oldItem.playlistId == newItem.playlistId
        }

        override fun areContentsTheSame(oldItem: Playlist, newItem: Playlist): Boolean {
            return oldItem == newItem
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val playlistName: TextView = itemView.findViewById(R.id.playlistNameTextView)
        val playlistImage: ImageView = itemView.findViewById(R.id.playlistImageView)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick?.invoke(getItem(position))
                }
            }

            itemView.setOnLongClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemLongClick?.invoke(getItem(position))
                }
                false
            }
        }
    }

    // Inflate layout item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val contactView = inflater.inflate(R.layout.playlist_item_vertical_layout, parent, false)
        return ViewHolder(contactView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val playlist: Playlist = getItem(position)
        holder.playlistName.text = playlist.title
        holder.playlistImage.setImageResource(R.drawable.song)
    }

    override fun getItemCount(): Int {
        return currentList.size
    }
}
