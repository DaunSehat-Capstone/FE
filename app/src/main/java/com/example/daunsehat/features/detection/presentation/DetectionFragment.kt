package com.example.daunsehat.features.detection.presentation

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.daunsehat.R
import com.example.daunsehat.databinding.FragmentDetectionBinding
import com.example.daunsehat.features.detection.presentation.BottomSheetDetectionFragment.Companion.CAMERA_X_RESULT
import com.example.daunsehat.utils.rotateFile
import com.yalantis.ucrop.UCrop
import java.io.File

class DetectionFragment : Fragment() {
    private var _binding: FragmentDetectionBinding? = null
    private val binding get() = _binding!!
    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val croppedImageUri = arguments?.getString("CROPPED_IMAGE_URI")
        if (croppedImageUri != null) {
            imageUri = Uri.parse(croppedImageUri)
            Glide.with(this)
                .load(imageUri)
                .into(binding.ivSelectedImage)
        }

        binding.btnDetect.setOnClickListener {
            imageUri?.let { analyzeImage(it) }
        }

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.btnRetake.setOnClickListener {
            val intent = Intent(requireContext(), CameraActivity::class.java)
            launchIntentCameraX.launch(intent)
        }
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
        val fragment = DetectionFragment()
        val bundle = Bundle()
        bundle.putString("CROPPED_IMAGE_URI", croppedUri.toString())
        fragment.arguments = bundle

        // Menambahkan fragment ke dalam Activity
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)  // Gantilah 'fragment_container' dengan ID kontainer fragment Anda
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun analyzeImage(uri: Uri) {
        val intent = Intent(requireContext(), DetectionResultActivity::class.java).apply {
            putExtra(DetectionResultActivity.EXTRA_IMAGE_URI, uri.toString())
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}