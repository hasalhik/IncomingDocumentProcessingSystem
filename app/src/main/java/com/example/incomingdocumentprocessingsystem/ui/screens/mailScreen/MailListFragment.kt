package com.example.incomingdocumentprocessingsystem.ui.screens.mailScreen

import android.os.Build
import android.text.Html
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.incomingdocumentprocessingsystem.R
import com.example.incomingdocumentprocessingsystem.database.CURRENT_UID
import com.example.incomingdocumentprocessingsystem.database.NODE_MESSAGES
import com.example.incomingdocumentprocessingsystem.database.NODE_USERS
import com.example.incomingdocumentprocessingsystem.database.REF_DATABASE_ROOT
import com.example.incomingdocumentprocessingsystem.mail.API_KEY
import com.example.incomingdocumentprocessingsystem.mail.EMAIL_ID
import com.example.incomingdocumentprocessingsystem.mail.MailSlurpKotlinTest
import com.example.incomingdocumentprocessingsystem.models.CommonModel
import com.example.incomingdocumentprocessingsystem.ui.screens.base.BaseFragment
import com.example.incomingdocumentprocessingsystem.utilits.APP_ACTIVITY
import com.example.incomingdocumentprocessingsystem.utilits.hideKeyboard
import com.mailslurp.apis.AttachmentControllerApi
import com.mailslurp.apis.EmailControllerApi
import com.mailslurp.apis.InboxControllerApi
import com.mailslurp.apis.WaitForControllerApi
import com.mailslurp.models.Email
import kotlinx.android.synthetic.main.fragment_mail.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.lang.Thread.sleep
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*


class MailListFragment : BaseFragment(R.layout.fragment_mail) {

    private lateinit var recycleView: RecyclerView
    private lateinit var adapter: MailListAdapter
    private val refUser = REF_DATABASE_ROOT.child(NODE_USERS)
    private val refMessages = REF_DATABASE_ROOT.child(NODE_MESSAGES).child(CURRENT_UID)
    private var listItems = mutableListOf<CommonModel>()
    private var listEmail = mutableListOf<Email>()
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = "Входящие"

        setHasOptionsMenu(true)
        hideKeyboard()
        swipeRefreshLayout = mail_swipe_refresh
        initRecyclerView()
    }

    private fun initRecyclerView() {
        recycleView = mail_list_recycle_view
        adapter = MailListAdapter()
        recycleView.adapter = adapter
        swipeRefreshLayout.isRefreshing = true
        getMail() {
            swipeRefreshLayout.isRefreshing = false
            updateData()
        }
        swipeRefreshLayout.setOnRefreshListener { updateData() }

        // showToast(listEmail[0].subject.toString())


    }

    private fun getMail(function: () -> Unit) {
        (CoroutineScope(Dispatchers.IO).launch {
            val inboxController = InboxControllerApi(API_KEY)
            val uuid = UUID.fromString(EMAIL_ID)
            val waitForController = WaitForControllerApi(API_KEY)
            val emailControllerApi = EmailControllerApi(API_KEY)




            listEmail.add(
                waitForController.waitForLatestEmail(
                    uuid,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                )
            )
            var newModel: CommonModel = CommonModel(listEmail.last().id.toString())

            var str: String = listEmail.last().body.toString()
            str = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(str, Html.FROM_HTML_MODE_LEGACY).toString()
            } else {
                Html.fromHtml(str).toString()
            }

            newModel.text = str
            newModel.from = listEmail.last().from.toString()
            newModel.fullname = listEmail.last().subject.toString()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                newModel.timeStamp = listEmail.last().createdAt.format(
                    DateTimeFormatter.RFC_1123_DATE_TIME.withZone(
                        ZoneId.systemDefault()
                    )
                )
            }

            newModel.fileUrl = listEmail.last().attachments?.last() ?: String()
            val attachmentController = AttachmentControllerApi(API_KEY)
            newModel.phone = attachmentController.getAttachment(
                listEmail.last().attachments?.last() ?: String()
            ).name.toString()

            attachmentController.getAttachment(
                listEmail.last().attachments?.last() ?: String()
            ).name

            var att = listEmail.last().attachments
            Log.d("MyLog", att?.get(0)?.toString() ?: String())


            listItems.add(newModel)
        })
        function()
    }

    private fun updateData() {


        getMail() {}


        listItems.forEach { model ->
            showMail(model)
        }


        swipeRefreshLayout.isRefreshing = false

    }


    private fun showMail(model: CommonModel) {

        adapter.updateListItem(model)


    }

}