package com.example.daunsehat.features.profile.presentation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.daunsehat.R
import com.example.daunsehat.databinding.FragmentProfileBinding
import com.example.daunsehat.features.authentication.login.presentation.LoginActivity
import com.example.daunsehat.features.profile.presentation.viewmodel.ProfileViewModel
import com.example.daunsehat.utils.ViewModelFactory

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

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
            performLogout()
        }
        popupWindow.showAsDropDown(anchor, -380, 0)
    }

    private fun performLogout() {
        viewModel.logout()
        val intent = Intent(requireContext(), LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}