package com.example.incomingdocumentprocessingsystem.ui.screens.groups

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.incomingdocumentprocessingsystem.R
import com.example.incomingdocumentprocessingsystem.database.*
import com.example.incomingdocumentprocessingsystem.models.CommonModel
import com.example.incomingdocumentprocessingsystem.ui.screens.base.BaseFragment
import com.example.incomingdocumentprocessingsystem.utilits.*
import kotlinx.android.synthetic.main.fragment_add_contactst.*
import kotlinx.android.synthetic.main.fragment_main_list.*


class AddContactsFragment : BaseFragment(R.layout.fragment_add_contactst) {

    private lateinit var recycleView: RecyclerView
    private lateinit var adapter: AddContactsAdapter
    private val refContactsList = REF_DATABASE_ROOT.child(NODE_PHONES_CONTACTS).child(CURRENT_UID)
    private val refUser = REF_DATABASE_ROOT.child(NODE_USERS)
    private val refMessages = REF_DATABASE_ROOT.child(NODE_MESSAGES).child(CURRENT_UID)
    private var listItems = listOf<CommonModel>()
    override fun onResume() {
        super.onResume()
        listContacts.clear()
        APP_ACTIVITY.title = "Добавить участника"
        hideKeyboard()
        initRecyclerView()
        add_contacts_btn_next.setOnClickListener {
            if (listContacts.isEmpty()) showToast("Добавьте участника")
           // else replaceFragment(CreateGroupFragment(listContacts))
        }
    }

    private fun initRecyclerView() {
        recycleView = add_contacts_recycle_view
        adapter = AddContactsAdapter()

        //1
        refContactsList.addListenerForSingleValueEvent(AppValueEventListener { dataSnapshot ->
            listItems = dataSnapshot.children.map { it -> it.getCommonModel() }
            listItems.forEach { model ->
                
                //2
                refUser.child(model.id)
                    .addListenerForSingleValueEvent(AppValueEventListener { dataSnapshot1 ->

                        val newModel = dataSnapshot1.getCommonModel()

                        //3
                        refMessages.child(model.id).limitToLast(1)
                            .addListenerForSingleValueEvent(AppValueEventListener { dataSnapshot2 ->
                                val tempList = dataSnapshot2.children.map { it.getCommonModel() }


                                newModel.lastMessage = newModel.state
                                if (newModel.fullname.isEmpty()) {
                                    newModel.fullname = newModel.phone
                                }
                                adapter.updateListItem(newModel)
                            })
                    })
            }

        })
        recycleView.adapter = adapter
    }

    companion object {
        val listContacts = mutableListOf<CommonModel>()
    }

}