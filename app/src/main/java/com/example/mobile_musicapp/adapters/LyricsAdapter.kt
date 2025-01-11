package com.example.mobile_musicapp.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile_musicapp.R
import com.example.mobile_musicapp.models.LyricLine

class LyricsAdapter(private val lyrics: List<LyricLine>) : RecyclerView.Adapter<LyricsAdapter.LyricViewHolder>() {
    var highlightedPosition = -1

    inner class LyricViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.lyricText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LyricViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_lyric, parent, false)
        return LyricViewHolder(view)
    }

    override fun onBindViewHolder(holder: LyricViewHolder, position: Int) {
        val lyric = lyrics[position]
        holder.textView.text = lyric.text

        holder.textView.setTextColor(
            if (position <= highlightedPosition) Color.GREEN else Color.WHITE
        )
    }

    override fun getItemCount() = lyrics.size
}