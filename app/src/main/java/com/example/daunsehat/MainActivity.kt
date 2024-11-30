package com.example.daunsehat

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.daunsehat.features.community.presentation.CommunityActivity
import com.example.daunsehat.databinding.ActivityMainBinding
import com.example.daunsehat.features.detection.presentation.BottomSheetDetectionFragment
import com.example.daunsehat.features.history.presentation.HistoryActivity
import com.example.daunsehat.features.profile.presentation.ProfileActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        binding.btnDetection.setOnClickListener {
            val detectionFragment = BottomSheetDetectionFragment()
            detectionFragment.show(supportFragmentManager, "BottomSheetDetectionFragment")
        }

        binding.btnHistory.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }

        binding.btnCommunity.setOnClickListener {
            val intent = Intent(this, CommunityActivity::class.java)
            startActivity(intent)
        }

        if (intent.getBooleanExtra("SHOW_BOTTOM_SHEET", false)) {
            val detectionFragment = BottomSheetDetectionFragment()
            detectionFragment.show(supportFragmentManager, "BottomSheetDetectionFragment")
        }
    }

    companion object {
        const val CAMERA_X_RESULT = 200
    }
}