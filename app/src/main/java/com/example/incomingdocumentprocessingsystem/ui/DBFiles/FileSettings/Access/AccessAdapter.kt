package com.example.incomingdocumentprocessingsystem.ui.DBFiles.FileSettings.Access

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.incomingdocumentprocessingsystem.R
import com.example.incomingdocumentprocessingsystem.models.CommonModel
import com.example.incomingdocumentprocessingsystem.ui.DBFiles.FileSettings.Access.AccessFragment.Companion.listContacts
import com.example.incomingdocumentprocessingsystem.ui.screens.single_chat.SingleChatFragment
import com.example.incomingdocumentprocessingsystem.utilits.downloadAndSetImage
import com.example.incomingdocumentprocessingsystem.utilits.replaceFragment
import com.example.incomingdocumentprocessingsystem.utilits.showToast
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.add_contacts.view.*


class AccessAdapter(var access: String) : RecyclerView.Adapter<AccessAdapter.AccessHolder>() {

    private val listItem = mutableListOf<CommonModel>()

    class AccessHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemName: TextView = view.add_contacts_name
        val itemLastMessage: TextView = view.add_contacts_message
        val itemPhoto: CircleImageView = view.add_contacts_photo
        val itemChoice: CircleImageView = view.add_contacts_item_choice


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccessHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.add_contacts, parent, false)
        val holder = AccessHolder(view)


        holder.itemView.setOnClickListener {
            if (listContacts.contains(listItem[holder.absoluteAdapterPosition]))
             {
                holder.itemChoice.visibility = View.INVISIBLE
                 listContacts.remove(listItem[holder.absoluteAdapterPosition])
            } else {
                holder.itemChoice.visibility = View.VISIBLE
                listContacts.add(listItem[holder.absoluteAdapterPosition])

            }


        }

        return holder
    }

    override fun onBindViewHolder(holder: AccessHolder, position: Int) {


        if (listContacts.contains(listItem[position]))
        {
            holder.itemChoice.visibility = View.VISIBLE
        } else {
            holder.itemChoice.visibility = View.INVISIBLE

        }
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

