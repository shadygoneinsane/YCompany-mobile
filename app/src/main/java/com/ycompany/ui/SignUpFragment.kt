package com.ycompany.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.ycompany.R
import com.ycompany.databinding.FragmentSignupBinding
import com.ycompany.ui.base.BaseSnackBar
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch

class SignUpFragment : Fragment() {
    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SignInViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.signUpButton.visibility = View.VISIBLE
        binding.signUpButton.setOnClickListener {
            val displayName = binding.displayNameInput.text?.toString()?.trim() ?: ""
            val email = binding.emailInput.text?.toString()?.trim() ?: ""
            val password = binding.passwordInput.text?.toString() ?: ""
            val confirmPassword = binding.confirmPasswordInput.text?.toString() ?: ""
            if (displayName.isEmpty()) {
                binding.displayNameInputLayout.error = getString(R.string.invalid_username)
                BaseSnackBar.showWarning(binding.root, getString(R.string.invalid_username))
            } else if (!isValidEmail(email)) {
                binding.emailInputLayout.error = getString(R.string.invalid_username)
                BaseSnackBar.showWarning(binding.root, getString(R.string.invalid_username))
            } else if (password.length < 6) {
                binding.passwordInputLayout.error = getString(R.string.invalid_password)
                BaseSnackBar.showWarning(binding.root, getString(R.string.invalid_password))
            } else if (password != confirmPassword) {
                binding.confirmPasswordInputLayout.error = getString(R.string.invalid_password)
                BaseSnackBar.showWarning(binding.root, getString(R.string.invalid_password))
            } else {
                binding.displayNameInputLayout.error = null
                binding.emailInputLayout.error = null
                binding.passwordInputLayout.error = null
                binding.confirmPasswordInputLayout.error = null
                viewModel.signUpWithEmailAndName(displayName, email, password)
            }
        }
        observeViewModel()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.signInState.collect { state ->
                    when (state) {
                        is com.ycompany.ui.events.SignInState.Loading -> binding.loading.visibility = View.VISIBLE
                        is com.ycompany.ui.events.SignInState.Success -> {
                            binding.loading.visibility = View.GONE
                            // Move to SignIn tab
                            (activity?.findViewById<androidx.viewpager2.widget.ViewPager2>(R.id.authViewPager))?.currentItem = 0
                            BaseSnackBar.showSuccess(binding.root, state.message)
                        }
                        is com.ycompany.ui.events.SignInState.Error -> {
                            binding.loading.visibility = View.GONE
                            BaseSnackBar.showError(binding.root, state.message)
                        }
                        else -> binding.loading.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 