package com.example.incomingdocumentprocessingsystem.ui.DBFiles.Labels

import androidx.recyclerview.widget.RecyclerView
import com.example.incomingdocumentprocessingsystem.R
import com.example.incomingdocumentprocessingsystem.database.*
import com.example.incomingdocumentprocessingsystem.models.CommonModel
import com.example.incomingdocumentprocessingsystem.ui.message_recycler_view.view.MessageView
import com.example.incomingdocumentprocessingsystem.ui.screens.base.BaseFragment
import com.example.incomingdocumentprocessingsystem.utilits.*
import kotlinx.android.synthetic.main.fragment_new_label.*


class AddLabelFragment(val file: MessageView) : BaseFragment(R.layout.fragment_new_label) {
    companion object {
        val listChosen = mutableListOf<CommonModel>()
    }

    private lateinit var recycleView: RecyclerView
    private lateinit var adapter: AddLabelAdapter
    private val refLabels = REF_DATABASE_ROOT.child(NODE_LABELS)
    private var listItems = listOf<CommonModel>()


    override fun onResume() {
        super.onResume()
        listChosen.clear()
        APP_ACTIVITY.title = "Добавить признаки"
        hideKeyboard()
        initBtn()
        initRecyclerView()


    }

    private fun initBtn() {
        btn_create_label.setOnClickListener {
            if (settings_input_name_label.text.isEmpty()) {
                showToast(getString(R.string.setting_toast_name_is_empty))
            } else {
                addLabelToDB(settings_input_name_label.text.toString()) {
                    hideKeyboard()
                    initRecyclerView()
                    listChosen.clear()
                    settings_input_name_label.text.clear()

                }
            }
        }


        add_label_btn_next.setOnClickListener {
            if (listChosen.isEmpty()) showToast("Выберите признак")
            else addListLabelToDB(listChosen, file, {

            })
            {
                APP_ACTIVITY.supportFragmentManager.popBackStack()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        hideKeyboard()
    }


    private fun initRecyclerView() {
        recycleView = add_label_recycle_view
        adapter = AddLabelAdapter(file)

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