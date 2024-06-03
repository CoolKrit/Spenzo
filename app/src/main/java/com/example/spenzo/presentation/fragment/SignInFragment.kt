package com.example.spenzo.presentation.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.spenzo.R
import com.example.spenzo.databinding.FragmentSignInBinding
import com.example.spenzo.domain.listener.SignInListener
import com.example.spenzo.presentation.activity.MainActivity
import com.example.spenzo.presentation.viewmodel.SignInUpViewModel
import com.google.firebase.auth.FirebaseAuth

class SignInFragment : Fragment(), SignInListener {

    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!

    private val signInUpViewModel: SignInUpViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        signInUpViewModel.signInListener = this

        binding.signInButton.setOnClickListener {
            val email = binding.emailInputLayout.emailInputEditText.text.toString()
            val password = binding.passwordInputLayout.passwordInputEditText.text.toString()

            if (validateData(email, password)) signInUpViewModel.signIn(email, password)
        }

        binding.signUpTextView.setOnClickListener { findNavController().navigate(R.id.signUpFragment) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun validateData(userEmail: String, userPassword: String): Boolean {
        return when {
            userEmail.isEmpty() -> {
                binding.emailInputLayout.emailInputEditText.error = "Email is empty!"
                false
            }

            userPassword.isEmpty() -> {
                binding.passwordInputLayout.passwordInputEditText.error = "Password is empty!"
                false
            }

            !Patterns.EMAIL_ADDRESS.matcher(userEmail).matches() -> {
                binding.emailInputLayout.emailInputEditText.error = "Email is invalid!"
                false
            }

            else -> true
        }
    }

    override fun onSignInSuccess() {
        startActivity(Intent(requireActivity(), MainActivity::class.java))
    }

    override fun onSignInFailure(errorMessage: String) {
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
    }
}