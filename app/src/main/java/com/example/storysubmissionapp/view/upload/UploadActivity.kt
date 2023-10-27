package com.example.storysubmissionapp.view.upload

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.storysubmissionapp.R
import com.example.storysubmissionapp.data.ValidateType
import com.example.storysubmissionapp.data.model.UserModel
import com.example.storysubmissionapp.data.showToast
import com.example.storysubmissionapp.data.uriToFile
import com.example.storysubmissionapp.data.validate
import com.example.storysubmissionapp.databinding.ActivityUploadBinding
import com.example.storysubmissionapp.view.ViewModelFactory
import com.example.storysubmissionapp.view.welcome.WelcomeActivity
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import com.example.storysubmissionapp.data.Result
import com.example.storysubmissionapp.data.getImageUri
import com.example.storysubmissionapp.data.reduceFileImage
import com.example.storysubmissionapp.view.main.MainActivity

class UploadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadBinding
    private val viewModel: UploadViewModel by viewModels { ViewModelFactory.getInstance(this) }

    private var currentImageUri: Uri? = null
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                showToast(getString(R.string.permission_request_granted), true)
            } else {
                showToast(getString(R.string.permission_request_denied), true)
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        viewModel.getSessionData().observe(this@UploadActivity) { user ->
            if (!user.isLogin) {
                val intent = Intent(this, WelcomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            } else {
                setupAction(user)
            }
        }
    }

    private fun setupAction(user: UserModel) {
        binding.galleryButton.setOnClickListener { startGallery() }
        binding.cameraButton.setOnClickListener { startCamera() }
        binding.uploadButton.setOnClickListener {
            if (binding.deskripsi.validate(
                    "Description cannot be empty",
                    ValidateType.REQUIRED
                )
            ) uploadImage(user.token, binding.deskripsi.text.toString())
        }
    }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    private fun showImage() {
        currentImageUri?.let { uri ->
            Log.d("Image URI", "showImage: $uri")
            binding.previewImageView.setImageURI(uri)
        } ?: {
            Log.d("Empty Image", "Image is empty")
        }
    }

    private fun uploadImage(token: String, description: String) {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            Log.d("Image File", "showImage: ${imageFile.path}")

            val requestBody = description.toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                requestImageFile
            )

            viewModel.uploadImage(token, multipartBody, requestBody).observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            showLoading(true)
                            binding.uploadButton.isEnabled = false
                        }

                        is Result.Error -> {
                            showLoading(false)
                            binding.uploadButton.isEnabled = true
                            showToast(result.error)
                        }

                        is Result.Success -> {
                            showLoading(false)
                            binding.uploadButton.isEnabled = true
                            showToast(result.data.message)

                            val intent = Intent(this, MainActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }
                    }
                }
            }

        } ?: showToast(getString(R.string.image_is_required_to_make_a_story))
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.uploadButton.isEnabled = !isLoading
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", getString(R.string.no_media_selected))
            showToast(getString(R.string.no_media_selected))
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }


    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}