package com.example.mobile_musicapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mobile_musicapp.R
import com.example.mobile_musicapp.databinding.FragmentEnterPasswordBinding
import com.example.mobile_musicapp.databinding.FragmentOtpBinding
import com.example.mobile_musicapp.services.UserDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [EnterPassword.newInstance] factory method to
 * create an instance of this fragment.
 */
class EnterPassword : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentEnterPasswordBinding? = null
    private val binding get() = _binding!!

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
        _binding = FragmentEnterPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = EnterPasswordArgs.fromBundle(requireArguments())
        val resetToken = args.resetToken

        binding.btnRecover.setOnClickListener {

            val password = binding.passwordEditText.text.toString()
            val confirmPassword = binding.reEnterPasswordEditText.text.toString()

            if (password == confirmPassword) {
                // Call the reset password API
                try {
                    lifecycleScope.launch {
                        var response = withContext(Dispatchers.IO) {
                            UserDao.resetPassword(resetToken, password)
                        }

                        Toast.makeText(context, "Recover password successfully!", Toast.LENGTH_SHORT).show()

                        findNavController().navigate(R.id.action_enterPassFragment_to_login)

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(context, "An error occurred", Toast.LENGTH_SHORT).show()
                }
            } else {
                binding.reEnterPasswordEditText.error = "Password does not match"
            }
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EnterPassword.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EnterPassword().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}