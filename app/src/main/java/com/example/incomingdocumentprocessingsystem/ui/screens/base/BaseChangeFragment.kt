package com.example.incomingdocumentprocessingsystem.ui.screens.base

import android.view.*
import androidx.fragment.app.Fragment
import com.example.incomingdocumentprocessingsystem.MainActivity
import com.example.incomingdocumentprocessingsystem.R
import com.example.incomingdocumentprocessingsystem.utilits.hideKeyboard


open class BaseChangeFragment(layout: Int) : Fragment(layout) {
    override fun onStart() {
        super.onStart()
        setHasOptionsMenu(true)
        (activity as MainActivity).appDrawer.disableDrawer()
        hideKeyboard()
    }

    override fun onStop() {
        super.onStop()
        hideKeyboard()

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        (activity as MainActivity).menuInflater.inflate(R.menu.settings_menu_confirm, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.settings_confirm_change -> change()
        }
        return true
    }

    open fun change() {

    }

}