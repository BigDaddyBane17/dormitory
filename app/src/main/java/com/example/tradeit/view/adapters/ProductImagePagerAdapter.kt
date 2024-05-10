package com.example.tradeit.view.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tradeit.R

class ProductImagePagerAdapter(private val imageUris: MutableList<Uri>) : RecyclerView.Adapter<ProductImagePagerAdapter.ProductImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductImageViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ProductImageViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProductImageViewHolder, position: Int) {
        val imageUrl = imageUris[position]
        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .placeholder(R.drawable.profile)
            .error(R.drawable.profile)
            .into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return imageUris.size
    }

    fun addImage(imageUri: Uri) {
        imageUris.add(imageUri)
        notifyDataSetChanged()
    }

    fun getImagesUris(): List<Uri> {
        return imageUris.toList()
    }

    class ProductImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }
}