package com.example.mobile_musicapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile_musicapp.R
import com.example.mobile_musicapp.models.Option


class OptionAdapter(private val options : List<Option>) : RecyclerView.Adapter<OptionAdapter.ViewHolder>() {

    var onItemClick: ((Option) -> Unit)? = null

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val optionName: TextView = view.findViewById(R.id.optionName)
        val optionIcon: ImageView = view.findViewById(R.id.optionIcon)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick?.invoke(options[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : OptionAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val contactView = inflater.inflate(R.layout.action_item_vertical_layout, parent, false)
        return ViewHolder(contactView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val option = options[position]
        holder.optionName.text = option.title

        val imageResId = option.iconResId
        holder.optionIcon.setImageResource(imageResId)
    }

    override fun getItemCount(): Int = options.size
}