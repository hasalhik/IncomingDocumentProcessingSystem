package com.example.incomingdocumentprocessingsystem.ui.objects

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.widget.ImageView
import androidx.drawerlayout.widget.DrawerLayout
import com.example.incomingdocumentprocessingsystem.R
import com.example.incomingdocumentprocessingsystem.ui.screens.contacts.ContactsFragment
import com.example.incomingdocumentprocessingsystem.ui.screens.settings.SettingsFragment
import com.example.incomingdocumentprocessingsystem.utilits.APP_ACTIVITY
import com.example.incomingdocumentprocessingsystem.database.USER
import com.example.incomingdocumentprocessingsystem.database.deleteLabel
import com.example.incomingdocumentprocessingsystem.ui.DBFiles.FileFragment
import com.example.incomingdocumentprocessingsystem.ui.screens.groups.AddContactsFragment
import com.example.incomingdocumentprocessingsystem.ui.screens.mailScreen.MailListFragment
import com.example.incomingdocumentprocessingsystem.utilits.downloadAndSetImage
import com.example.incomingdocumentprocessingsystem.utilits.replaceFragment
import com.mikepenz.materialdrawer.AccountHeader
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader
import com.mikepenz.materialdrawer.util.DrawerImageLoader

class AppDrawer() {
    private lateinit var drawer: Drawer
    private lateinit var header: AccountHeader
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var currentProfile: ProfileDrawerItem
    fun create() {
        initLoader()
        createHeader()
        createDrawer()
        drawerLayout = drawer.drawerLayout
    }

    fun disableDrawer() {
        drawer.actionBarDrawerToggle?.isDrawerIndicatorEnabled = false
        APP_ACTIVITY.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        APP_ACTIVITY.toolbar.setNavigationOnClickListener {
            APP_ACTIVITY.supportFragmentManager.popBackStack()
        }

    }

    fun enableDrawer() {
        APP_ACTIVITY.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        drawer.actionBarDrawerToggle?.isDrawerIndicatorEnabled = true
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        APP_ACTIVITY.toolbar.setNavigationOnClickListener {
            drawer.openDrawer()
        }

    }

    private fun createDrawer() {
        drawer = DrawerBuilder()
            .withActivity(APP_ACTIVITY)
            .withToolbar(APP_ACTIVITY.toolbar)
            .withActionBarDrawerToggle(true)
            .withSelectedItem(-1)
            .withAccountHeader(header)
            .addDrawerItems(

                PrimaryDrawerItem()
                    .withIdentifier(103)
                    .withName("Пользователи")
                    .withSelectable(false)
                    .withIcon(R.drawable.ic_menu_contacts),

                PrimaryDrawerItem()
                    .withIdentifier(105)
                    .withName("Почта")
                    .withSelectable(false)
                    .withIcon(R.drawable.ic_mail_with),

                PrimaryDrawerItem()
                    .withIdentifier(106)
                    .withName("Настройки")
                    .withSelectable(false)
                    .withIcon(R.drawable.ic_menu_settings),

                PrimaryDrawerItem()
                    .withIdentifier(108)
                    .withName("О приложении")
                    .withSelectable(false)
                    .withIcon(R.drawable.ic_menu_help),


                ).withOnDrawerItemClickListener(object : Drawer.OnDrawerItemClickListener {
                override fun onItemClick(
                    view: View?,
                    position: Int,
                    drawerItem: IDrawerItem<*>
                ): Boolean {
                    clickToItem(position)

                    return false
                }
            })
            .build()

    }

    private fun clickToItem(position: Int) {
        when (position) {
            1 -> replaceFragment(ContactsFragment())
            2 -> replaceFragment(MailListFragment())
            3 -> replaceFragment(SettingsFragment())
            4 ->{

                val builder = AlertDialog.Builder(APP_ACTIVITY)
                builder.setTitle("Разработчик: Венскус А.Е.(hasalhik@gmail.com)")
                    .setCancelable(true)
                    .setPositiveButton("Да") { dialog, id ->

                    }

                builder.show()

            }

        }
    }

    private fun createHeader() {
        currentProfile = ProfileDrawerItem()
            .withName(USER.fullname)
            .withEmail(USER.phone)
            .withIcon(USER.photoUrl)
            .withIdentifier(200)
        header = AccountHeaderBuilder()
            .withActivity(APP_ACTIVITY)
            .withHeaderBackground(R.drawable.header)
            .addProfiles(
                currentProfile
            ).withSelectionListEnabled(false).build()
    }

    fun updateHeader() {
        currentProfile
            .withName(USER.fullname)
            .withEmail(USER.phone)
            .withIcon(USER.photoUrl)
        header.updateProfile(currentProfile)
    }

    private fun initLoader() {
        DrawerImageLoader.init(object : AbstractDrawerImageLoader() {
            override fun set(imageView: ImageView, uri: Uri, placeholder: Drawable) {
                imageView.downloadAndSetImage(uri.toString())
            }

        })

    }
}
















