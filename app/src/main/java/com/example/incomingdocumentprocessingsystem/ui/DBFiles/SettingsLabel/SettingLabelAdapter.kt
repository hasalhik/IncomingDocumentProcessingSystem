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
import com.example.incomingdocumentprocessingsystem.database.USER
import com.example.incomingdocumentprocessingsystem.database.addTagToFile
import com.example.incomingdocumentprocessingsystem.database.deleteTag
import com.example.incomingdocumentprocessingsystem.models.CommonModel
import com.example.incomingdocumentprocessingsystem.ui.message_recycler_view.view.MessageView
import com.example.incomingdocumentprocessingsystem.utilits.APP_ACTIVITY
import com.example.incomingdocumentprocessingsystem.utilits.hideKeyboard
import kotlinx.android.synthetic.main.tag_item.view.*


class SettingLabelAdapter(private val label: CommonModel, private val file: MessageView) :
    RecyclerView.Adapter<SettingLabelAdapter.SettingLabelsHolder>() {

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
            addTagToFile(file, label, listItem[holder.absoluteAdapterPosition]) {
                APP_ACTIVITY.supportFragmentManager.popBackStack()
                hideKeyboard()
            }


        }


        if (USER.admin) {
            holder.itemDelete.setOnClickListener {

                val builder = AlertDialog.Builder(APP_ACTIVITY)
                builder.setTitle("Вы действительно хотите удалить свойство?")
                    .setCancelable(true)
                    .setPositiveButton("Да") { dialog, id ->
                        deleteTag(listItem[holder.absoluteAdapterPosition].id, label) {
                            listItem.removeAt(holder.absoluteAdapterPosition)
                            notifyDataSetChanged()
                        }
                    }
                    .setNegativeButton("Нет",
                        DialogInterface.OnClickListener { dialog, id ->
                        })
                builder.show()
            }
        } else holder.itemDelete.visibility = View.GONE
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

