package com.example.daunsehat.features.profile.presentation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.daunsehat.data.repository.ResultApi
import com.example.daunsehat.databinding.BottomSheetEditProfileBinding
import com.example.daunsehat.features.authentication.login.presentation.LoginActivity
import com.example.daunsehat.features.profile.presentation.viewmodel.ProfileViewModel
import com.example.daunsehat.utils.NetworkUtils
import com.example.daunsehat.utils.ViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

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

    }

    private fun observeSession() {
        viewModel.getSession().observe(viewLifecycleOwner) { user ->
            if (!user.isLogin) {
                startActivity(Intent(requireContext(), LoginActivity::class.java))
                requireActivity().finish()
            } else {
                if (NetworkUtils.isInternetAvailable(requireContext())) {
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

                    setupView()
                    setupAction()
                }
            }
        }
    }

    private fun setupAction() {
        binding.btnSave.setOnClickListener {
            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()

            if (name.isEmpty() || email.isEmpty()) {
                Toast.makeText(requireContext(), "Name and email cannot be empty", Toast.LENGTH_SHORT).show()
            } else if ( !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() ) {
                Toast.makeText(requireContext(), "Invalid email", Toast.LENGTH_SHORT).show()
            }
            else {
                viewModel.editProfile(name, email).observe(viewLifecycleOwner) { result ->
                    when (result) {
                        is ResultApi.Loading -> showLoading(true)
                        is ResultApi.Success -> {
                            showLoading(false)
                            Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show()
                            dismiss()
                        }
                        is ResultApi.Error -> {
                            showLoading(false)
                            Toast.makeText(requireContext(), "Error: ${result.error}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun setupView() {
        viewModel.getProfile().observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultApi.Loading -> showLoading(true)
                is ResultApi.Success -> {
                    showLoading(false)
                    val profile = result.data.user
                    profile?.let {
                        binding.etName.setText(it.name)
                        binding.etEmail.setText(it.email)
                    }
                }
                is ResultApi.Error -> {
                    showLoading(false)
                    Toast.makeText(requireContext(), "Error: ${result.error}", Toast.LENGTH_SHORT).show()
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