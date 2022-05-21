package com.network.fiesta

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONObject

class EditTicketStatusAdapter(val Parent: Activity, val p: Int, val ticket_kinds: ArrayList<String>, val Data: MutableList<JSONObject>, val click:(authId: String, nickName: String, ticketKinds: String, ticketStatus: String, Notes: String, Mail: String) -> Unit, val click_delete:(authId: String) -> Unit, val click_switch:(authId: String, isChecked: Boolean, TicketStatus: String, switch_check: Switch) -> Unit): RecyclerView.Adapter<EditTicketStatusAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.row_edit_ticket_status, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val Data = Data.get(position)
        holder.authId = Data.getString("authId")
        holder.nickName = Data.getString("nickName")
        if(Data.getString("ticketKinds") == "None"){
            holder.ticketKinds = "一般票"
        }else{
            holder.ticketKinds = Data.getString("ticketKinds")
        }


        holder.Mail = Data.getString("Mail")
        holder.ticketStatus = Data.getString("ticketStatus")
        holder.Notes = Data.getString("Notes")
        holder.authId = Data.getString("authId")

        holder.tv_nickName.text = holder.nickName

        if(holder.ticketStatus == "1"){
            holder.switch_check.isChecked = true
        }
        /*
        if(holder.ticketKinds == "None"){
            holder.tv_ticketKinds.text = "一般票"
        }else{
            holder.tv_ticketKinds.text = "( " + holder.ticketKinds + " )"
        }

         */


        if(p == 0){
            if(holder.ticketKinds == "None"){
                holder.tv_ticketKinds.text = "一般票"
            }else{
                holder.tv_ticketKinds.text = holder.ticketKinds
            }
            /*
            if (holder.ticketStatus == "1"){
                holder.img_check.setImageResource(R.drawable.ui_check)
            }else{
                holder.img_check.setImageResource(R.drawable.ic_cancel)
            }

             */

            holder.btn_delete.setOnClickListener {
                click_delete(holder.authId)
            }

            holder.switch_check.setOnCheckedChangeListener { buttonView, isChecked ->
                click_switch(holder.authId, isChecked, holder.ticketStatus, holder.switch_check)
            }

            holder.itemView.setOnClickListener {
                click(holder.authId,holder.nickName,holder.ticketKinds,holder.ticketStatus,holder.Notes, holder.Mail)
            }
        }else if(ticket_kinds[p] == holder.ticketKinds){
            holder.tv_ticketKinds.text = holder.ticketKinds
            /*
            if (holder.ticketStatus == "1"){
                holder.img_check.setImageResource(R.drawable.ui_check)
            }else{
                holder.img_check.setImageResource(R.drawable.ic_cancel)
            }

             */

            holder.btn_delete.setOnClickListener {
                click_delete(holder.authId)
            }

            holder.switch_check.setOnCheckedChangeListener { buttonView, isChecked ->
                click_switch(holder.authId, isChecked,holder.ticketStatus, holder.switch_check)
            }

            holder.itemView.setOnClickListener {
                click(holder.authId,holder.nickName,holder.ticketKinds,holder.ticketStatus,holder.Notes, holder.Mail)
            }
        }else{
            holder.tv_nickName.visibility = View.GONE
            holder.tv_ticketKinds.visibility = View.GONE
            holder.btn_delete.visibility = View.GONE
            holder.imageView11.visibility = View.GONE
            holder.switch_check.visibility = View.GONE
        }


    }

    override fun getItemCount(): Int {
        return Data.size
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val tv_nickName = view.findViewById<TextView>(R.id.tv_nickName)
        val tv_ticketKinds = view.findViewById<TextView>(R.id.tv_ticketKinds)
        val btn_delete = view.findViewById<ImageView>(R.id.btn_delete)
        val imageView11 = view.findViewById<ImageView>(R.id.imageView11)
        val switch_check = view.findViewById<Switch>(R.id.switch_check)
        lateinit var authId: String
        lateinit var nickName: String
        lateinit var ticketKinds: String
        lateinit var ticketStatus: String
        lateinit var Notes: String
        lateinit var Mail: String
    }
}