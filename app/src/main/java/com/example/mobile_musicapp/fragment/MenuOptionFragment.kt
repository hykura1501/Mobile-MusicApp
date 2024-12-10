package com.example.mobile_musicapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile_musicapp.R
import com.example.mobile_musicapp.adapters.OptionAdapter
import com.example.mobile_musicapp.models.Option
import com.example.mobile_musicapp.models.Playlist
import com.example.mobile_musicapp.viewModels.ShareViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MenuOptionFragment : BottomSheetDialogFragment() {

    private lateinit var optionsAdapter: OptionAdapter
    private var options: List<Option> = emptyList()

    companion object {
        fun newInstance(options: List<String>): MenuOptionFragment {
            val fragment = MenuOptionFragment()
            val args = Bundle()
            args.putStringArrayList("OPTIONS", ArrayList(options))
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val receivedOptions = arguments?.getStringArrayList("OPTIONS") ?: emptyList()
        options = receivedOptions.mapNotNull { Option.fromTitle(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_menu_option, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.options_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)

        optionsAdapter = OptionAdapter(options)
        optionsAdapter.onItemClick = { option ->
            handleOptionClick(option)
        }
        recyclerView.adapter = optionsAdapter
    }

    private fun handleOptionClick(option: Option) {
        when (option) {
            // TODO: Handle click events for each option
            Option.ADD_TO_PLAYLIST -> { /* Xử lý thêm vào playlist */ }
            Option.REMOVE_FROM_PLAYLIST -> { /* Xử lý xóa khỏi playlist */ }
            Option.DELETE_PLAYLIST -> { removePlaylist() }
            Option.DOWNLOAD -> { /* Xử lý tải xuống */ }
            Option.SHARE -> { /* Xử lý chia sẻ */ }
        }
        dismiss()
    }

    private fun removePlaylist() {
        var playlist: Playlist? = null
        val sharedViewModel = ViewModelProvider(requireActivity())[ShareViewModel::class.java]
        sharedViewModel.longSelectedPlaylist.observe(viewLifecycleOwner) { longSelectedPlaylist ->
            longSelectedPlaylist?.let {
                playlist = it
                // TODO delete this playlist from database
            }
        }
        sharedViewModel.deletedPlaylist.value = playlist
    }
}