package com.example.daunsehat.features.history.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.daunsehat.databinding.ActivityHistoryBinding

class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}