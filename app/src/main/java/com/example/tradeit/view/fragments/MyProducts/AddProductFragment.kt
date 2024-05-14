package com.example.tradeit.view.fragments.MyProducts

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.tradeit.databinding.FragmentAddProductBinding
import com.example.tradeit.view.adapters.ProductImagePagerAdapter
import com.example.tradeit.viewModel.TradeViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class AddProductFragment : Fragment() {

    private var _binding: FragmentAddProductBinding? = null
    private val viewModel: TradeViewModel by activityViewModels<TradeViewModel>()
    private val binding get() = _binding!!
    private val REQUEST_IMAGE_PICK = 100
    private lateinit var productImageAdapter: ProductImagePagerAdapter
    private lateinit var storage: FirebaseStorage
    private lateinit var database: DatabaseReference
    private lateinit var roomNumber : String
    private val currentUser = FirebaseAuth.getInstance().currentUser


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        productImageAdapter = ProductImagePagerAdapter(mutableListOf())
        binding.productImagePager.adapter = productImageAdapter
        storage = FirebaseStorage.getInstance()
        database = FirebaseDatabase.getInstance().reference

        binding.addImageButton.setOnClickListener {
            openGallery()
        }


        //получение номера комнаты текущего юзера
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



        //добавление продукта в базу
        binding.saveButton.setOnClickListener() {
            val productName = binding.productNameEditText.text.toString()
            val productPrice = binding.productPriceEditText.text.toString()
            val descriptionText = binding.descriptionEditText.text.toString()
            val userId = currentUser?.uid

            binding.loadingProgressBar.visibility = View.VISIBLE

            if (userId != null) {
                viewModel.addProduct(productName, productPrice, roomNumber, descriptionText, userId, productImageAdapter)
            }
            findNavController().popBackStack()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddProductBinding.inflate(inflater, container, false)

        return binding.root

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



}