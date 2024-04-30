package com.example.spenzo.domain

interface SignUpListener {
    fun onSignUpSuccess()
    fun onSignUpFailure(errorMessage: String)
}