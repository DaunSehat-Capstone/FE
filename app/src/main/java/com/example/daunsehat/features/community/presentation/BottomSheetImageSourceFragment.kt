//package com.example.daunsehat.features.community.presentation
//
//import android.Manifest
//import android.content.pm.PackageManager
//import android.net.Uri
//import android.os.Bundle
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Toast
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.core.app.ActivityCompat
//import androidx.lifecycle.ViewModelProvider
//import com.example.daunsehat.features.community.presentation.viewmodel.PostViewModel
//import com.example.daunsehat.databinding.BottomSheetImageSourceBinding
//import com.google.android.material.bottomsheet.BottomSheetDialogFragment
//
//class BottomSheetImageSourceFragment : BottomSheetDialogFragment() {
//    private var _binding: BottomSheetImageSourceBinding? = null
//    private val binding get() = _binding!!
//    private lateinit var postViewModel: PostViewModel
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = BottomSheetImageSourceBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        postViewModel = ViewModelProvider(requireActivity())[PostViewModel::class.java]
//
//        binding.btnCancel.setOnClickListener {
//            dismiss()
//        }
//
//        binding.btnFromGallery.setOnClickListener {
//            openGallery()
//        }
//
//        binding.btnFromCamera.setOnClickListener {
//            handleCameraPermission()
//        }
//    }
//
//    private fun openGallery() {
//        pickImageLauncher.launch("image/*")
//    }
//
//    private val pickImageLauncher =
//        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
//            Log.d("PostActivity", "Gallery URI: $uri")
//            if (uri != null) {
//                postViewModel.setImageUri(uri)
//            } else {
//                Toast.makeText(requireContext(), "No image selected", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//    private fun handleCameraPermission() {
//        if (postViewModel.checkCameraPermission(this)) {
//            openCamera()
//        } else {
//            requestCameraPermission()
//        }
//    }
//
//    private fun openCamera() {
//        val uri = postViewModel.createImageUri()
//        if (uri != null) {
//            postViewModel.setImageUri(uri)
//            takePictureLauncher.launch(uri)
//        } else {
//            Toast.makeText(requireContext(), "Failed to create image file", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    private fun requestCameraPermission() {
//        ActivityCompat.requestPermissions(
//            requireActivity(),
//            arrayOf(Manifest.permission.CAMERA),
//            CAMERA_PERMISSION_REQUEST_CODE
//        )
//    }
//
//    private val takePictureLauncher =
//        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess: Boolean ->
//            if (isSuccess) {
//                postViewModel.selectedImageUri.value?.let {
//                    postViewModel.setImageUri(it)
//                }
//            } else {
//                Toast.makeText(requireContext(), "Failed to take picture", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == PostActivity.CAMERA_PERMISSION_REQUEST_CODE) {
//            val isGranted = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
//            if (isGranted) {
//                openCamera()
//            } else {
//                Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    companion object {
//        const val CAMERA_PERMISSION_REQUEST_CODE = 100
//    }
//}