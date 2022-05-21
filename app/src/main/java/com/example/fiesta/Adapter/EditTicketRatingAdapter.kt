package com.network.fiesta

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EditTicketRatingAdapter(val temp1:MutableList<String>,val temp2:MutableList<Float>,val isEdit:Boolean): RecyclerView.Adapter<EditTicketRatingAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.row_rating, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tv_title.text = temp1.get(position)
        if (temp2.get(position).isNaN()) {
            holder.tv_title.visibility = View.GONE
            holder.rb_stars.visibility = View.GONE
        } else {
            holder.rb_stars.rating = temp2.get(position)
            holder.rb_stars.setIsIndicator(!isEdit)
            if(isEdit){
                holder.rb_stars.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
                    temp2.set(position,rating)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return temp1.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tv_title = view.findViewById<TextView>(R.id.tv_title)
        val rb_stars = view.findViewById<RatingBar>(R.id.rb_stars)
    }
}