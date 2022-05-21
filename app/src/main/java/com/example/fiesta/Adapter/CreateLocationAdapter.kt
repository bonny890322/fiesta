package com.network.fiesta

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CreateLocationAdapter(val title: MutableList<String>, val text: MutableList<String>, val check: MutableList<Boolean>,val click:(position: Int)-> Unit): RecyclerView.Adapter<CreateLocationAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.row_location, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return title.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tv_title.text = title.get(position)
        holder.tv_text.text = text.get(position)
        if (check.get(position)){
            holder.img_check.setImageResource(R.drawable.ui_check)
        }else{
            holder.img_check.setImageResource(0)
        }
        holder.itemView.setOnClickListener {
            click(position)
        }
    }


    inner class ViewHolder(View: View) : RecyclerView.ViewHolder(View) {
        val tv_title = View.findViewById<TextView>(R.id.tv_title)
        val tv_text = View.findViewById<TextView>(R.id.tv_text)
        val img_check = View.findViewById<ImageView>(R.id.img_check)
    }
}