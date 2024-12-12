package com.example.daunsehat.features.profile.presentation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.daunsehat.R
import com.example.daunsehat.data.repository.ResultApi
import com.example.daunsehat.databinding.BottomSheetEditPhotoProfileBinding
import com.example.daunsehat.features.authentication.login.presentation.LoginActivity
import com.example.daunsehat.features.profile.presentation.viewmodel.ProfileViewModel
import com.example.daunsehat.utils.NetworkUtils
import com.example.daunsehat.utils.ViewModelFactory
import com.example.daunsehat.utils.reduceFileImage
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.yalantis.ucrop.UCrop
import java.io.File

class BottomSheetEditPhotoProfileFragment : BottomSheetDialogFragment() {
    private var _binding: BottomSheetEditPhotoProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    private var currentImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetEditPhotoProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeSession()

    }

    private fun setupAction() {
        binding.btnEditPhoto.setOnClickListener {
            openGallery()
        }

        binding.btnSave.setOnClickListener {
            val imageUri  = currentImageUri

            if (imageUri != null) {
                val imageFile = File(imageUri.path ?: "").reduceFileImage()
                viewModel.editPhotoProfile(imageFile).observe(viewLifecycleOwner) { result ->
                    when (result) {
                        is ResultApi.Loading -> showLoading(true)
                        is ResultApi.Success -> {
                            showLoading(false)
                            Toast.makeText(requireContext(), "Photo profile updated", Toast.LENGTH_SHORT).show()
                            dismiss()
                        }
                        is ResultApi.Error -> {
                            showLoading(false)
                            Toast.makeText(requireContext(), "Error: ${result.error}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Please select an image", Toast.LENGTH_SHORT).show()
            }

        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun openGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            startCrop(uri)
        }
    }

    private fun startCrop(sourceUri: Uri) {
        val destinationUri = Uri.fromFile(File(requireContext().cacheDir, "cropped_image.jpg"))
        UCrop.of(sourceUri, destinationUri)
            .withAspectRatio(1f, 1f)
            .withMaxResultSize(500, 500)
            .start(requireContext(), this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UCrop.REQUEST_CROP && resultCode == Activity.RESULT_OK) {
            val resultUri = UCrop.getOutput(data!!)
            if (resultUri != null) {
                currentImageUri = resultUri
                Glide.with(requireContext())
                    .load(resultUri)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .placeholder(R.drawable.ic_photo_profile)
                    .error(R.drawable.ic_photo_profile)
                    .into(binding.ivProfilePhoto)
            }
            binding.ivProfilePhoto.invalidate()
            binding.ivProfilePhoto.requestLayout()
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val error = UCrop.getError(data!!)
            Log.e("UCrop", "Error: $error")
        }
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

    private fun setupView() {
        viewModel.getProfile().observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultApi.Loading -> showLoading(true)
                is ResultApi.Success -> {
                    showLoading(false)
                    val profile = result.data.user
                    profile?.let {
                        Glide.with(requireContext())
                            .load(it.imageUrl)
                            .placeholder(R.drawable.ic_photo_profile)
                            .error(R.drawable.ic_photo_profile)
                            .into(binding.ivProfilePhoto)
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