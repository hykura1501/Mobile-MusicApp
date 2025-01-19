package com.example.mobile_musicapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.mobile_musicapp.R
import com.example.mobile_musicapp.models.Artist

class ArtistHorizontalAdapter(
    private var artists: List<Artist>,
    private val onArtistClick: (Artist) -> Unit
) : RecyclerView.Adapter<ArtistHorizontalAdapter.ArtistViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_artist_horizontal, parent, false)
        return ArtistViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArtistViewHolder, position: Int) {
        val artist = artists[position]
        holder.bind(artist, onArtistClick)
    }

    override fun getItemCount(): Int = artists.size

    fun updateArtists(newArtists: List<Artist>) {
        artists = newArtists
        notifyDataSetChanged()
    }

    class ArtistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val artistName: TextView = itemView.findViewById(R.id.artistName)
        private val artistAvatar: ImageView = itemView.findViewById(R.id.artistAvatar)

        fun bind(artist: Artist, onArtistClick: (Artist) -> Unit) {
            artistName.text = artist.name
            Glide.with(itemView.context)
                .load(artist.avatar)
                .apply(RequestOptions.circleCropTransform())
                .into(artistAvatar)

            itemView.setOnClickListener {
                onArtistClick(artist)
            }
        }
    }
}