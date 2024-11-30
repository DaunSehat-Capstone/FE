package com.example.daunsehat.features.community.presentation.viewmodel

import android.Manifest
import android.app.Application
import android.content.ContentValues
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.daunsehat.features.community.presentation.BottomSheetImageSourceFragment

class PostViewModel(application: Application) : AndroidViewModel(application) {

    private val _selectedImageUri = MutableLiveData<Uri?>()
    val selectedImageUri: LiveData<Uri?> get() = _selectedImageUri

    private val _cameraPermissionGranted = MutableLiveData<Boolean>()
    val cameraPermissionGranted: LiveData<Boolean> get() = _cameraPermissionGranted

    fun setImageUri(uri: Uri?) {
        _selectedImageUri.value = uri
    }

    fun createImageUri(): Uri? {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "IMG_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }
        return getApplication<Application>().contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues
        )
    }

    fun checkCameraPermission(fragment: BottomSheetImageSourceFragment): Boolean {
        val permission = ContextCompat.checkSelfPermission(
            fragment.requireContext(),
            Manifest.permission.CAMERA
        )
        return if (permission == PackageManager.PERMISSION_GRANTED) {
            _cameraPermissionGranted.value = true
            true
        } else {
            fragment.requestPermissions(
                arrayOf(Manifest.permission.CAMERA),
                BottomSheetImageSourceFragment.CAMERA_PERMISSION_REQUEST_CODE
            )
            false
        }
    }

}
