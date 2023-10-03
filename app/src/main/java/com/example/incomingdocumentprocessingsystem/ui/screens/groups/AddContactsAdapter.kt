package com.example.incomingdocumentprocessingsystem.ui.screens.groups

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.incomingdocumentprocessingsystem.R
import com.example.incomingdocumentprocessingsystem.models.CommonModel
import com.example.incomingdocumentprocessingsystem.ui.screens.single_chat.SingleChatFragment
import com.example.incomingdocumentprocessingsystem.utilits.downloadAndSetImage
import com.example.incomingdocumentprocessingsystem.utilits.replaceFragment
import com.example.incomingdocumentprocessingsystem.utilits.showToast
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.add_contacts.view.*


class AddContactsAdapter : RecyclerView.Adapter<AddContactsAdapter.AddContactsHolder>() {

    private val listItem = mutableListOf<CommonModel>()

    class AddContactsHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemName: TextView = view.add_contacts_name
        val itemLastMessage: TextView = view.add_contacts_message
        val itemPhoto: CircleImageView = view.add_contacts_photo
        val itemChoice: CircleImageView = view.add_contacts_item_choice


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddContactsHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.add_contacts, parent, false)
        val holder = AddContactsHolder(view)
        holder.itemView.setOnClickListener {
            if (AddContactsFragment.listContacts.contains(listItem[holder.absoluteAdapterPosition]))
             {
                holder.itemChoice.visibility = View.INVISIBLE
                AddContactsFragment.listContacts.remove(listItem[holder.absoluteAdapterPosition])
            } else {
                holder.itemChoice.visibility = View.VISIBLE
                AddContactsFragment.listContacts.add(listItem[holder.absoluteAdapterPosition])

            }

        }
        return holder
    }

    override fun onBindViewHolder(holder: AddContactsHolder, position: Int) {
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

