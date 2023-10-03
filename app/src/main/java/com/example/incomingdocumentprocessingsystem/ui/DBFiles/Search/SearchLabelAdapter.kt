package com.example.incomingdocumentprocessingsystem.ui.DBFiles.Labels

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
import com.example.incomingdocumentprocessingsystem.database.addTagToFile
import com.example.incomingdocumentprocessingsystem.database.deleteTag
import com.example.incomingdocumentprocessingsystem.models.CommonModel
import com.example.incomingdocumentprocessingsystem.ui.message_recycler_view.view.MessageView
import com.example.incomingdocumentprocessingsystem.utilits.APP_ACTIVITY
import com.example.incomingdocumentprocessingsystem.utilits.hideKeyboard
import kotlinx.android.synthetic.main.tag_item.view.*


class SearchLabelAdapter(private val label: CommonModel) :
    RecyclerView.Adapter<SearchLabelAdapter.SettingLabelsHolder>() {

    private val listItem = mutableListOf<CommonModel>()

    class SettingLabelsHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemName: TextView = view.tag_name
        val itemDelete: ImageView = view.tag_delete
        val itemBlock: ConstraintLayout = view.tag_bloc
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingLabelsHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.tag_item, parent, false)
        val holder = SettingLabelsHolder(view)
        holder.itemBlock.setOnClickListener {

            var searchParameter:CommonModel= CommonModel(id = label.id)
            searchParameter.text=listItem[holder.absoluteAdapterPosition].fullname
            searchParameter.from=label.fullname
            searchParameter.type="l"
            APP_ACTIVITY.searchParameters.add(searchParameter)
            APP_ACTIVITY.supportFragmentManager.popBackStack()
            APP_ACTIVITY.supportFragmentManager.popBackStack()

        }

        holder.itemDelete.visibility = View.GONE
        return holder
    }



    override fun onBindViewHolder(holder: SettingLabelsHolder, position: Int) {
        holder.itemName.text = listItem[position].fullname
    }

    override fun getItemCount(): Int = listItem.size
    fun updateListItem(item: CommonModel) {
        listItem.add(item)
        notifyItemInserted(listItem.size)

    }
}

