package com.example.daunsehat

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.daunsehat.guidance.GuidanceDetailActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val fabScan = findViewById<FloatingActionButton>(R.id.fab_scan)

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    true
                }
                R.id.menu_history -> {
//                    menuju halaman riwayat
                    true
                }
                R.id.menu_community -> {
//                    menuju halaman komunitas
                    true
                }
                R.id.menu_profile -> {
//                    menuju halaman profil
                    true
                }
                else -> false
            }
        }

        fabScan.setOnClickListener {
            Toast.makeText(this, "Scan button clicked!", Toast.LENGTH_SHORT).show()
        }

        val calendar = Calendar.getInstance()

        val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        val monthYear = monthYearFormat.format(calendar.time)

        val txtMonthYear: TextView = findViewById(R.id.txt_month_year)
        txtMonthYear.text = monthYear

        val currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        for (i in 1..31) {
            val dateTextViewId = resources.getIdentifier("date_$i", "id", packageName)
            val dateTextView = findViewById<TextView>(dateTextViewId)
            if (dateTextView != null) {
                if (i == currentDayOfMonth) {
                    dateTextView.setBackgroundResource(R.drawable.bg_highlight)
                }
            }
        }

        val tipsRecyclerView: RecyclerView = findViewById(R.id.recycler_tips)
        val tipsList = listOf(
            TipItem(R.drawable.dummy_img_tip, "Biji-bijian"),
            TipItem(R.drawable.dummy_img_tip, "Kacang-kacangan"),
            TipItem(R.drawable.dummy_img_tip, "Buah-buahan"),
            TipItem(R.drawable.dummy_img_tip, "Sayur-mayur")
        )

        val tipsAdapter = TipsAdapter(tipsList) { tipItem ->
            val intent = Intent(this, GuidanceDetailActivity::class.java)
            intent.putExtra("tip_name", tipItem.name)
            startActivity(intent)
        }

        tipsRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        tipsRecyclerView.adapter = tipsAdapter
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.scroll_content, fragment)
            .commit()
    }
}