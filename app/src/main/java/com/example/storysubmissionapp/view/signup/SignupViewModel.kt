package com.example.storysubmissionapp.view.signup

import androidx.lifecycle.ViewModel
import com.example.storysubmissionapp.data.StoryRepository


class SignupViewModel(private val mStoryRepository: StoryRepository) : ViewModel() {

    fun register(name: String, email: String, password: String) =
        mStoryRepository.registerUser(name, email, password)

    fun login(email: String, password: String) =
        mStoryRepository.loginUser(email, password)
}