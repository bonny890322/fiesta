package com.network.fiesta

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class TicketSortAdapter(val Parent: Activity, val ticketSort:  ArrayList<String>, val ticketQuantity:  ArrayList<String>, val ticketPrice:  ArrayList<String>, val ticketId:  ArrayList<String>,val refresh:() -> Unit) : RecyclerView.Adapter<TicketSortAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.row_ticket_sort, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return ticketSort.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tv_ticketKinds.text = ticketSort[position]
        holder.tv_people.setText(ticketQuantity[position])
        holder.tv_price.setText(ticketPrice[position])
        holder.btn_delete.setOnClickListener{
            Thread(Runnable {
                run{
                    if(API(Parent.applicationContext,holder.token).deleteTicket(ticketId[position]).getString("code")=="001"){
                        refresh()
                    }else{
                        Parent.runOnUiThread {
                           Toast.makeText(Parent,"網路連接失敗",Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }).start()
        }
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val tv_ticketKinds = view.findViewById<TextView>(R.id.tv_ticketKinds)
        val tv_people = view.findViewById<TextView>(R.id.tv_people)
        val tv_price = view.findViewById<TextView>(R.id.tv_price)
        val btn_delete = view.findViewById<Button>(R.id.btn_delete)
        val token = Parent.getSharedPreferences("Data", Context.MODE_PRIVATE).getString("token", "token")!!
    }
}