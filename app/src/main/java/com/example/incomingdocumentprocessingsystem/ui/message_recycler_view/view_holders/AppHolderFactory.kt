package com.example.incomingdocumentprocessingsystem.ui.message_recycler_view.view_holders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.incomingdocumentprocessingsystem.R
import com.example.incomingdocumentprocessingsystem.ui.message_recycler_view.view.MessageView

class AppHolderFactory {
    companion object {
        fun getHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return when (viewType) {
                MessageView.MESSAGE_IMAGE -> {
                    val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.message_item_image, parent, false)
                    HolderImageMessage(view)
                }
                MessageView.MESSAGE_VOICE -> {
                    val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.message_item_voice, parent, false)
                    HolderVoiceMessage(view)
                }
                MessageView.MESSAGE_FILE -> {
                    val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.file_item, parent, false)
                    HolderFile(view)

                        // работа с собщением-файлом
                       // .inflate(R.layout.message_item_file, parent, false)
                    //HolderFileMessage(view)
                }
                else ->{
                    val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.message_item_text, parent, false)
                    HolderTextMessage(view)
                }

            }

        }

    }
}