package com.example.incomingdocumentprocessingsystem.ui.DBFiles.Labels

import androidx.recyclerview.widget.RecyclerView
import com.example.incomingdocumentprocessingsystem.R
import com.example.incomingdocumentprocessingsystem.database.*
import com.example.incomingdocumentprocessingsystem.models.CommonModel
import com.example.incomingdocumentprocessingsystem.ui.message_recycler_view.view.MessageView
import com.example.incomingdocumentprocessingsystem.ui.screens.base.BaseFragment
import com.example.incomingdocumentprocessingsystem.utilits.*
import kotlinx.android.synthetic.main.fragment_search_label.*
import kotlinx.android.synthetic.main.fragment_search_label.settings_label_name
import kotlinx.android.synthetic.main.fragment_search_label.settings_label_recycle_view


class SearchLabelFragment( private val label: CommonModel) : BaseFragment(R.layout.fragment_search_label) {

    private lateinit var recycleView: RecyclerView
    private lateinit var adapter: SearchLabelAdapter
    private val refLabels = REF_DATABASE_ROOT.child(NODE_TAGS).child(label.id)
    private var listItems = listOf<CommonModel>()


    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = "Выберите свойство"
        hideKeyboard()
        initBtn()
        initRecyclerView()


    }

    private fun initBtn() {
        settings_label_name.text = label.fullname
    }

    override fun onStop() {
        super.onStop()
        hideKeyboard()
    }


    private fun initRecyclerView() {
        recycleView = settings_label_recycle_view
        adapter = SearchLabelAdapter(label)

        //1
        refLabels.addListenerForSingleValueEvent(AppValueEventListener { dataSnapshot ->
            listItems = dataSnapshot.children.map { it -> it.getCommonModel() }
            listItems.forEach { model ->
                adapter.updateListItem(model)
            }
            recycleView.adapter = adapter
        })
    }
}