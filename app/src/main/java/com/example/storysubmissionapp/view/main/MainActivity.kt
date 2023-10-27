package com.example.storysubmissionapp.view.main

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storysubmissionapp.R
import com.example.storysubmissionapp.data.model.UserModel
import com.example.storysubmissionapp.data.showToast
import com.example.storysubmissionapp.databinding.ActivityMainBinding
import com.example.storysubmissionapp.view.ViewModelFactory
import com.example.storysubmissionapp.view.adapter.StoryAdapter
import com.example.storysubmissionapp.view.upload.UploadActivity
import com.example.storysubmissionapp.view.welcome.WelcomeActivity
import kotlinx.coroutines.launch
import com.example.storysubmissionapp.data.Result

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, UploadActivity::class.java)
            startActivity(intent)
        }
        viewModel.getSessionData().observe(this@MainActivity) { user ->
            if (!user.isLogin) {
                val intent = Intent(this, WelcomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            } else {
                loadStories(user)
            }
        }
    }

    private fun loadStories(user: UserModel) {
        viewModel.getStories(user.token).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        showLoading(true)
                        binding.progressBar.isVisible = true
                    }

                    is Result.Error -> {
                        showLoading(false)
                        showToast(result.error)
                        binding.apply {
                            binding.progressBar.isVisible = false
                        }
                    }

                    is Result.Success -> {
                        showLoading(false)
                        binding.progressBar.isVisible = false
                        val layoutManager = LinearLayoutManager(this)
                        binding.rvStory.layoutManager = layoutManager
                        binding.rvStory.adapter = StoryAdapter(result.data.listStory)
                    }
                }
            }
        }
    }

    private fun showLoading(state: Boolean) {
        if (state) binding.progressBar.visibility = View.VISIBLE
        else binding.progressBar.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        binding
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_item, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                lifecycleScope.launch {
                    viewModel.logout()
                }
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }

            R.id.settings -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }
        }
        return super.onOptionsItemSelected(item)
    }
}