package com.example.tradeit.view.fragments.MyProducts

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.tradeit.databinding.FragmentMyProductCardBinding

import com.example.tradeit.model.Product
import com.example.tradeit.view.adapters.ProductImagePagerAdapter
import com.example.tradeit.viewModel.TradeViewModel


class MyProductCardFragment : Fragment() {

    private var _binding: FragmentMyProductCardBinding? = null
    private val viewModel: TradeViewModel by activityViewModels<TradeViewModel>()

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyProductCardBinding.inflate(inflater, container, false)
        val product = arguments?.getParcelable<Product>("product")
        product?.let {
            binding.myProductNameCard.text = product.name
            binding.myProductPriceCard.text = product.price + " ₽"
            binding.myProductDescriptionCard.text = product.description
            binding.myOwnerRoom.text = "Комната: " + product.room
            val imageAdapter = ProductImagePagerAdapter(product.imageUrls)
            binding.myProductImageCard.adapter = imageAdapter
        }


        binding.deleteProduct.setOnClickListener() {
            showDeleteConfirmationDialog(product?.productId ?: "")
        }


        return binding.root

    }

    private fun showDeleteConfirmationDialog(productId: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Удалить продукт")
            .setMessage("Вы уверены, что хотите удалить этот продукт?")
            .setPositiveButton("Да") { _, _ ->
                viewModel.deleteProduct(productId)
                parentFragmentManager.popBackStack()

            }
            .setNegativeButton("Отмена", null)
            .show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}