package com.example.tradeit.view.fragments.MyProducts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tradeit.R
import com.example.tradeit.databinding.FragmentAdBinding
import com.example.tradeit.model.Product
import com.example.tradeit.view.adapters.MyProductsAdapter
import com.example.tradeit.viewModel.TradeViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference

class AdFragment : Fragment() {
    private var _binding: FragmentAdBinding? = null
    private lateinit var productsRecyclerView: RecyclerView
    private lateinit var productsList : ArrayList<Product>
    private lateinit var adapter : MyProductsAdapter
    private lateinit var mAuth : FirebaseAuth
    private lateinit var mDbRef : DatabaseReference
    private lateinit var userId : String
    private val viewModel: TradeViewModel by activityViewModels<TradeViewModel>()


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
        val productsList = ArrayList<Product>()
        adapter = MyProductsAdapter(requireContext(), viewModel.productsLiveData)
        productsRecyclerView.layoutManager = GridLayoutManager(context, 2)
        productsRecyclerView.adapter = adapter


        FirebaseAuth.getInstance().currentUser?.uid?.let { userId ->
            this.userId = userId
            viewModel.loadProductsForUser(userId)
        }

        viewModel.productsLiveData.observe(viewLifecycleOwner) { products ->
            productsList.clear()
            productsList.addAll(products)
            adapter.notifyDataSetChanged()
        }
        return binding.root

    }


}