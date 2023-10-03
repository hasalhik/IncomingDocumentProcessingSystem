package com.example.incomingdocumentprocessingsystem

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.incomingdocumentprocessingsystem.database.AUTH
import com.example.incomingdocumentprocessingsystem.database.initFirebase
import com.example.incomingdocumentprocessingsystem.database.initUser
import com.example.incomingdocumentprocessingsystem.databinding.ActivityMainBinding
import com.example.incomingdocumentprocessingsystem.models.CommonModel
import com.example.incomingdocumentprocessingsystem.ui.DBFiles.FileFragment
import com.example.incomingdocumentprocessingsystem.ui.screens.main_list.MainListFragment
import com.example.incomingdocumentprocessingsystem.ui.screens.register.EnterPhoneNumberFragment
import com.example.incomingdocumentprocessingsystem.ui.objects.AppDrawer
import com.example.incomingdocumentprocessingsystem.utilits.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var appDrawer: AppDrawer
    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    var searchParameters = mutableListOf<CommonModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        APP_ACTIVITY = this
        initFirebase()
        initUser {
            CoroutineScope(Dispatchers.IO).launch {
                initContacts()
            }
            initFields()
            initFunc()
        }
    }


    private fun initFunc() {
        setSupportActionBar(toolbar)
        if (AUTH.currentUser != null) {
            appDrawer.create()
            replaceFragment(FileFragment(), false)
        } else
            replaceFragment(EnterPhoneNumberFragment(),false)

    }


    private fun initFields() {
        toolbar = binding.mainToolbar
        appDrawer = AppDrawer()


    }

    override fun onStart() {
        super.onStart()
        AppStates.updateState(AppStates.ONLINE)
    }

    override fun onStop() {
        super.onStop()
        if(AUTH.currentUser != null)
        AppStates.updateState(AppStates.OFFLINE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (ContextCompat.checkSelfPermission(
                APP_ACTIVITY,
                READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            initContacts()
        }
    }


}