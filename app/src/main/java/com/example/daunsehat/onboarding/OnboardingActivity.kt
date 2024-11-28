package com.example.daunsehat.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.daunsehat.MainActivity
import com.example.daunsehat.R
import com.google.android.material.button.MaterialButton
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator

class OnboardingActivity : AppCompatActivity() {
    private lateinit var btnNext: MaterialButton
    private lateinit var btnBack: MaterialButton
    private lateinit var btnSkip: MaterialButton
    private lateinit var viewPager: ViewPager2
    private lateinit var dotsIndicator: DotsIndicator
    private lateinit var onboardingAdapter: OnboardingAdapter
    private lateinit var onboardingItems: List<OnboardingItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        btnNext = findViewById(R.id.btn_next)
        btnBack = findViewById(R.id.btn_back)
        btnSkip = findViewById(R.id.btn_skip)
        viewPager = findViewById(R.id.vp_onboarding)
        dotsIndicator = findViewById(R.id.dots_indicator)

        onboardingItems = listOf(
            OnboardingItem(
                R.drawable.onboarding_1,
                "Deteksi Penyakit Tanaman dengan Mudah!",
                "Ambil atau unggah foto tanaman Anda, dan kami akan membantu mendeteksi penyakit serta memberikan solusi terbaik untuk perawatan."
            ),
            OnboardingItem(
                R.drawable.onboarding_2,
                "Pantau Riwayat Perawatan Tanaman Anda",
                "Simpan dan kelola hasil deteksi untuk memantau kesehatan tanaman Anda secara berkelanjutan."
            ),
            OnboardingItem(
                R.drawable.onboarding_3,
                "Bergabunglah dengan Komunitas Hijau",
                "Temukan inspirasi, bagikan pengalaman Anda, dan belajar dari sesama pecinta tanaman dalam komunitas."
            )
        )

        onboardingAdapter = OnboardingAdapter(onboardingItems)
        viewPager.adapter = onboardingAdapter
        dotsIndicator.setViewPager2(viewPager)

        btnNext.setOnClickListener {
            if (viewPager.currentItem < onboardingItems.size - 1) {
                viewPager.currentItem += 1
            } else {
                finishOnboarding()
            }
        }

        btnBack.setOnClickListener {
            if (viewPager.currentItem > 0) {
                viewPager.currentItem -= 1
            }
        }

        btnSkip.setOnClickListener {
            finishOnboarding()
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateNavigationButtons(position, onboardingItems.size)

                onboardingAdapter.notifyItemChanged(position)
            }
        })

        updateNavigationButtons(viewPager.currentItem, onboardingItems.size)
    }

    private fun updateNavigationButtons(position: Int, totalItems: Int) {
        btnBack.visibility = if (position == 0) MaterialButton.GONE else MaterialButton.VISIBLE
        btnNext.text = if (position == totalItems - 1) "Selesai" else "Selanjutnya"
    }

    private fun finishOnboarding() {
        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        sharedPreferences.edit().putBoolean("onboarding_completed", true).apply()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
