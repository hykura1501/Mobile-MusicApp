package com.example.mobile_musicapp.fragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile_musicapp.R
import com.example.mobile_musicapp.adapters.SleepTimerAdapter
import com.example.mobile_musicapp.models.SleepTimerOption
import com.example.mobile_musicapp.services.PlayerManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class ChoosingSleepTimerFragment : BottomSheetDialogFragment() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_choosing_sleep_timer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.sleep_timer_recycler_view)

        val timerOptions = listOf(
            SleepTimerOption.ONE_MINUTE,
            SleepTimerOption.FIVE_MINUTES,
            SleepTimerOption.TEN_MINUTES,
            SleepTimerOption.FIFTEEN_MINUTES,
            SleepTimerOption.THIRTY_MINUTES,
            SleepTimerOption.SIXTY_MINUTES
        )

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = SleepTimerAdapter(timerOptions) { selectedTime ->
            PlayerManager.startSleepCountdown(selectedTime)
            dismiss()
        }
    }
}