package com.network.fiesta

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import com.network.fiesta.Lib.TimeLine.Model.OrderStatus
import com.network.fiesta.Lib.TimeLine.Model.TimeLineModel
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class TicketBuyShowPagerAdapter(val Parent: Activity, val days: Int, val showArray: JSONArray, val eventStart: Date, val realTime: String, val click:(showId: String?, showName: String?, detail: String?) -> Unit): PagerAdapter() {

    val showname: ArrayList<String> = ArrayList()
    val showtime: ArrayList<String> = ArrayList()
    val showId: ArrayList<String> = ArrayList()
    val showdetail: ArrayList<String> = ArrayList()

    override fun isViewFromObject(view: View, o: Any): Boolean {
        return view == o
    }

    override fun getCount(): Int {
        return days+1
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(container.context).inflate(R.layout.row_show, container, false)

        val showView = view.findViewById<RecyclerView>(R.id.showView)
        val txt_day = view.findViewById<TextView>(R.id.txt_day)
        val mDataList = java.util.ArrayList<TimeLineModel>()
        showtime.clear()
        showname.clear()
        showId.clear()
        showdetail.clear()
        txt_day.text = "第" + (position + 1) + "天"
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
                    mDataList.add(TimeLineModel("${showname[i]}", "${showId[i]}", "${showtime[i]}","${showdetail[i]}", OrderStatus.COMPLETED,false))
                }
            }else if(date1.parse(showtime[0])>date2.parse(realTime) && date1.parse(showtime[0]).day != date2.parse(realTime).day){
                for (i in 0..showname.size-1){
                    mDataList.add(TimeLineModel("${showname[i]}", "${showId[i]}", "${showtime[i]}","${showdetail[i]}", OrderStatus.INACTIVE,false))
                }
            }else{
                try {
                    val temp = date2.parse(realTime)
                    for (i in 0..showname.size-1){
                        if (date1.parse(showtime[i]) < temp) {
                            if(i != showtime.size-1 && date1.parse(showtime[i+1]) > temp){
                                mDataList.add(TimeLineModel("${showname[i]}", "${showId[i]}", "${showtime[i]}","${showdetail[i]}", OrderStatus.ACTIVE,false))
                            }else{
                                mDataList.add(TimeLineModel("${showname[i]}", "${showId[i]}", "${showtime[i]}","${showdetail[i]}", OrderStatus.COMPLETED,false))
                            }
                        } else {
                            mDataList.add(TimeLineModel("${showname[i]}", "${showId[i]}", "${showtime[i]}","${showdetail[i]}", OrderStatus.INACTIVE,false))
                        }
                    }
                } catch (e: Exception) { }
            }

            showView.layoutManager = LinearLayoutManager(Parent,RecyclerView.VERTICAL, false)
            showView.adapter = TimeLineAdapter(mDataList,click)
        }else{
            showView.layoutManager = LinearLayoutManager(Parent)
            showView.adapter = TextAdapter("無排程")
        }
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, any: Any) {
        container.removeView(any as View)
    }

}