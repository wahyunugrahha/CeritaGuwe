package com.example.storysubmissionapp.view.detail

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.storysubmissionapp.data.loadImage
import com.example.storysubmissionapp.databinding.ActivityDetailBinding
import com.example.storysubmissionapp.view.ViewModelFactory
import com.example.storysubmissionapp.data.Result
import com.example.storysubmissionapp.data.showToast


class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val viewModel: DetailViewModel by viewModels { ViewModelFactory.getInstance(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val storyId = intent.getStringExtra(ID_KEY)

        if (storyId != null) {
            viewModel.getSessionData().observe(this) { user ->
                loadStory(user.token, storyId)
            }
        }
    }

    private fun loadStory(token: String, id: String) {
        viewModel.getDetailStory(token, id).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.pbDetail.isVisible = true
                    }

                    is Result.Error -> {
                        binding.pbDetail.isVisible = false
                        showToast(result.error)
                    }

                    is Result.Success -> {
                        binding.pbDetail.isVisible = false
                        val story = result.data.story
                        binding.apply {
                            tvName.text = story.name
                            tvDeskripsi.text = story.description
                            ivAvatar.loadImage(this@DetailActivity, story.photoUrl)
                        }
                    }
                }
            }

        }

    }

    companion object {
        const val ID_KEY = "id_key"
        const val DETAIL_STORY = "detail_story"
    }
}