package com.example.daunsehat.features.profile.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.fragment.app.Fragment
import com.example.daunsehat.R
import com.example.daunsehat.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivMenu.setOnClickListener {
            showLogoutMenu(it)
        }

        binding.btnEditProfile.setOnClickListener {
            val bottomsheet = BottomSheetEditProfileFragment()
            bottomsheet.show(childFragmentManager, "EditProfileFragment")
        }
    }

    private fun showLogoutMenu(anchor: View) {
        val popupView = LayoutInflater.from(requireContext()).inflate(R.layout.menu_item_logout, null)

        val popupWindow = PopupWindow(popupView, 400, 100, true)
        popupWindow.isFocusable = true

        popupView.setOnClickListener {
            popupWindow.dismiss()
            // Implement logout functionality here
        }
        popupWindow.showAsDropDown(anchor, -380, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}