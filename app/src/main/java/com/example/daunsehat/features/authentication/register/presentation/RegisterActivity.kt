package com.example.daunsehat.features.authentication.register.presentation

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.daunsehat.data.repository.ResultApi
import com.example.daunsehat.databinding.ActivityRegisterBinding
import com.example.daunsehat.features.authentication.login.presentation.LoginActivity
import com.example.daunsehat.features.authentication.register.presentation.viewmodel.RegisterViewModel
import com.example.daunsehat.utils.ViewModelFactory

class RegisterActivity : AppCompatActivity() {

    private val viewModel by viewModels<RegisterViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.isEnabled = false
        setupAction()
    }

    private fun setupAction() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                validateInput()
            }
        }

        binding.emailEditText.addTextChangedListener(textWatcher)
        binding.nameEditText.addTextChangedListener(textWatcher)
        binding.passwordEditText.addTextChangedListener(textWatcher)
        binding.repeatPasswordEditText.addTextChangedListener(textWatcher)

        binding.btnRegister.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val name = binding.nameEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val repeatPassword = binding.repeatPasswordEditText.text.toString()

            if (password != repeatPassword) {
                binding.repeatPasswordEditTextLayout.error = "Password tidak cocok"
                AlertDialog.Builder(this)
                    .setTitle("Password Tidak Cocok")
                    .setMessage("Password dan Ulangi Password harus sama. Silakan periksa kembali.")
                    .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                    .show()
                return@setOnClickListener
            } else {
                binding.repeatPasswordEditTextLayout.error = null
            }

            viewModel.registerUser(email, name, password).observe(this) { result ->
                when (result) {
                    is ResultApi.Loading -> showLoading(true)
                    is ResultApi.Success -> {
                        showLoading(false)
                        AlertDialog.Builder(this)
                            .setTitle("Registrasi Berhasil")
                            .setMessage("Selamat datang, ${result.data.user?.name}. Silakan login!")
                            .setPositiveButton("OK") { _, _ -> navigateToLogin() }
                            .show()
                    }
                    is ResultApi.Error -> {
                        showLoading(false)
                        AlertDialog.Builder(this)
                            .setTitle("Registrasi Gagal")
                            .setMessage(result.error)
                            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                            .show()
                    }
                }
            }
        }

        binding.txtLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun validateInput() {
        val email = binding.emailEditText.text.toString()
        val name = binding.nameEditText.text.toString()
        val password = binding.passwordEditText.text.toString()

        val isEmailValid = email.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val isNameValid = name.isNotEmpty()
        val isPasswordValid = password.isNotEmpty() && password.length >= 8

        binding.btnRegister.isEnabled = isEmailValid && isNameValid && isPasswordValid
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}