package com.ycompany.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.ycompany.R
import com.ycompany.databinding.ActivitySignInBinding
import com.ycompany.ui.base.BaseActivity
import com.ycompany.ui.base.BaseSnackBar
import com.ycompany.ui.dashboard.DashboardActivity
import com.ycompany.ui.events.SignInState
import kotlinx.coroutines.launch
import com.google.android.material.tabs.TabLayoutMediator
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2

class SignInActivity : BaseActivity<ActivitySignInBinding, SignInViewModel>(SignInViewModel::class),
    SignInFragment.GoogleSignInHandler {

    private lateinit var credentialManager: CredentialManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBindingAndViewModel(R.layout.activity_sign_in)
        credentialManager = CredentialManager.create(this)
        window.statusBarColor = ContextCompat.getColor(this, R.color.bg_light) // or white
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        val tabLayout = binding.authTabLayout
        val viewPager = binding.authViewPager
        viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = 2
            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> SignInFragment()
                    1 -> SignUpFragment()
                    else -> throw IllegalStateException()
                }
            }
        }
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = if (position == 0) getString(R.string.action_sign_in_short) else getString(R.string.action_sign_up)
        }.attach()
        setupObservers()
    }

    override fun setupObservers() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.signInState.collect { state ->
                    when (state) {
                        SignInState.Proceed -> redirectToDashboard()
                        else -> {} // All other UI handled by fragment
                    }
                }
            }
        }
    }

    private fun redirectToDashboard() {
        val intent = Intent(this, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finishAffinity()
    }

    override fun onGoogleSignInRequested() {
        if (isGooglePlayServicesAvailable()) {
            launchGoogleSignIn()
        } else {
            BaseSnackBar.showWarning(
                binding.root,
                getString(R.string.google_play_services_not_available)
            )
        }
    }

    private fun isGooglePlayServicesAvailable(): Boolean {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this)
        return resultCode == com.google.android.gms.common.ConnectionResult.SUCCESS
    }

    private fun launchGoogleSignIn() {
        try {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(getString(R.string.default_web_client_id))
                .build()

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
                            getString(R.string.log_tag_sign_in) ?: "Unknown error",
                            "Unexpected credential type: ${result.credential.javaClass.name}"
                        )
                        BaseSnackBar.showError(
                            binding.root,
                            getString(R.string.error_google_sign_in_not_found).orEmpty()
                        )
                    }
                } catch (e: SecurityException) {
                    Log.e(
                        getString(R.string.log_tag_sign_in) ?: "Unknown error",
                        "Security Exception: ${e.message}"
                    )
                    BaseSnackBar.showError(
                        binding.root,
                        getString(R.string.error_google_sign_in_not_available)
                    )
                } catch (e: Exception) {
                    Log.e(
                        getString(R.string.log_tag_sign_in) ?: "Unknown error",
                        "Google Sign-In failed: ${e.localizedMessage}"
                    )
                    val errorMsg = getString(
                        R.string.error_google_sign_in_failed,
                        e.localizedMessage ?: getString(R.string.error_unknown)
                    )
                    BaseSnackBar.showError(binding.root, errorMsg)
                }
            }
        } catch (e: Exception) {
            Log.e(
                getString(R.string.log_tag_sign_in) ?: "Unknown error",
                "Error setting up Google Sign-In: ${e.message}"
            )
            BaseSnackBar.showError(
                binding.root,
                getString(R.string.error_setting_up_google_sign_in)
            )
        }
    }
}