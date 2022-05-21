package com.network.fiesta

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import com.network.fiesta.Lib.TimeLine.Model.OrderStatus
import com.network.fiesta.Lib.TimeLine.Model.TimeLineModel
import java.lang.Exception

class ShowAdapter(val Parent: Activity, val days: Int, val showArray: JSONArray, val eventStart: Date, val realTime: String, val click:(showId: String?, showName: String?, detail: String?) -> Unit): RecyclerView.Adapter<ShowAdapter.ViewHolder>() {
    val showname: ArrayList<String> = ArrayList()
    val showtime: ArrayList<String> = ArrayList()
    val showId: ArrayList<String> = ArrayList()
    val showdetail: ArrayList<String> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.row_show, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return days + 1
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val c = Calendar.getInstance()
        showtime.clear()
        showname.clear()
        showId.clear()
        holder.txt_day.text = "第" + (position + 1) + "天"
        for(i in 0..showArray.length()-1){
            val dateFormat = SimpleDateFormat("yyyy-MM-dd")
            val showDate = dateFormat.parse(showArray.getJSONObject(i).getString("showTime"))
            val timediff = showDate.time - eventStart.time
            var day = TimeUnit.MILLISECONDS.toDays(timediff).toInt()

            if(day == position){
                showId.add(showArray.getJSONObject(i).getString("Id"))
                showname.add(showArray.getJSONObject(i).getString("showName"))
                showtime.add(showArray.getJSONObject(i).getString("showTime"))
                if(showArray.getJSONObject(i).getString("Detail")!=""){
                    showdetail.add(showArray.getJSONObject(i).getString("Detail"))
                }else{
                    showdetail.add("none")
                }
            }
        }
        if(showId.isNotEmpty()){
            val date1 = SimpleDateFormat("yyyy-MM-dd HH:mm")
            val date2 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            if(date1.parse(showtime[0])<date2.parse(realTime) && date1.parse(showtime[0]).day != date2.parse(realTime).day){
                for (i in 0..showname.size-1){
                    holder.mDataList.add(TimeLineModel("${showname[i]}", "${showId[i]}", "${showtime[i]}","${showdetail[i]}", OrderStatus.COMPLETED,true))
                }
            }else if(date1.parse(showtime[0])>date2.parse(realTime) && date1.parse(showtime[0]).day != date2.parse(realTime).day){
                for (i in 0..showname.size-1){
                    holder.mDataList.add(TimeLineModel("${showname[i]}", "${showId[i]}", "${showtime[i]}","${showdetail[i]}", OrderStatus.INACTIVE,false))
                }
            }else{
                try {
                    val temp = date2.parse(realTime)
                    for (i in 0..showname.size-1){
                        if (date1.parse(showtime[i]) < temp) {
                            if(i != showtime.size-1 && date1.parse(showtime[i+1]) > temp){
                                holder.mDataList.add(TimeLineModel("${showname[i]}", "${showId[i]}", "${showtime[i]}","${showdetail[i]}", OrderStatus.ACTIVE,true))
                            }else{
                                holder.mDataList.add(TimeLineModel("${showname[i]}", "${showId[i]}", "${showtime[i]}","${showdetail[i]}", OrderStatus.COMPLETED,true))
                            }
                        } else {
                            holder.mDataList.add(TimeLineModel("${showname[i]}", "${showId[i]}", "${showtime[i]}","${showdetail[i]}", OrderStatus.INACTIVE,false))
                        }
                    }
                } catch (e: Exception) { }
            }

            holder.showView.layoutManager = LinearLayoutManager(Parent,RecyclerView.VERTICAL, false)
            holder.showView.adapter = TimeLineAdapter(holder.mDataList,click)
        }else{
            holder.showView.layoutManager = LinearLayoutManager(Parent)
            holder.showView.adapter = TextAdapter("無排程")
        }
        holder.setIsRecyclable(false)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val showView = itemView.findViewById<RecyclerView>(R.id.showView)
        val txt_day = itemView.findViewById<TextView>(R.id.txt_day)
        val mDataList = java.util.ArrayList<TimeLineModel>()
        val button_timeline_score = itemView.findViewById<Button>(R.id.button_timeline_score)
        //val img = itemView.findViewById<ImageView>(R.id.img)
    }
}