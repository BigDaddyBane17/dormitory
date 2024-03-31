package com.example.tradeit.adapters

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tradeit.R
import com.example.tradeit.fragments.ChatFragment
import com.example.tradeit.fragments.DialogFragment
import com.example.tradeit.model.User
import com.google.firebase.auth.FirebaseAuth


class UserAdapter(private val context : Context, private val userList: ArrayList<User>)
    : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.user_view_holder, parent, false)
        return UserViewHolder(view)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = userList[position]
        holder.username.text = currentUser.username
        holder.surname.text = currentUser.surname
        Glide.with(context)
            .load(currentUser.profileImage)
            .placeholder(R.drawable.profile)
            .error(R.drawable.profile)
            .into(holder.avatar)

        holder.itemView.setOnClickListener {
            val bundle = Bundle()
            val navController = Navigation.findNavController(holder.itemView)

            bundle.putString("username", currentUser.username)
            bundle.putString("uid", currentUser.uid)
            navController.navigate(R.id.action_chatFragment_to_dialogFragment, bundle)
        }

    }


    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username: TextView = itemView.findViewById(R.id.nameTextView)
        val surname: TextView = itemView.findViewById(R.id.surnameTextView)
        val avatar: de.hdodenhof.circleimageview.CircleImageView = itemView.findViewById(R.id.avatarImageView)
    }
}