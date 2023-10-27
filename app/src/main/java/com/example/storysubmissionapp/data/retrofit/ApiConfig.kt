package com.example.storysubmissionapp.data.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiConfig {
    fun getApiService(): ApiService {
        return Retrofit.Builder()
            .baseUrl("https://story-api.dicoding.dev/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(ApiService::class.java)
    }
}