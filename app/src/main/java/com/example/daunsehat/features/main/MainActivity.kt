package com.example.daunsehat.features.main

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
import com.example.daunsehat.features.detection.presentation.BottomSheetDetectionFragment
import com.example.daunsehat.features.history.presentation.HistoryFragment
import com.example.daunsehat.features.homepage.presentation.HomePageFragment
import com.example.daunsehat.features.main.viewmodel.MainViewModel
import com.example.daunsehat.features.profile.presentation.ProfileFragment
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
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupBottomNavigation()

        binding.fab.setOnClickListener {
            val bottomSheetFragment = BottomSheetDetectionFragment()
            if (supportFragmentManager.findFragmentByTag(BottomSheetDetectionFragment::class.java.simpleName) == null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.bottom_sheet_container, bottomSheetFragment, BottomSheetDetectionFragment::class.java.simpleName)
                    .commit()
            }
        }

        if (savedInstanceState == null) {
            showFragment(HomePageFragment())
        }
    }

    private fun setupBottomNavigation() {
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setOnItemSelectedListener  { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    showFragment(HomePageFragment())
                    true
                }
                R.id.menu_history -> {
                    showFragment(HistoryFragment())
                    true
                }
                R.id.menu_detection -> {
                    true
                }
                R.id.menu_community -> {
                    showFragment(CommunityFragment())
                    Log.d("MainActivity", "Show community fragment")
                    true
                }
                R.id.menu_profile -> {
                    showFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun showFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()


        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    companion object {
        const val CAMERA_X_RESULT = 200
    }
}