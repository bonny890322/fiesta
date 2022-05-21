package com.network.fiesta

import android.app.Activity
import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import com.network.fiesta.Lib.TimeLine.Model.OrderStatus
import com.network.fiesta.Lib.TimeLine.Model.TimeLineModel
import kotlinx.android.synthetic.main.dialog_advance_time.view.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class DatePagerAdapter(val Parent: Activity,val act_Id: String, val show: MutableList<MutableList<JSONObject>>,val eventStart: String, val refresh: (int:Int) -> Unit): PagerAdapter() {

    val token = Parent.getSharedPreferences("Data", Context.MODE_PRIVATE).getString("token", "token")!!
    lateinit var timeView: RecyclerView

    override fun isViewFromObject(view: View, o: Any): Boolean {
        return view == o
    }

    override fun getCount(): Int {
        return show.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(container.context).inflate(R.layout.fragment_date, container, false)
        val add = view.findViewById<Button>(R.id.btn_add)
        timeView = view.findViewById(R.id.timeView)

        val click = fun (showId: String?, showName: String?, detail: String?){}
        val mDataList = mutableListOf<TimeLineModel>()
        for(i in 0..show[position].size-1){
            mDataList.add(TimeLineModel("${show[position][i].getString("showName")}", "${show[position][i].getString("Id")}", "${show[position][i].getString("showTime")}","${show[position][i].getString("Detail")}", OrderStatus.INACTIVE,false))
        }
        timeView.layoutManager = LinearLayoutManager(Parent,RecyclerView.VERTICAL, false)
        timeView.adapter = AdvanceTimeAdapter(Parent,mDataList,click)

        add.setOnClickListener{
            val myDialog = LayoutInflater.from(Parent).inflate(R.layout.dialog_advance_time,null)
            val d = AlertDialog.Builder(Parent).setView(myDialog)
            val myAlertDialog = d.show()
            myDialog.btn_cancel.setOnClickListener{
                myAlertDialog.dismiss()
            }

            myDialog.txt_starttime.setOnClickListener{
                val c = Calendar.getInstance()
                val hour = c.get(Calendar.HOUR_OF_DAY)
                val min = c.get(Calendar.MINUTE)

                TimePickerDialog(Parent,android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                    TimePickerDialog.OnTimeSetListener { view, hour, min->
                        myDialog.txt_starttime.setText("$hour:$min", TextView.BufferType.EDITABLE)
                    }, hour, min,false
                ).show()
            }

            myDialog.btn_done.setOnClickListener{
                myAlertDialog.dismiss()

                if (myDialog.txt_name.text.toString() != "" || myDialog.txt_starttime.text.toString() != "" || myDialog.txt_description.text.toString() != "") {
                    Thread(Runnable {
                        run{
                            val dateFormat = SimpleDateFormat("yyyy-MM-dd")
                            //val eventStart = dateFormat.parse(eventStart)
                            val time = eventStart.split(" ")[0].split("-")
                            val c = Calendar.getInstance()
                            c.set(time[0].toInt(),time[1].toInt()-1,time[2].toInt())
                            c.add(Calendar.DATE,position)

                            val timeStart = myDialog.txt_starttime.text.toString().split(":")
                            val temp = API(Parent.applicationContext,token).uploadShow(myDialog.txt_name.text.toString(),act_Id,myDialog.txt_description.text.toString(), dateFormat.format(c.time) + "-" + timeStart[0] + "-" + timeStart[1] + "-00")

                            if(temp.getString("code")=="001"){
                                refresh(position)
                            }else{
                                refresh(position)
                                Parent.runOnUiThread {
                                    Toast.makeText(this.Parent.applicationContext,"發生意外錯誤",Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }).start()
                }else{
                    Toast.makeText(Parent,"請輸入資料", Toast.LENGTH_SHORT).show()
                }
            }
        }
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, any: Any) {
        container.removeView(any as View)
    }
}