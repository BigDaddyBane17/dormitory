package com.example.tradeit.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.tradeit.R
import com.example.tradeit.model.Product
import com.example.tradeit.view.fragments.MyProducts.AdFragmentDirections


class MyProductsAdapter(private val context: Context, private val productsLiveData: LiveData<List<Product>>)
    : RecyclerView.Adapter<MyProductsAdapter.ProductViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.product_view_holder, parent, false)
        return ProductViewHolder(view)
    }

    override fun getItemCount(): Int {
        return productsLiveData.value?.size ?: 0
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val productList = productsLiveData.value
        val product = productList?.get(position)

        product?.let {
            holder.productName.text = it.name
            holder.productPrice.text = "Цена: " + it.price
            holder.roomNumber.text = "Комната: " + it.room
            val imageAdapter = ProductImagePagerAdapter(it.imageUrls)
            holder.productImages.adapter = imageAdapter

            val productForNavigation = it

            holder.itemView.setOnClickListener {
                val action = AdFragmentDirections.actionAdFragmentToProductCardFragment(productForNavigation)
                holder.itemView.findNavController().navigate(action)
            }
        }
    }

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.product_name)
        val productPrice: TextView = itemView.findViewById(R.id.product_price)
        val roomNumber: TextView = itemView.findViewById(R.id.room_number)
        val productImages : ViewPager2 = itemView.findViewById(R.id.product_image)
    }
}