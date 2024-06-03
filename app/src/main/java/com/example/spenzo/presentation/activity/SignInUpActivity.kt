package com.example.spenzo.presentation.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.spenzo.R
import com.example.spenzo.databinding.ActivityMainBinding
import com.example.spenzo.databinding.ActivitySignInUpBinding

class SignInUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignInUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}