package com.example.daunsehat.features.detection.presentation

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.daunsehat.R
import com.example.daunsehat.databinding.BottomSheetImageSourceBinding
import com.example.daunsehat.features.authentication.login.presentation.LoginActivity
import com.example.daunsehat.features.detection.presentation.viewmodel.PredictViewModel
import com.example.daunsehat.utils.NetworkUtils
import com.example.daunsehat.utils.ViewModelFactory
import com.example.daunsehat.utils.rotateFile
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.yalantis.ucrop.UCrop
import java.io.File

class BottomSheetPredictFragment : BottomSheetDialogFragment() {
    private var _binding: BottomSheetImageSourceBinding? = null
    private val binding get() = _binding!!

    private var currentImageUri: Uri? = null

    private val viewModel by viewModels<PredictViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    private val pickMediaLauncher = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
        if (uri != null) {
            cropImage(uri)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetImageSourceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.setOnShowListener {
            val bottomSheetDialog = dialog as BottomSheetDialog
            val bottomSheet =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        observeSession()
    }

    private fun observeSession() {
        viewModel.getSession().observe(viewLifecycleOwner) { user ->
            if (!user.isLogin) {
                startActivity(Intent(requireContext(), LoginActivity::class.java))
                requireActivity().finish()
            } else {
                if (NetworkUtils.isInternetAvailable(requireContext())) {
                    setupAction()
                } else {
                    Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupAction() {
        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnFromGallery.setOnClickListener {
            startGallery()
        }

        binding.btnFromCamera.setOnClickListener {
            startCameraX()
        }
    }

    private fun startCameraX() {
        val intent = Intent(requireContext(), CameraActivity::class.java)
        launchIntentCameraX.launch(intent)
    }

    @Suppress("DEPRECATION")
    private val launchIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == CAMERA_X_RESULT) {
            val myFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                result.data?.getSerializableExtra("picture", File::class.java)
            } else {
                result.data?.getSerializableExtra("picture")
            } as? File

            val isBackCamera = result.data?.getBooleanExtra("isBackCamera", true) ?: true

            myFile?.let { file ->
                rotateFile(file, isBackCamera)
                cropImage(Uri.fromFile(file))
            }
        }
    }

    private fun cropImage(sourceUri: Uri) {
        val fileName = "cropped_image_${System.currentTimeMillis()}.jpg"
        val destinationUri = Uri.fromFile(File(requireContext().cacheDir, fileName))

        UCrop.of(sourceUri, destinationUri)
            .start(requireContext(), this)
    }

    private fun startGallery() {
        pickMediaLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UCrop.REQUEST_CROP) {
            if (data != null) {
                currentImageUri = UCrop.getOutput(data)
                if (currentImageUri != null) {
                    navigateToPredict(currentImageUri!!)
                } else {
                    Log.e("UCrop", "Crop failed: URI is null")
                }
            } else {
                Log.e("UCrop", "Crop failed: Data is null")
            }
            dismiss()
        }
    }

    private fun navigateToPredict(croppedUri: Uri) {
        val fragment = PredictFragment()
        val bundle = Bundle()
        bundle.putString("CROPPED_IMAGE_URI", croppedUri.toString())
        fragment.arguments = bundle

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val CAMERA_X_RESULT = 200

    }
}
