package com.example.avtopark

import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView

class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val ID:TextView=itemView.findViewById(R.id.ID)
    val stoping:TextView=itemView.findViewById(R.id.stoping)
    val sity:TextView=itemView.findViewById(R.id.sity)
}