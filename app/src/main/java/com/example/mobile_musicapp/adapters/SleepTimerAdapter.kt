package com.example.mobile_musicapp.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile_musicapp.R
import com.example.mobile_musicapp.models.SleepTimerOption

class SleepTimerAdapter(
    private val timerList: List<SleepTimerOption>,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<SleepTimerAdapter.TimerViewHolder>() {

    private var selectedPosition = RecyclerView.NO_POSITION

    inner class TimerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val timeTextView: TextView = itemView.findViewById(R.id.sleepTimerTextView)

        @SuppressLint("NotifyDataSetChanged")
        fun bind(timerOption: SleepTimerOption, position: Int) {
            timeTextView.text = timerOption.title

            timeTextView.setTextColor(
                if (position == selectedPosition) {
                    ContextCompat.getColor(itemView.context, R.color.green)
                }
                else {
                    ContextCompat.getColor(itemView.context, R.color.white)
                }
            )

            itemView.setOnClickListener {
                selectedPosition = position
                notifyItemChanged(selectedPosition)
                onItemClick(timerOption.time)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.sleep_timer_layout, parent, false)
        return TimerViewHolder(view)
    }

    override fun onBindViewHolder(holder: TimerViewHolder, position: Int) {
        val timerOption = timerList[position]
        holder.bind(timerOption, position)
    }

    override fun getItemCount() = timerList.size
}
