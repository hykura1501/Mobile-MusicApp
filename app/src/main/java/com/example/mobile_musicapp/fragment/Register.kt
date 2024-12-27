package com.example.mobile_musicapp.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mobile_musicapp.R
import com.example.mobile_musicapp.databinding.FragmentRegisterBinding
import com.example.mobile_musicapp.services.AuthApiResult
import com.example.mobile_musicapp.services.AuthDao
import com.example.mobile_musicapp.services.TokenManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.launch

class Register : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 100

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Google Sign-In Client
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        binding.btnRegister.setOnClickListener { registerUser() }
        binding.btnLogin.setOnClickListener { navigateToLogin() }
        binding.btnBack.setOnClickListener { navigateToHome() }
        binding.btnGoogle.setOnClickListener { signInWithGoogle() }
    }

    private fun registerUser() {
        val email = binding.emailEditText.text.toString().trim()
        val fullname = binding.fullNameEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString().trim()

        var isValid = true
        if (email.isEmpty()) {
            binding.tvErrorEmail.text = "Email is required"
            isValid = false
        } else {
            binding.tvErrorEmail.text = ""
        }

        if (password.isEmpty()) {
            binding.tvErrorPassword.text = "Password is required"
            isValid = false
        } else {
            binding.tvErrorPassword.text = ""
        }

        if (fullname.isEmpty()) {
            binding.tvErrorFullName.text = "Full Name is required"
            isValid = false
        } else {
            binding.tvErrorFullName.text = ""
        }

        if (isValid) {
            lifecycleScope.launch {
                try {
                    when (val response = AuthDao.register(fullname, email, password)) {
                        is AuthApiResult.Success -> handleAuthSuccess(response.data?.token)
                        is AuthApiResult.Error -> handleAuthError(response.code, response.message)
                        null -> showError("Unable to connect to the server. Please try again.")
                    }
                } catch (e: Exception) {
                    showError("An error occurred: ${e.message}")
                }
            }
        }
    }

    private fun signInWithGoogle() {
        googleSignInClient.signOut().addOnCompleteListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val account = task.getResult(ApiException::class.java)
            if (account != null) {
                val idToken = account.idToken
                Log.d("Register", "ID Token: $idToken")
                if (!idToken.isNullOrEmpty()) {
                    sendIdTokenToServer(idToken)
                }
            }
        } catch (e: ApiException) {
            Log.e("Register", "Google Sign-In failed: ${e.message}")
            Toast.makeText(requireContext(), "Google Sign-In failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendIdTokenToServer(idToken: String) {
        lifecycleScope.launch {
            try {
                when (val response = AuthDao.loginGoogle(idToken)) {
                    is AuthApiResult.Success -> handleAuthSuccess(response.data?.token)
                    is AuthApiResult.Error -> showError(response.message)
                    null -> showError("Unable to connect to the server. Please try again.")
                }
            } catch (e: Exception) {
                showError("An error occurred: ${e.message}")
            }
        }
    }

    private fun handleAuthSuccess(token: String?) {
        if (token != null) {
            TokenManager.saveToken(requireContext(), token)
            clearInputs()
            navigateToHome()
        } else {
            showError("Unexpected error: Token is missing.")
        }
    }

    private fun handleAuthError(code: Int, message: String) {
        val errorMessage = when (code) {
            400 -> message
            else -> "Error $code: $message"
        }
        showError(errorMessage)
    }

    private fun showError(message: String) {
        binding.tvErrorPassword.text = message
    }

    private fun clearInputs() {
        binding.emailEditText.text.clear()
        binding.passwordEditText.text.clear()
        binding.fullNameEditText.text.clear()
    }

    private fun navigateToHome() {
        findNavController().navigate(R.id.action_register_to_home)
    }

    private fun navigateToLogin() {
        findNavController().navigate(R.id.action_register_to_login)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
