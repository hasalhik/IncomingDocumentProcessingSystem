package com.example.incomingdocumentprocessingsystem.ui.screens.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.incomingdocumentprocessingsystem.R
import com.example.incomingdocumentprocessingsystem.database.USER
import com.example.incomingdocumentprocessingsystem.database.setBioToDatabase
import com.example.incomingdocumentprocessingsystem.databinding.FragmentChangeBioBinding
import com.example.incomingdocumentprocessingsystem.ui.screens.base.BaseChangeFragment


class ChangeBioFragment : BaseChangeFragment(R.layout.fragment_change_bio) {
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
        binding.settingsInputBio.setText(USER.bio)
    }

    override fun change() {
        super.change()
        val newBio = binding.settingsInputBio.text.toString()

        setBioToDatabase(newBio)

    }


}