package com.example.storysubmissionapp.data.response

import com.example.storysubmissionapp.data.model.Story

data class StoriesResponse(
    val listStory: List<Story>,
    val error: Boolean,
    val message: String
)

