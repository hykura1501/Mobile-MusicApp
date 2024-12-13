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
import com.example.mobile_musicapp.models.CategoryModel
import com.example.mobile_musicapp.models.Song

class CategoryAdapter constructor(private val context : Context, private val songs : List<CategoryModel>) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    var onItemClick: ((CategoryModel) -> Unit)? = null
    var onOptionClick: ((CategoryModel) -> Unit)? = null

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val categoryName: TextView = view.findViewById(R.id.categoryNameTextView)
        val categoryThumbnail: ImageView = view.findViewById(R.id.categoryImageView)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick?.invoke(songs[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : CategoryAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val contactView = inflater.inflate(R.layout.item_category, parent, false)
        return ViewHolder(contactView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Get the data model based on position
        val song: CategoryModel = songs[position]
        // Set item views based on your views and data model
        val categoryNameTextView = holder.categoryName
        categoryNameTextView.text = song.name
        val categoryThumbnailView = holder.categoryThumbnail
        val imageResId = R.drawable.song

        Glide.with(context).load(song.url).error(imageResId).into(categoryThumbnailView)
//        songThumbnailView.setImageResource(imageResId)
    }

    override fun getItemCount(): Int = songs.size
}