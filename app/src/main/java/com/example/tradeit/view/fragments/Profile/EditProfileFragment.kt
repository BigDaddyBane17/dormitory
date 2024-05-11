package com.example.tradeit.view.fragments.Profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.tradeit.databinding.FragmentEditProfileBinding
import com.example.tradeit.viewModel.TradeViewModel
import com.google.firebase.auth.FirebaseAuth

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val userId = currentUser?.uid
    private var currentRoom : String = ""
    private val viewModel: TradeViewModel by activityViewModels<TradeViewModel>()
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)


        viewModel.userDataLiveData.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.editTextName.setText(user.username)
                binding.editTextSurname.setText(user.surname)
                binding.editTextVK.setText(user.vkLink)
                binding.editTextRoom.setText(user.room)
                currentRoom = binding.editTextRoom.text.toString()
                Log.d("testLog", "текущая комната - ${currentRoom}")
            }
        }

        viewModel.getUserData(userId)

        binding.buttonSaveChanges.setOnClickListener() {
            val updatedUsername = binding.editTextName.text.toString().trim()
            val updatedSurname = binding.editTextSurname.text.toString().trim()
            val updatedRoom = binding.editTextRoom.text.toString().trim()
            val updatedVkLink = binding.editTextVK.text.toString().trim()

            if (updatedUsername.isNotEmpty() && updatedSurname.isNotEmpty() &&
                updatedRoom.isNotEmpty() && updatedVkLink.isNotEmpty()) {
                viewModel.updateUser(userId, updatedUsername, updatedSurname, updatedRoom, updatedVkLink, currentRoom)
                findNavController().popBackStack()

            } else {
                Toast.makeText(context, "Заполните все поля", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}