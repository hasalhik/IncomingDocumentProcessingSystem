package com.example.incomingdocumentprocessingsystem.ui.screens.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.incomingdocumentprocessingsystem.R
import com.example.incomingdocumentprocessingsystem.database.AUTH
import com.example.incomingdocumentprocessingsystem.databinding.FragmentEnterPhoneNumberBinding
import com.example.incomingdocumentprocessingsystem.utilits.*
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class EnterPhoneNumberFragment : Fragment(R.layout.fragment_enter_phone_number) {
    private lateinit var binding: FragmentEnterPhoneNumberBinding
    private lateinit var phoneNumber:String
    private lateinit var callback:PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEnterPhoneNumberBinding.inflate(inflater)
        return binding.root
    }

    override fun onStart() {
        APP_ACTIVITY.title = "Введите номер телефона"
        super.onStart()
        callback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    AUTH.signInWithCredential(credential).addOnCompleteListener { task->
                        if (task.isSuccessful) {
                            showToast("Добро пожаловать")
                            restartActivity()
                        } else showToast(task.exception?.message.toString())
                    }

            }

            override fun onVerificationFailed(p0: FirebaseException) {
                showToast(p0.message.toString())

            }

            override fun onCodeSent(id: String, token: PhoneAuthProvider.ForceResendingToken) {
                replaceFragment(EnterCodeFragment(phoneNumber,id))
            }
        }
        binding.apply {
            registerBtnNext.setOnClickListener() {
                if (registerInputPhoneNumber.text.toString().isEmpty()) {
                    showToast(getString(R.string.register_toast_enter_phone))
                } else {
                    authUser()

                }
            }
        }


    }

    private fun authUser() {
        phoneNumber = binding.registerInputPhoneNumber.text.toString()
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber,
            60,
            TimeUnit.SECONDS,
            APP_ACTIVITY,
            callback
        )
    }

}