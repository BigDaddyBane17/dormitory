package com.example.tradeit

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tradeit.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater);
        setContentView(binding.root);
        binding.loginButton.setOnClickListener() {
            if(binding.emailEdLogin.text.toString().isEmpty() || binding.passwordEdLogin.text.toString().isEmpty()) {
                Toast.makeText(applicationContext, "Заполните все поля!", Toast.LENGTH_SHORT).show()
            }
            else {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(binding.emailEdLogin.text.toString(), binding.passwordEdLogin.text.toString())
                    .addOnCompleteListener() { task ->
                        if (task.isSuccessful) {
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        }
                    }
            }
        }
        binding.noAccount.setOnClickListener() {
            startActivity(Intent(this@LoginActivity, RegistrationActivity::class.java))
        }
    }
}