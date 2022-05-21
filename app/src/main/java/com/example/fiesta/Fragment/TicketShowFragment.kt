package com.network.fiesta

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.dialog_ticket_show_rating.view.*
import kotlinx.android.synthetic.main.fragment_ticket_show.*
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import android.text.format.DateFormat
import java.lang.Exception

class TicketShowFragment(val act_Id:String) : Fragment() {

    var showArray = JSONArray()
    var Id = ""
    var token = ""
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_ticket_show, container, false )
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Id = activity!!.getSharedPreferences("Data", AppCompatActivity.MODE_PRIVATE).getString("authId", "authId")!!
        token = activity!!.getSharedPreferences("Data", AppCompatActivity.MODE_PRIVATE).getString("token", "token")!!

        reload()
        Thread(Runnable {
            run{
                var temp = true
                while (temp){
                    Thread.sleep(30000)
                    if(context != null){
                        reload()
                    }else{
                        temp = false
                    }
                }
            }
        }).start()
    }
    fun click(showId: String?, showName: String?, detail: String?){
        if(detail == ""){
            val myDialog = LayoutInflater.from(activity).inflate(R.layout.dialog_ticket_show_rating, null)
            val d = AlertDialog.Builder(activity).setView(myDialog).setTitle("回報:")
            val myAlertDialog = d.show()
            myDialog.tv_title.text = showName
            myDialog.btn_cancel.setOnClickListener {
                myAlertDialog.dismiss()
            }
            myDialog.btn_done.setOnClickListener {
                if(myDialog.et_detail.text.toString() != ""){
                    val dff = SimpleDateFormat("yyyy-MM-dd")
                    dff.setTimeZone(TimeZone.getTimeZone("Asia/Taipei"))
                    val Date = dff.format(Date())
                    API(context,token).uploadShowScore(act_Id,showId,Id,Date,myDialog.et_detail.text.toString())
                }
                myAlertDialog.dismiss()
            }
        } else {
            if(detail == "none"){
                AlertDialog.Builder(context)
                    .setTitle(showName)
                    .setMessage("無詳細內容")
                    .setNegativeButton("確認", null)
                    .show()
            }else{
                AlertDialog.Builder(context)
                    .setTitle(showName)
                    .setMessage(detail)
                    .setNegativeButton("確認", null)
                    .show()
            }
        }
    }

    fun reload(){
        Thread(Runnable {
            run {
                try {
                    val temp1 = API(context,token).getActivityById(act_Id)
                    val temp2 = API(context,token).getShow(act_Id)
                    if( temp2.getString("code")== "001"){
                        showArray = temp2.getJSONArray("result")
                    }else if(temp2.getString("code") == "013"){

                    }else{
                        activity!!.runOnUiThread {
                            Toast.makeText(activity!!,"網路連線失敗", Toast.LENGTH_SHORT).show()
                        }
                    }

                    if (temp1.getString("code") == "001") {
                        val timetemp = API(context).getTime()
                        if(timetemp.getString("code")=="001"){
                            val realTime = timetemp.getString("result")
                            val dateFormat = SimpleDateFormat("yyyy-MM-dd")
                            val c = Calendar.getInstance()
                            for (i in 0..temp1.getJSONArray("result").length() - 1) {

                                val result = temp1.getJSONArray("result").getJSONObject(i)
                                val starttime = result.getString("startTime")
                                val endtime = result.getString("endTime")
                                val eventStart = dateFormat.parse(starttime)
                                val eventEnd = dateFormat.parse(endtime)
                                val timediff = eventStart.time - c.timeInMillis
                                val timediff2 = eventEnd.time - c.timeInMillis
                                val timediffs = eventEnd.time - eventStart.time
                                val day = TimeUnit.MILLISECONDS.toDays(timediff).toInt()
                                val day2 = TimeUnit.MILLISECONDS.toDays(timediff2).toInt()
                                val days = TimeUnit.MILLISECONDS.toDays(timediffs).toInt()
                                activity!!.runOnUiThread {
                                    if (day == 0) {
                                        txt_start.visibility = View.GONE
                                    } else if (day < 0 && day2 >= 0) {
                                        txt_start.visibility = View.GONE
                                    } else if (day2 < 0) {
                                        txt_start.text = "活動已結束"
                                    } else {
                                        txt_start.text = "活動尚未開始"
                                    }

                                    if (showArray.length() == 0) {
                                        ticketShowView.layoutManager = LinearLayoutManager(activity!!)
                                        ticketShowView.adapter = TextAdapter("主辦方沒有設定排程")
                                    } else {
                                        ticketShowView.layoutManager = LinearLayoutManager(activity!!)
                                        ticketShowView.adapter = ShowAdapter(activity!! , days, showArray, eventStart, realTime, ::click)

                                        val date1 = SimpleDateFormat("yyyy-MM-dd HH:mm")
                                        val date2 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                        val temp1 = date1.parse(starttime)
                                        val temp2 = date2.parse(realTime)
                                        if(DateFormat.format("yyyy",temp2) == DateFormat.format("yyyy",temp1)){
                                            if(DateFormat.format("MM",temp2) == DateFormat.format("MM",temp1)){
                                                if(DateFormat.format("dd",temp2).toString().toInt() - DateFormat.format("dd",temp1).toString().toInt() >= 0){
                                                    ticketShowView.scrollToPosition(DateFormat.format("dd",temp2).toString().toInt() - DateFormat.format("dd",temp1).toString().toInt())
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }else{
                            activity!!.runOnUiThread {
                                Toast.makeText(activity!!, "網路連線失敗", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        activity!!.runOnUiThread {
                            Toast.makeText(activity!!, "網路連線失敗", Toast.LENGTH_SHORT).show()
                        }
                    }
                }catch (e:Exception){
                    try {
                        activity!!.runOnUiThread {
                            Toast.makeText(context,"發生意外之錯誤",Toast.LENGTH_SHORT).show()
                        }
                    }catch (e:Exception){ }
                }
            }
        }).start()
    }
}