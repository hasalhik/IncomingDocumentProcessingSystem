package com.example.incomingdocumentprocessingsystem.mail

import com.example.incomingdocumentprocessingsystem.utilits.showToast
import com.mailslurp.apis.InboxControllerApi
import com.mailslurp.apis.WaitForControllerApi
import com.mailslurp.models.Email
import com.mailslurp.models.EmailPreview
import java.util.*


const val API_KEY = "1a0f6c2f4bfcef2df69bcc97c0711b452ea2a7c3e51e39d5c4d0109c6f74d420"
const val EMAIL_ADDRESS = "20780f47-8501-4643-ae4c-1b34ed4e1a30@mailslurp.com"
const val EMAIL_ID = "20780f47-8501-4643-ae4c-1b34ed4e1a30"


class MailSlurpKotlinTest {
    lateinit var email: Email
    var listEmail = listOf<EmailPreview>()

    private var apiKey = API_KEY

    fun sendAndReceiveEmail() {


        val inboxController = InboxControllerApi(API_KEY)
        val waitForController = WaitForControllerApi(apiKey)


        val uuid = UUID.fromString(EMAIL_ID)

        listEmail = inboxController.getEmails(
            uuid,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        )


    }

}