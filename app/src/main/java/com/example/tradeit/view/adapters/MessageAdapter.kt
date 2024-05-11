package com.example.tradeit.view.adapters
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tradeit.R
import com.example.tradeit.model.Message
import com.example.tradeit.viewModel.tradeViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MessageAdapter(val context : Context,
                     val messageList  : ArrayList<Message>,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val ITEM_RECEIVE = 1
    private val ITEM_SENT = 2

    class SentViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val sentMessage: TextView = itemView.findViewById(R.id.sentMessageTextView)
        val sentTimeSnap: TextView = itemView.findViewById(R.id.sentMessageDateTextView)
    }

    class ReceiveViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val receiveMessage: TextView  = itemView.findViewById(R.id.incomeMessageTextView)
        val receiveTimeSnap: TextView  = itemView.findViewById(R.id.incomeMessageDateTextView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType == 1) {
            val view = LayoutInflater.from(context).inflate(R.layout.income_message, parent, false)
            return ReceiveViewHolder(view)
        }
        else {
            val view = LayoutInflater.from(context).inflate(R.layout.sent_message, parent, false)
            return SentViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun getItemViewType(position: Int): Int {
        val currentMessage = messageList[position]

        if(FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.senderId)) {
            return ITEM_SENT
        }
        else {
            return ITEM_RECEIVE
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = messageList[position]


        if(holder.javaClass == SentViewHolder::class.java) {
            val viewHolder = holder as SentViewHolder
            holder.sentMessage.text = currentMessage.message
            holder.sentTimeSnap.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(currentMessage.timestamp))
        }
        else {
            val viewHolder = holder as ReceiveViewHolder
            holder.receiveMessage.text = currentMessage.message
            holder.receiveTimeSnap.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(currentMessage.timestamp))
        }
    }
}