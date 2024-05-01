package com.example.spenzo.presentation.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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

    private lateinit var signInUpViewModel: SignInUpViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        signInUpViewModel = ViewModelProvider(this)[SignInUpViewModel::class.java]
        signInUpViewModel.signInListener = this

        binding.signInButton.setOnClickListener {
            val email = binding.emailInputLayout.emailInputEditText.text.toString()
            val password = binding.passwordInputLayout.passwordInputEditText.text.toString()

            if (validateData(email, password)) signInUpViewModel.signIn(email, password)
        }

        binding.signUpTextView.setOnClickListener { findNavController().navigate(R.id.signUpFragment) }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()


        if (FirebaseAuth.getInstance().currentUser != null) {
            onSignInSuccess()
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
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