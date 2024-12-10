package com.example.daunsehat.features.authentication.login.presentation

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.daunsehat.features.main.MainActivity
import com.example.daunsehat.data.pref.UserModel
import com.example.daunsehat.data.repository.ResultApi
import com.example.daunsehat.databinding.ActivityLoginBinding
import com.example.daunsehat.features.authentication.login.presentation.viewmodel.LoginViewModel
import com.example.daunsehat.features.authentication.register.presentation.RegisterActivity
import com.example.daunsehat.utils.ViewModelFactory

class LoginActivity : AppCompatActivity() {
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.isEnabled = false

        setupAction()
    }

    private fun setupAction() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Do nothing
            }

            override fun afterTextChanged(s: Editable?) {
                validateInput()
            }
        }

        binding.emailEditText.addTextChangedListener(textWatcher)
        binding.passwordEditText.addTextChangedListener(textWatcher)

        binding.btnLogin.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            viewModel.loginUser(email, password).observe(this) { result ->
                when (result) {
                    is ResultApi.Loading -> {
                        showLoading(true)
                        Log.d("LoginActivity", "Loading: Login request in progress")
                    }

                    is ResultApi.Success -> {
                        showLoading(false)
                        Log.d("LoginActivity", "Success: Login successful - ${result.data}")
                        result.data.token?.let { it1 ->
                            UserModel(email,
                                it1
                            )
                        }?.let { it2 -> viewModel.saveSession(it2) }
                        val dialog = AlertDialog.Builder(this)
                            .setTitle("Yeah!")
                            .setMessage("Anda berhasil login.")
                            .setPositiveButton("Lanjut") { _, _ ->
                                navigateToMain()
                            }
                            .create()

                        dialog.setOnDismissListener {
                            navigateToMain()
                        }
                        dialog.show()
                    }

                    is ResultApi.Error -> {
                        showLoading(false)
                        Log.e("LoginActivity", "ErrorLogin: ${result.error}")
                        AlertDialog.Builder(this).apply {
                            setTitle("Error")
                            setMessage(result.error)
                            setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                            create()
                            show()
                        }
                    }
                }
            }
        }

        binding.txtRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun validateInput() {
        val email = binding.emailEditText.text.toString()
        val password = binding.passwordEditText.text.toString()

        val isEmailValid = email.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val isPasswordValid = password.isNotEmpty() && password.length >= 8

        binding.btnLogin.isEnabled = isEmailValid && isPasswordValid
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}