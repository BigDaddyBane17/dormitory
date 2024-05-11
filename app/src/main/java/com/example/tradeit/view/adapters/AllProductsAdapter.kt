package com.example.tradeit.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.tradeit.R
import com.example.tradeit.model.Product
import com.example.tradeit.view.fragments.HomeScreen.HomeScreenDirections

class AllProductsAdapter(private val context : Context, private var productsList: ArrayList<Product>)
    : RecyclerView.Adapter<AllProductsAdapter.AllProductViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllProductViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.product_view_holder, parent, false)
        return AllProductViewHolder(view)
    }

    fun setFilteredList(filteredList : ArrayList<Product>) {
        this.productsList = filteredList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return productsList.size
    }

    override fun onBindViewHolder(holder: AllProductViewHolder, position: Int) {
        val product = productsList[position]
        holder.productName.text = product.name
        holder.productPrice.text = "Цена: " + product.price
        holder.roomNumber.text = "Комната: " + product.room
        val imageAdapter = ProductImagePagerAdapter(product.imageUrls)
        holder.productImages.adapter = imageAdapter


        holder.itemView.setOnClickListener() {
            val action = HomeScreenDirections.actionSearchFragmentToProductCardFragment(product)
            holder.itemView.findNavController().navigate(action)
        }
    }


    class AllProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.product_name)
        val productPrice: TextView = itemView.findViewById(R.id.product_price)
        val roomNumber: TextView = itemView.findViewById(R.id.room_number)
        val productImages : ViewPager2 = itemView.findViewById(R.id.product_image)

    }

}