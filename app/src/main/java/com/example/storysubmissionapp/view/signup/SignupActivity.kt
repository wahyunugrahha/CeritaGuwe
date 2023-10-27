package com.example.storysubmissionapp.view.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.storysubmissionapp.R
import com.example.storysubmissionapp.data.ValidateType
import com.example.storysubmissionapp.data.validate
import com.example.storysubmissionapp.databinding.ActivitySignupBinding
import com.example.storysubmissionapp.view.ViewModelFactory
import com.example.storysubmissionapp.view.login.LoginActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.example.storysubmissionapp.data.Result
import com.example.storysubmissionapp.data.showToast


class SignupActivity : AppCompatActivity() {

    private val viewModel: SignupViewModel by viewModels { ViewModelFactory.getInstance(this) }
    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
        playAnimation()
        setupAction()
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f)
            .apply {
                duration = 6000
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.REVERSE
            }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f)
            .setDuration(100)
        val nameTextView = ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f)
            .setDuration(100)
        val nameEditTextLayout =
            ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f)
                .setDuration(100)
        val nameEditText = ObjectAnimator.ofFloat(binding.nameEditText, View.ALPHA, 1f)
            .setDuration(100)
        val emailTextView =
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f)
                .setDuration(100)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f)
                .setDuration(100)
        val emailEditText =
            ObjectAnimator.ofFloat(binding.emailEditText, View.ALPHA, 1f)
                .setDuration(100)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f)
                .setDuration(100)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f)
                .setDuration(100)
        val passwordEditText =
            ObjectAnimator.ofFloat(binding.passwordEditText, View.ALPHA, 1f)
                .setDuration(100)
        val signupButton = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f)
            .setDuration(100)

        val name = AnimatorSet().apply {
            playTogether(
                nameTextView,
                nameEditTextLayout,
                nameEditText
            )
        }
        val email = AnimatorSet().apply {
            playTogether(
                emailTextView,
                emailEditTextLayout,
                emailEditText
            )
        }
        val password = AnimatorSet().apply {
            playTogether(
                passwordTextView,
                passwordEditTextLayout,
                passwordEditText
            )
        }

        AnimatorSet().apply {
            playSequentially(
                title,
                name,
                email,
                password,
                signupButton
            )
            start()
        }
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

    private fun setupAction() {
        binding.apply {
            signupButton.setOnClickListener {
                if (validateForm()) {
                    val name = nameEditText.text.toString()
                    val email = emailEditText.text.toString()
                    val password = passwordEditText.text.toString()

                    viewModel.register(name, email, password)
                        .observe(this@SignupActivity) { result ->
                            if (result != null) {
                                when (result) {
                                    is Result.Loading -> {
                                        showLoading(true)
                                        signupButton.isEnabled = false

                                    }

                                    is Result.Error -> {
                                        showLoading(false)
                                        pbSignup.isVisible = false
                                        showToast(result.error)
                                    }

                                    is Result.Success -> {
                                        showLoading(false)
                                        pbSignup.isVisible = false
                                        MaterialAlertDialogBuilder(this@SignupActivity)
                                            .setTitle(getString(R.string.your_account_has_been_created))
                                            .setMessage(getString(R.string.please_login_to_your_account))
                                            .setPositiveButton("Login") { dialog, _ ->
                                                dialog.dismiss()
                                                val intent =
                                                    Intent(
                                                        this@SignupActivity,
                                                        LoginActivity::class.java
                                                    )
                                                intent.flags =
                                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                startActivity(intent)
                                            }
                                            .create().show()
                                    }
                                }
                            }
                        }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.pbSignup.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.signupButton.isEnabled = !isLoading
    }

    private fun validateForm(): Boolean {
        binding.apply {
            val validates = listOf(
                emailEditText.validate("Email", ValidateType.REQUIRED),
                emailEditText.validate("Email", ValidateType.EMAIL),
                nameEditText.validate("Name", ValidateType.REQUIRED),
                passwordEditText.validate("Password", ValidateType.REQUIRED),
                passwordEditText.validate("Password", ValidateType.MIN_CHAR),
            )

            return !validates.contains(false)
        }
    }
}