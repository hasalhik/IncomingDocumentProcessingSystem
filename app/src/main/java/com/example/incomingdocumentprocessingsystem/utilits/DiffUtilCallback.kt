package com.example.incomingdocumentprocessingsystem.utilits

import androidx.recyclerview.widget.DiffUtil
import com.example.incomingdocumentprocessingsystem.models.CommonModel

class DiffUtilCallback (
    private var oldList: List<CommonModel>,
    private var newList: List<CommonModel>
    ): DiffUtil.Callback()
{
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int=newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition].timeStamp == newList[newItemPosition].timeStamp
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition] == newList[newItemPosition]

}