package com.example.tradeit.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.tradeit.R
import com.example.tradeit.model.Product
import com.example.tradeit.view.fragments.HomeScreen.HomeScreenDirections

class AllProductsAdapter(private val context : Context, private var allProductsLiveData: LiveData<List<Product>>)
    : RecyclerView.Adapter<AllProductsAdapter.AllProductViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllProductViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.product_view_holder, parent, false)
        return AllProductViewHolder(view)
    }

    fun setFilteredList(filteredList: List<Product>) {
        val filteredLiveData = MutableLiveData<List<Product>>()
        filteredLiveData.value = filteredList
        allProductsLiveData = filteredLiveData
        notifyDataSetChanged()
    }


    override fun getItemCount(): Int {
        return allProductsLiveData.value?.size ?: 0
    }

    override fun onBindViewHolder(holder: AllProductViewHolder, position: Int) {
        val productList = allProductsLiveData.value
        val product = productList?.get(position)

        product?.let {
            holder.productName.text = it.name
            holder.productPrice.text = "Цена: " + it.price
            holder.roomNumber.text = "Комната: " + it.room
            val imageAdapter = ProductImagePagerAdapter(it.imageUrls)
            holder.productImages.adapter = imageAdapter


            holder.itemView.setOnClickListener() {
                val action = HomeScreenDirections.actionSearchFragmentToProductCardFragment(product)
                holder.itemView.findNavController().navigate(action)
            }
        }
    }


    class AllProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.product_name)
        val productPrice: TextView = itemView.findViewById(R.id.product_price)
        val roomNumber: TextView = itemView.findViewById(R.id.room_number)
        val productImages : ViewPager2 = itemView.findViewById(R.id.product_image)

    }

}