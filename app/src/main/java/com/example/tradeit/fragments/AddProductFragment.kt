package com.example.tradeit.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.navigation.fragment.findNavController
import com.example.tradeit.adapters.ProductImagePagerAdapter
import com.example.tradeit.databinding.FragmentAddProductBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.util.UUID

class AddProductFragment : Fragment() {

    private var _binding: FragmentAddProductBinding? = null
    private val binding get() = _binding!!
    private val REQUEST_IMAGE_PICK = 100
    private lateinit var productImageAdapter: ProductImagePagerAdapter
    private lateinit var storage: FirebaseStorage
    private lateinit var database: DatabaseReference
    private lateinit var roomNumber : String
    private val currentUser = FirebaseAuth.getInstance().currentUser


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        productImageAdapter = ProductImagePagerAdapter(requireContext(), mutableListOf())
        binding.productImagePager.adapter = productImageAdapter
        storage = FirebaseStorage.getInstance()
        database = FirebaseDatabase.getInstance().reference

        binding.addImageButton.setOnClickListener {
            openGallery()
        }


        currentUser?.let { user ->
            val uid = user.uid
            val databaseReference = FirebaseDatabase.getInstance().reference
            val usersReference = databaseReference.child("Users")
            usersReference.child(uid).child("room").addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    roomNumber = dataSnapshot.getValue(String::class.java).toString()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
        }


        binding.saveButton.setOnClickListener() {
            val productName = binding.productNameEditText.text.toString()
            val productPrice = binding.productPriceEditText.text.toString()

            val productId = database.child("Products").push().key
            val product = hashMapOf(
                "name" to productName,
                "price" to productPrice,
                "room" to roomNumber
            )
            productId?.let {
                database.child("Products").child(it).setValue(product)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Товар успешно добавлен", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Ошибка при добавлении товара", Toast.LENGTH_SHORT).show()
                    }
            }
            for (imageUri in productImageAdapter.getImagesUris()) {
                uploadImageToStorage(imageUri, productId)
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddProductBinding.inflate(inflater, container, false)

        return binding.root

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri = data.data
            selectedImageUri?.let { productImageAdapter.addImage(it) }
        }
    }

    private fun uploadImageToStorage(imageUri: Uri, productId: String?) {
        val imageRef: StorageReference = storage.reference.child("product_images/$productId/${UUID.randomUUID()}")

        val uploadTask: UploadTask = imageRef.putFile(imageUri)

        uploadTask.addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                productId?.let {
                    database.child("Products").child(it).child("images").push().setValue(uri.toString())
                }
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(requireContext(), "Ошибка при загрузке изображения: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
    }

}