package com.ycompany.ui

import android.content.Context
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ycompany.R
import com.ycompany.databinding.FragmentLoginBinding
import com.ycompany.ui.base.BaseSnackBar
import com.ycompany.ui.events.SignInState
import kotlinx.coroutines.launch

class SignInFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SignInViewModel by activityViewModels()
    private var googleSignInCallback: (() -> Unit)? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is GoogleSignInHandler) {
            googleSignInCallback = { context.onGoogleSignInRequested() }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signInButton.setOnClickListener {
            val email = binding.emailInput.text?.toString()?.trim() ?: ""
            val password = binding.passwordInput.text?.toString() ?: ""
            if (!isValidEmail(email)) {
                binding.emailInputLayout.error = getString(R.string.invalid_username)
                BaseSnackBar.showWarning(binding.root, getString(R.string.invalid_username))
            } else if (password.length < 6) {
                binding.passwordInputLayout.error = getString(R.string.invalid_password)
                BaseSnackBar.showWarning(binding.root, getString(R.string.invalid_password))
            } else {
                binding.emailInputLayout.error = null
                binding.passwordInputLayout.error = null
                viewModel.signInWithEmail(email, password)
            }
        }

        binding.googleSignInButton.setOnClickListener {
            googleSignInCallback?.invoke()
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.signInState.collect { state ->
                    when (state) {
                        is SignInState.Idle -> {}
                        is SignInState.Loading -> binding.loading.visibility = View.VISIBLE
                        is SignInState.Success -> {
                            binding.loading.visibility = View.GONE
                            BaseSnackBar.showSuccess(binding.root, state.message)
                        }

                        is SignInState.Error -> {
                            binding.loading.visibility = View.GONE
                            BaseSnackBar.showError(binding.root, state.message)
                        }

                        SignInState.Proceed -> {
                            binding.loading.visibility = View.GONE
                            // Navigation to dashboard should be handled by the activity or a callback
                        }
                    }
                }
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    interface GoogleSignInHandler {
        fun onGoogleSignInRequested()
    }
} 