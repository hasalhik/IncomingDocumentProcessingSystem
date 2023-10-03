package com.example.incomingdocumentprocessingsystem.ui.screens.contacts

import android.app.AlertDialog
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.incomingdocumentprocessingsystem.R
import com.example.incomingdocumentprocessingsystem.database.*
import com.example.incomingdocumentprocessingsystem.models.CommonModel
import com.example.incomingdocumentprocessingsystem.ui.screens.base.BaseFragment
import com.example.incomingdocumentprocessingsystem.ui.screens.single_chat.SingleChatFragment
import com.example.incomingdocumentprocessingsystem.utilits.*
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.contact_item.view.*
import kotlinx.android.synthetic.main.fragment_contacts.*


class ContactsFragment : BaseFragment(R.layout.fragment_contacts) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FirebaseRecyclerAdapter<CommonModel, ContactsHolder>
    private lateinit var refContacts: DatabaseReference
    private lateinit var refUsers: DatabaseReference
    private lateinit var refUsersListener: AppValueEventListener
    private var mapListener = hashMapOf<DatabaseReference, AppValueEventListener>()


    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = "Пользователи"
        initRecycleView()
    }

    private fun initRecycleView() {
        recyclerView = contacts_recycle_view
        refContacts = REF_DATABASE_ROOT.child(NODE_USERS)
        val options = FirebaseRecyclerOptions.Builder<CommonModel>()
            .setQuery(refContacts, CommonModel::class.java)
            .build()
        adapter = object : FirebaseRecyclerAdapter<CommonModel, ContactsHolder>(options) {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.contact_item, parent, false)
                return ContactsHolder(view)
            }

            override fun onBindViewHolder(
                holder: ContactsHolder,
                position: Int,
                model: CommonModel
            ) {
                refUsers = REF_DATABASE_ROOT.child(NODE_USERS).child(model.id)

                refUsersListener = AppValueEventListener {
                    val contact = it.getCommonModel()
                    if (contact.fullname.isEmpty()) {
                        holder.name.text = model.fullname
                    } else holder.name.text = contact.fullname
                    holder.status.text = contact.state
                    holder.photo.downloadAndSetImage(contact.photoUrl)
                    holder.itemView.setOnClickListener {

                    }
                    if (contact.admin)
                        holder.admin.visibility = View.VISIBLE
                    else holder.admin.visibility = View.INVISIBLE

                    if (USER.admin) {
                        holder.addAdmin.visibility = View.VISIBLE
                        holder.delete.visibility = View.VISIBLE
                        holder.delete.setOnClickListener{
                            val builder = AlertDialog.Builder(APP_ACTIVITY)
                            builder.setTitle("Вы действительно хотите удалить пользователя?")
                                .setCancelable(true)
                                .setPositiveButton("Да") { dialog, id ->
                                        deleteUser(contact){
                                        notifyDataSetChanged()
                                    }
                                }
                                .setNegativeButton("Нет",
                                    DialogInterface.OnClickListener { dialog, id ->
                                    })
                            builder.show()
                            
                            
                        }
                        holder.addAdmin.setOnClickListener {

                            val builder = AlertDialog.Builder(APP_ACTIVITY)
                            builder.setTitle("Вы действительно хотите сделать пользователя администратором?")
                                .setCancelable(true)
                                .setPositiveButton("Да") { dialog, id ->
                                    addAdmin(contact){
                                        notifyDataSetChanged()
                                    }
                                }
                                .setNegativeButton("Нет",
                                    DialogInterface.OnClickListener { dialog, id ->
                                    })
                            builder.show()

                            
                        }
                    } else{
                        holder.addAdmin.visibility = View.GONE
                        holder.delete.visibility = View.GONE
                    }

                }

                refUsers.addValueEventListener(refUsersListener)
                mapListener[refUsers] = refUsersListener


            }

        }
        recyclerView.adapter = adapter
        adapter.startListening()
    }

    private fun addAdmin(contact: CommonModel, function: () -> Unit) {
        REF_DATABASE_ROOT.child(NODE_USERS).child(contact.id).child(USER_ADMIN).setValue(true)
    }

    private fun deleteUser(contact: CommonModel, function: () -> Unit) {
        if (contact.admin)
            showToast("Администратора не возможно удалить")
        else
        refContacts.child(contact.id).removeValue()
    }

    class ContactsHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.contact_fullname
        val status: TextView = view.contact_status
        val admin: TextView = view.contact_admin
        val photo: CircleImageView = view.contact_photo
        val delete: ImageView = view.contact_delete
        val addAdmin: ImageView = view.contact_add_admin

    }

    override fun onPause() {
        super.onPause()
        adapter.stopListening()
        mapListener.forEach {
            it.key.removeEventListener(it.value)
        }
    }

}


