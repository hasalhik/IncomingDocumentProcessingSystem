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
import com.example.incomingdocumentprocessingsystem.database.deleteLabel
import com.example.incomingdocumentprocessingsystem.models.CommonModel
import com.example.incomingdocumentprocessingsystem.ui.message_recycler_view.view.MessageView
import com.example.incomingdocumentprocessingsystem.utilits.APP_ACTIVITY
import kotlinx.android.synthetic.main.label_item.view.*


class AddLabelAdapter(val file: MessageView) :
    RecyclerView.Adapter<AddLabelAdapter.AddLabelsHolder>() {

    private val listItem = mutableListOf<CommonModel>()

    class AddLabelsHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemName: TextView = view.label_name
        val itemTag: TextView = view.label_tag
        val itemDelete: ImageView = view.label_delete
        val itemChoice: ImageView = view.label_choice
        val itemBlock: ConstraintLayout = view.label_bloc
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddLabelsHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.label_item, parent, false)
        val holder = AddLabelsHolder(view)
        holder.itemBlock.setOnClickListener {

            if (holder.itemChoice.visibility == View.INVISIBLE) {

                AddLabelFragment.listChosen.add(listItem[holder.absoluteAdapterPosition])
                holder.itemChoice.visibility = View.VISIBLE

            } else {
                AddLabelFragment.listChosen.remove(listItem[holder.absoluteAdapterPosition])
                holder.itemChoice.visibility = View.INVISIBLE

            }
        }


        if (USER.admin) {
            holder.itemDelete.setOnClickListener {

                val builder = AlertDialog.Builder(APP_ACTIVITY)
                builder.setTitle("Вы действительно хотите удалить признак?")
                    .setCancelable(true)
                    .setPositiveButton("Да") { dialog, id ->
                        deleteLabel(listItem[holder.absoluteAdapterPosition].id) {
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


    override fun onBindViewHolder(holder: AddLabelsHolder, position: Int) {
        holder.itemName.text = listItem[position].fullname
        holder.itemTag.text = listItem[position].state
    }

    override fun getItemCount(): Int = listItem.size
    fun updateListItem(item: CommonModel) {
        listItem.add(item)
        notifyItemInserted(listItem.size)

    }
}

