package com.example.tradeit.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tradeit.adapters.MessageAdapter
import com.example.tradeit.databinding.FragmentDialogBinding
import com.example.tradeit.model.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class DialogFragment : Fragment() {
    private var _binding: FragmentDialogBinding? = null
    private val binding get() = _binding!!


    private lateinit var messageBox : EditText
    private lateinit var sendButton : Button
    private lateinit var chatRecyclerView : RecyclerView
    private lateinit var name : TextView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList : ArrayList<Message>
    private  lateinit var mDbRef : DatabaseReference

    var receiverRoom : String? = null
    var senderRoom : String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentDialogBinding.inflate(inflater, container, false)
        val username = arguments?.getString("username")
        val receiverUid = arguments?.getString("uid")

        val senderUid = FirebaseAuth.getInstance().currentUser?.uid
        mDbRef = FirebaseDatabase.getInstance().reference

        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid

        chatRecyclerView = binding.dialogRecyclerView
        sendButton = binding.sendMessageButton
        messageBox = binding.messageEditText
        name = binding.recipientNameTextView

        messageList = ArrayList()
        messageAdapter = MessageAdapter(requireContext(), messageList)

        name.text = username


        chatRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        chatRecyclerView.adapter = messageAdapter

        mDbRef.child("Chats").child(senderRoom!!).child("Messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()
                    for(postSnapshot in snapshot.children) {
                        val message = postSnapshot.getValue(Message::class.java)
                        messageList.add(message!!)
                    }
                    messageAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

        sendButton.setOnClickListener() {
            val message = messageBox.text.toString()

            if(message.isNotEmpty()) {
                val messageObject = Message(message, senderUid)

                mDbRef.child("Chats").child(senderRoom!!).child("Messages").push()
                    .setValue(messageObject).addOnSuccessListener {
                        mDbRef.child("Chats").child(receiverRoom!!).child("Messages").push()
                            .setValue(messageObject)
                    }
                messageBox.setText("")
            }
        }


        return binding.root
    }
}
