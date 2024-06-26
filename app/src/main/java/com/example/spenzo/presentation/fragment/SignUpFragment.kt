package com.example.spenzo.presentation.fragment

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
import com.example.spenzo.databinding.FragmentSignUpBinding
import com.example.spenzo.domain.listener.SignUpListener
import com.example.spenzo.presentation.viewmodel.SignInUpViewModel

class SignUpFragment : Fragment(), SignUpListener {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private val signInUpViewModel: SignInUpViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        signInUpViewModel.signUpListener = this

        binding.signUpButton.setOnClickListener {
            val name = binding.nameInputEditText.text.toString()
            val email = binding.emailInputLayout.emailInputEditText.text.toString()
            val password = binding.passwordInputLayout.passwordInputEditText.text.toString()

            if (validateData(name, email, password)) signInUpViewModel.signUp(name, email, password)
        }

        binding.signInTextView.setOnClickListener { findNavController().popBackStack() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun validateData(userName: String, userEmail: String, userPassword: String): Boolean {
        return when {
            userName.isEmpty() -> {
                binding.nameInputEditText.error = "Name is empty!"
                false
            }

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

    override fun onSignUpSuccess() {
        findNavController().popBackStack()
    }

    override fun onSignUpFailure(errorMessage: String) {
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
    }
}