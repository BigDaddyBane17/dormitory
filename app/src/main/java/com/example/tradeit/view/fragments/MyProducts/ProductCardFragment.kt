package com.example.tradeit.view.fragments.MyProducts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.tradeit.view.adapters.ProductImagePagerAdapter
import com.example.tradeit.databinding.FragmentProductCardBinding
import com.example.tradeit.model.Product
import com.example.tradeit.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProductCardFragment : Fragment() {

    private var _binding: FragmentProductCardBinding? = null
    private val binding get() = _binding!!
    private var username : String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProductCardBinding.inflate(inflater, container, false)

        val product = arguments?.getParcelable<Product>("product")

        product?.let {
            binding.productNameCard.text = product.name
            binding.productPriceCard.text = product.price + " ₽"
            binding.productDescriptionCard.text = product.description
            binding.ownerRoom.text = "Комната: " + product.room
            val imageAdapter = ProductImagePagerAdapter(product.imageUrls)
            binding.productImageCard.adapter = imageAdapter
        }

        val uid = product?.let {
            FirebaseDatabase.getInstance().reference.child("Users").child(it.userId).key
        }

        uid?.let {
            FirebaseDatabase.getInstance().reference.child("Users").child(
                it
            )
        }?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                username = user?.username.toString()
                val surname = user?.surname
                val profileImage = user?.profileImage
                binding.ownerName.text = "$username $surname"
                profileImage?.let {
                    Glide.with(requireContext())
                        .load(it)
                        .circleCrop()
                        .into(binding.avatarProductCard)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })

        binding.writeButton.setOnClickListener {

            val action = ProductCardFragmentDirections.actionProductCardFragmentToDialogFragment(
                username,
                uid!!
            )
            findNavController().navigate(action)
        }

        return binding.root

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}