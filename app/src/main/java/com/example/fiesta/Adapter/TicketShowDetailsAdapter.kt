package com.network.fiesta

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONObject

class TicketShowDetailsAdapter(val Parent:Activity, val Data:MutableList<JSONObject>,val checkActivity:(act_Id:String) -> Unit): RecyclerView.Adapter<TicketShowDetailsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.row_ticket_details, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return Data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tv_time.text = Data.get(position).getString("startTime")
        holder.tv_title.text = Data.get(position).getString("act_Name")
        if(Data.get(position).getString("Photo") != "None"){
            Thread(Runnable {
                run{
                    try {
                        val bitmap = API(Parent.applicationContext).SearchIMG(Data.get(position).getString("Photo"))
                        Parent.runOnUiThread {
                            holder.img.setImageBitmap(bitmap)
                        }
                    }catch (e:Exception){
                        Parent.runOnUiThread {
                            holder.img.setImageResource(R.drawable.ui_fiestalogo)
                        }
                    }
                }
            }).start()
        }

        holder.itemView.setOnClickListener {
            checkActivity(Data.get(position).getString("act_Id"))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv_time = itemView.findViewById<TextView>(R.id.tv_time)
        val tv_title = itemView.findViewById<TextView>(R.id.tv_title)
        val tv_status = itemView.findViewById<TextView>(R.id.tv_status)
        val img = itemView.findViewById<ImageView>(R.id.img)
    }
}