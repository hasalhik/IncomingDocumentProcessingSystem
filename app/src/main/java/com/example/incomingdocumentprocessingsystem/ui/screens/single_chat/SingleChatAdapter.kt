package com.example.incomingdocumentprocessingsystem.ui.screens.single_chat

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.incomingdocumentprocessingsystem.ui.message_recycler_view.view.MessageView
import com.example.incomingdocumentprocessingsystem.ui.message_recycler_view.view_holders.*

class SingleChatAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var listMessagesCache = mutableListOf<MessageView>()
    private var listHolder = mutableListOf<MessageHolder>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AppHolderFactory.getHolder(parent, viewType)
    }

    override fun getItemViewType(position: Int): Int {
        return listMessagesCache[position].getTypeView()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MessageHolder).drawMessage(listMessagesCache[position])

    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        (holder as MessageHolder).onAttach(listMessagesCache[holder.adapterPosition])
        listHolder.add(holder as MessageHolder)
        super.onViewAttachedToWindow(holder)
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        (holder as MessageHolder).onDetach()
        listHolder.remove(holder as MessageHolder)
        super.onViewDetachedFromWindow(holder)
    }


    override fun getItemCount(): Int = listMessagesCache.size
    fun setList(list: List<MessageView>) {
        // notifyDataSetChanged()
    }

    fun addItemToBottom(item: MessageView, osSuccess: () -> Unit) {

        if (!listMessagesCache.contains(item)) {
            listMessagesCache.add(item)
            notifyItemInserted(listMessagesCache.size)
        }
        osSuccess()
    }

    fun addItemToTop(item: MessageView, osSuccess: () -> Unit) {

        if (!listMessagesCache.contains(item)) {
            listMessagesCache.add(item)
            listMessagesCache.sortBy { it.timeStamp.toString() }
            notifyItemInserted(0)
        }
        osSuccess()
    }

    fun onDestroy() {
        listHolder.forEach{
            it.onDetach()
        }
    }


}


