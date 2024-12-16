package com.example.mobile_musicapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.example.mobile_musicapp.R
import com.example.mobile_musicapp.databinding.FragmentLoginBinding
import com.example.mobile_musicapp.services.AuthDao
import com.example.mobile_musicapp.services.TokenManager
import androidx.navigation.fragment.findNavController
// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Login.newInstance] factory method to
 * create an instance of this fragment.
 */


private var _binding: FragmentLoginBinding? = null
// This property is only valid between onCreateView and
// onDestroyView.
private val binding get() = _binding!!

class Login : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            if (email.isEmpty()) {
                binding.tvErrorEmail.text = "Email is required"
            } else {
                binding.tvErrorEmail.text = ""
            }
            if (password.isEmpty()) {
                binding.tvErrorPassword.text = "Password is required"
            } else {
                binding.tvErrorPassword.text = ""
            }

            if (email.isNotEmpty() && password.isNotEmpty()) {
                lifecycleScope.launch {
                    try {
                        val token = AuthDao.login(email, password)
                        if (token != null) {
                            TokenManager.saveToken(requireContext(), token)
                            binding.tvErrorEmail.text = ""
                            binding.tvErrorPassword.text = ""
                            binding.emailEditText.text.clear()
                            binding.passwordEditText.text.clear()
                            findNavController().navigate(R.id.action_login_to_home)
                        } else {
                            binding.tvErrorPassword.text = "Login failed"
                        }
                    } catch (e: Exception) {
                        // Handle exception
                        binding.tvErrorPassword.text = "An error occurred: ${e.message}"
                    }
                }
            }
        }
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
         * @return A new instance of fragment Login.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Login().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}