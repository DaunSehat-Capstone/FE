package com.example.daunsehat.features.community.presentation

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.daunsehat.databinding.ActivityCommunityBinding

class CommunityActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCommunityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCommunityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fabCreatePost.setOnClickListener {
            val intent = Intent(this, PostActivity::class.java)
            startActivity(intent)
        }
    }
}