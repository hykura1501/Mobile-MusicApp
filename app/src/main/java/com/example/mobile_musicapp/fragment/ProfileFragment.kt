package com.example.mobile_musicapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.mobile_musicapp.R
import com.example.mobile_musicapp.databinding.FragmentLoginBinding
import com.example.mobile_musicapp.databinding.FragmentProfileBinding
import com.example.mobile_musicapp.models.User
import com.example.mobile_musicapp.services.AuthDao
import com.example.mobile_musicapp.services.TokenManager
import com.example.mobile_musicapp.services.UserDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    private var _binding: FragmentProfileBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var User : User? = User()
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
            Toast.makeText(context, "Upload Song", Toast.LENGTH_SHORT).show()
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
        binding.tvFullName.text = User?.fullName
        binding.tvEmail.text = User?.email

        val url = User?.avatar
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