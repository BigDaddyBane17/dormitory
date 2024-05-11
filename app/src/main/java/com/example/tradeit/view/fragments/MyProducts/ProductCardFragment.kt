package com.example.tradeit.view.fragments.MyProducts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.tradeit.view.adapters.ProductImagePagerAdapter
import com.example.tradeit.databinding.FragmentProductCardBinding
import com.example.tradeit.model.Product

class ProductCardFragment : Fragment() {

    private var _binding: FragmentProductCardBinding? = null
    private val binding get() = _binding!!

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
        return binding.root

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}