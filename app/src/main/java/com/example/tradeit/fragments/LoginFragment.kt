package com.example.tradeit.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.tradeit.R
import com.example.tradeit.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)


        binding.loginButton.setOnClickListener() {
            if(binding.emailEdLogin.text.toString().isEmpty() || binding.passwordEdLogin.text.toString().isEmpty()) {
                Toast.makeText(requireContext(), "Заполните все поля!", Toast.LENGTH_SHORT).show()
            }
            else {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(binding.emailEdLogin.text.toString(), binding.passwordEdLogin.text.toString())
                    .addOnCompleteListener() { task ->
                        if (task.isSuccessful) {
                            val transaction = requireActivity().supportFragmentManager.beginTransaction()
                            transaction.replace(R.id.login, HomeScreen()) // замените NewFragment на ваш фрагмент
                            transaction.addToBackStack(null) // добавляем в стек возврата для возможности возврата назад
                            transaction.commit()
                        }
                    }
            }
        }
        binding.noAccount.setOnClickListener() {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.login, RegistrationFragment()) // замените NewFragment на ваш фрагмент
            transaction.addToBackStack(null) // добавляем в стек возврата для возможности возврата назад
            transaction.commit()
        }


        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
