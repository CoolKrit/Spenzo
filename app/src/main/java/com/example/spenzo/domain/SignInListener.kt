package com.example.spenzo.domain

interface SignInListener {
    fun onSignInSuccess()
    fun onSignInFailure(errorMessage: String)
}