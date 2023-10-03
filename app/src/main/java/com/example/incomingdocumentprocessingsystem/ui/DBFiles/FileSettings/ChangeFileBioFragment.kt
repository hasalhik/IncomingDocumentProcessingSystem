package com.example.incomingdocumentprocessingsystem.ui.DBFiles.FileSettings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.incomingdocumentprocessingsystem.R
import com.example.incomingdocumentprocessingsystem.database.setFileBioToDatabase
import com.example.incomingdocumentprocessingsystem.databinding.FragmentChangeBioBinding
import com.example.incomingdocumentprocessingsystem.ui.message_recycler_view.view.MessageView
import com.example.incomingdocumentprocessingsystem.ui.screens.base.BaseChangeFragment
import com.example.incomingdocumentprocessingsystem.utilits.APP_ACTIVITY
import com.example.incomingdocumentprocessingsystem.utilits.hideKeyboard


class ChangeFileBioFragment(private val file: MessageView, private val fileBio: String) : BaseChangeFragment(R.layout.fragment_change_bio) {
    private lateinit var binding: FragmentChangeBioBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChangeBioBinding.inflate(inflater)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.settingsInputBio.setText(fileBio)
    }

    override fun change() {
        super.change()
        val newBio = binding.settingsInputBio.text.toString()

        setFileBioToDatabase(newBio, file)

    }

    override fun onStop() {
        super.onStop()
        hideKeyboard()
    }


}