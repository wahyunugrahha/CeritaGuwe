package com.example.storysubmissionapp.view.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import com.example.storysubmissionapp.R
import com.example.storysubmissionapp.databinding.ActivityLoginBinding
import com.example.storysubmissionapp.view.ViewModelFactory
import com.example.storysubmissionapp.view.main.MainActivity
import com.example.storysubmissionapp.data.Result
import com.example.storysubmissionapp.data.ValidateType
import com.example.storysubmissionapp.data.showToast
import com.example.storysubmissionapp.data.validate


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels { ViewModelFactory.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
        playAnimation()
        setupAction()

    }

    private fun setupAction() {
        binding.apply {
            loginButton.setOnClickListener {
                if (validateForm()) {
                    val email = emailEditText.text.toString()
                    val password = passwordEditText.text.toString()

                    viewModel.login(email, password)
                        .observe(this@LoginActivity) { result ->
                            if (result != null) {
                                when (result) {
                                    is Result.Loading -> {
                                        showLoading(true)
                                    }

                                    is Result.Error -> {
                                        showLoading(false)
                                        showToast("Error: ${result.error}")
                                    }

                                    is Result.Success -> {
                                        showLoading(false)
                                        showToast(getString(R.string.success, "Login"))
                                        val intent =
                                            Intent(this@LoginActivity, MainActivity::class.java)
                                        intent.flags =
                                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        startActivity(intent)
                                    }
                                }
                            }
                        }
                }
            }

        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.pbLogin.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.loginButton.isEnabled = !isLoading
    }

    private fun validateForm(): Boolean {
        binding.apply {
            val validates = listOf(
                emailEditText.validate("Email", ValidateType.REQUIRED),
                emailEditText.validate("Email", ValidateType.EMAIL),
                passwordEditText.validate("Password", ValidateType.REQUIRED),
                passwordEditText.validate("Password", ValidateType.MIN_CHAR),
            )

            return !validates.contains(false)
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(100)
        val message =
            ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(100)
        val emailTextView =
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(100)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(100)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                title,
                message,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                login
            )
            startDelay = 100
        }.start()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }
}