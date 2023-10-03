package com.example.incomingdocumentprocessingsystem.ui.screens.settings

import android.os.Bundle
import android.view.*
import com.example.incomingdocumentprocessingsystem.R
import com.example.incomingdocumentprocessingsystem.database.*
import com.example.incomingdocumentprocessingsystem.databinding.FragmentChengeUsernameBinding
import com.example.incomingdocumentprocessingsystem.ui.screens.base.BaseChangeFragment
import com.example.incomingdocumentprocessingsystem.utilits.*


class ChangeUsernameFragment : BaseChangeFragment(R.layout.fragment_chenge_username) {
    private lateinit var binding: FragmentChengeUsernameBinding
    private lateinit var newUsername: String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentChengeUsernameBinding.inflate(inflater)
        return binding.root

    }

    override fun onResume() {
        super.onResume()
        setHasOptionsMenu(true)
        binding.settingsInputUsername.setText(USER.username)
    }



    override fun change() {
        newUsername = binding.settingsInputUsername.text.toString().lowercase()
        if (newUsername.isEmpty()) {
            showToast(getString(R.string.setting_toast_name_is_empty))
        } else {
            REF_DATABASE_ROOT.child(NODE_USERNAMES)
                .addListenerForSingleValueEvent(AppValueEventListener {
                    if (it.hasChild(newUsername)) {
                        showToast("Такой пользователь уже существует")
                    } else {
                        changeUsername()
                    }
                }
                )
        }

    }

    private fun changeUsername() {
        REF_DATABASE_ROOT.child(NODE_USERNAMES).child(newUsername).setValue(CURRENT_UID)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    updateCurrentUsername(newUsername)
                }
            }
    }




}