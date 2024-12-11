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
import com.example.mobile_musicapp.services.MockDao
import com.example.mobile_musicapp.services.PlaylistDao
import com.example.mobile_musicapp.viewModels.ShareViewModel
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


@Suppress("RemoveExplicitTypeArguments")
class NewPlaylist : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var cancelButton : Button
    private lateinit var createButton : Button
    private lateinit var playlistTitle : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_playlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        connectUI(view)

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

    private fun connectUI(view: View) {
        cancelButton = view.findViewById<Button>(R.id.cancelButton) as Button
        createButton = view.findViewById<Button>(R.id.createButton) as Button
        playlistTitle = view.findViewById<EditText>(R.id.playlistTitle) as EditText
    }
}