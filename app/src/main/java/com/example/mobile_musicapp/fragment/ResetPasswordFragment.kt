package com.example.mobile_musicapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.mobile_musicapp.R
import com.example.mobile_musicapp.databinding.FragmentProfileBinding
import com.example.mobile_musicapp.databinding.FragmentResetPasswordBinding
import com.example.mobile_musicapp.services.TokenManager
import com.example.mobile_musicapp.services.UserApiResult
import com.example.mobile_musicapp.services.UserDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class ResetPasswordFragment : Fragment() {

    private var _binding: FragmentResetPasswordBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentResetPasswordBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }



        binding.btnChangePassword.setOnClickListener {
            val oldPassword = binding.oldPasswordEditText.text.toString()
            val newPassword = binding.passwordEditText.text.toString()
            val reEnteredPassword = binding.reEnterPasswordEditText.text.toString()

            if (oldPassword.isEmpty() || newPassword.isEmpty() || reEnteredPassword.isEmpty()) {
                binding.tvError.text = "Please fill all fields"
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    when (val response = UserDao.changePassword(oldPassword, newPassword, reEnteredPassword)) {
                        is UserApiResult.Success -> {
                            Toast.makeText(requireContext(), "Password changed successfully", Toast.LENGTH_SHORT).show()
                            requireActivity().onBackPressed()
                        }
                        is UserApiResult.Error -> {
                            binding.tvError.text = response.message
                        }

                        null -> TODO()
                    }
                } catch (
                    e: Exception
                ) {
                    binding.tvError.text = "An error occurred"
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}