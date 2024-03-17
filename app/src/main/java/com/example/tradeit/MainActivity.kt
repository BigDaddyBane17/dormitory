package com.example.tradeit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.example.tradeit.fragments.LoginFragment

import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



            supportFragmentManager.beginTransaction()
                .replace(R.id.login, LoginFragment())
                .commit()



    }
}