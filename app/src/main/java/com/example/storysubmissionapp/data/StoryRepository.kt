package com.example.storysubmissionapp.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.storysubmissionapp.data.model.UserModel
import com.example.storysubmissionapp.data.pref.UserPreferences
import com.example.storysubmissionapp.data.response.LoginResponse
import com.example.storysubmissionapp.data.response.MessageResponse
import com.example.storysubmissionapp.data.response.StoriesResponse
import com.example.storysubmissionapp.data.response.StoryResponse
import com.example.storysubmissionapp.data.retrofit.ApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryRepository(
    private val apiService: ApiService,
    private val pref: UserPreferences
) {
    fun uploadImage(
        token: String,
        imageUri: MultipartBody.Part,
        description: RequestBody
    ): LiveData<Result<MessageResponse>> =
        liveData {
            emit(Result.Loading)
            try {
                val response = apiService.uploadStory("bearer $token", imageUri, description)
                if (response.error) {
                    emit(Result.Error("Upload Error: ${response.message}"))
                    Log.d("Upload Error", response.message)
                } else {
                    emit(Result.Success(response))
                    Log.d("Upload Success", response.message)
                }
            } catch (e: Exception) {
                emit(Result.Error("Error : ${e.message.toString()}"))
                Log.d("Upload Exception", e.message.toString())
            }
        }

    fun getDetailStory(
        token: String,
        id: String
    ): LiveData<Result<StoryResponse>> =
        liveData {
            emit(Result.Loading)
            try {
                val response = apiService.getDetailStory("Bearer $token", id)
                if (response.error) {
                    emit(Result.Error("Detail Error: ${response.message}"))
                    Log.d("Detail Error", response.message)
                } else {
                    emit(Result.Success(response))
                    Log.d("Detail Success", response.message)
                }
            } catch (e: Exception) {
                emit(Result.Error("Error : ${e.message.toString()}"))
                Log.d("Detail Exception", e.message.toString())
            }
        }

    fun getStories(
        token: String
    ): LiveData<Result<StoriesResponse>> =
        liveData {
            emit(Result.Loading)
            try {
                val response = apiService.getStories("Bearer $token")
                if (response.error) {
                    emit(Result.Error("Stories Error: ${response.message}"))
                    Log.d("Stories Error", response.message)
                } else {
                    emit(Result.Success(response))
                    Log.d("Stories Success", response.message)
                }
            } catch (e: Exception) {
                emit(Result.Error("Error : ${e.message.toString()}"))
                Log.d("Stories Exception", e.message.toString())
            }
        }

    fun getSession() = pref.getSession()

    suspend fun logout() {
        pref.logout()
    }

    fun registerUser(
        name: String,
        email: String,
        password: String
    ): LiveData<Result<String>> =
        liveData {
            emit(Result.Loading)
            try {
                val response = apiService.register(name, email, password)
                if (response.error) {
                    emit(Result.Error("Register Error: ${response.message}"))
                    Log.d("Register Error", response.message)
                } else {
                    emit(Result.Success("User Created"))
                    Log.d("Register Success", response.message)
                }
            } catch (e: Exception) {
                emit(Result.Error("Error : ${e.message.toString()}"))
                Log.d("Register Exception", e.message.toString())
            }
        }

    fun loginUser(
        email: String,
        password: String
    ): LiveData<Result<LoginResponse>> =
        liveData {
            emit(Result.Loading)
            try {
                val response = apiService.login(email, password)
                if (response.error) {
                    Log.d("Login Error", response.message)
                    emit(Result.Error("Login Error: ${response.message}"))
                } else {
                    Log.d("Login Success", response.message)
                    emit(Result.Success(response))

                    pref.saveSession(
                        UserModel(
                            response.loginResult.userId,
                            response.loginResult.name,
                            response.loginResult.token,
                            true
                        )
                    )
                }
            } catch (e: Exception) {
                Log.d("Login Exception", e.message.toString())
                emit(Result.Error("Error : ${e.message.toString()}"))
            }
        }


    companion object {
        private var instance: StoryRepository? = null
        fun getInstance(apiService: ApiService, pref: UserPreferences): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, pref)
            }.also { instance = it }
    }
}