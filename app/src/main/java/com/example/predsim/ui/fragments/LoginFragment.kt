package com.example.predsim.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.predsim.R
import com.example.predsim.databinding.FragmentLoginBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginFragment : Fragment() {
    
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        with(binding) {
            btnLogin.setOnClickListener {
                val username = etUsername.text?.toString()
                val password = etPassword.text?.toString()

                if (validateInput(username, password)) {
                    attemptLogin(username!!, password!!)
                }
            }
        }
    }

    private fun validateInput(username: String?, password: String?): Boolean {
        if (username.isNullOrBlank()) {
            showError(getString(R.string.error_username_required))
            return false
        }

        if (password.isNullOrBlank()) {
            showError(getString(R.string.error_password_required))
            return false
        }

        return true
    }

    private fun attemptLogin(username: String, password: String) {
        showLoading(true)

        // TODO: Replace with actual authentication logic
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Simulate network delay
                kotlinx.coroutines.delay(1000)

                // For demo purposes, accept any non-empty credentials
                // In production, implement proper authentication
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    navigateToDashboard()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    showError(getString(R.string.error_invalid_credentials))
                }
            }
        }
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.btnLogin.isEnabled = !show
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setAction("OK") { }
            .show()
    }

    private fun navigateToDashboard() {
        findNavController().navigate(R.id.action_loginFragment_to_dashboardFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
