package com.example.mobile_musicapp.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mobile_musicapp.R
import com.example.mobile_musicapp.databinding.FragmentLoginBinding
import com.example.mobile_musicapp.services.AuthDao
import com.example.mobile_musicapp.services.TokenManager
import com.example.mobile_musicapp.services.AuthApiResult
import com.facebook.CallbackManager
import com.facebook.FacebookSdk
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.launch
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult


class Login : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val callbackManager: CallbackManager by lazy {
        CallbackManager.Factory.create()
    }

    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 100

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

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
                loginUser(email, password)
            }
        }

        binding.btnRegister.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_register)
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_home)
        }

        binding.btnGoogle.setOnClickListener {
            signInWithGoogle()
        }

        // Initialize Google Sign-In Client
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        binding.fbLoginButton.setPermissions("email", "public_profile")
        binding.fbLoginButton.setFragment(this)
        binding.fbLoginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                val accessToken = loginResult.accessToken.token
                Log.d("Login", "Facebook Access Token: $accessToken")
                sendAccessTokenToServer(accessToken)
            }

            override fun onCancel() {
                Toast.makeText(requireContext(), "Facebook Login Cancelled", Toast.LENGTH_SHORT).show()
            }

            override fun onError(exception: FacebookException) {
                Log.e("Login", "Facebook Login Error: ${exception.message}")
                Toast.makeText(requireContext(), "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        })

        binding.btnFacebook.setOnClickListener {
            binding.fbLoginButton.performClick()
        }

        binding.btnRecoveryPassword.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_recoveryPassword)
        }
    }

    private fun sendAccessTokenToServer(accessToken: String) {
        lifecycleScope.launch {
            try {
                when (val response = AuthDao.loginFacebook(accessToken)) {
                    is AuthApiResult.Success -> {
                        val token = response.data?.token
                        if (token != null) {
                            TokenManager.saveToken(requireContext(), token)
                            LoginManager.getInstance().logOut()
                            navigateToHome()
                        } else {
                            showError("Unexpected error: Token is missing.")
                        }
                    }
                    is AuthApiResult.Error -> showError(response.message)
                    null -> showError("Unable to connect to the server. Please try again.")
                }
            } catch (e: Exception) {
                showError("An error occurred: ${e.message}")
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

        // Facebook Login callback
        callbackManager.onActivityResult(requestCode, resultCode, data)

        // Google Sign-In callback
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
                Log.d("Login", "ID Token: $idToken")

                // Send ID Token to server for authentication
                if (!idToken.isNullOrEmpty()) {
                    sendIdTokenToServer(idToken)
                }
            }
        } catch (e: ApiException) {
            Log.e("Login", "Google Sign-In failed: ${e.message}")
            Toast.makeText(requireContext(), "Google Sign-In failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendIdTokenToServer(idToken: String) {
        lifecycleScope.launch {
            try {
                when (val response = AuthDao.loginGoogle(idToken)) {
                    is AuthApiResult.Success -> {
                        val token = response.data?.token
                        if (token != null) {
                            TokenManager.saveToken(requireContext(), token)
                            clearInputs()
                            navigateToHome()
                        } else {
                            showError("Unexpected error: Token is missing.")
                        }
                    }
                    is AuthApiResult.Error -> {
                        showError(response.message)
                    }
                    null -> {
                        showError("Unable to connect to the server. Please try again.")
                    }
                }
            } catch (e: Exception) {
                showError("An error occurred: ${e.message}")
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        lifecycleScope.launch {
            try {
                when (val response = AuthDao.login(email, password)) {
                    is AuthApiResult.Success -> {
                        val token = response.data?.token
                        if (token != null) {
                            TokenManager.saveToken(requireContext(), token)
                            clearInputs()
                            navigateToHome()
                        } else {
                            showError("Unexpected error: Token is missing.")
                        }
                    }
                    is AuthApiResult.Error -> {
                        showError(response.message)
                    }
                    null -> {
                        showError("Unable to connect to the server. Please try again.")
                    }
                }
            } catch (e: Exception) {
                showError("An error occurred: ${e.message}")
            }
        }
    }

    private fun showError(message: String) {
        binding.tvErrorPassword.text = message
    }

    private fun clearInputs() {
        binding.emailEditText.text.clear()
        binding.passwordEditText.text.clear()
    }

    private fun navigateToHome() {
        findNavController().navigate(R.id.action_login_to_home)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
