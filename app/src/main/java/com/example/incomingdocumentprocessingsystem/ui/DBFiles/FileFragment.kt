package com.example.incomingdocumentprocessingsystem.ui.DBFiles

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import android.widget.AbsListView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.incomingdocumentprocessingsystem.R
import com.example.incomingdocumentprocessingsystem.database.*
import com.example.incomingdocumentprocessingsystem.models.CommonModel
import com.example.incomingdocumentprocessingsystem.ui.DBFiles.Search.SearchFile
import com.example.incomingdocumentprocessingsystem.ui.message_recycler_view.view_holders.AppViewFactory
import com.example.incomingdocumentprocessingsystem.ui.screens.mailScreen.MailListFragment
import com.example.incomingdocumentprocessingsystem.utilits.*
import com.google.firebase.database.DatabaseReference
import kotlinx.android.synthetic.main.fragment_files.*

class FileFragment() :
    Fragment(R.layout.fragment_files) {
    private lateinit var refUser: DatabaseReference
    private lateinit var refFiles: DatabaseReference
    private lateinit var adapter: FiletAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var messagesListener: AppChildEventListener
    private var countMessages = 15
    private var isScrolling = false
    private var smoothScrollToPosition = true
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var layoutManager: LinearLayoutManager


    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = "Файлы"
        APP_ACTIVITY.appDrawer.enableDrawer()
        initFields()
        initRecycleView()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initFields() {
        setHasOptionsMenu(true)

        swipeRefreshLayout = files_swipe_refresh
        layoutManager = LinearLayoutManager(this.context)

        files_btn_file_upload.setOnClickListener { uploadFile() }
        files_btn_mail.setOnClickListener { replaceFragment(MailListFragment()) }
        files_start_search.setOnClickListener { replaceFragment(SearchFile()) }
        if (!APP_ACTIVITY.searchParameters.isEmpty()) {
            files_clear_search.visibility = View.VISIBLE
            files_clear_search.setOnClickListener {
                APP_ACTIVITY.searchParameters.clear()
                onResume()
            }
            files_text_search.visibility = View.VISIBLE
            APP_ACTIVITY.searchParameters.forEach {
                files_text_search.text =
                    files_text_search.text.toString() + it.text.toString() + "; "

            }
        } else {
            files_clear_search.visibility = View.GONE
            files_text_search.visibility= View.GONE
            files_text_search.text =""

        }


    }


    private fun uploadFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        attachFile.launch(intent)


    }


    private var attachFile =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.data != null) {
                val uri = result.data?.data
                val messageKey = getFileKey()
                val filename = getFileFromUri(uri!!)
                uploadFileToStorage(uri, messageKey, CURRENT_UID, TYPE_MESSAGE_FILE, filename)



            }

        }


    private fun initRecycleView() {
        recyclerView = files_recycle_view
        adapter = FiletAdapter()
        refFiles = REF_DATABASE_ROOT.child(NODE_FILES)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        recyclerView.isNestedScrollingEnabled = false

        messagesListener = AppChildEventListener {
            val file = it.getCommonModel()
            if(file.access.isNullOrEmpty() or file.access.contains(USER.id) or USER.admin){
            search(file, 0) }
        }


        refFiles.limitToLast(countMessages).addChildEventListener(messagesListener)


        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (isScrolling && dy < 0 && layoutManager.findFirstVisibleItemPosition() < 4)
                    updateData()
            }
        })

        swipeRefreshLayout.setOnRefreshListener { updateData() }

    }




    private fun updateData() {
        smoothScrollToPosition = false
        isScrolling = false
        countMessages += 10
        refFiles.removeEventListener(messagesListener)
        refFiles.limitToLast(countMessages).addChildEventListener(messagesListener)

    }


    override fun onPause() {
        super.onPause()
        refFiles.removeEventListener(messagesListener)
        refFiles.removeEventListener(messagesListener)
        hideKeyboard()
    }

    override fun onDestroy() {
        super.onDestroy()
        APP_ACTIVITY.appDrawer.disableDrawer()
        adapter.onDestroy()
    }
    private fun search(message: CommonModel, index: Int) {
        if (search2(message,index)){
        if (smoothScrollToPosition) {
            adapter.addItemToBottom(AppViewFactory.getView(message)) {
                recyclerView.smoothScrollToPosition(adapter.itemCount)
            }
        } else {
            adapter.addItemToTop(AppViewFactory.getView(message)) {
                swipeRefreshLayout.isRefreshing = false
            }
        }
        }
    }

    private fun search2(file: CommonModel, i: Int,): Boolean {
        var index = i
        APP_ACTIVITY.searchParameters.forEach {
            when (it.type) {
                "d" -> {
                    if( file.phone != it.text)
                        return false
                }
                "t" -> {
                    if (file.text.contains(it.text) or file.bio.contains(it.text)){}
                    else{return false}
                }
                "l" -> {
                    if (index==0){
                    REF_DATABASE_ROOT.child(NODE_LABELS).child(it.id).child(NODE_LABELS_OWNERS)
                        .addListenerForSingleValueEvent(AppValueEventListener { dataSnapshot ->
                           if (dataSnapshot.hasChild(file.id) && dataSnapshot.child(file.id).value.toString() == it.text){
                               search(file,index+1)
                           }

                        })

                        return false
                    }else
                    {
                        index -= 1
                    }
                }
            }
        }

        return true
    }


}