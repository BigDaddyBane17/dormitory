package com.example.tradeit.view.fragments.Chats

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tradeit.view.adapters.UserAdapter
import com.example.tradeit.databinding.FragmentChatBinding
import com.example.tradeit.model.Message
import com.example.tradeit.model.User
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
    private lateinit var mAuth : FirebaseAuth
    private lateinit var mDbRef : DatabaseReference

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

        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().reference


        mDbRef.child("Users").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(userSnapshot in snapshot.children) {
                    if(userSnapshot.key.equals(mAuth.currentUser?.uid)) {
                        continue
                    }
                    val currentUser = userSnapshot.getValue(User::class.java)

                    if (currentUser != null) {
                        val uid = currentUser.uid
                        mDbRef.child("Chats").child(mAuth.currentUser!!.uid + uid).child("Messages")
                            .orderByChild("timestamp").limitToLast(1)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    for (postSnapshot in dataSnapshot.children) {
                                        val lastMessage = postSnapshot.getValue(Message::class.java)
                                        currentUser.lastMessage = lastMessage?.message
                                    }
                                    adapter.notifyDataSetChanged()
                                }

                                override fun onCancelled(databaseError: DatabaseError) {
                                    // Обработка ошибок
                                }
                            })

                        userList.add(currentUser)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Обработка ошибок
            }
        })





        return rootView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}