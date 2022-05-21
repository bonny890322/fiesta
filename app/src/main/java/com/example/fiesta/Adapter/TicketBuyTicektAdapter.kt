package com.network.fiesta

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray

class TicketBuyTicektAdapter(val data:JSONArray,val buyticket:(Int)->Unit): RecyclerView.Adapter<TicketBuyTicektAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.row_ticketbuy, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.length()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tv_title.text = data.getJSONObject(position).getString("ticketKinds")
        holder.tv_price.text = "NT. "+data.getJSONObject(position).getString("Price")
        holder.itemView.setOnClickListener {
            buyticket(position)
        }
    }


    inner class ViewHolder(View: View) : RecyclerView.ViewHolder(View) {
        val tv_title = View.findViewById<TextView>(R.id.tv_title)
        val tv_price = View.findViewById<TextView>(R.id.tv_price)
    }
}