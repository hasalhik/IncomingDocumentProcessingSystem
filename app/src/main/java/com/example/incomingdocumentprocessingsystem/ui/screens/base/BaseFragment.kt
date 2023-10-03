package com.example.incomingdocumentprocessingsystem.ui.screens.base

import androidx.fragment.app.Fragment
import com.example.incomingdocumentprocessingsystem.utilits.APP_ACTIVITY
import com.example.incomingdocumentprocessingsystem.utilits.hideKeyboard

open class BaseFragment( layout:Int) : Fragment(layout) {

    override fun onStart() {
        super.onStart()
        APP_ACTIVITY.appDrawer.disableDrawer()
        hideKeyboard()

    }

}
