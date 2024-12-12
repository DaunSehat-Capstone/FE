package com.example.daunsehat.features.main

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.daunsehat.R
import com.example.daunsehat.databinding.ActivityMainBinding
import com.example.daunsehat.features.authentication.login.presentation.LoginActivity
import com.example.daunsehat.features.community.presentation.CommunityFragment
import com.example.daunsehat.features.detection.presentation.BottomSheetPredictFragment
import com.example.daunsehat.features.history.presentation.HistoryFragment
import com.example.daunsehat.features.homepage.presentation.HomePageFragment
import com.example.daunsehat.features.main.viewmodel.MainViewModel
import com.example.daunsehat.features.profile.presentation.ProfileFragment
import com.example.daunsehat.onboarding.OnboardingActivity
import com.example.daunsehat.utils.NetworkUtils
import com.example.daunsehat.utils.ViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createNotificationChannel()

        // Onboarding
        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val isOnboardingCompleted = sharedPreferences.getBoolean("onboarding_completed", false)

        Log.d("MainActivity", "Is onboarding completed? $isOnboardingCompleted")

        if (!isOnboardingCompleted) {
            startActivity(Intent(this, OnboardingActivity::class.java))
            finish()
            return
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                val intent = Intent(this, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                }
                startActivity(intent)
                finish()
            } else {
                if (!NetworkUtils.isInternetAvailable(this)) {
                    Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
                }
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
            val imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom

            binding.bottomAppBar.translationY = if (imeVisible) -imeHeight.toFloat() else 0f
            insets
        }

        setupBottomNavigation()

        if (NetworkUtils.isInternetAvailable(this)) {
            binding.fab.setOnClickListener {
                clearBackStack()
                val bottomSheetFragment = BottomSheetPredictFragment()
                val fragmentTransaction = supportFragmentManager.beginTransaction()

                val existingFragment =
                    supportFragmentManager.findFragmentByTag(BottomSheetPredictFragment::class.java.simpleName)

                if (existingFragment != null) {
                    fragmentTransaction.show(existingFragment)
                } else {
                    fragmentTransaction.add(R.id.bottom_sheet_container, bottomSheetFragment, BottomSheetPredictFragment::class.java.simpleName)
                }
                fragmentTransaction.commit()
            }
        } else {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
        }

        if (savedInstanceState == null) {
            clearBackStack()
            showFragment(HomePageFragment())
        }
    }

    private fun setupBottomNavigation() {
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setOnItemSelectedListener { item ->
            val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
            when (item.itemId) {
                R.id.menu_home -> {
                    if (currentFragment !is HomePageFragment) {
                        clearBackStack()
                        showFragment(HomePageFragment())
                    }
                    true
                }
                R.id.menu_history -> {
                    if (currentFragment !is HistoryFragment) {
                        clearBackStack()
                        showFragment(HistoryFragment())
                    }
                    true
                }
                R.id.menu_detection -> {
                    clearBackStack()
                    true
                }
                R.id.menu_community -> {
                    if (currentFragment !is CommunityFragment) {
                        clearBackStack()
                        showFragment(CommunityFragment())
                    }
                    true
                }
                R.id.menu_profile -> {
                    if (currentFragment !is ProfileFragment) {
                        clearBackStack()
                        showFragment(ProfileFragment())
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun showFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()
    }

    private fun clearBackStack() {
        val fragmentManager = supportFragmentManager
        if (fragmentManager.backStackEntryCount > 0) {
            for (i in 0 until fragmentManager.backStackEntryCount) {
                fragmentManager.popBackStack()
            }
        }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "reminder_channel",
            "Reminder Menyiram Tanaman",
            NotificationManager.IMPORTANCE_HIGH
        )
        channel.description = "Channel untuk reminder menyiram tanaman"

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        const val CAMERA_X_RESULT = 200
    }
}