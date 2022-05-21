package com.network.fiesta

import android.app.Activity
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ShowShowAdapter(val Parent: Activity, val showName: ArrayList<String>, val showtime: ArrayList<String>, val showId: ArrayList<String>, val realTime: String, val today: Boolean, val click:(showId: String?, showName: String?) -> Unit) : RecyclerView.Adapter<ShowShowAdapter.ViewHolder>() {
    val c = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
    val dateFormat2 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.row_showshow, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return showName.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txt_name.text = showName[position]
        holder.txt_time.text = showtime[position].split("-")[1] + "/" + showtime[position].split("-")[2]
        holder.showId = showId[position]
        if (today) {
            if(showtime[position] != ""){
                try {
                    val temp1 = dateFormat.parse(showtime[position])
                    val temp2 = dateFormat.parse(showtime[position + 1])
                    val A = (c.get(Calendar.HOUR_OF_DAY) * 60 + c.get(Calendar.MINUTE)) - (temp1.hours * 60 + temp1.minutes)
                    val B = (c.get(Calendar.HOUR_OF_DAY) * 60 + c.get(Calendar.MINUTE)) - (temp2.hours * 60 + temp2.minutes)
                    if (A >= 0 && B <= 0) {
                        holder.bk.setBackgroundColor(Color.parseColor("#FF9800"))
                    } else {
                        holder.bk.setBackgroundColor(Color.parseColor("#FFFFFF"))
                    }
                } catch (e: Exception) {
                    val temp1 = dateFormat.parse(showtime[position])
                    val A = (c.get(Calendar.HOUR_OF_DAY) * 60 + c.get(Calendar.MINUTE)) - (temp1.hours * 60 + temp1.minutes)
                    if(A >= 0){
                        holder.bk.setBackgroundColor(Color.parseColor("#FF9800"))
                    }else{
                        holder.bk.setBackgroundColor(Color.parseColor("#FFFFFF"))
                    }
                }
            }
        }
        try {
                val temp1 = dateFormat.parse(showtime[position])
                val temp2 = dateFormat2.parse(realTime)
                if (temp1.time < temp2.time) {
                    holder.btn.visibility = View.VISIBLE
                    holder.btn.setOnClickListener {
                        click(holder.showId, holder.txt_name.text.toString())
                    }
                } else {
                    holder.btn.visibility = View.INVISIBLE
                    holder.itemView.setOnClickListener {
                        Toast.makeText(Parent, "活動尚未開始無法評分", Toast.LENGTH_SHORT).show()
                    }
                }
        } catch (e: Exception) { }

        holder.setIsRecyclable(false)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txt_name = itemView.findViewById<TextView>(R.id.txt_name)
        val txt_time = itemView.findViewById<TextView>(R.id.txt_time)
        val bk = itemView.findViewById<ConstraintLayout>(R.id.bk)
        var showId = ""
        val btn = itemView.findViewById<Button>(R.id.btn_show_feeback)
    }
}