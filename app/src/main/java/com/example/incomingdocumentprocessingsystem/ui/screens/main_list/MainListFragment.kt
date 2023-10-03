package com.example.incomingdocumentprocessingsystem.ui.screens.main_list

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.incomingdocumentprocessingsystem.R
import com.example.incomingdocumentprocessingsystem.database.*
import com.example.incomingdocumentprocessingsystem.models.CommonModel
import com.example.incomingdocumentprocessingsystem.utilits.*
import kotlinx.android.synthetic.main.fragment_main_list.*


class MainListFragment : Fragment(R.layout.fragment_main_list) {

    private lateinit var recycleView: RecyclerView
    private lateinit var adapter: MainListAdapter
    private val refMainList = REF_DATABASE_ROOT.child(NODE_MAIN_LIST).child(CURRENT_UID)
    private val refUser = REF_DATABASE_ROOT.child(NODE_USERS)
    private val refMessages = REF_DATABASE_ROOT.child(NODE_MESSAGES).child(CURRENT_UID)
    private var listItems = listOf<CommonModel>()
    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = "Чяты"
        APP_ACTIVITY.appDrawer.enableDrawer()
        hideKeyboard()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        recycleView = main_list_recycle_view
        adapter = MainListAdapter()

        //1
        refMainList.addListenerForSingleValueEvent(AppValueEventListener { dataSnapshot ->
            listItems = dataSnapshot.children.map { it -> it.getCommonModel() }
            listItems.forEach { model ->
                when(model.type){
                    TYPE_CHAT-> showChat(model)
                TYPE_GROUP->showGroup(model)

                }

            }

        })
        recycleView.adapter = adapter
    }

    private fun showGroup(model: CommonModel) {
        //2
        REF_DATABASE_ROOT.child(NODE_GROUPS).child(model.id)
            .addListenerForSingleValueEvent(AppValueEventListener { dataSnapshot1 ->

                val newModel = dataSnapshot1.getCommonModel()

                //3
                REF_DATABASE_ROOT.child(NODE_GROUPS).child(model.id).child(NODE_MESSAGES)
                    .limitToLast(1)
                    .addListenerForSingleValueEvent(AppValueEventListener { dataSnapshot2 ->
                        val tempList = dataSnapshot2.children.map { it.getCommonModel() }


                        if (tempList.isEmpty())
                            newModel.lastMessage = "Чят очищен"
                        else
                            newModel.lastMessage = tempList[0].text
                        newModel.type = TYPE_GROUP

                        adapter.updateListItem(newModel)

                    })


            })

    }

    private fun showChat(model: CommonModel) {
        //2
        refUser.child(model.id)
            .addListenerForSingleValueEvent(AppValueEventListener { dataSnapshot1 ->

                val newModel = dataSnapshot1.getCommonModel()

                //3
                refMessages.child(model.id).limitToLast(1)
                    .addListenerForSingleValueEvent(AppValueEventListener { dataSnapshot2 ->
                        val tempList = dataSnapshot2.children.map { it.getCommonModel() }


                        if (tempList.isEmpty())
                            newModel.lastMessage = "Чят очищен"
                        else
                            newModel.lastMessage = tempList[0].text
                        if (newModel.fullname.isEmpty()) {
                            newModel.fullname = newModel.phone
                        }
                        newModel.type = TYPE_CHAT
                        adapter.updateListItem(newModel)

                    })


            })

    }

}