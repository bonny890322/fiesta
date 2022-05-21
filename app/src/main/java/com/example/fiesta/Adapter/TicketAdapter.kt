package com.network.fiesta

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONObject
import java.util.*

class TicketAdapter(val Parent: Activity, val Data: MutableList<JSONObject>, val checkActivity:(act_Id:String) -> Unit) : RecyclerView.Adapter<TicketAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.row_ticket, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return Data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val Data = Data.get(position)
        holder.tv_title.text = Data.getString("act_Name")
        holder.tv_date.text = Data.getString("startTime")+"~"+Data.getString("endTime")
        holder.tv_location.text = Data.getString("Location")
        var tags = ""
        for (i in 0..Data.getJSONArray("Tag").length()-1){
            tags += "#"+Data.getJSONArray("Tag").getString(i)
        }
        holder.tv_tag.text = tags
        val act_Id = Data.getString("act_Id")
        if (Data.getString("viewStatus") == "1") {
            val startdateTime = Data.getString("startTime").split(" ")[0].split("-")
            val startdate = Date(startdateTime[0].toInt(), startdateTime[1].toInt()-1, startdateTime[2].toInt())
            val enddateTime = Data.getString("endTime").split(" ")[0].split("-")
            val enddate = Date(enddateTime[0].toInt(), enddateTime[1].toInt()-1, enddateTime[2].toInt())
            val currentTime = Calendar.getInstance().time
            if (currentTime > startdate && currentTime < enddate) {
                holder.background.setBackgroundResource(R.color.orange)
                holder.tv_title.setTextColor(Parent.resources.getColor(R.color.white))
                holder.tv_date.setTextColor(Parent.resources.getColor(R.color.white))
                holder.tv_location.setTextColor(Parent.resources.getColor(R.color.white))
                holder.tv_tag.setTextColor(Parent.resources.getColor(R.color.white))
            }
        }
        try {
            Thread(Runnable {
                run{
                    if(Data.getString("Photo")!="None"){
                        try {
                            val bitmap = API(Parent.applicationContext).SearchIMG(Data.getString("Photo"))
                            Parent.runOnUiThread {
                                holder.img.setImageBitmap(bitmap)
                            }
                        }catch (e:Exception){
                            Parent.runOnUiThread {
                                holder.img.setImageResource(R.drawable.ui_fiestalogo)
                            }
                        }
                    }
                }
            }).start()
        }catch (e:Exception){
            Toast.makeText(Parent,"載入圖片失敗",Toast.LENGTH_SHORT).show()
        }

        holder.itemView.setOnClickListener {
            checkActivity(act_Id)
        }

    }
    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val tv_title = view.findViewById<TextView>(R.id.tv_title)
        val tv_date = view.findViewById<TextView>(R.id.tv_date)
        val tv_tag = view.findViewById<TextView>(R.id.tv_tag)
        val tv_location = view.findViewById<TextView>(R.id.tv_location)
        val img = view.findViewById<ImageView>(R.id.img)
        val background = view.findViewById<ConstraintLayout>(R.id.background)
    }

}