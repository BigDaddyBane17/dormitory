package com.example.tradeit.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.tradeit.R

class ProductImagePagerAdapter(private val imageUris: MutableList<Uri>) : RecyclerView.Adapter<ProductImagePagerAdapter.ProductImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductImageViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ProductImageViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProductImageViewHolder, position: Int) {
        holder.bind(imageUris[position])
    }

    override fun getItemCount(): Int {
        return imageUris.size
    }

    fun addImage(imageUri: Uri) {
        imageUris.add(imageUri)
        notifyItemInserted(imageUris.size - 1)
    }

    fun getImagesUris(): List<Uri> {
        return imageUris.toList()
    }

    class ProductImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageView)

        fun bind(imageUri: Uri) {
            imageView.setImageURI(imageUri)
        }
    }
}
