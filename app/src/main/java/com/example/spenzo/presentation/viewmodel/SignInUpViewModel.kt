package com.example.spenzo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.spenzo.domain.listener.SignInListener
import com.example.spenzo.domain.listener.SignUpListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignInUpViewModel : ViewModel() {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseStore = FirebaseFirestore.getInstance()
    var signInListener: SignInListener? = null
    var signUpListener: SignUpListener? = null

    fun signUp(name: String, email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.isSuccessful) {
                        val userMap = hashMapOf(
                            "name" to name
                        )
                        firebaseStore.collection("users").document(firebaseAuth.currentUser!!.uid)
                            .set(userMap)
                        signUpListener?.onSignUpSuccess()
                    } else {
                        signUpListener?.onSignUpFailure(task.exception?.message ?: "Sign Up failed")
                    }
                }
            }
    }

    fun signIn(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    signInListener?.onSignInSuccess()
                } else {
                    signInListener?.onSignInFailure(task.exception?.message ?: "Sign In failed")
                }
            }
    }
}