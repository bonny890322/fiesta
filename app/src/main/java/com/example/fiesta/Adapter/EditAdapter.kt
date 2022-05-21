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

class EditAdapter(val Parent: Activity, val Data: MutableList<JSONObject>) : RecyclerView.Adapter<EditAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.row_edit, parent, false)
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
        try {
            if(Data.getString("viewStatus")=="1"){
                val datestartTime = Data.getString("startTime").split(" ")[0].split("-")
                val datestart = Calendar.getInstance()
                datestart.set(datestartTime[0].toInt(), datestartTime[1].toInt()-1, datestartTime[2].toInt())

                val dateendTime = Data.getString("endTime").split(" ")[0].split("-")
                val dateend = Calendar.getInstance()
                dateend.set(dateendTime[0].toInt(), dateendTime[1].toInt()-1, dateendTime[2].toInt())
                val currentTime = Calendar.getInstance().time
                if (currentTime > datestart.time && currentTime < dateend.time) {
                    holder.background.setBackgroundResource(R.drawable.shape_round_corner_orange_28)
                    holder.tv_title.setTextColor(Parent.resources.getColor(R.color.white))
                    holder.tv_date.setTextColor(Parent.resources.getColor(R.color.white))
                    holder.tv_location.setTextColor(Parent.resources.getColor(R.color.white))
                    holder.tv_tag.setTextColor(Parent.resources.getColor(R.color.white))
                    holder.switch_public.setTextColor(Parent.resources.getColor(R.color.white))
                }
                Parent.runOnUiThread {
                    holder.switch_public.text = "已發佈"
                    holder.switch_public.isChecked = true
                }
            }else{
                Parent.runOnUiThread {
                    holder.switch_public.text = "未發佈"
                    holder.switch_public.isChecked = false
                }
            }
        }catch (e:Exception){
            Parent.runOnUiThread {
                holder.switch_public.text = "已結束"
                holder.switch_public.isChecked = true
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
            if(holder.switch_public.text == "未發佈"){
                val intent = Intent(Parent,EditActivity::class.java)
                    .putExtra("act_Id",act_Id)
                Parent.startActivity(intent)
            }else{
                val intent = Intent(Parent,EditTicketActivity::class.java)
                    .putExtra("act_Id",act_Id)
                    .putExtra("act_Name",Data.getString("act_Name"))
                Parent.startActivity(intent)
            }

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
        val switch_public = view.findViewById<Switch>(R.id.switch_public)
    }

}