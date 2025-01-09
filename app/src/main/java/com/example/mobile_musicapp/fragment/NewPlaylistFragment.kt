package com.example.mobile_musicapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mobile_musicapp.R
import com.example.mobile_musicapp.services.PlaylistDao
import com.example.mobile_musicapp.viewModels.ShareViewModel
import kotlinx.coroutines.launch

@Suppress("RemoveExplicitTypeArguments")
class NewPlaylistFragment : Fragment() {

    private lateinit var cancelButton : Button
    private lateinit var createButton : Button
    private lateinit var playlistTitle : EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_new_playlist, container, false)
        cancelButton = view.findViewById<Button>(R.id.cancelButton) as Button
        createButton = view.findViewById<Button>(R.id.createButton) as Button
        playlistTitle = view.findViewById<EditText>(R.id.playlistTitle) as EditText
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cancelButton.setOnClickListener{
            findNavController().navigateUp()
        }

        createButton.setOnClickListener{
            val playlistTitle = playlistTitle.text.toString()
            val sharedViewModel = ViewModelProvider(requireActivity())[ShareViewModel::class.java]
            sharedViewModel.addPlaylist(playlistTitle)
            findNavController().navigateUp()
        }
    }
}