package com.example.avtopark

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class UserAdapter(var userList: MutableList<Routes> = mutableListOf()) :
    RecyclerView.Adapter<UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_item_recycle, parent, false)
        return UserViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val route = userList[position]
        holder.ID.text = "Номер маршрута: ${route.id}"
        holder.stoping.text = "Остановки: ${route.stoping}"
        holder.sity.text = "Город: ${route.sity_routes}"
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    fun updateData(newDataList: MutableList<Routes>) {
        userList.clear()
        userList.addAll(newDataList)
        notifyDataSetChanged()
    }
}
