package com.example.mobile_musicapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile_musicapp.R
import com.example.mobile_musicapp.models.SleepTimerOption

class SleepTimerAdapter(
    private val timerList: List<SleepTimerOption>,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<SleepTimerAdapter.TimerViewHolder>() {

    class TimerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val timeTextView: TextView = itemView.findViewById(R.id.sleepTimerTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.sleep_timer_layout, parent, false)
        return TimerViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TimerViewHolder, position: Int) {
        val timerOption = timerList[position]
        holder.timeTextView.text = "${timerOption.time} phút"
        holder.itemView.setOnClickListener {
            onItemClick(timerOption.time)
        }
    }

    override fun getItemCount() = timerList.size
}