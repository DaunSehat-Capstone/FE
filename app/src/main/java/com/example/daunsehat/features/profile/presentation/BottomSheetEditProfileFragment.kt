package com.example.daunsehat.features.profile.presentation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.daunsehat.R
import com.example.daunsehat.data.repository.ResultApi
import com.example.daunsehat.databinding.BottomSheetEditProfileBinding
import com.example.daunsehat.features.authentication.login.presentation.LoginActivity
import com.example.daunsehat.features.profile.presentation.viewmodel.ProfileViewModel
import com.example.daunsehat.utils.NetworkUtils
import com.example.daunsehat.utils.ViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.yalantis.ucrop.UCrop
import java.io.File

class BottomSheetEditProfileFragment : BottomSheetDialogFragment() {
    private var _binding: BottomSheetEditProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeSession()

        dialog?.setOnShowListener { dialog ->
            val bottomSheetDialog = dialog as BottomSheetDialog
            val bottomSheet =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)

            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                behavior.isFitToContents = false
                behavior.halfExpandedRatio = 0.9f
                behavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
            }
        }

        binding.btnSave.setOnClickListener {
            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()

            if (name.isEmpty() || email.isEmpty()) {
                Snackbar.make(binding.root, "Name and email cannot be empty", Snackbar.LENGTH_SHORT).show()
            } else {
                viewModel.editProfile(name, email).observe(viewLifecycleOwner) { result ->
                    when (result) {
                        is ResultApi.Loading -> showLoading(true)
                        is ResultApi.Success -> {
                            Log.d("EditProfilexxx", "bottomSheet editProfile dataxxx: ${result.data}")
                            showLoading(false)
                            Snackbar.make(binding.root, "Profile updated", Snackbar.LENGTH_SHORT).show()
                            dismiss()
                        }
                        is ResultApi.Error -> {
                            showLoading(false)
                            Snackbar.make(binding.root, "Error: ${result.error}", Snackbar.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun observeSession() {
        viewModel.getSession().observe(viewLifecycleOwner) { user ->
            if (!user.isLogin) {
                startActivity(Intent(requireContext(), LoginActivity::class.java))
                requireActivity().finish()
            } else {
                if (NetworkUtils.isInternetAvailable(requireContext())) {
                    setupView()
                }
            }
        }
    }

    private fun setupView() {
        viewModel.getProfile().observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultApi.Loading -> showLoading(true)
                is ResultApi.Success -> {
                    showLoading(false)
                    Log.d("EditProfilexxx", "bottomSheet getProfile dataxxx: ${result.data}")
                    val profile = result.data.user
                    profile?.let {
                        binding.etName.setText(it.name)
                        binding.etEmail.setText(it.email)
                    }
                }
                is ResultApi.Error -> {
                    showLoading(false)
                    Snackbar.make(binding.root, "Error: ${result.error}", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}