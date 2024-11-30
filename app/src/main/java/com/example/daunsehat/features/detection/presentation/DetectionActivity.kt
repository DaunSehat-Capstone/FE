package com.example.daunsehat.features.detection.presentation

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.daunsehat.databinding.ActivityDetectionBinding
import com.example.daunsehat.features.detection.presentation.BottomSheetDetectionFragment.Companion.CAMERA_X_RESULT
import com.example.daunsehat.utils.rotateFile
import com.yalantis.ucrop.UCrop
import java.io.File

class DetectionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetectionBinding
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val croppedImageUri = intent.getStringExtra("CROPPED_IMAGE_URI")
        if (croppedImageUri != null) {
            imageUri = Uri.parse(croppedImageUri)
            Glide.with(this)
                .load(imageUri)
                .into(binding.ivSelectedImage)
        }

        binding.btnDetect.setOnClickListener {
            if (imageUri != null) {
                analyzeImage(imageUri!!)
            }
        }
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnRetake.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
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
        val destinationUri = Uri.fromFile(File(cacheDir, fileName))

        UCrop.of(sourceUri, destinationUri)
            .withAspectRatio(1f, 1f)
            .withMaxResultSize(1000, 1000)
            .start(this)
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
        val intent = Intent(this, DetectionActivity::class.java).apply {
            putExtra("CROPPED_IMAGE_URI", croppedUri.toString())
        }
        startActivity(intent)
    }

    private fun analyzeImage(uri: Uri) {
        val intent = Intent(this, DetectionResultActivity::class.java)
        intent.putExtra(DetectionResultActivity.EXTRA_IMAGE_URI, uri.toString())
        startActivity(intent)
    }
}