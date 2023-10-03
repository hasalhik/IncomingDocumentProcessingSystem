package com.example.incomingdocumentprocessingsystem.ui.DBFiles.Dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.incomingdocumentprocessingsystem.R

import com.example.incomingdocumentprocessingsystem.database.deleteFile
import com.example.incomingdocumentprocessingsystem.ui.DBFiles.FileFragment
import com.example.incomingdocumentprocessingsystem.ui.message_recycler_view.view.MessageView
import com.example.incomingdocumentprocessingsystem.utilits.replaceFragment

class ConfirmDialogFragment(val file:MessageView) : DialogFragment() {
    lateinit var  builder: AlertDialog.Builder
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            builder = AlertDialog.Builder(it)
            builder.setTitle(getString(R.string.TitleConfirmDelete))
                .setCancelable(true)
                .setPositiveButton(getString(R.string.Yes)) { dialog, id ->
                    deleteFile(file){
                        replaceFragment(FileFragment())
                    }
                }
                .setNegativeButton(getString(R.string.No),
                    DialogInterface.OnClickListener { dialog, id ->
                    })
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }


}