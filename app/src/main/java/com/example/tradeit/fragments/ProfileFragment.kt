package com.example.tradeit.fragments

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
import com.bumptech.glide.Glide
import com.example.tradeit.LoginActivity
import com.example.tradeit.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage


class ProfileFragment : Fragment() {


    private var _binding: FragmentProfileBinding? = null
    private var filePath: Uri? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        loadUserInfo()


        binding.avatar.setOnClickListener() {
            selectImage()
        }



        binding.logoutButton.setOnClickListener() {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }


        return binding.root


    }


    private fun loadUserInfo() {
        FirebaseDatabase.getInstance().reference.child("Users").child(
            FirebaseAuth.getInstance().currentUser!!.uid
        )
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val username = snapshot.child("username").value.toString()
                    val surname = snapshot.child("surname").value.toString()
                    val room = snapshot.child("room").value.toString()
                    val vkLink = snapshot.child("vkLink").value.toString()
                    val profileImage = snapshot.child("profileImage").value.toString()
                    binding.nameTv.text = "$username $surname"
                    binding.roomEdNumber.text = room
                    binding.pageEdLink.text = vkLink
                    if (!profileImage.isEmpty()) {
                        context?.let { Glide.with(it).load(profileImage).into(binding.avatar) }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
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

                uploadImage()
            }
        }


    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        pickImageActivityResultLauncher.launch(intent)
    }

    private fun uploadImage() {
        filePath?.let { filePath ->
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            val storageReference = FirebaseStorage.getInstance().getReference("images/$uid")

            storageReference.putFile(filePath)
                .addOnSuccessListener { taskSnapshot ->
                    Toast.makeText(
                        requireContext(),
                        "Фото профиля обновленно!",
                        Toast.LENGTH_SHORT
                    ).show()

                    storageReference.downloadUrl.addOnSuccessListener { uri ->
                        FirebaseDatabase.getInstance().getReference("Users").child(uid!!)
                            .child("profileImage").setValue(uri.toString())
                    }
                }
        }
    }



}