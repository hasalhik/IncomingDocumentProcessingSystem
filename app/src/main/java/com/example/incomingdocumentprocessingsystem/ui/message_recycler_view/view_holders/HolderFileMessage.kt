package com.example.incomingdocumentprocessingsystem.ui.message_recycler_view.view_holders

import android.os.Environment
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.incomingdocumentprocessingsystem.database.CURRENT_UID
import com.example.incomingdocumentprocessingsystem.database.getFileFromStorage
import com.example.incomingdocumentprocessingsystem.ui.message_recycler_view.view.MessageView
import com.example.incomingdocumentprocessingsystem.utilits.*
import kotlinx.android.synthetic.main.message_item_file.view.*
import java.io.File

class HolderFileMessage(view: View) : RecyclerView.ViewHolder(view), MessageHolder {


    //File
    private val blocReceivedFileMessage: ConstraintLayout = view.bloc_received_file_message
    private val blockUserFileMessage: ConstraintLayout = view.bloc_user_file_message
    private val chatUserFileMessageTime: TextView = view.chat_user_file_time
    private val chatReceivedFileMessageTime: TextView = view.chat_received_file_time
    private val chatUserFilename: TextView = view.chat_user_filename
    private val chatUserBtnDownload: ImageView = view.chat_user_btn_download
    private val chatUserProgressBar: ProgressBar = view.chat_user_progress_bar

    private val chatReceivedFilename: TextView = view.chat_received_filename
    private val chatReceivedBtnDownload: ImageView = view.chat_received_btn_download
    private val chatReceivedProgressBar: ProgressBar = view.chat_received_progress_bar


    override fun drawMessage(view: MessageView) {
        if (view.from == CURRENT_UID) {
            blocReceivedFileMessage.visibility = View.GONE
            blockUserFileMessage.visibility = View.VISIBLE
            chatUserFileMessageTime.text =
                view.timeStamp.asTime()
            chatUserFilename.text = view.text

        } else {
            blocReceivedFileMessage.visibility = View.VISIBLE
            blockUserFileMessage.visibility = View.GONE
            chatReceivedFileMessageTime.text =
                view.timeStamp.asTime()
            chatReceivedFilename.text = view.text
        }

    }

    override fun onAttach(view: MessageView) {
        if (view.from == CURRENT_UID) chatUserBtnDownload.setOnClickListener {
            clickBtnFile(view)
        } else chatReceivedBtnDownload.setOnClickListener {
            clickBtnFile(view)
        }


    }

    private fun clickBtnFile(view: MessageView) {
        if (view.from == CURRENT_UID) {
            chatUserBtnDownload.visibility = View.INVISIBLE
            chatUserProgressBar.visibility = View.VISIBLE
        } else {
            chatReceivedBtnDownload.visibility = View.INVISIBLE
            chatReceivedProgressBar.visibility = View.VISIBLE
        }
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            view.text
        )
        try {
            if (checkPermission(WRITE_FILES)&&checkPermission(READ_FILES))
                file.createNewFile()
            getFileFromStorage(file, view.fileUrl) {
                if (view.from == CURRENT_UID) {
                    chatUserBtnDownload.visibility = View.VISIBLE
                    chatUserProgressBar.visibility = View.INVISIBLE
                } else {
                    chatReceivedBtnDownload.visibility = View.VISIBLE
                    chatReceivedProgressBar.visibility = View.INVISIBLE

                }
            }
        } catch (e:Exception) {
            showToast(e.message.toString())
        }
    }


    override fun onDetach() {
        chatUserBtnDownload.setOnClickListener(null)
        chatReceivedBtnDownload.setOnClickListener(null)

    }

}