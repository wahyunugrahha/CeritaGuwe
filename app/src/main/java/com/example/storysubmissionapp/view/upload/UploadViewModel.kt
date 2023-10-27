package com.example.storysubmissionapp.view.upload

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.storysubmissionapp.data.StoryRepository
import com.example.storysubmissionapp.data.model.UserModel
import okhttp3.MultipartBody
import okhttp3.RequestBody

class UploadViewModel(private val mStoryRepository: StoryRepository) : ViewModel() {
    fun uploadImage(
        token: String,
        imageUri: MultipartBody.Part,
        description: RequestBody
    ) = mStoryRepository.uploadImage(token, imageUri, description)

    fun getSessionData(): LiveData<UserModel> =
        mStoryRepository.getSession().asLiveData()
}
