package com.example.tradeit.view.fragments.Profile

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.tradeit.view.activities.LoginActivity
import com.example.tradeit.R
import com.example.tradeit.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.example.tradeit.viewModel.TradeViewModel

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val viewModel: TradeViewModel by activityViewModels<TradeViewModel>()

    private val binding get() = _binding!!

    private var filePath: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        viewModel.loadUserInfo()


        binding.avatar.setOnClickListener {
            selectImage()
        }

        binding.editButton.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }

        binding.logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }

        viewModel.userDataLiveData.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.nameTv.text = "${user.username} ${user.surname}"
                binding.roomEdNumber.text = user.room
                binding.pageEdLink.text = user.vkLink
                if (user.profileImage?.isNotEmpty() == true) {
                    Glide.with(requireContext()).load(user.profileImage).into(binding.avatar)
                }
            }
        }
        viewModel.loadUserInfo()


        return binding.root
    }



    private val pickImageActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null && result.data!!.data != null) {
                filePath = result.data!!.data

                try {
                    val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(
                        requireContext().contentResolver,
                        filePath
                    )
                    binding.avatar.setImageBitmap(bitmap)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                viewModel.uploadImage(filePath)
                Toast.makeText(
                    requireContext(),
                    "Фото профиля обновлено!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        pickImageActivityResultLauncher.launch(intent)
    }

}