package com.ycompany.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.common.SignInButton
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.ycompany.R
import com.ycompany.databinding.ActivityMainBinding
import com.ycompany.ui.dashboard.DashboardActivity
import com.ycompany.ui.events.SignInState
import kotlinx.coroutines.launch

class SignInActivity : AppCompatActivity() {


    private lateinit var credentialManager: CredentialManager
    private lateinit var binding: ActivityMainBinding

    private val viewModel: SignInViewModel by lazy {
        ViewModelProvider(this)[SignInViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        credentialManager = CredentialManager.create(this)

        binding.signInButton.setSize(SignInButton.SIZE_WIDE)
        binding.signInButton.setOnClickListener {
            launchGoogleSignIn()
        }

        // Collect the StateFlow with lifecycle awareness
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.signInState.collect { state ->
                    // React to state changes here
                    when (state) {
                        is SignInState.Idle -> {
                            // Do something for idle
                        }

                        is SignInState.Loading -> {
                            binding.loadingSpinner.hide()
                        }

                        is SignInState.Success -> {
                            binding.loadingSpinner.hide()
                            Toast.makeText(this@SignInActivity, state.message, Toast.LENGTH_SHORT)
                                .show()
                        }

                        is SignInState.Error -> {
                            binding.loadingSpinner.hide()
                            Toast.makeText(
                                this@SignInActivity, state.message, Toast.LENGTH_SHORT
                            ).show()
                        }

                        SignInState.Proceed -> redirectToDashboard()
                    }
                }
            }
        }
    }

    private fun launchGoogleSignIn() {
        binding.loadingSpinner.show()
        val googleIdOption = GetGoogleIdOption.Builder().setFilterByAuthorizedAccounts(true)
            .setServerClientId(getString(R.string.default_web_client_id)).build()

        val request = GetCredentialRequest(listOf(googleIdOption))

        lifecycleScope.launch {
            try {
                val result = credentialManager.getCredential(this@SignInActivity, request)

                val customCredential = result.credential as? CustomCredential

                if (customCredential != null && customCredential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {

                    val googleIdCredential =
                        GoogleIdTokenCredential.createFrom(customCredential.data)
                    val idToken = googleIdCredential.idToken

                    viewModel.firebaseAuthWithGoogle(idToken)
                } else {
                    Log.e(
                        "SignIn", "Unexpected credential type: ${result.credential.javaClass.name}"
                    )
                    binding.loadingSpinner.hide()
                }
            } catch (e: Exception) {
                Log.e("SignIn", "Google Sign-In failed: ${e.localizedMessage}")
                Toast.makeText(this@SignInActivity, "No Google sign-ins found on this device. Please add an account.", Toast.LENGTH_SHORT)
                    .show()
                binding.loadingSpinner.hide()
            }
        }
    }

    private fun redirectToDashboard() {
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }

    private fun signOut() {
        FirebaseAuth.getInstance().signOut()
    }
}