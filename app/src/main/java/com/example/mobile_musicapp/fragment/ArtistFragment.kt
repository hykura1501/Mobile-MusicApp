package com.example.mobile_musicapp.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mobile_musicapp.R
import com.example.mobile_musicapp.adapters.ArtistSongAdapter
import com.example.mobile_musicapp.models.Artist
import com.example.mobile_musicapp.models.Song
import com.example.mobile_musicapp.models.SongListWithIndex
import com.example.mobile_musicapp.services.ApiService
import com.example.mobile_musicapp.services.RetrofitClient
import com.example.mobile_musicapp.viewModels.PlayerBarViewModel
import com.example.mobile_musicapp.viewModels.ShareViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ArtistFragment : Fragment() {

    private lateinit var artistName: TextView
    private lateinit var artistDescription: TextView
    private lateinit var artistAvatar: ImageView
    private lateinit var songsRecyclerView: RecyclerView
    private lateinit var followButton: ImageButton
    private lateinit var playerBarViewModel: PlayerBarViewModel
    private lateinit var shareViewModel: ShareViewModel
    private lateinit var apiService: ApiService
    private lateinit var artist: Artist
    private var isFollowed: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_artist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        artistName = view.findViewById(R.id.artistName)
        artistDescription = view.findViewById(R.id.artistDescription)
        artistAvatar = view.findViewById(R.id.artistAvatar)
        songsRecyclerView = view.findViewById(R.id.songsRecyclerView)
        followButton = view.findViewById(R.id.followButton)
        val backButton: ImageButton = view.findViewById(R.id.backButton)

        backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        playerBarViewModel = ViewModelProvider(requireActivity())[PlayerBarViewModel::class.java]
        shareViewModel = ViewModelProvider(requireActivity())[ShareViewModel::class.java]

        songsRecyclerView.layoutManager = LinearLayoutManager(context)

        val artistId = arguments?.getString("artistId") ?: return
        fetchArtistDetails(artistId)

        followButton.setOnClickListener {
            if (isFollowed) {
                unfollowArtist(artist._id)
            } else {
                followArtist(artist._id)
            }
        }
    }

    private fun fetchArtistDetails(artistId: String) {
        apiService = RetrofitClient.instance

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.getArtistDetails(artistId)
                if (response.isSuccessful && response.body()?.code == 200) {
                    withContext(Dispatchers.Main) {
                        response.body()?.data?.let { data ->
                            artist = data.artist
                            checkFollowStatus(artist._id)
                            displayArtistInfo(artist)
                            setupRecyclerView(data.songs)
                        }
                    }
                } else {
                    Log.e("ArtistFragment", "Error fetching artist details: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("ArtistFragment", "Exception fetching artist details", e)
            }
        }
    }

    private fun checkFollowStatus(artistId: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.followArtist(artistId)
                withContext(Dispatchers.Main) {
                    Log.d("ArtistFragment", "Check follow status response: ${response.body()}")
                    if (response.body() == null || (response.body()?.code == 400 && response.body()?.message == "Already followed")) {
                        isFollowed = true
                    } else {
                        isFollowed = false
                        unfollowArtist(artistId)
                    }
                    updateFollowButton()
                }
            } catch (e: Exception) {
                Log.e("ArtistFragment", "Exception checking follow status", e)
            }
        }
    }

    private fun displayArtistInfo(artist: Artist) {
        artistName.text = artist.name
        artistDescription.text = artist.description
        Glide.with(this).load(artist.avatar).into(artistAvatar)
    }

    private fun setupRecyclerView(songs: List<Song>) {
        val adapter = ArtistSongAdapter(songs)
        songsRecyclerView.adapter = adapter

        adapter.onItemClick = { song ->
            val selectedIndex = songs.indexOf(song)
            val action = ArtistFragmentDirections.actionArtistFragmentToPlayMusicFragment(SongListWithIndex(songs, selectedIndex))
            findNavController().navigate(action)
        }

        adapter.onOptionClick = { selectedItem ->
            shareViewModel.selectedSong.value = selectedItem
        }
    }

    private fun followArtist(artistId: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.followArtist(artistId)
                if (response.isSuccessful && response.body()?.code == 200) {
                    withContext(Dispatchers.Main) {
                        Log.d("ArtistFragment", "Followed artist successfully")
                        isFollowed = true
                        updateFollowButton()
                    }
                } else {
                    Log.e("ArtistFragment", "Error following artist: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("ArtistFragment", "Exception following artist", e)
            }
        }
    }

    private fun unfollowArtist(artistId: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.unfollowArtist(artistId)
                if (response.isSuccessful && response.body()?.code == 200) {
                    withContext(Dispatchers.Main) {
                        Log.d("ArtistFragment", "Unfollowed artist successfully")
                        isFollowed = false
                        updateFollowButton()
                    }
                } else {
                    Log.e("ArtistFragment", "Error unfollowing artist: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("ArtistFragment", "Exception unfollowing artist", e)
            }
        }
    }

    private fun updateFollowButton() {
        if (isFollowed) {
            followButton.setImageResource(R.drawable.ic_heart_filled)
        } else {
            followButton.setImageResource(R.drawable.ic_heart)
        }
    }
}