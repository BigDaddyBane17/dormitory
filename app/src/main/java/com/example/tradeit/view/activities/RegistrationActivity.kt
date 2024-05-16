package com.example.tradeit.view.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.tradeit.databinding.ActivityRegistrationBinding
import com.example.tradeit.viewModel.TradeViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegistrationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistrationBinding
    private lateinit var viewModel: TradeViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[TradeViewModel::class.java]

        binding.registerButton.setOnClickListener {
            if (isFieldsEmpty()) {
                Toast.makeText(applicationContext, "Заполните все поля!", Toast.LENGTH_SHORT).show()
            } else {
                registerUser()
            }
        }
    }

    private fun isFieldsEmpty(): Boolean {
        return binding.emailEd.text.toString().isEmpty() ||
                binding.passwordEd.text.toString().isEmpty() ||
                binding.roomEd.text.toString().isEmpty() ||
                binding.nameEd.text.toString().isEmpty() ||
                binding.surnameEd.text.toString().isEmpty() ||
                binding.vkLink.text.toString().isEmpty()
    }

    private fun registerUser() {
        val email = binding.emailEd.text.toString().trim()
        val password = binding.passwordEd.text.toString().trim()
        val name = binding.nameEd.text.toString().trim()
        val surname = binding.surnameEd.text.toString().trim()
        val room = binding.roomEd.text.toString().trim()
        val vkLink = binding.vkLink.text.toString().trim()

        viewModel.registerUser(email, password, name, surname, room, vkLink,
            onSuccess = {
                Toast.makeText(applicationContext, "Пользователь успешно зарегистрирован", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@RegistrationActivity, LoginActivity::class.java))
            },
            onError = {
                Toast.makeText(applicationContext, "Ошибка при регистрации пользователя", Toast.LENGTH_SHORT).show()
            }
        )
    }
}
