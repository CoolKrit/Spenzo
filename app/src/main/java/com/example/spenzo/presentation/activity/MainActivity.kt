package com.example.spenzo.presentation.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.spenzo.R
import com.example.spenzo.databinding.ActivityMainBinding
import com.example.spenzo.presentation.fragment.HomeFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val navController by lazy {
        (supportFragmentManager.findFragmentById(R.id.mainFragmentContainerView) as NavHostFragment).navController
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigationView.setupWithNavController(navController)
    }

    companion object {
        const val SETTINGS = "settings"

        const val DARK_THEME_ENABLED_KEY = "isDarkThemeEnabled"

        const val CURRENCY = "currency"
    }
}