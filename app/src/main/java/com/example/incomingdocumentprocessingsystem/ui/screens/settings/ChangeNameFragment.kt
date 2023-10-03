package com.example.incomingdocumentprocessingsystem.ui.screens.settings

import android.os.Bundle
import android.view.*
import com.example.incomingdocumentprocessingsystem.R
import com.example.incomingdocumentprocessingsystem.database.USER
import com.example.incomingdocumentprocessingsystem.database.setNameToDataBase
import com.example.incomingdocumentprocessingsystem.databinding.FragmentChangeNameBinding
import com.example.incomingdocumentprocessingsystem.ui.screens.base.BaseChangeFragment
import com.example.incomingdocumentprocessingsystem.utilits.*


class ChangeNameFragment : BaseChangeFragment(R.layout.fragment_change_name) {

    private lateinit var binding: FragmentChangeNameBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChangeNameBinding.inflate(inflater)
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        val fullnameList = USER.fullname.split(" ")
        if (fullnameList.size > 1) {
            binding.settingsInputName.setText(fullnameList[0])
            binding.settingsInputSurname.setText(fullnameList[1])
        } else {
            binding.settingsInputName.setText(fullnameList[0])
        }
    }



    override fun change() {
        val name = binding.settingsInputName.text.toString()
        val surname = binding.settingsInputSurname.text.toString()
        if (name.isEmpty()) {
            showToast(getString(R.string.setting_toast_name_is_empty))
        } else {
            if (surname.isEmpty()) {
                showToast(getString(R.string.setting_toast_surname_is_empty))
            } else {
                val fullname = "$name $surname"
                setNameToDataBase(fullname)
            }

        }
    }


}