package com.example.mobile_musicapp.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mobile_musicapp.R
import com.example.mobile_musicapp.models.Song

class SongPagingAdapter( private val onClick: (Song) -> Unit, private val onOptionClick: (Song) -> Unit) : PagingDataAdapter<Song, SongPagingAdapter.ItemViewHolder>(ItemComparator) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.song_item_vertical_layout, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item, onClick, onOptionClick)
        }
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val TAG = "ItemPagingAdapter"
        fun bind(item: Song, onClick : (Song) -> Unit, onOptionClick: (Song) -> Unit) {
            Log.d(TAG, "bind: =====> data = $item")
            // Bind item to UI components
            itemView.findViewById<TextView>(R.id.songNameTextView).text = item.title
            itemView.findViewById<TextView>(R.id.artistNameTextView).text = item.artistName
            itemView.findViewById<ConstraintLayout>(R.id.layout_main).setOnClickListener {
                onClick(item)
            }
            itemView.findViewById<ImageView>(R.id.optionsButton).setOnClickListener {
                onOptionClick(item)
            }
            val songImageView = itemView.findViewById<ImageView>(R.id.songImageView)
            if (item.thumbnail.isNotEmpty()) {
                Glide.with(itemView.context)
                    .load(item.thumbnail)
                    .error(R.drawable.song) // Default image in case of error
                    .into(songImageView)
            } else {
                // Set default image when thumbnail is null or empty
                songImageView.setImageResource(R.drawable.song)
            }
            Log.d(TAG, "bind: ===> $item")
        }
    }

    object ItemComparator : DiffUtil.ItemCallback<Song>() {
        override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem._id == newItem._id
        }

        override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem == newItem
        }
    }
}
