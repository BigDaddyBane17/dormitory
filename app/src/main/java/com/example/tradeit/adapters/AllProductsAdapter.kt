package com.example.tradeit.adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tradeit.R
import com.example.tradeit.model.User

class AllProductsAdapter(private val context : Context, private val userList: ArrayList<User>)
    : RecyclerView.Adapter<AllProductsAdapter.AllProductsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllProductsViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.user_view_holder, parent, false)
        return AllProductsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: AllProductsViewHolder, position: Int) {
        val currentProduct = userList[position]


    }


    class AllProductsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
}