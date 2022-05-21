//package com.network.fiesta
//
//import android.app.Activity
//import android.app.AlertDialog
//import android.app.TimePickerDialog
//import android.content.Context
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import android.widget.Toast
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import androidx.viewpager.widget.PagerAdapter
//import com.network.fiesta.Lib.TimeLine.Model.OrderStatus
//import com.network.fiesta.Lib.TimeLine.Model.TimeLineModel
//import kotlinx.android.synthetic.main.dialog_advance_time.view.*
//import org.json.JSONArray
//import java.text.SimpleDateFormat
//import java.util.*
//import java.util.concurrent.TimeUnit
//import kotlin.collections.ArrayList
//
//
//class TestAdapter(val Parent: Activity, val days: Int, val showArray: JSONArray, val Id: String, val eventStart: Date,  val refresh2: (int:Int) -> Unit): PagerAdapter() {
//
//    val showname: ArrayList<String> = ArrayList()
//    val showtime: ArrayList<String> = ArrayList()
//    val showId: ArrayList<String> = ArrayList()
//    val showdetail: ArrayList<String> = ArrayList()
//    val token = Parent.getSharedPreferences("Data", Context.MODE_PRIVATE).getString("token", "token")!!
//
//    override fun isViewFromObject(view: View, o: Any): Boolean {
//        return view == o
//    }
//
//    override fun getCount(): Int {
//        return days+1
//    }
//
//    override fun instantiateItem(container: ViewGroup, position: Int): Any {
//        val view = LayoutInflater.from(container.context).inflate(R.layout.fragment_date, container, false)
//
//        val timeView = view.findViewById<RecyclerView>(R.id.timeView)
//        val btn_add = view.findViewById<TextView>(R.id.btn_add)
//        val mDataList = java.util.ArrayList<TimeLineModel>()
//        showtime.clear()
//        showname.clear()
//        showId.clear()
//        showdetail.clear()
//
//        for(i in 0..showArray.length()-1){
//            val dateFormat = SimpleDateFormat("yyyy-MM-dd")
//            val showDate = dateFormat.parse(showArray.getJSONObject(i).getString("showTime"))
//            val timediff = showDate.time - eventStart.time
//            var day = TimeUnit.MILLISECONDS.toDays(timediff).toInt()
//
//            if(day == position){
//                showId.add(showArray.getJSONObject(i).getString("Id"))
//                showname.add(showArray.getJSONObject(i).getString("showName"))
//                showtime.add(showArray.getJSONObject(i).getString("showTime"))
//                if(showArray.getJSONObject(i).getString("Detail")!=""){
//                    showdetail.add(showArray.getJSONObject(i).getString("Detail"))
//                }else{
//                    showdetail.add("none")
//                }
//            }
//        }
//        if(showId.isNotEmpty()){
//            for (i in 0..showname.size-1){
//                mDataList.add(TimeLineModel("${showname[i]}", "${showId[i]}", "${showtime[i]}","${showdetail[i]}", OrderStatus.INACTIVE,false))
//            }
//            fun noaction(showId: String?, showName: String?, detail: String?){}
//            timeView.layoutManager = LinearLayoutManager(Parent,RecyclerView.VERTICAL, false)
//            timeView.adapter = TimeLineAdapter(mDataList,::noaction)
//        }else{
//            timeView.layoutManager = LinearLayoutManager(Parent)
//            timeView.adapter = TextAdapter("無排程")
//        }
//
//
//        btn_add.setOnClickListener{
//            val myDialog = LayoutInflater.from(Parent).inflate(R.layout.dialog_advance_time,null)
//            val d = AlertDialog.Builder(Parent).setView(myDialog)
//            val myAlertDialog = d.show()
//            myDialog.btn_cancel.setOnClickListener{
//                myAlertDialog.dismiss()
//            }
//
//            myDialog.txt_starttime.setOnClickListener{
//                val c = Calendar.getInstance()
//                val hour = c.get(Calendar.HOUR_OF_DAY)
//                val min = c.get(Calendar.MINUTE)
//
//                TimePickerDialog(Parent,android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
//                    TimePickerDialog.OnTimeSetListener { view, hour, min->
//                        myDialog.txt_starttime.setText("$hour:$min", TextView.BufferType.EDITABLE)
//                    }, hour, min,false
//                ).show()
//            }
//
//            myDialog.btn_done.setOnClickListener{
//                myAlertDialog.dismiss()
//
//                if (myDialog.txt_name.text.toString() != "" || myDialog.txt_starttime.text.toString() != "" || myDialog.txt_description.text.toString() != "") {
//                    Thread(Runnable {
//                        run{
//                            val dateFormat = SimpleDateFormat("yyyy-MM-dd")
//                            //val eventStart = dateFormat.parse(eventStart)
//                            val time = eventStart.split(" ")[0].split("-")
//                            val c = Calendar.getInstance()
//                            c.set(time[0].toInt(),time[1].toInt()-1,time[2].toInt())
//                            c.add(Calendar.DATE,position)
//
//                            val timeStart = myDialog.txt_starttime.text.toString().split(":")
//                            val temp = API(Parent.applicationContext,token).uploadShow(myDialog.txt_name.text.toString(),Id,myDialog.txt_description.text.toString(), dateFormat.format(c.time) + "-" + timeStart[0] + "-" + timeStart[1] + "-00")
//                            Parent.runOnUiThread {
//                                if(temp.getString("code")=="001"){
//                                    refresh2(position)
//                                }else{
//                                    refresh2(position)
//                                    Toast.makeText(this.Parent.applicationContext,"發生意外錯誤", Toast.LENGTH_SHORT).show()
//                                }
//                            }
//                        }
//                    }).start()
//                }else{
//                    Toast.makeText(Parent,"請輸入資料", Toast.LENGTH_SHORT).show()
//                }
//            }
//
//        container.addView(view)
//        return view
//    }
//
//    override fun destroyItem(container: ViewGroup, position: Int, any: Any) {
//        container.removeView(any as View)
//    }
//
//}