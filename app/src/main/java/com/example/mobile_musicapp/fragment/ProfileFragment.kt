package com.example.mobile_musicapp.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.mobile_musicapp.LanguageChangeActivity
import com.example.mobile_musicapp.R
import com.example.mobile_musicapp.databinding.FragmentProfileBinding
import com.example.mobile_musicapp.helpers.FileHelper
import com.example.mobile_musicapp.models.User
import com.example.mobile_musicapp.services.SongDao
import com.example.mobile_musicapp.services.TokenManager
import com.example.mobile_musicapp.services.UserDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var filePickerLauncher: ActivityResultLauncher<String>


    private var _binding: FragmentProfileBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var User = User()
    private var isLogin: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showLoadingState()

        lifecycleScope.launch {
            try {
                val userResponse = withContext(Dispatchers.IO) {
                    UserDao.getMe()
                }

                if (userResponse != null) {
                    User = userResponse
                    isLogin = true
                    showUserProfile()
                } else {
                    isLogin = false
                    showLoginButton()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                showLoginButton()
            }
        }

        // init file picker
        filePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            // Xử lý khi người dùng chọn tệp
            if (uri != null) {
                // Gọi phương thức để xử lý tệp đã chọn
                handleSelectedMp3(uri)
            } else {
                Toast.makeText(requireContext(), "No file selected", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnLogin.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_login)
        }
        binding.btnLogout.setOnClickListener {
            lifecycleScope.launch {
                try {
                    TokenManager.clearToken(requireContext())
                    showLoginButton()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        binding.uploadedSong.setOnClickListener {
            Toast.makeText(context, "Uploaded Song", Toast.LENGTH_SHORT).show()
            //val action = ProfileFragmentDirections.actionProfileFragmentToSongsFragment("Uploaded Song")
            //findNavController().navigate(action)
        }

        binding.favoriteSong.setOnClickListener {
            val action = ProfileFragmentDirections.actionProfileFragmentToSongsFragment("Favorite Songs")
            findNavController().navigate(action)
        }

        binding.recentlyPlayed.setOnClickListener {
            val action = ProfileFragmentDirections.actionProfileFragmentToSongsFragment("Recently Played")
            findNavController().navigate(action)
        }

        binding.language.setOnClickListener {
            val intent = Intent(requireActivity(), LanguageChangeActivity::class.java)
            startActivity(intent)
        }

        binding.changePassword.setOnClickListener {
            Toast.makeText(context, "Change Password", Toast.LENGTH_SHORT).show()
            // TODO : Change Password
        }

        binding.upload.setOnClickListener {
            //TODO : Upload Song
            filePickerLauncher.launch("audio/*")
        }

        binding.btnEditProfile.setOnClickListener {
            val action = ProfileFragmentDirections.actionProfileFragmentToEditProfileFragment(User!!)
            findNavController().navigate(action)
        }

        binding.getPremium.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_preminumFragment)
        }
    }

    private fun showLoadingState() {
        binding.userProfile.visibility = View.GONE
        binding.loginLayout.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun showUserProfile() {
        binding.progressBar.visibility = View.GONE
        binding.userProfile.visibility = View.VISIBLE
        binding.loginLayout.visibility = View.GONE
        binding.tvFullName.text = User.fullName
        if (User.isPremium) {
            binding.tvFullName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.premium_yellow, 0)
        }
        binding.tvEmail.text = User.email

        val url = User.avatar
        if (url != null) {
            Glide.with(binding.ivProfilePicture.context)
                .load(url)
                .into(binding.ivProfilePicture)
        } else {
            binding.ivProfilePicture.setImageResource(R.drawable.default_profile_avatar)
        }
    }

    private fun showLoginButton() {
        binding.progressBar.visibility = View.GONE
        binding.userProfile.visibility = View.GONE
        binding.loginLayout.visibility = View.VISIBLE
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun handleSelectedMp3(uri: Uri) {
        val contentResolver = requireContext().contentResolver
        val mimeType = contentResolver.getType(uri)

        if (mimeType == "audio/mpeg") {
            val fileName = FileHelper.getFileNameFromUri(uri, requireContext().contentResolver)
            Toast.makeText(requireContext(), "Selected: $fileName", Toast.LENGTH_SHORT).show()
            // upload file to server
            lifecycleScope.launch {
                val isUploaded = SongDao.uploadSong(uri, requireContext().contentResolver, requireContext())
                if (isUploaded) {
                    Toast.makeText(requireContext(), "Song uploaded successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Failed to upload song.", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(requireContext(), "Please select a valid MP3 file", Toast.LENGTH_SHORT).show()
        }
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}