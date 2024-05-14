package com.example.tradeit.view.fragments.Chats

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tradeit.view.adapters.UserAdapter
import com.example.tradeit.databinding.FragmentChatBinding
import com.example.tradeit.model.Message
import com.example.tradeit.model.User
import com.example.tradeit.viewModel.TradeViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatFragment : Fragment() {

    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userList : ArrayList<User>
    private lateinit var adapter : UserAdapter
    private val viewModel: TradeViewModel by activityViewModels<TradeViewModel>()

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        val rootView = binding.root

        userRecyclerView = binding.userRecyclerView
        userList = ArrayList()
        adapter = context?.let { UserAdapter(it, userList) }!!
        userRecyclerView.layoutManager = LinearLayoutManager(context)
        userRecyclerView.adapter = adapter


        viewModel.userList.observe(viewLifecycleOwner, Observer { userList ->
            adapter.userList.clear()
            adapter.userList.addAll(userList)
            adapter.notifyDataSetChanged()
            viewModel.fetchUsersWithLastMessage()

        })
        viewModel.fetchUsersWithLastMessage()

        return rootView
    }

}
