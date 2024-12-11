package com.example.mobile_musicapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile_musicapp.adapters.SongHorizontalAdapter
import com.example.mobile_musicapp.services.SongDao
import com.example.mobile_musicapp.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {

    private lateinit var newReleaseSongsRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        newReleaseSongsRecyclerView = view.findViewById(R.id.newReleaseSongsRecyclerView)

        setupRecyclerView()
        loadNewReleaseSongs(1, 25) // Example: Fetch the first page with 5 items per page
    }

    private fun setupRecyclerView() {
        newReleaseSongsRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun loadNewReleaseSongs(page: Int, perPage: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            val newReleaseSongs = withContext(Dispatchers.IO) {
                SongDao.getNewReleaseSongs(page, perPage)
            }
            newReleaseSongsRecyclerView.adapter = SongHorizontalAdapter(newReleaseSongs)
        }
    }
}
