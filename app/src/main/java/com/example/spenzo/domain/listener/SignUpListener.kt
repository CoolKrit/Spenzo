package com.example.spenzo.domain.listener

interface SignUpListener {
    fun onSignUpSuccess()
    fun onSignUpFailure(errorMessage: String)
}