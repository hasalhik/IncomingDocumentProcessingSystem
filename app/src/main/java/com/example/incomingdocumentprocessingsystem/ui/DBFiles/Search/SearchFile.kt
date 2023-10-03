package com.example.incomingdocumentprocessingsystem.ui.DBFiles.Search

import android.app.DatePickerDialog
import android.view.*
import androidx.recyclerview.widget.RecyclerView
import com.example.incomingdocumentprocessingsystem.R
import com.example.incomingdocumentprocessingsystem.database.*
import com.example.incomingdocumentprocessingsystem.models.CommonModel
import com.example.incomingdocumentprocessingsystem.ui.DBFiles.Dialog.ConfirmDialogFragment
import com.example.incomingdocumentprocessingsystem.ui.DBFiles.Labels.AddLabelFragment
import com.example.incomingdocumentprocessingsystem.ui.message_recycler_view.view.MessageView
import com.example.incomingdocumentprocessingsystem.ui.screens.base.BaseFragment
import com.example.incomingdocumentprocessingsystem.utilits.APP_ACTIVITY
import com.example.incomingdocumentprocessingsystem.utilits.AppValueEventListener
import com.example.incomingdocumentprocessingsystem.utilits.showToast
import kotlinx.android.synthetic.main.fragment_search_file.*
import java.util.*


class SearchFile() : BaseFragment(R.layout.fragment_search_file) {



    private lateinit var recycleView: RecyclerView
    private lateinit var adapter: SearchFileAdapter


    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = "Настройки"
        setHasOptionsMenu(true)
        initFields()
        initRecyclerView()
    }


    private fun initRecyclerView() {
        recycleView = settings_search_recycle_view
        adapter = SearchFileAdapter()


        REF_DATABASE_ROOT.child(NODE_LABELS).addListenerForSingleValueEvent(AppValueEventListener
        { dataSnapshot ->
            var listItems = dataSnapshot.children.map { it -> it.getCommonModel() }
            listItems.forEach { model ->
                            adapter.updateListItem(model)
            }
        })
        recycleView.adapter = adapter

    }



    private fun initFields() {

        settings_search_btn_date.setOnClickListener {
            searchDate(){
                APP_ACTIVITY.supportFragmentManager.popBackStack()
            }

        }
        settings_add_search.setOnClickListener {
            if (settings_search_edit_text.text.isEmpty())
                showToast("Ввыдите текст для поиска")
            else{
                var searchParameter:CommonModel= CommonModel()
                searchParameter.text=settings_search_edit_text.text.toString()
                searchParameter.type="t"
                APP_ACTIVITY.searchParameters.add(searchParameter)
                APP_ACTIVITY.supportFragmentManager.popBackStack()
            }

        }


    }

    private fun searchDate(function: () -> Unit) {

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)


        val dpd = DatePickerDialog(
            APP_ACTIVITY,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
               var searchParameter:CommonModel= CommonModel()
                searchParameter.text="$dayOfMonth.$monthOfYear.$year"
                searchParameter.type="d"
                APP_ACTIVITY.searchParameters.add(searchParameter)
                function()
            },
            year,
            month,
            day
        )

        dpd.show()
    }




}