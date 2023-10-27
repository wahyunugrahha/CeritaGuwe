package com.example.storysubmissionapp.di

import android.content.Context
import com.example.storysubmissionapp.data.StoryRepository
import com.example.storysubmissionapp.data.pref.UserPreferences
import com.example.storysubmissionapp.data.pref.dataStore
import com.example.storysubmissionapp.data.retrofit.ApiConfig


object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val apiService = ApiConfig.getApiService()
        val pref = UserPreferences.getInstance(context.dataStore)
        return StoryRepository.getInstance(apiService, pref)
    }
}