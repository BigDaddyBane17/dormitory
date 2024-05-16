package com.example.tradeit.view.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.tradeit.databinding.ActivityLoginBinding
import com.example.tradeit.viewModel.TradeViewModel
import com.google.firebase.auth.FirebaseAuth


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: TradeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater);
        setContentView(binding.root);
        viewModel = ViewModelProvider(this)[TradeViewModel::class.java]

        binding.loginButton.setOnClickListener {
            if (binding.emailEdLogin.text.toString().isEmpty() || binding.passwordEdLogin.text.toString().isEmpty()) {
                Toast.makeText(applicationContext, "Заполните все поля!", Toast.LENGTH_SHORT).show()
            } else {
                loginUser()
            }
        }

        binding.noAccount.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegistrationActivity::class.java))
        }
    }

    private fun loginUser() {
        val email = binding.emailEdLogin.text.toString()
        val password = binding.passwordEdLogin.text.toString()

        viewModel.loginUser(email, password) {
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
        }
    }
}