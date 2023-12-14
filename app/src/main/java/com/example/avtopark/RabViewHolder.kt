package com.example.avtopark

import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView

class RabViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val ID: TextView =itemView.findViewById(R.id.ID)
    val codition: TextView =itemView.findViewById(R.id.codition)
    val precent: TextView =itemView.findViewById(R.id.precent)
    val model: TextView =itemView.findViewById(R.id.model)
}