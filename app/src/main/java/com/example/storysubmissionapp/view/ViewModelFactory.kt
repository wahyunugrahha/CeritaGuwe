package com.example.storysubmissionapp.view

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storysubmissionapp.data.StoryRepository
import com.example.storysubmissionapp.di.Injection
import com.example.storysubmissionapp.view.detail.DetailViewModel
import com.example.storysubmissionapp.view.login.LoginViewModel
import com.example.storysubmissionapp.view.main.MainViewModel
import com.example.storysubmissionapp.view.signup.SignupViewModel
import com.example.storysubmissionapp.view.upload.UploadViewModel

class ViewModelFactory(private val storyRepository: StoryRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignupViewModel::class.java)) {
            return SignupViewModel(storyRepository) as T
        } else if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(storyRepository) as T
        } else if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(storyRepository) as T
        } else if (modelClass.isAssignableFrom(UploadViewModel::class.java)) {
            return UploadViewModel(storyRepository) as T
        } else if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            return DetailViewModel(storyRepository) as T
        }
        throw UnsupportedOperationException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(Injection.provideRepository(context))
            }.also { instance = it }
    }
}
