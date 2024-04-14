package com.example.tradeit.fragments

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tradeit.R
import com.example.tradeit.adapters.MyProductsAdapter
import com.example.tradeit.adapters.UserAdapter
import com.example.tradeit.databinding.FragmentAdBinding
import com.example.tradeit.databinding.FragmentHomeScreenBinding
import com.example.tradeit.model.Product
import com.example.tradeit.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdFragment : Fragment() {


    private var _binding: FragmentAdBinding? = null
    private lateinit var productsRecyclerView: RecyclerView
    private lateinit var productsList : ArrayList<Product>
    private lateinit var adapter : MyProductsAdapter
    private lateinit var mAuth : FirebaseAuth
    private lateinit var mDbRef : DatabaseReference
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdBinding.inflate(inflater, container, false)


        binding.addProductBtn.setOnClickListener() {
            findNavController().navigate(R.id.action_adFragment_to_addProductFragment)
        }

        productsRecyclerView = binding.myProductRecyclerView
        productsList = ArrayList()
        adapter = context?.let { MyProductsAdapter( it, productsList) }!!
        binding.myProductRecyclerView.layoutManager = GridLayoutManager(context, 2)
        binding.myProductRecyclerView.adapter = adapter

        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().reference


        mDbRef.child("Products").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                productsList.clear() // Очищаем список перед загрузкой новых данных
                for (productSnapshot in snapshot.children) {
                    val currentProduct = productSnapshot.getValue(Product::class.java)
                    currentProduct?.let {
                        val imageUrls = mutableListOf<Uri>()
                        val imagesSnapshot = productSnapshot.child("images")
                        for (imageSnapshot in imagesSnapshot.children) {
                            val imageUrl = Uri.parse(imageSnapshot.getValue(String::class.java))
                            imageUrls.add(imageUrl)
                        }
                        it.imageUrls = imageUrls

                        productsList.add(it)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })



        return binding.root

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}