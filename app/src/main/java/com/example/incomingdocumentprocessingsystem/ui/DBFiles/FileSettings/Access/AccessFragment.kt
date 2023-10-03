package com.example.incomingdocumentprocessingsystem.ui.DBFiles.FileSettings.Access

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.incomingdocumentprocessingsystem.R
import com.example.incomingdocumentprocessingsystem.database.*
import com.example.incomingdocumentprocessingsystem.models.CommonModel
import com.example.incomingdocumentprocessingsystem.ui.message_recycler_view.view.MessageView
import com.example.incomingdocumentprocessingsystem.ui.screens.base.BaseFragment
import com.example.incomingdocumentprocessingsystem.utilits.*
import kotlinx.android.synthetic.main.fragment_access.*


class AccessFragment(val file: MessageView) : BaseFragment(R.layout.fragment_access) {

    private lateinit var recycleView: RecyclerView
    private lateinit var adapter: AccessAdapter
    private val refUser = REF_DATABASE_ROOT.child(NODE_USERS)
    private var listItems = listOf<CommonModel>()
    var access:String = ""
    override fun onResume() {
        super.onResume()
        listContacts.clear()
        APP_ACTIVITY.title = "Ограничить доступ"
        hideKeyboard()
        REF_DATABASE_ROOT.child(NODE_FILES).child(file.id)
            .addListenerForSingleValueEvent(AppValueEventListener { it -> access = it.getCommonModel().access })
        initRecyclerView()
        access_file_name.text = file.text
        access_file_date.text = dateFormatter(file.timeStamp)
        access_btn_complete.setOnClickListener {
            if (listContacts.isEmpty()) showToast("Добавьте пользователей")
           else addAccess() { APP_ACTIVITY.supportFragmentManager.popBackStack()}
        }
    }

    private fun addAccess(function: () -> Unit) {
        access=""
        listContacts.forEach{ access += it.id }
        REF_DATABASE_ROOT.child(NODE_FILES).child(file.id).child(NODE_ACCESS).setValue(access)
            .addOnFailureListener { showToast(it.message.toString()) }
            .addOnSuccessListener { function() }

    }

    private fun initRecyclerView() {
        recycleView = access_recycle_view
        adapter = AccessAdapter(access)

        //1
        refUser.addListenerForSingleValueEvent(AppValueEventListener { dataSnapshot ->
            listItems = dataSnapshot.children.map { it -> it.getCommonModel() }
            listItems.forEach { model ->
                if (access.contains(model.id))
                    listContacts.add(model)
                adapter.updateListItem(model)

            }

        })
        recycleView.adapter = adapter
    }

    companion object {
        val listContacts = mutableListOf<CommonModel>()
    }

}