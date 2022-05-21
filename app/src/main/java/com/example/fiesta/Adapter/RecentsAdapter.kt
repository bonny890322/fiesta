package com.network.fiesta

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecentsAdapter(val recentsSort: ArrayList<String>): RecyclerView.Adapter<RecentsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.row_recents, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return recentsSort.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.rec_Sort.text = recentsSort[position]
    }


    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val rec_Img = itemView.findViewById<ImageView>(R.id.rec_Img)
        val rec_Sort = itemView.findViewById<TextView>(R.id.rec_Sort)
        val rec_Content = itemView.findViewById<TextView>(R.id.rec_Content)
        val img_star = itemView.findViewById<ImageView>(R.id.img_star)
    }
}