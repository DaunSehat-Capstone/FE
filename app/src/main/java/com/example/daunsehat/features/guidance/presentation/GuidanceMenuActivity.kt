package com.example.daunsehat.features.guidance.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.daunsehat.R
import com.example.daunsehat.data.pref.GuidanceItem
import com.example.daunsehat.databinding.ActivityGuidanceMenuBinding

class GuidanceMenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGuidanceMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGuidanceMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val guidanceList = listOf(
            GuidanceItem(R.drawable.img_plant, "Biji-bijian"),
            GuidanceItem(R.drawable.img_plant, "Kacang-kacangan"),
            GuidanceItem(R.drawable.img_plant, "Buah-buahan"),
            GuidanceItem(R.drawable.img_plant, "Sayur-mayur"),
            GuidanceItem(R.drawable.img_plant, "Industri"),
            GuidanceItem(R.drawable.img_plant, "Rempah"),
            GuidanceItem(R.drawable.img_plant, "Umbi-umbian"),
            GuidanceItem(R.drawable.img_plant, "Hias")
        )

        val adapter = GuidanceAdapter(guidanceList, { guidanceItem ->
            val intent = GuidanceDetailActivity.newIntent(this, guidanceItem.name)
            startActivity(intent)
        }, useMenuLayout = true)

        binding.recyclerGuidanceMenu.layoutManager = GridLayoutManager(this, 2)
        binding.recyclerGuidanceMenu.adapter = adapter
    }
}