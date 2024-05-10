package com.example.tradeit.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.tradeit.R
import com.example.tradeit.databinding.FragmentEditProfileBinding
import com.example.tradeit.databinding.FragmentHomeScreenBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val userId = currentUser?.uid

    private lateinit var username : String
    private lateinit var surname : String
    private lateinit var room : String
    private lateinit var vkLink : String
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)


        val databaseReference = userId?.let {
            FirebaseDatabase.getInstance().reference.child("Users").child(it)
        }

        databaseReference?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val userData = snapshot.value as? Map<*, *>
                    userData?.let {
                        username = userData["username"] as? String ?: ""
                        surname = userData["surname"] as? String ?: ""
                        vkLink = userData["vkLink"] as? String ?: ""
                        room = userData["room"] as? String ?: ""

                        binding.editTextName.setText(username)
                        binding.editTextSurname.setText(surname)
                        binding.editTextVK.setText(vkLink)
                        binding.editTextRoom.setText(room)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        binding.buttonSaveChanges.setOnClickListener() {
            val updatedUsername = binding.editTextName.text.toString().trim()
            val updatedSurname = binding.editTextSurname.text.toString().trim()
            val updatedRoom = binding.editTextRoom.text.toString().trim()
            val updatedVkLink = binding.editTextVK.text.toString().trim()

            if (updatedUsername.isNotEmpty() && updatedSurname.isNotEmpty() &&
                updatedRoom.isNotEmpty() && updatedVkLink.isNotEmpty()) {
                userId?.let { userId ->
                    val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(userId)

                    val updatedUserData = hashMapOf(
                        "username" to updatedUsername,
                        "surname" to updatedSurname,
                        "room" to updatedRoom,
                        "vkLink" to updatedVkLink
                    )

                    updateRoomNumberForUserProducts(userId, room, updatedRoom)

                    userRef.updateChildren(updatedUserData as Map<String, Any>)
                        .addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                Toast.makeText(context, "Данные успешно обновлены", Toast.LENGTH_SHORT).show()
                                findNavController().popBackStack()
                            } else {
                                Toast.makeText(context, "Не удалось обновить данные", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            } else {
                Toast.makeText(context, "Заполните все поля", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    private fun updateRoomNumberForUserProducts(userId: String?, currentRoomNumber: String, updatedRoomNumber: String) {
        userId?.let { userId ->
            val productsRef = FirebaseDatabase.getInstance().reference.child("Products")
            productsRef.orderByChild("room").equalTo(currentRoomNumber).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (productSnapshot in snapshot.children) {
                        // Обновляем номер комнаты для каждого товара
                        productSnapshot.ref.child("room").setValue(updatedRoomNumber)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Обработка ошибок
                }
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}