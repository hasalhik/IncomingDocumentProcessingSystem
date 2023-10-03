package com.example.incomingdocumentprocessingsystem.ui.message_recycler_view.view_holders

import android.content.Intent
import android.os.Environment
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.incomingdocumentprocessingsystem.database.getFileFromStorage
import com.example.incomingdocumentprocessingsystem.database.getFullnameForID
import com.example.incomingdocumentprocessingsystem.ui.DBFiles.FileSettings.SettingsFile
import com.example.incomingdocumentprocessingsystem.ui.message_recycler_view.view.MessageView
import com.example.incomingdocumentprocessingsystem.utilits.*
import com.pspdfkit.configuration.activity.PdfActivityConfiguration
import com.pspdfkit.ui.PdfActivity
import kotlinx.android.synthetic.main.file_item.view.*
import java.io.File


class HolderFile(view: View) : RecyclerView.ViewHolder(view), MessageHolder {


    //File

    private val blockFile: ConstraintLayout = view.file_bloc_file
    private val fileTime: TextView = view.file_time
    private val filename: TextView = view.file_filename
    private val fileFrom: TextView = view.file_from
    private val fileBtnDownload: ImageView = view.file_btn_download
    private val fileProgressBar: ProgressBar = view.file_received_progress_bar
    private val fileBtnEdit: ImageView = view.file_btn_edit


    override fun drawMessage(view: MessageView) {


        fileTime.text = dateFormatter(view.timeStamp)
        filename.text = view.text
        getFullnameForID(view.from){
            fileFrom.text = it
        }


    }

    override fun onAttach(view: MessageView) {
        filename.setOnClickListener{
            if(filename.maxLines==2){
                filename.maxLines=10
            }else filename.maxLines=2
        }
        fileBtnDownload.setOnClickListener {
            clickBtnFile(view)
        }
        fileBtnEdit.setOnClickListener{
            replaceFragment(SettingsFile(view))
        }
    }

    private fun clickBtnFile(view: MessageView) {

        fileBtnDownload.visibility = View.INVISIBLE
        fileProgressBar.visibility = View.VISIBLE

        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            view.text
        )
        try {
            if (checkPermission(WRITE_FILES) && checkPermission(READ_FILES)// && checkPermission(MANAGE_EXTERNAL_STORAGE)
            ) {
                file.createNewFile()
                getFileFromStorage(file, view.fileUrl) {
                    fileBtnDownload.visibility = View.VISIBLE
                    fileProgressBar.visibility = View.INVISIBLE
                    //openFile(file)
                    openDocument(file.toString())
                }
            }
        } catch (e: Exception) {
            showToast(e.message.toString())
            fileBtnDownload.visibility = View.VISIBLE
            fileProgressBar.visibility = View.INVISIBLE
        }
    }

    private fun openFile(file: File) {
        val matcherPDF: Regex = Regex("\\.pdf$")
        if (file.path.contains(matcherPDF)) {
            val config =
                PdfActivityConfiguration.Builder(APP_ACTIVITY)
                    .build()
            PdfActivity.showDocument(
                APP_ACTIVITY,
                file.toUri(),
                config
            )
        }
    }


    override fun onDetach() {
        fileBtnDownload.setOnClickListener(null)
    }



}