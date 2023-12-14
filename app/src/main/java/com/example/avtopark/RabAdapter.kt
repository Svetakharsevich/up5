package com.example.avtopark

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class RabAdapter(var rabList: MutableList<Bus> = mutableListOf()) : RecyclerView.Adapter<RabViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RabViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_item_recycle_rab, parent, false)
        return RabViewHolder(itemView)
    }
    override fun onBindViewHolder(holder: RabViewHolder, position: Int) {
        val currentUser = rabList[position]

        holder.ID.text = "Номер автобуса: ${currentUser.id}"

        holder.codition.text = "Состояние: ${currentUser.condition}"

        holder.precent.text = "Процент амортизации: ${currentUser.percent}"

        holder.model.text="Модель автобуса: ${currentUser.model}"
    }

    override fun getItemCount(): Int {
        return rabList.size
    }
    fun updateData(newDataList: MutableList<Bus>) {
        rabList.clear()
        rabList.addAll(newDataList)
        notifyDataSetChanged()
    }

}