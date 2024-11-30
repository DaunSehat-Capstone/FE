package com.example.daunsehat.features.community.presentation

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.daunsehat.features.community.presentation.viewmodel.PostViewModel
import com.example.daunsehat.databinding.ActivityPostBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostBinding
    private lateinit var postViewModel: PostViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        postViewModel = ViewModelProvider(this)[PostViewModel::class.java]

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.btnClose.setOnClickListener {
            finish()
        }

        binding.btnPost.setOnClickListener {
            Toast.makeText(this, "Post uploaded successfully", Toast.LENGTH_SHORT).show()
            finish()
        }

        binding.btnAddImage.setOnClickListener {
            val existingFragment = supportFragmentManager.findFragmentByTag("BottomSheetImageSourceFragment")
            if (existingFragment == null) {
                val detectionFragment = BottomSheetImageSourceFragment()
                detectionFragment.show(supportFragmentManager, "BottomSheetImageSourceFragment")
            }
        }

        binding.ivRemoveImage.setOnClickListener {
            postViewModel.setImageUri(null)
        }
    }

    private fun observeViewModel() {
        postViewModel.selectedImageUri.observe(this) { uri ->
            Log.d("PostActivity", "Selected URI: $uri")
            if (uri != null) {
                // Load the image into the ImageView using Glide
                Glide.with(this)
                    .load(uri)
                    .into(binding.ivAddedImage)

                binding.ivAddedImage.visibility = View.VISIBLE
                binding.ivRemoveImage.visibility = View.VISIBLE

                val bottomSheetFragment = supportFragmentManager.findFragmentByTag("BottomSheetImageSourceFragment")
                (bottomSheetFragment as? BottomSheetDialogFragment)?.dismiss()
            } else {
                binding.ivAddedImage.visibility = View.GONE
                binding.ivRemoveImage.visibility = View.GONE
            }
        }
    }

    companion object {
        const val CAMERA_PERMISSION_REQUEST_CODE = 100
    }
}
