package com.example.tradeit.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.tradeit.R
import com.example.tradeit.databinding.FragmentLoginBinding
import com.example.tradeit.databinding.FragmentRegistrationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class RegistrationFragment : Fragment() {
    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)

        binding.registerButton.setOnClickListener() {
            if(binding.emailEd.text.toString().isEmpty() || binding.passwordEd.text.toString().isEmpty() || binding.roomEd.text.toString().isEmpty()
                || binding.nameEd.text.toString().isEmpty() || binding.surnameEd.text.toString().isEmpty()) {
                Toast.makeText(requireContext(), "Заполните все поля!", Toast.LENGTH_SHORT).show()
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
                        FirebaseAuth.getInstance().currentUser?.let { currentUser ->
                            FirebaseDatabase.getInstance().reference.child("Users").child(currentUser.uid).setValue(userInfo)
                                .addOnCompleteListener { databaseTask ->
                                    if (databaseTask.isSuccessful) {
                                        Toast.makeText(requireContext(), "Пользователь успешно зарегистрирован", Toast.LENGTH_SHORT).show()
                                        val transaction = requireActivity().supportFragmentManager.beginTransaction()
                                        transaction.replace(R.id.login, LoginFragment())
                                        transaction.addToBackStack(null) // добавляем в стек возврата для возможности возврата назад
                                        transaction.commit()
                                    } else {
                                        Toast.makeText(requireContext(), "Ошибка при регистрации пользователя", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }
                    } else {
                        Toast.makeText(requireContext(), "Ошибка при регистрации пользователя", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }

        return binding.root
    }

}
