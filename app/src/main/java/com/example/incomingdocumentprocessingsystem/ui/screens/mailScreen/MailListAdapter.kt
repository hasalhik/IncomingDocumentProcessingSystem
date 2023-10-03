package com.example.incomingdocumentprocessingsystem.ui.screens.mailScreen

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Build
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.incomingdocumentprocessingsystem.R
import com.example.incomingdocumentprocessingsystem.database.CURRENT_UID
import com.example.incomingdocumentprocessingsystem.database.addAttachToDBFinal
import com.example.incomingdocumentprocessingsystem.database.deleteLabelFormFile
import com.example.incomingdocumentprocessingsystem.database.uploadFileToStorage
import com.example.incomingdocumentprocessingsystem.mail.API_KEY
import com.example.incomingdocumentprocessingsystem.mail.EMAIL_ID
import com.example.incomingdocumentprocessingsystem.models.CommonModel
import com.example.incomingdocumentprocessingsystem.utilits.*
import com.mailslurp.apis.AttachmentControllerApi
import com.mailslurp.apis.BulkActionsControllerApi
import com.mailslurp.apis.EmailControllerApi
import com.mailslurp.apis.InboxControllerApi
import kotlinx.android.synthetic.main.mail_item.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.*


class MailListAdapter : RecyclerView.Adapter<MailListAdapter.MailListHolder>() {

    private val listItem = mutableListOf<CommonModel>()

    class MailListHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemName: TextView = view.mail_name
        val itemBody: TextView = view.mail_body
        val itemFrom: TextView = view.mail_from
        val itemTime: TextView = view.mail_time
        val itemFileName: TextView = view.mail_file_name
        val itemBtnDownload: ImageView = view.mail_dwl_file
        val itemBtnDelete: ImageView = view.mail_delete
        val itemBtnAdd: ImageView = view.mail_add
        val itemBlock: ConstraintLayout = view.mail_bloc
        val itemProgressBar: ProgressBar = view.file_mail_progress_bar


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MailListHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.mail_item, parent, false)
        val holder = MailListHolder(view)
        initBtm(holder)
        return holder
    }

    private fun initBtm(holder: MailListHolder) {
        holder.itemBtnDelete.setOnClickListener{
            val builder = AlertDialog.Builder(APP_ACTIVITY)
            builder.setTitle("Вы действительно хотите удалить это письмо?")
                .setCancelable(true)
                .setPositiveButton("Да") { dialog, id ->
                    deleteMail(listItem[holder.absoluteAdapterPosition]) {
                        listItem.removeAt(holder.absoluteAdapterPosition)
                        notifyDataSetChanged()
                    }
                }
                .setNegativeButton("Нет",
                    DialogInterface.OnClickListener { dialog, id ->
                    })
            builder.show()
        }
        holder.itemBtnDownload.setOnClickListener {
            holder.itemProgressBar.visibility = View.VISIBLE
            holder.itemBtnDownload.visibility = View.INVISIBLE
            downloadFile(
                listItem[holder.absoluteAdapterPosition].fileUrl,
                listItem[holder.absoluteAdapterPosition].phone
            ) {
                holder.itemProgressBar.visibility = View.INVISIBLE
                holder.itemBtnDownload.visibility = View.VISIBLE
            }

        }
        holder.itemBlock.setOnClickListener {
            if (holder.itemBody.maxLines == 20) {
                holder.itemBody.maxLines = 2
                holder.itemFrom.maxLines = 2
                holder.itemName.maxLines = 1
            } else {
                holder.itemBody.maxLines = 20
                holder.itemFrom.maxLines = 5
                holder.itemName.maxLines = 5
            }
        }
        holder.itemFileName.setOnClickListener {
            if (holder.itemFileName.maxLines == 5) {
                holder.itemFileName.maxLines = 2
            } else {
                holder.itemFileName.maxLines = 5
            }
        }
        holder.itemBtnAdd.setOnClickListener {
            holder.itemProgressBar.visibility = View.VISIBLE
            holder.itemBtnDownload.visibility = View.INVISIBLE
            addAttachToDB(listItem[holder.absoluteAdapterPosition]) {
                holder.itemProgressBar.visibility = View.INVISIBLE
                holder.itemBtnDownload.visibility = View.VISIBLE
                APP_ACTIVITY.supportFragmentManager.popBackStack()
            }
        }
    }

    private fun deleteMail(Mail: CommonModel, function: () -> Unit) {
        (CoroutineScope(Dispatchers.IO).launch {

            val emailControllerApi=EmailControllerApi(API_KEY)
            emailControllerApi.deleteEmail(UUID.fromString(Mail.id))



        })
        function()

    }

    private fun addAttachToDB(mail: CommonModel, function: () -> Unit) {
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            mail.phone
        )

        if (checkPermission(WRITE_FILES) && checkPermission(READ_FILES)) {
            if (file.exists()) {
                addAttachToDBFinal(file,mail){function()}
            } else {
                file.createNewFile()
                CoroutineScope(Dispatchers.IO).launch {
                    try {

                        val attachmentController = AttachmentControllerApi(API_KEY)

                        Log.d("MyLog", attachmentController.baseUrl)
                        var dowAttaDto =
                            attachmentController.downloadAttachmentAsBase64Encoded(mail.fileUrl)



                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            val fileByteArray =
                                Base64.getDecoder().decode(dowAttaDto.base64FileContents)
                            file.writeBytes(fileByteArray)
                            addAttachToDBFinal(file,mail){function()}
                        } else {
                        }
                    } catch (e: Exception) {
                        showToast(e.message.toString())
                    }
                }

            }
        }
    }



    private fun downloadFile(fileUrl: String, nameFile: String, function: () -> Unit) {

        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            nameFile
        )

        if (checkPermission(WRITE_FILES) && checkPermission(READ_FILES))
            file.createNewFile()

        CoroutineScope(Dispatchers.IO).launch {
            try {

                val attachmentController = AttachmentControllerApi(API_KEY)

                Log.d("MyLog", attachmentController.baseUrl)
                var dowAttaDto = attachmentController.downloadAttachmentAsBase64Encoded(fileUrl)



                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val fileByteArray = Base64.getDecoder().decode(dowAttaDto.base64FileContents)
                    file.writeBytes(fileByteArray)
                    openDocument(file.toString())
                } else {
                }
            } catch (e: Exception) {
                showToast(e.message.toString())
            }
        }


    }


    override fun onBindViewHolder(holder: MailListHolder, position: Int) {
        holder.itemName.text = listItem[position].fullname
        holder.itemBody.text = listItem[position].text
        holder.itemFrom.text = listItem[position].from
        holder.itemTime.text = listItem[position].timeStamp.toString()
        holder.itemFileName.text = listItem[position].phone
    }

    override fun getItemCount(): Int = listItem.size
    fun updateListItem(item: CommonModel) {
        if (!listItem.contains(item)) {
            listItem.add(item)
            notifyItemInserted(listItem.size)
        }

    }
}



