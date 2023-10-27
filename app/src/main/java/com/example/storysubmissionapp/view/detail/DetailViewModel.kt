package com.example.storysubmissionapp.view.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.storysubmissionapp.data.StoryRepository


class DetailViewModel(private val mStoryRepository: StoryRepository) : ViewModel() {

    fun getDetailStory(token: String, id: String) = mStoryRepository.getDetailStory(token, id)

    fun getSessionData() = mStoryRepository.getSession().asLiveData()
}