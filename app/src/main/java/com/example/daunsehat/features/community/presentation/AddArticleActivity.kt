package com.example.daunsehat.features.community.presentation

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.daunsehat.R
import com.example.daunsehat.data.repository.ResultApi
import com.example.daunsehat.databinding.ActivityPostBinding
import com.example.daunsehat.features.community.presentation.viewmodel.AddArticleViewModel
import com.example.daunsehat.features.community.presentation.viewmodel.SharedViewModel
import com.example.daunsehat.utils.ViewModelFactory
import com.example.daunsehat.utils.getImageUri
import com.example.daunsehat.utils.reduceFileImage
import com.example.daunsehat.utils.uriToFile
import java.io.File

class AddArticleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostBinding

    private val viewModel by viewModels<AddArticleViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private var currentImageUri: Uri? = null

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Permission request granted", Toast.LENGTH_LONG).show()
                currentImageUri = getImageUri(this)
                launcherIntentCamera.launch(currentImageUri!!)
            } else {
                Toast.makeText(this, "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState != null) {
            val uriString = savedInstanceState.getString("currentImageUri")
            currentImageUri = uriString?.let { Uri.parse(it) }
            if (currentImageUri != null) {
                showImage()
            }
        }

        binding.btnPost.isEnabled = false

        binding.btnPost.backgroundTintList = ContextCompat.getColorStateList(this, R.color.light_grey)

        binding.etTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateInput()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.etDescription.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateInput()
            }

            override fun afterTextChanged(s: Editable?) {}
        })


        binding.btnGalleryBottom.setOnClickListener {
            startGallery()
        }

        binding.btnCameraBottom.setOnClickListener {
            startCamera()
        }

        binding.btnPost.setOnClickListener {
            postArticle()
        }

        binding.btnClose.setOnClickListener {
            finish()
        }

        binding.btnRemoveImage.setOnClickListener {
            currentImageUri = null
            binding.flImageContainer.visibility = View.GONE
            viewModel.currentImageUri.value = null
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        currentImageUri?.let {
            outState.putString("currentImageUri", it.toString())
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            binding.flImageContainer.visibility = View.VISIBLE
            binding.ivAddedImage.setImageURI(it)
        }
        viewModel.currentImageUri.value = currentImageUri
    }

    private fun startCamera() {
        if (allPermissionsGranted()) {
            currentImageUri = getImageUri(this)
            launcherIntentCamera.launch(currentImageUri!!)
        } else {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        } else {
            currentImageUri = null
        }
    }

    private fun postArticle() {
        showLoading(true)

        val title = binding.etTitle.text.toString()
        val description = binding.etDescription.text.toString()

        if (currentImageUri != null) {
            val imageFile = uriToFile(currentImageUri!!, this).reduceFileImage()
            viewModel.getSession().observe(this) { article ->
                val token = article.token
                viewModel.addArticle(token, imageFile, title, description).observe(this) { result ->
                    if (result != null) {
                        when (result) {
                            is ResultApi.Loading -> {
                                showLoading(true)
                            }
                            is ResultApi.Success -> {
                                showLoading(false)
                                Toast.makeText(this, "Article Posted", Toast.LENGTH_SHORT).show()
                                val intent = Intent().apply {
                                    putExtra("ARTICLE_UPLOADED", true)
                                }
                                setResult(RESULT_OK, intent)
                                finish()
                            }
                            is ResultApi.Error -> {
                                showLoading(false)
                                Log.d("AddArticleActivityxxx", "postArticle: ${result.error}")
                                Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        } else {
            val imageFile: File? = null
            viewModel.getSession().observe(this) { article ->
                val token = article.token
                viewModel.addArticle(token, imageFile, title, description).observe(this) { result ->
                    if (result != null) {
                        when (result) {
                            is ResultApi.Loading -> {
                                showLoading(true)
                            }
                            is ResultApi.Success -> {
                                showLoading(false)
                                Toast.makeText(this, "Article Posted", Toast.LENGTH_SHORT).show()
                                val intent = Intent().apply {
                                    putExtra("ARTICLE_UPLOADED", true)
                                }
                                setResult(RESULT_OK, intent)
                                finish()
                            }
                            is ResultApi.Error -> {
                                showLoading(false)
                                Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun validateInput() {
        val isTitleValid = binding.etTitle.text.toString().isNotEmpty()
        val isDescriptionValid = binding.etDescription.text.toString().isNotEmpty()
        binding.btnPost.isEnabled = isDescriptionValid && isTitleValid
        binding.btnPost.backgroundTintList = ContextCompat.getColorStateList(
            this,
            if (isDescriptionValid && isTitleValid) R.color.green else R.color.light_grey
        )
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        const val REQUIRED_PERMISSION = android.Manifest.permission.CAMERA
    }
}
