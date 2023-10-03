package com.example.incomingdocumentprocessingsystem.ui.screens.main_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.incomingdocumentprocessingsystem.R
import com.example.incomingdocumentprocessingsystem.models.CommonModel
import com.example.incomingdocumentprocessingsystem.ui.screens.groups.GroupChatFragment
import com.example.incomingdocumentprocessingsystem.ui.screens.single_chat.SingleChatFragment
import com.example.incomingdocumentprocessingsystem.utilits.TYPE_CHAT
import com.example.incomingdocumentprocessingsystem.utilits.TYPE_GROUP
import com.example.incomingdocumentprocessingsystem.utilits.downloadAndSetImage
import com.example.incomingdocumentprocessingsystem.utilits.replaceFragment
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.main_list_tem.view.*

class MainListAdapter : RecyclerView.Adapter<MainListAdapter.MainListHolder>() {

    private val listItem = mutableListOf<CommonModel>()

    class MainListHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemName: TextView = view.main_list_item_name
        val itemLastMessage: TextView = view.main_list_last_message
        val itemPhoto: CircleImageView = view.main_list_item_photo

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainListHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.main_list_tem, parent, false)
        val holder = MainListHolder(view)
        holder.itemView.setOnClickListener {
            when (listItem[holder.absoluteAdapterPosition].type) {
                TYPE_CHAT ->
                    replaceFragment(SingleChatFragment(listItem[holder.absoluteAdapterPosition]))
                TYPE_GROUP -> replaceFragment(GroupChatFragment(listItem[holder.absoluteAdapterPosition]))
            }
        }
        return holder
    }

    override fun onBindViewHolder(holder: MainListHolder, position: Int) {
        holder.itemName.text = listItem[position].fullname
        holder.itemLastMessage.text = listItem[position].lastMessage
        holder.itemPhoto.downloadAndSetImage(listItem[position].photoUrl)
    }

    override fun getItemCount(): Int = listItem.size
    fun updateListItem(item: CommonModel) {
        listItem.add(item)
        notifyItemInserted(listItem.size)

    }
}

