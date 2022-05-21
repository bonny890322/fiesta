package com.network.fiesta

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FavoritesAdapter(val Parent: Activity, val favoritesSort: ArrayList<String>): RecyclerView.Adapter<FavoritesAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.row_tag_fav, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return favoritesSort.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tv_interest.text = favoritesSort[position]
        holder.itemView.setOnClickListener{
            val intent = Intent(Parent, LoginTagActivity::class.java)
            intent.putExtra("msg", "personal")
            Parent.startActivity(intent)
        }
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val tv_interest = itemView.findViewById<TextView>(R.id.tv_interest)
    }
}
