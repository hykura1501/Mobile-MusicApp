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
import com.example.mobile_musicapp.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class ResetPasswordFragment : Fragment() {

    private lateinit var btnBack: ImageButton
    private lateinit var btnChangePassword: Button
    private lateinit var passwordEditText: EditText
    private lateinit var reEnterPasswordEditText: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_reset_password, container, false)
        btnBack = view.findViewById(R.id.btnBack)
        btnChangePassword = view.findViewById(R.id.btnChangePassword)
        passwordEditText = view.findViewById(R.id.passwordEditText)
        reEnterPasswordEditText = view.findViewById(R.id.reEnterPasswordEditText)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        btnChangePassword.setOnClickListener {
            val newPassword = passwordEditText.text.toString()
            val reEnteredPassword = reEnterPasswordEditText.text.toString()
            if (newPassword == reEnteredPassword) {

            }
            else {
                Toast.makeText(
                    requireContext(),
                    "Two passwords are not the same.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}