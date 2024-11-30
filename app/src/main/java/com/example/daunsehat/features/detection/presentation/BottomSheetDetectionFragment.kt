package com.example.daunsehat.features.detection.presentation

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.daunsehat.databinding.BottomSheetImageSourceBinding
import com.example.daunsehat.utils.rotateFile
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.yalantis.ucrop.UCrop
import java.io.File

class BottomSheetDetectionFragment : BottomSheetDialogFragment() {
    private var _binding: BottomSheetImageSourceBinding? = null
    private val binding get() = _binding!!

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

    private fun startGallery() {
        pickMediaLauncher.launch("image/*")
    }

    private val pickMediaLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            cropImage(uri)
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun cropImage(sourceUri: Uri) {
        val fileName = "cropped_image_${System.currentTimeMillis()}.jpg"
        val destinationUri = Uri.fromFile(File(requireContext().cacheDir, fileName))

        UCrop.of(sourceUri, destinationUri)
            .withAspectRatio(1f, 1f)
            .withMaxResultSize(1000, 1000)
            .start(requireContext(), this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UCrop.REQUEST_CROP) {
            val resultUri = UCrop.getOutput(data!!)
            if (resultUri != null) {
                navigateToDetection(resultUri)
            } else {
                Log.e("UCrop", "Crop failed")
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
            Log.e("UCrop", "Crop error: ${cropError?.message}")
        }
    }

    private fun navigateToDetection(croppedUri: Uri) {
        val intent = Intent(requireContext(), DetectionActivity::class.java).apply {
            putExtra("CROPPED_IMAGE_URI", croppedUri.toString())
        }
        startActivity(intent)
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val CAMERA_X_RESULT = 200

    }
}
