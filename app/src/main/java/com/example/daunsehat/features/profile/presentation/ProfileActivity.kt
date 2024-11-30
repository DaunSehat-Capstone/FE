package com.example.daunsehat.features.profile.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import com.example.daunsehat.R
import com.example.daunsehat.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivMenu.setOnClickListener {
            showLogoutMenu(it)
        }

        binding.btnEditProfile.setOnClickListener {
            val bottomsheet = BottomSheetEditProfileFragment()
            bottomsheet.show(supportFragmentManager, "EditProfileFragment")
        }
    }

    private fun showLogoutMenu(anchor: android.view.View) {
        val popupView = LayoutInflater.from(this).inflate(R.layout.menu_item_logout, null)

        val popupWindow = PopupWindow(popupView, 400, 100, true)
        popupWindow.isFocusable = true

        popupView.setOnClickListener {
            popupWindow.dismiss()
            // Implement logout functionality here
        }
        popupWindow.showAsDropDown(anchor, -380, 0)
    }
}