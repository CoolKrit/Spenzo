package com.example.spenzo.domain.listener

interface SignInListener {
    fun onSignInSuccess()
    fun onSignInFailure(errorMessage: String)
}