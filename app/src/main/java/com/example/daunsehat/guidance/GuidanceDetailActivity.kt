package com.example.daunsehat.guidance

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.daunsehat.R

class GuidanceDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guidance_detail)

        val tipName = intent.getStringExtra("tip_name")
    }
}