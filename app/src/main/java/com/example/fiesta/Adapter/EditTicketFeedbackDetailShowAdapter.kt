package com.network.fiesta

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView

class EditTicketFeedbackDetailShowAdapter(val title: ArrayList<String>, val detail: ArrayList<String>): RecyclerView.Adapter<EditTicketFeedbackDetailShowAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.row_ticket_feedback_detail_show, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return title.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tv_title.text = title.get(position)
        holder.et_detail.setText(detail.get(position))
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv_title = itemView.findViewById<TextView>(R.id.tv_title)
        val et_detail = itemView.findViewById<EditText>(R.id.et_detail)
    }
}