package com.example.tradeit.view.adapters
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tradeit.R
import com.example.tradeit.model.Message
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MessageAdapter(val context : Context,
                     var messageList  : ArrayList<Message>,

                     ) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val ITEM_RECEIVE = 1
    private val ITEM_SENT = 2

    class SentViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val sentMessage: TextView = itemView.findViewById(R.id.sentMessageTextView)
        val sentTimeSnap: TextView = itemView.findViewById(R.id.sentMessageDateTextView)
        val sentCalendar: TextView = itemView.findViewById(R.id.sentMessageCalendar)
    }

    class ReceiveViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val receiveMessage: TextView  = itemView.findViewById(R.id.incomeMessageTextView)
        val receiveTimeSnap: TextView  = itemView.findViewById(R.id.incomeMessageDateTextView)
        val receiveCalendar: TextView = itemView.findViewById(R.id.incomeMessageCalendar)
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

        if (holder is SentViewHolder) {
            holder.sentMessage.text = currentMessage.message
            holder.sentTimeSnap.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(currentMessage.timestamp))
            holder.sentCalendar.text = SimpleDateFormat("dd.MM.yy", Locale.getDefault()).format(Date(currentMessage.timestamp))

        } else if (holder is ReceiveViewHolder) {
            holder.receiveMessage.text = currentMessage.message
            holder.receiveTimeSnap.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(currentMessage.timestamp))
            holder.receiveCalendar.text = SimpleDateFormat("dd.MM.yy", Locale.getDefault()).format(Date(currentMessage.timestamp))

        }
    }

    fun updateMessages(newMessages: List<Message>) {
        messageList = newMessages as ArrayList<Message>
        notifyDataSetChanged()
    }

}