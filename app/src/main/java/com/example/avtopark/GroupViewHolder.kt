package com.example.avtopark

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    // Например, у вас может быть TextView для отображения заголовка группы
    private val groupTextView: TextView = itemView.findViewById(R.id.codition)

    fun bind(groupName: String) {
        // Настройка данных для заголовка группы
        groupTextView.text = groupName
    }
}