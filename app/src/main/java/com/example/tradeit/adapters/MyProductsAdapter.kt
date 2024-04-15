package com.example.tradeit.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.tradeit.R
import com.example.tradeit.model.Product


class MyProductsAdapter(private val context : Context, private val productsList: ArrayList<Product>)
    : RecyclerView.Adapter<MyProductsAdapter.ProductViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.product_view_holder, parent, false)
        return ProductViewHolder(view)
    }


    override fun getItemCount(): Int {
        return productsList.size
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productsList[position]
        holder.productName.text = product.name
        holder.productPrice.text = "Цена: " + product.price
        holder.roomNumber.text = "Комната: " + product.room
//        Glide.with(context)
//            .load(product.imageUrls.firstOrNull())
//            .placeholder(R.drawable.profile)
//            .error(R.drawable.profile)
//            .into(holder.productImages)
        val imageAdapter = ProductImagePagerAdapter(context, product.imageUrls)
        holder.productImages.adapter = imageAdapter

    }


    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.product_name)
        val productPrice: TextView = itemView.findViewById(R.id.product_price)
        val roomNumber: TextView = itemView.findViewById(R.id.room_number)
        val productImages : ViewPager2 = itemView.findViewById(R.id.product_image)

    }
}