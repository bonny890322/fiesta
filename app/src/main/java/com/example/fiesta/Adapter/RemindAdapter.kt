package com.network.fiesta

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RemindAdapter(val Items: Array<Array<String>>) : RecyclerView.Adapter<RemindAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val Item: View = LayoutInflater.from(parent.context).inflate(R.layout.row_remind, parent, false)
        return ViewHolder(Item)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.Title.text = Items[position][0]
        holder.Date.text = Items[position][1]
    }

    override fun getItemCount(): Int {
        return Items.size
    }

    class ViewHolder(View: View) : RecyclerView.ViewHolder(View) {
        val Title: TextView = View.findViewById(R.id.tv_title)
        val Date: TextView = View.findViewById(R.id.tv_date)
    }
}