package com.example.storysubmissionapp.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.storysubmissionapp.data.StoryRepository
import com.example.storysubmissionapp.data.model.UserModel
import kotlinx.coroutines.launch

class MainViewModel(private val mStoryRepository: StoryRepository) : ViewModel() {
    fun getSessionData(): LiveData<UserModel> =
        mStoryRepository.getSession().asLiveData()

    fun getStories(token: String) =
        mStoryRepository.getStories(token)

    fun logout() {
        viewModelScope.launch {
            mStoryRepository.logout()
        }
    }
}