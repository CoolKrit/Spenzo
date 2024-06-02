package com.example.spenzo.presentation.fragment

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.spenzo.App
import com.example.spenzo.R
import com.example.spenzo.databinding.FragmentProfileBinding
import com.example.spenzo.presentation.activity.MainActivity.Companion.CURRENCY
import com.example.spenzo.presentation.activity.MainActivity.Companion.DARK_THEME_ENABLED_KEY
import com.example.spenzo.presentation.activity.MainActivity.Companion.SETTINGS
import com.example.spenzo.presentation.viewmodel.TransactionViewModel
import com.example.spenzo.presentation.viewmodel.TransactionViewModelFactory
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import java.text.NumberFormat
import java.util.Locale

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var transactionViewModel: TransactionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        transactionViewModel = ViewModelProvider(
            this,
            TransactionViewModelFactory(requireActivity().application)
        )[TransactionViewModel::class.java]

        setupObservers()
        transactionViewModel.loadItems("", null, null, isOnline(requireContext()))

        val adapter = ViewPagerAdapter(this)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Income"
                1 -> "Expenses"
                2 -> "Currency Converter"
                else -> null
            }
        }.attach()

        binding.buttonSettings.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_theme -> {
                    // Переключение темы
                    switchTheme()
                    true
                }

                R.id.nav_logout -> {
                    // Выход из аккаунта
                    logout()
                    true
                }

                R.id.nav_currency -> {
                    // Выбор валюты
                    selectCurrency()
                    true
                }

                else -> false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupObservers() {
        transactionViewModel.balance.observe(viewLifecycleOwner) { balance ->
            binding.textViewBalance.text = formatNumber(balance)
        }

        transactionViewModel.maxEarned.observe(viewLifecycleOwner) { maxEarned ->
            binding.textViewMaxEarned.text = '+' + formatNumber(maxEarned)
        }

        transactionViewModel.maxSpent.observe(viewLifecycleOwner) { maxSpent ->
            binding.textViewMaxSpent.text = '-' + formatNumber(maxSpent)
        }
    }

    private fun formatNumber(number: Double): String {
        val numberFormat = NumberFormat.getNumberInstance(Locale.US)
        val currencySymb =
            when (requireActivity().getSharedPreferences(SETTINGS, Context.MODE_PRIVATE).getString(
                CURRENCY, "USD"
            )) {
                "USD" -> "$"
                "EUR" -> "€"
                "RUB" -> "₽"
                else -> "$"
            }
        return currencySymb + numberFormat.format(number)
    }

    private fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

    class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = 3

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> IncomeAnalysisFragment()
                1 -> ExpensesAnalysisFragment()
                2 -> CurrencyConverterFragment()
                else -> throw IllegalStateException("Unexpected position $position")
            }
        }
    }

    private fun selectCurrency() {
        // Логика выбора валюты
        val currencies = arrayOf("USD", "EUR", "RUB")
        val sharedPreferences =
            requireActivity().getSharedPreferences(SETTINGS, Context.MODE_PRIVATE)
        val currentCurrency = sharedPreferences.getString(CURRENCY, currencies[0])

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Select Currency")
        builder.setSingleChoiceItems(
            currencies,
            currencies.indexOf(currentCurrency)
        ) { dialog, which ->
            val editor = sharedPreferences.edit()
            editor.putString(CURRENCY, currencies[which])
            editor.apply()
            dialog.dismiss()
            Toast.makeText(
                requireContext(),
                "Currency selected: ${currencies[which]}",
                Toast.LENGTH_SHORT
            ).show()
            setupObservers()
        }
        builder.create().show()
    }

    private fun switchTheme() {
        // Логика переключения темы
        val sharedPreferences =
            requireActivity().getSharedPreferences(SETTINGS, Context.MODE_PRIVATE)
        val isDarkThemeEnabled = sharedPreferences.getBoolean(DARK_THEME_ENABLED_KEY, false)
        if (isDarkThemeEnabled) {
            sharedPreferences.edit().putBoolean(DARK_THEME_ENABLED_KEY, false).apply()
            (binding.root.context.applicationContext as App).applyTheme(false)
        } else {
            sharedPreferences.edit().putBoolean(DARK_THEME_ENABLED_KEY, true).apply()
            (binding.root.context.applicationContext as App).applyTheme(true)
        }
    }

    private fun logout() {
        // Логика выхода из аккаунта
        findNavController().navigate(R.id.action_profileFragment_to_signInUpActivity)
    }
}