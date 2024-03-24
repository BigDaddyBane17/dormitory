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
        binding = ActivityRegistrationBinding.inflate(layoutInflater);
        setContentView(binding.root);

        binding.registerButton.setOnClickListener() {
            if(binding.emailEd.text.toString().isEmpty() || binding.passwordEd.text.toString().isEmpty() || binding.roomEd.text.toString().isEmpty()
                || binding.nameEd.text.toString().isEmpty() || binding.surnameEd.text.toString().isEmpty() || binding.vkLink.text.toString().isEmpty()) {
                Toast.makeText(applicationContext, "Заполните все поля!", Toast.LENGTH_SHORT).show()
            }
            else {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    binding.emailEd.text.toString(),
                    binding.passwordEd.text.toString()
                ).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userInfo = HashMap<String, String>()
                        userInfo.put("email", binding.emailEd.text.toString())
                        userInfo.put("username", binding.nameEd.text.toString())
                        userInfo.put("surname", binding.surnameEd.text.toString())
                        userInfo.put("room", binding.roomEd.text.toString())
                        userInfo.put("vkLink", binding.vkLink.text.toString())
                        userInfo.put("profileImage", "")
                        FirebaseAuth.getInstance().currentUser?.let { currentUser ->
                            FirebaseDatabase.getInstance().reference.child("Users").child(currentUser.uid).setValue(userInfo)
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
    }
}