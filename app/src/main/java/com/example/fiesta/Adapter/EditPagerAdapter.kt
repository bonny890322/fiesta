package com.network.fiesta

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import com.example.fiesta.Lib.WrapContentLinearLayoutManager
import org.json.JSONObject
import java.util.*


class EditPagerAdapter(val Parent: Activity, val activityArray: MutableList<JSONObject>, val expArray: MutableList<JSONObject> ): PagerAdapter() {


    override fun isViewFromObject(view: View, o: Any): Boolean {
        return view == o
    }

    override fun getCount(): Int {
        return 2
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(container.context).inflate(R.layout.row_recycleview, container, false)

        val recycleview = view.findViewById<RecyclerView>(R.id.recycleview)
        recycleview.layoutManager = WrapContentLinearLayoutManager(Parent,LinearLayoutManager.VERTICAL,false)
        if(position == 0){
            if(activityArray.size != 0){
                val temp1 = mutableListOf<JSONObject>()
                val temp2 = mutableListOf<JSONObject>()
                for(i in 0..activityArray.size-1){
                    val dateTime = activityArray[i].getString("startTime").split(" ")[0]
                    val date = Date(dateTime[0].toInt(), dateTime[1].toInt(), dateTime[2].toInt())
                    val currentTime = Calendar.getInstance().time
                    if(currentTime>date){
                        temp1.add(activityArray[i])
                    }else{
                        temp2.add(activityArray[i])
                    }
                }
                for(i in 0..temp2.size-1){
                    temp1.add(temp2[i])
                }
                recycleview.adapter = EditAdapter(Parent,temp1)
            }else{
                recycleview.adapter = TextAdapter("尚無創建活動")
            }
        }else if(position == 1){
            if(expArray.size != 0){
                recycleview.adapter = EditAdapter(Parent,expArray)
            }else{
                recycleview.adapter = TextAdapter("尚無創建活動")
            }
        }

        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, any: Any) {
        container.removeView(any as View)
    }

}