package com.example.tradeit.view.fragments.Chats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tradeit.view.adapters.MessageAdapter
import com.example.tradeit.databinding.FragmentDialogBinding
import com.example.tradeit.model.Message
import com.example.tradeit.viewModel.TradeViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class DialogFragment : Fragment() {
    private var _binding: FragmentDialogBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TradeViewModel by activityViewModels<TradeViewModel>()
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

        viewModel.getMessageList(senderRoom!!).observe(viewLifecycleOwner, Observer { messages ->
            messages?.let {
                messageAdapter.updateMessages(it)
                chatRecyclerView.scrollToPosition(messages.size - 1)
            }
        })

        sendButton.setOnClickListener {
            val messageText = messageBox.text.toString()
            if (messageText.isNotEmpty()) {
                val messageObject = Message(messageText, senderUid, System.currentTimeMillis())
                viewModel.sendMessage(senderRoom!!, receiverRoom!!, messageObject)
                messageBox.setText("")
            }
        }
        return binding.root
    }
}