package com.example.tradeit.view.fragments.HomeScreen

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tradeit.view.adapters.AllProductsAdapter
import com.example.tradeit.databinding.FragmentHomeScreenBinding
import com.example.tradeit.model.Product
import com.example.tradeit.view.adapters.MyProductsAdapter
import com.example.tradeit.viewModel.TradeViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class HomeScreen : Fragment() {
    private var _binding: FragmentHomeScreenBinding? = null
    private lateinit var productsRecyclerView: RecyclerView
    private lateinit var productsList : ArrayList<Product>
    private lateinit var adapter : AllProductsAdapter
    private lateinit var mAuth : FirebaseAuth
    private lateinit var mDbRef : DatabaseReference
    private val viewModel: TradeViewModel by activityViewModels<TradeViewModel>()


    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeScreenBinding.inflate(inflater, container, false)


        productsRecyclerView = binding.allProductRecyclerView
        val productsList = ArrayList<Product>()
        adapter = AllProductsAdapter(requireContext(), viewModel.productsLiveData)
        binding.allProductRecyclerView.layoutManager = GridLayoutManager(context, 2)
        binding.allProductRecyclerView.adapter = adapter


        viewModel.loadAllProducts()


        viewModel.AllProductsLiveData.observe(viewLifecycleOwner) { products ->
            productsList.clear()
            productsList.addAll(products)
            adapter.notifyDataSetChanged()
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                //filterList(newText)
                return true
            }
        })


        return binding.root

    }


//    fun filterList(text : String) {
//        val filteredList : LiveData<List<Product>>
//
//        for(product in productsList) {
//            if(product.name.lowercase().contains(text.lowercase())) {
//                filteredList.add(product)
//            }
//        }
//        if(filteredList.isEmpty()) {
//            //Toast.makeText(requireContext(), "Не найдено", Toast.LENGTH_SHORT).show()
//        }
//        else {
//            adapter.setFilteredList(filteredList)
//        }
//    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}