package com.example.incomingdocumentprocessingsystem.ui.DBFiles.Labels

import androidx.recyclerview.widget.RecyclerView
import com.example.incomingdocumentprocessingsystem.R
import com.example.incomingdocumentprocessingsystem.database.*
import com.example.incomingdocumentprocessingsystem.models.CommonModel
import com.example.incomingdocumentprocessingsystem.ui.message_recycler_view.view.MessageView
import com.example.incomingdocumentprocessingsystem.ui.screens.base.BaseFragment
import com.example.incomingdocumentprocessingsystem.utilits.*
import kotlinx.android.synthetic.main.fragment_settings_label.*


class SettingLabelFragment(private val file: MessageView, private val label: CommonModel) : BaseFragment(R.layout.fragment_settings_label) {

    private lateinit var recycleView: RecyclerView


    private lateinit var adapter: SettingLabelAdapter
    private val refLabels = REF_DATABASE_ROOT.child(NODE_TAGS).child(label.id)
    private var listItems = listOf<CommonModel>()


    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = "Добавить свойство"
        hideKeyboard()
        initBtn()
        initRecyclerView()


    }

    private fun initBtn() {
        settings_label_name.text = label.fullname
        btn_create_tag.setOnClickListener {
            if (settings_input_name_tag.text.isEmpty()) {
                showToast(getString(R.string.setting_toast_name_is_empty))
            } else {
                addTagToDB(settings_input_name_tag.text.toString(), label) {
                    hideKeyboard()
                    initRecyclerView()
                    settings_input_name_tag.text.clear()

                }
            }
        }



    }

    override fun onStop() {
        super.onStop()
        hideKeyboard()
    }


    private fun initRecyclerView() {
        recycleView = settings_label_recycle_view
        adapter = SettingLabelAdapter(label,file)

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