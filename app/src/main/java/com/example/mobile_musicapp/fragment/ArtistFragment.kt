package com.example.mobile_musicapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.mobile_musicapp.R
import com.example.mobile_musicapp.models.Artist
import com.example.mobile_musicapp.services.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ArtistFragment : Fragment() {

    private lateinit var artistName: TextView
    private lateinit var artistDescription: TextView
    private lateinit var artistAvatar: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_artist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        artistName = view.findViewById(R.id.artistName)
        artistDescription = view.findViewById(R.id.artistDescription)
        artistAvatar = view.findViewById(R.id.artistAvatar)
        val backButton: ImageButton = view.findViewById(R.id.backButton)

        backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        val artistId = arguments?.getString("artistId") ?: return
        fetchArtistDetails(artistId)
    }

    private fun fetchArtistDetails(artistId: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://backend-mobile-xi.vercel.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.getArtistDetails(artistId)
                if (response.isSuccessful && response.body()?.code == 200) {
                    withContext(Dispatchers.Main) {
                        response.body()?.data?.artist?.let { displayArtistInfo(it) }
                    }
                } else {
                    // Handle error
                }
            } catch (e: Exception) {
                // Handle exception
            }
        }
    }

    private fun displayArtistInfo(artist: Artist) {
        artistName.text = artist.name
        artistDescription.text = artist.description
        Glide.with(this).load(artist.avatar).into(artistAvatar)
    }
}