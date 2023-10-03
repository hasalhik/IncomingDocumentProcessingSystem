package com.example.incomingdocumentprocessingsystem.ui.DBFiles.Search

import android.app.AlertDialog
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.incomingdocumentprocessingsystem.R
import com.example.incomingdocumentprocessingsystem.database.deleteLabelFormFile
import com.example.incomingdocumentprocessingsystem.models.CommonModel
import com.example.incomingdocumentprocessingsystem.ui.DBFiles.Labels.SearchLabelFragment
import com.example.incomingdocumentprocessingsystem.ui.DBFiles.Labels.SettingLabelFragment
import com.example.incomingdocumentprocessingsystem.ui.message_recycler_view.view.MessageView
import com.example.incomingdocumentprocessingsystem.utilits.APP_ACTIVITY
import com.example.incomingdocumentprocessingsystem.utilits.replaceFragment
import kotlinx.android.synthetic.main.label_item.view.*


class SearchFileAdapter() :
    RecyclerView.Adapter<SearchFileAdapter.SettingFileHolder>() {

    private val listItem = mutableListOf<CommonModel>()

    class SettingFileHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemName: TextView = view.label_name
        val itemTag: TextView = view.label_tag
        val itemDelete: ImageView = view.label_delete
        val itemChoice: ImageView = view.label_choice
        val itemBlock: ConstraintLayout = view.label_bloc
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingFileHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.label_item, parent, false)
        val holder = SettingFileHolder(view)
        holder.itemBlock.setOnClickListener {

            replaceFragment(SearchLabelFragment(listItem[holder.absoluteAdapterPosition]))
        }



        holder.itemDelete.visibility = View.GONE
        return holder
    }




    override fun onBindViewHolder(holder: SettingFileHolder, position: Int) {
        holder.itemName.text = listItem[position].fullname

    }

    override fun getItemCount(): Int = listItem.size
    fun updateListItem(item: CommonModel) {
        listItem.add(item)
        notifyItemInserted(listItem.size)

    }
}

