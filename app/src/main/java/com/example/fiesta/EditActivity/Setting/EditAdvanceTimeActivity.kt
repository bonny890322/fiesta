package com.network.fiesta

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.listener.OnTimeSelectListener
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_advance_time.*
import kotlinx.android.synthetic.main.activity_advance_time.DateEnd
import kotlinx.android.synthetic.main.activity_advance_time.DateStart
import org.json.JSONObject
import java.util.*
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit


class EditAdvanceTimeActivity : AppCompatActivity() {
    var models = mutableListOf<String>()
    var datestart = ""
    var dateend = ""
    var show:JSONArray = JSONArray()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_advance_time)

        val intent = getIntent()
        val Id = intent.getStringExtra("Id")
        val token = getSharedPreferences("Data", MODE_PRIVATE).getString("token", "token")!!
        var temp = JSONObject()

        Thread(Runnable {
            run {
                temp = API(this.applicationContext,token).getActivityById(Id)
                if (temp.getString("code") == "001") {
                    val Data = temp.getJSONArray("result").getJSONObject(0)
                    val start = Data.getString("startTime").split(" ")
                    val end = Data.getString("endTime").split(" ")
                    datestart = start[0] + " " + start[1]
                    dateend = end[0] + " " + end[1]
                    if(Data.getString("Models").toString()!="None"){
                        models = Data.getString("Models").split(",").toMutableList()
                        for(i in 0..models.size-1){
                            if (models[i] == "4"){
                                runOnUiThread {
                                    switch_advance.isChecked = true
                                    tabs_date.visibility = View.VISIBLE
                                    datepager.visibility = View.VISIBLE
                                }
                            }
                        }
                    }

                    var days = 0
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd")
                    val eventStart = dateFormat.parse(datestart)
                    val eventEnd = dateFormat.parse(dateend)
                    val timediff = eventEnd.time - eventStart.time + 1
                    days = TimeUnit.MILLISECONDS.toDays(timediff).toInt() + 1

                    for(i in 1..days){
                        runOnUiThread {
                            tabs_date.addTab(tabs_date.newTab().setText("第${i}天"))
                        }
                    }

                    val Data2 = API(this.applicationContext).getShow(Id)
                    var temp1= mutableListOf<MutableList<JSONObject>>()
                    if(Data2.getString("code") == "001") {
                        var temp2 = Data2.getJSONArray("result")
                        for(i in 0..days-1){
                            var temp3= mutableListOf<JSONObject>()
                            for (j in 0..temp2.length()-1){
                                val showTime = temp2.getJSONObject(j).getString("showTime")
                                var time = showTime.split(" ")
                                val dateFormat = SimpleDateFormat("yyyy-MM-dd")
                                val eventStart = dateFormat.parse(datestart)
                                val showDate = dateFormat.parse(showTime)
                                val timediff = showDate.time - eventStart.time
                                val day = TimeUnit.MILLISECONDS.toDays(timediff).toInt()
                                if (day == i) {
                                    temp3.add(temp2.getJSONObject(j))
                                }
                            }
                            temp1.add(temp3)
                        }
                    }

                    runOnUiThread {
                        DateStart.setText(datestart)
                        DateEnd.setText(dateend)
                        datepager.adapter = DatePagerAdapter(this, Id, temp1, datestart, ::refresh)
                    }

                    datepager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs_date))
                    tabs_date.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(datepager))
                } else {
                    Toast.makeText(this,"網路連接失敗",Toast.LENGTH_SHORT).show()
                }
            }
        }).start()

        switch_advance.setOnCheckedChangeListener{buttonView, isChecked ->
            Thread(Runnable {
                run {
                    if (isChecked) {
                        runOnUiThread {
                            tabs_date.visibility = View.VISIBLE
                            datepager.visibility = View.VISIBLE
                        }
                        var temp = ""
                        if(models.size != 0){
                            for(i in 0..models.size-1){
                                if (models[i] != "4"){
                                    temp += models[i]+","
                                }
                            }
                            API(this.applicationContext,token).updateActivity(Id, Models = temp + "4")
                        }else{
                            API(this.applicationContext,token).updateActivity(Id, Models = "4")
                        }
                    } else {
                        runOnUiThread {
                            tabs_date.visibility = View.GONE
                            datepager.visibility = View.GONE
                        }
                        if (models.remove("4").toString() == "") {
                            API(this.applicationContext,token).updateActivity(Id, Models = "")
                        } else {
                            var temp = ""
                            for (i in 0..models.size - 1) {
                                if (i == models.size - 1) {
                                    temp += models[i]
                                } else {
                                    temp += models[i] + ","
                                }
                            }
                            API(this.applicationContext,token).updateActivity(Id, Models = temp)
                        }
                    }
                }
            }).start()
        }

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
        lateinit var realtime:String

        Thread(Runnable {
            run{
                realtime  = API(this.applicationContext).getTime().getString("result")
            }
        }).start()

        DateStart.setOnClickListener {

            val ime = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            ime.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
            val startDate = Calendar.getInstance()
            val endDate = Calendar.getInstance()
            val time = realtime.split(" ")
            val year = time[0].split("-")[0].toInt()
            val month = time[0].split("-")[1].toInt()
            val day = time[0].split("-")[2].toInt()
            val thiscontext = this
            startDate.set(year,month-1,day)
            endDate.set(year+1,11,31)
            val pvTime = TimePickerBuilder(this, object : OnTimeSelectListener {
                override fun onTimeSelect(date: Date, v: View?) {
                    DateStart.setText(dateFormat.format(date))
                    datestart = DateStart.text.toString()

                    API(thiscontext,token).updateActivity(Id,startTime = "$datestart")
                    if(switch_advance.isChecked){
                        refresh(0)
                    }
                }
            }).setType(booleanArrayOf(true, true, true, true, true, false))
                .setRangDate(startDate,endDate)
                .setCancelText("取消")
                .setSubmitText("確定")
                .setContentTextSize(20)
                .setTitleSize(22)
                .setOutSideCancelable(false)
                .setLabel("", "", "", "時", "分", "")
                .isCenterLabel(false)
                .build()
            pvTime.show()
        }

        DateEnd.setOnClickListener {

            if(DateStart.text.toString() == ""){
                Toast.makeText(this,"請先選取開始時間", Toast.LENGTH_SHORT).show()
            }else{
                val ime = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                ime.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
                val time = DateStart.text.toString().split(" ")
                val year = time[0].split("-")[0].toInt()
                val month = time[0].split("-")[1].toInt()
                val day = time[0].split("-")[2].toInt()
                val startDate = Calendar.getInstance()
                val endDate = Calendar.getInstance()
                val thiscontext = this
                startDate.set(year,month-1,day)
                endDate.set(year+1,11,31)

                val pvTime = TimePickerBuilder(this, object : OnTimeSelectListener {
                    override fun onTimeSelect(date: Date, v: View?) {
                        DateEnd.setText(dateFormat.format(date))
                        dateend = DateEnd.text.toString()
                        API(thiscontext,token).updateActivity(Id,endTime = "$dateend")
                        if(switch_advance.isChecked){
                            refresh(0)
                        }
                    }
                }).setType(booleanArrayOf(true, true, true, true, true, false))
                    .setRangDate(startDate,endDate)
                    .setCancelText("取消")
                    .setSubmitText("確定")
                    .setContentTextSize(20)
                    .setTitleSize(22)
                    .setOutSideCancelable(false)
                    .setLabel("", "", "", "時", "分", "")
                    .isCenterLabel(false)
                    .build()
                pvTime.show()
            }
        }
    }


    fun refresh(int: Int){
        val Id = intent.getStringExtra("Id")
        val token = getSharedPreferences("Data", MODE_PRIVATE).getString("token", "token")!!
        val Data = API(this.applicationContext,token).getActivityById(Id).getJSONArray("result").getJSONObject(0)
        val start = Data.getString("startTime").split(" ")
        val end = Data.getString("endTime").split(" ")
        datestart = start[0] + " " + start[1]
        dateend = end[0] + " " + end[1]
        var days = 0
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val eventStart = dateFormat.parse(datestart)
        val eventEnd = dateFormat.parse(dateend)
        val timediff = eventEnd.time - eventStart.time + 1
        days = TimeUnit.MILLISECONDS.toDays(timediff).toInt() + 1

        if(days > tabs_date.tabCount){
            val d = days - tabs_date.tabCount
            for(i in 1..d){
                runOnUiThread {
                    tabs_date.addTab(tabs_date.newTab().setText("第${tabs_date.tabCount + 1}天"))
                }
            }
        }else if(days < tabs_date.tabCount){
            val d = tabs_date.tabCount - days
            for(i in 1..d){
                runOnUiThread {
                    tabs_date.removeTab(tabs_date.getTabAt(days)!!)
                }
            }
        }else{ }

        val Data2 = API(this.applicationContext).getShow(Id)
        var temp1= mutableListOf<MutableList<JSONObject>>()
        if(Data2.getString("code") == "001") {
            var temp2 = Data2.getJSONArray("result")
            for(i in 0..days-1){
                var temp3= mutableListOf<JSONObject>()
                for (j in 0..temp2.length()-1){
                    val showTime = temp2.getJSONObject(j).getString("showTime")
                    var time = showTime.split(" ")
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd")
                    val eventStart = dateFormat.parse(datestart)
                    val showDate = dateFormat.parse(showTime)
                    val timediff = showDate.time - eventStart.time
                    val day = TimeUnit.MILLISECONDS.toDays(timediff).toInt()
                    Log.e("day",day.toString())
                    if (day == i) {
                        temp3.add(temp2.getJSONObject(j))
                    }
                }
                temp1.add(temp3)
            }

        }

        runOnUiThread {
            DateStart.setText(datestart)
            DateEnd.setText(dateend)
            datepager.adapter = DatePagerAdapter(this, Id, temp1, datestart, ::refresh)
            datepager.setCurrentItem(int)
        }
    }

}