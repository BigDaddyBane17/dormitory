package com.example.tradeit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.tradeit.databinding.ActivityLoginBinding
import com.example.tradeit.databinding.ActivityRegistrationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegistrationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistrationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        val email = binding.emailEd.text.toString()
        val password = binding.passwordEd.text.toString()

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val currentUser = FirebaseAuth.getInstance().currentUser
                    currentUser?.let { user ->
                        val userInfo = hashMapOf(
                            "uid" to user.uid,
                            "email" to email,
                            "username" to binding.nameEd.text.toString(),
                            "surname" to binding.surnameEd.text.toString(),
                            "room" to binding.roomEd.text.toString(),
                            "vkLink" to binding.vkLink.text.toString(),
                            "profileImage" to ""
                        )

                        FirebaseDatabase.getInstance().reference.child("Users").child(user.uid)
                            .setValue(userInfo)
                            .addOnCompleteListener { databaseTask ->
                                if (databaseTask.isSuccessful) {
                                    Toast.makeText(applicationContext, "Пользователь успешно зарегистрирован", Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this@RegistrationActivity, LoginActivity::class.java))
                                } else {
                                    Toast.makeText(applicationContext, "Ошибка при регистрации пользователя", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                } else {
                    Toast.makeText(applicationContext, "Ошибка при регистрации пользователя", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
