package com.example.incomingdocumentprocessingsystem.ui.screens.settings

import android.os.Bundle
import android.view.*
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.example.incomingdocumentprocessingsystem.R
import com.example.incomingdocumentprocessingsystem.database.*
import com.example.incomingdocumentprocessingsystem.databinding.FragmentSettingsBinding
import com.example.incomingdocumentprocessingsystem.ui.screens.base.BaseFragment
import com.example.incomingdocumentprocessingsystem.utilits.*


class SettingsFragment : BaseFragment(R.layout.fragment_settings) {

    private lateinit var binding: FragmentSettingsBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = "Настройки"
        setHasOptionsMenu(true)
        initFields()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity?.menuInflater?.inflate(R.menu.settings_action_menu, menu)
    }

    private fun initFields() = with(binding) {

        settingsUsername.text = USER.username
        settingsBio.text = USER.bio
        settingsFullName.text = USER.fullname
        settingsPhoneNumber.text = USER.phone
        settingsStatus.text = USER.state
        settingsBtnChangeUsername.setOnClickListener {
            replaceFragment(ChangeUsernameFragment())
        }
        settingsBtnChangeBio.setOnClickListener {
            replaceFragment(ChangeBioFragment())
        }

        binding.settingsUserPhoto.downloadAndSetImage(USER.photoUrl)
    }

    private fun changePhotoUser() {

        cropImage.launch(
            options {
                setAspectRatio(1, 1)
                setRequestedSize(250, 250)
                setCropShape(CropImageView.CropShape.OVAL)

            }
        )

    }

    var cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            val uriContent = result.uriContent
            val path = REF_STORAGE_ROOT.child(FOLDER_PROFILE_IMAGE)
                .child(CURRENT_UID)

            putFileToStorage(uriContent, path) {
                getUrlFromStorage(path) {
                    putUrlToDatabase(it) {
                        showToast(getString(R.string.toast_data_update))
                        USER.photoUrl = it
                        binding.settingsUserPhoto.downloadAndSetImage(it)
                        APP_ACTIVITY.appDrawer.updateHeader()

                    }
                }
            }

        } else {
            // an error occurred
            val exception = result.error
            showToast(exception?.message.toString())
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings_menu_exit -> {
                AppStates.updateState(AppStates.OFFLINE)
                AUTH.signOut()
                restartActivity()

            }
            R.id.settings_menu_change_name -> replaceFragment(ChangeNameFragment())
            R.id.settings_menu_change_photo -> changePhotoUser()
        }
        return true
    }
}