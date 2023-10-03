package com.example.incomingdocumentprocessingsystem.ui.DBFiles.FileSettings

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.RecyclerView
import com.example.incomingdocumentprocessingsystem.R
import com.example.incomingdocumentprocessingsystem.database.*
import com.example.incomingdocumentprocessingsystem.databinding.FragmentSettingsFileBinding
import com.example.incomingdocumentprocessingsystem.ui.DBFiles.Dialog.ConfirmDialogFragment
import com.example.incomingdocumentprocessingsystem.ui.DBFiles.FileSettings.Access.AccessFragment
import com.example.incomingdocumentprocessingsystem.ui.DBFiles.Labels.AddLabelFragment
import com.example.incomingdocumentprocessingsystem.ui.message_recycler_view.view.MessageView
import com.example.incomingdocumentprocessingsystem.ui.screens.base.BaseFragment
import com.example.incomingdocumentprocessingsystem.utilits.*
import kotlinx.android.synthetic.main.fragment_settings_file.*
import java.util.*


class SettingsFile(val view: MessageView) : BaseFragment(R.layout.fragment_settings_file) {
    var file = view
    private lateinit var binding: FragmentSettingsFileBinding

    private lateinit var recycleView: RecyclerView
    private lateinit var adapter: SettingFileAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentSettingsFileBinding.inflate(inflater)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = "Настройки документа"
        setHasOptionsMenu(true)
        initFields()
        initRecyclerView()
    }


    private fun initRecyclerView() {
        recycleView = settings_file_recycle_view
        adapter = SettingFileAdapter(file)


        REF_DATABASE_ROOT.child(NODE_LABELS).addListenerForSingleValueEvent(AppValueEventListener
        { dataSnapshot ->
            var listItems = dataSnapshot.children.map { it -> it.getCommonModel() }
            listItems.forEach { model ->
                val newModel = model
                REF_DATABASE_ROOT.child(NODE_LABELS).child(model.id).child(NODE_LABELS_OWNERS)
                    .addListenerForSingleValueEvent(AppValueEventListener {
                        if (it.hasChild(file.id)) {
                            newModel.state = it.child(file.id).value.toString()
                            adapter.updateListItem(newModel)

                        }
                    })
            }
        })
        recycleView.adapter = adapter

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity?.menuInflater?.inflate(R.menu.settings_file_action_menu, menu)
    }

    private fun initFields() = with(binding) {
        file = view
        settingsFileName.text = file.text
        settingsFileDate.text = dateFormatter(file.timeStamp)
        getExecutionToDb(file){
            settingsExecution.text = it
        }

        getFullnameForID(file.from) {
            fileFrom.text = it
        }
        var fileBio:String=""
         getBioFile(file){ settingsFileDescription.text = it
         fileBio = it }

        settingsFileBtnChangeDescription.setOnClickListener {
            replaceFragment(ChangeFileBioFragment(file, fileBio))
        }
        settingsBtnChangeExecution.setOnClickListener {
            setDate()
        }
        if (USER.admin){
        settingsFileBtnAccess.setOnClickListener {
            replaceFragment(AccessFragment(file))
        }}else
            settingsFileBtnAccess.visibility = View.GONE


    }

    private fun FragmentSettingsFileBinding.setDate() {

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)


        val dpd = DatePickerDialog(
            APP_ACTIVITY,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                setExecutionToDb("$dayOfMonth.$monthOfYear.$year", file) {
                    settingsExecution.text = "$dayOfMonth.$monthOfYear.$year"
                }

            },
            year,
            month,
            day
        )

        dpd.show()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings_file_delete -> {
                if(USER.admin)
                createConfirmDialog()
                else showToast("Только администратор может удалять документы")

            }
            R.id.settings_file_new_label -> replaceFragment(AddLabelFragment(file))
        }
        return true
    }

    private fun createConfirmDialog() {
        val confirmFragment = ConfirmDialogFragment(file)
        val manager = APP_ACTIVITY.supportFragmentManager
        confirmFragment.show(manager, "1")
    }
}