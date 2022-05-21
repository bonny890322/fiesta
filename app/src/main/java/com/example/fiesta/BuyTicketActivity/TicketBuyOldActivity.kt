package com.network.fiesta


import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_ticket_buy_old.*
import kotlinx.android.synthetic.main.dialog_edit.view.btn_cancel
import kotlinx.android.synthetic.main.dialog_edit.view.btn_done
import kotlinx.android.synthetic.main.dialog_edit.view.et_text
import kotlinx.android.synthetic.main.dialog_edit2.view.*
import org.json.JSONArray
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class TicketBuyOldActivity : AppCompatActivity() , OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ticket_buy_old)

        img_loading.visibility = View.VISIBLE
        progress_Bar.visibility = View.VISIBLE
        val token = getSharedPreferences("Data", MODE_PRIVATE).getString("token", "token")!!
        val authId = getSharedPreferences("Data", MODE_PRIVATE).getString("authId","authId")
        val Id = getIntent().getStringExtra("Id")
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        Thread(Runnable {
            run {
                val Data = API(this,token).getActivityById(Id).getJSONArray("result").getJSONObject(0)
                if (Data.getString("Photo") != "None") {
                    val bitmap = API(this.applicationContext).SearchIMG(Data.getString("Photo"))
                    runOnUiThread{
                        img.setImageBitmap(bitmap)
                    }
                }
                if(Data.getString("peopleMaxium") == "None" || Data.getString("joinedCount") == "None"){
                    runOnUiThread {
                        tv_status1.text = "誰設定的人數欄位是空的"
                        tv_status2.text = "誰設定的人數欄位是空的"
                        tv_status1.setTextColor(resources.getColor(R.color.red))
                        tv_status2.setTextColor(resources.getColor(R.color.red))
                    }
                }else{
                    if (Data.getString("peopleMaxium").toInt() - Data.getString("joinedCount").toInt() <= 0) {
                        runOnUiThread {
                            tv_status1.text = "已額滿"
                            tv_status2.text = "已額滿"
                            tv_status1.setTextColor(resources.getColor(R.color.red))
                            line.setColorFilter(resources.getColor(R.color.red))
                        }
                    }else{
                        runOnUiThread {
                            tv_status1.text = "尚未額滿"
                            tv_status2.text = "我要購票"
                            tv_status1.setTextColor(resources.getColor(R.color.green))
                            line.setColorFilter(resources.getColor(R.color.green))
                            if(token!="token"){
                                line.setOnClickListener {

                                    val temp1 = API(this.applicationContext,token).getTicket(Id)
                                    var p = 0
                                    if (temp1.getString("code") == "001") {
                                        val data = temp1.getJSONArray("result")
                                        val myDialog = LayoutInflater.from(this).inflate(R.layout.dialog_edit2, null)
                                        val d = AlertDialog.Builder(this).setView(myDialog).setTitle("票券資訊:")
                                        val myAlertDialog = d.show()

                                        val ticketarray = ArrayList<String>()
                                        for(i in 0..data.length() - 1){
                                            if(data.getJSONObject(i).getString("Remainder").toInt() <= 0){
                                                ticketarray.add(data.getJSONObject(i).getString("ticketKinds") +": " + "已售完")
                                            }else{
                                                ticketarray.add(data.getJSONObject(i).getString("ticketKinds") +": " + data.getJSONObject(i).getString("Price")+"元")
                                            }

                                        }
                                        myDialog.sp_ticket.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, ticketarray)
                                        myDialog.sp_ticket.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                                            override fun onNothingSelected(parent: AdapterView<*>?) {
                                            }

                                            override fun onItemSelected(
                                                parent: AdapterView<*>?,
                                                view: View?,
                                                position: Int,
                                                id: Long
                                            ) {
                                                p = position
                                            }


                                        }
                                        myDialog.btn_cancel.setOnClickListener {
                                            myAlertDialog.dismiss()
                                        }
                                        myDialog.btn_done.setOnClickListener {
                                            Thread(Runnable {
                                                run{
                                                    val act_Id = data.getJSONObject(p).getString("act_Id")
                                                    val authId = getSharedPreferences("Data", MODE_PRIVATE).getString("authId","authId")
                                                    val token = getSharedPreferences("Data", MODE_PRIVATE).getString("token","token")!!
                                                    val ticketKinds = data.getJSONObject(p).getString("ticketKinds")
                                                    if(data.getJSONObject(p).getString("Remainder").toInt() <= 0){
                                                        runOnUiThread {
                                                            Toast.makeText(this,"此票種已售完",Toast.LENGTH_LONG).show()
                                                        }
                                                    }else{
                                                        val temp = API(this.applicationContext,token).buyTicket(act_Id,authId,myDialog.et_text.text.toString(),ticketKinds)
                                                        runOnUiThread {
                                                            AlertDialog.Builder(this)
                                                                .setTitle("一個人一場活動只能購買一張票券\n確定要購買嗎?")
                                                                .setNegativeButton("否", null)
                                                                .setPositiveButton("是") { dialog, which ->
                                                                    Thread(Runnable {
                                                                        run{
                                                                            if (temp.getString("code") == "001") {
                                                                                runOnUiThread {
                                                                                    val intent = Intent(this, TicketDetailActivity::class.java).putExtra("act_Id", act_Id)
                                                                                    startActivity(intent)
                                                                                    myAlertDialog.dismiss()
                                                                                    finish()
                                                                                }
                                                                            } else if (temp.getString("code") == "018") {
                                                                                runOnUiThread {
                                                                                    Toast.makeText(this, "已購買此活動票券\n請至選單->票券查看", Toast.LENGTH_LONG).show()
                                                                                }
                                                                            } else {
                                                                                runOnUiThread {
                                                                                    Toast.makeText(this, "請先驗證信箱", Toast.LENGTH_SHORT).show()
                                                                                }
                                                                            }
                                                                        }
                                                                    }).start()

                                                                }.show()
                                                        }
                                                    }

                                                }
                                            }).start()
                                        }
                                    }else{
                                        val temp2 = API(this.applicationContext,token).getActivityById(Id)
                                        if (temp2.getString("code") == "001") {
                                            val Data = temp2.getJSONArray("result").getJSONObject(0)
                                            var Price = "0"
                                            if (Data.getString("Price") == "None") {
                                                Price = "0"
                                            } else {
                                                Price = Data.getString("Price")
                                            }
                                            val myDialog = LayoutInflater.from(this).inflate(R.layout.dialog_edit2, null)
                                            val d = AlertDialog.Builder(this).setView(myDialog).setTitle("票券資訊:")
                                            val myAlertDialog = d.show()

                                            val ticketarray = ArrayList<String>()
                                            ticketarray.add("一般票" + Price + "元")
                                            myDialog.sp_ticket.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, ticketarray)

                                            myDialog.btn_cancel.setOnClickListener {
                                                myAlertDialog.dismiss()
                                            }
                                            myDialog.btn_done.setOnClickListener {
                                                AlertDialog.Builder(this)
                                                    .setTitle("一個人一場活動只能購買一張票券\n確定要購買嗎?")
                                                    .setNegativeButton("否", null)
                                                    .setPositiveButton("是") { dialog, which ->
                                                        Thread(Runnable {
                                                            run {
                                                                val temp = API(this.applicationContext,token).buyTicket(Id, authId, myDialog.et_text.text.toString())
                                                                if (temp.getString("code") == "001") {
                                                                    runOnUiThread {
                                                                        val intent = Intent(this, TicketDetailActivity::class.java)
                                                                            .putExtra("act_Id", Id)
                                                                        startActivity(intent)
                                                                        Toast.makeText(this, "購買成功", Toast.LENGTH_SHORT).show()
                                                                    }
                                                                } else if (temp.getString("code") == "018") {
                                                                    runOnUiThread {
                                                                        Toast.makeText(this, "已購買此活動票券\n請至選單->票券查看", Toast.LENGTH_LONG).show()
                                                                    }
                                                                } else {
                                                                    runOnUiThread {
                                                                        Toast.makeText(this, "請先驗證信箱", Toast.LENGTH_SHORT).show()
                                                                    }
                                                                }
                                                                myAlertDialog.dismiss()
                                                            }
                                                        }).start()
                                                    }.show()
                                            }
                                        }else {
                                            Toast.makeText(this, "網路連接失敗", Toast.LENGTH_SHORT).show()
                                            finish()
                                        }

                                    }
                                }
                            }else{
                                tv_status2.setOnClickListener {
                                    runOnUiThread {
                                        Toast.makeText(this,"請先登入",Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    }
                }

                try {
                    var showArray = JSONArray()
                    val temp1 = API(this,token).getActivityById(Id)
                    val temp2 = API(this,token).getShow(Id)
                    if( temp2.getString("code")== "001"){
                        showArray = temp2.getJSONArray("result")
                    }else if(temp2.getString("code") == "013"){

                    }else{
                        this.runOnUiThread {
                            Toast.makeText(this,"網路連線失敗", Toast.LENGTH_SHORT).show()
                        }
                    }

                    if (temp1.getString("code") == "001") {
                        val timetemp = API(this).getTime()
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
                                val timediffs = eventEnd.time - eventStart.time
                                val days = TimeUnit.MILLISECONDS.toDays(timediffs).toInt()
                                this.runOnUiThread {
                                    if (showArray.length() == 0) {
                                        view.layoutManager = LinearLayoutManager(this)
                                        view.adapter = TextAdapter("主辦方沒有設定排程")
                                    } else {
                                        view.layoutManager = LinearLayoutManager(this)
                                        view.adapter = TicketBuyShowAdapterOld(this , days, showArray, eventStart, realTime, ::click)
                                    }
                                }
                            }
                        }else{
                            this.runOnUiThread {
                                Toast.makeText(this, "網路連線失敗", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        this.runOnUiThread {
                            Toast.makeText(this, "網路連線失敗", Toast.LENGTH_SHORT).show()
                        }
                    }
                }catch (e:Exception) {
                    try {
                        this.runOnUiThread {
                            Toast.makeText(this, "發生意外之錯誤", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) { }
                }

                val temp1 = API(this.applicationContext,token).getTicket(Id)
                if(temp1.getString("code")=="001"){
                    runOnUiThread {
                        ticketView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
//                        ticketView.adapter = TicketBuyTicektAdapter(temp1.getJSONArray("result"))
                        ticketView.visibility = View.VISIBLE
                    }
                }else{
                    runOnUiThread {
                        if(Data.getString("Price") == "None"){
                            tv_price.text = "售價: 0 元"
                            tv_price.visibility = View.VISIBLE
                        }else{
                            tv_price.text = "售價: "+Data.getString("Price")+" 元"
                            tv_price.visibility = View.VISIBLE
                        }
                    }
                }

                var tag = ""
                val temp2 = Data.getJSONArray("Tag")
                for (i in 0..temp2.length() - 1) {
                    if (i == temp2.length() - 1) {
                        tag += temp2[i].toString()
                    } else {
                        tag += temp2[i].toString() + ","
                    }
                }
                try {
                    latitude = Data.getString("Latitude").toDouble()
                    longitude = Data.getString("Longitude").toDouble()
                }catch (e:Exception){ }
                runOnUiThread {
                    tv_tag.text = tag
                    tv_title.text = Data.getString("act_Name")
                    tv_date.text = Data.getString("startTime") + "~\n" + Data.getString("endTime")
                    tv_description.text = Data.getString("act_Description")
                    tv_location.text = Data.getString("Location")
                    img_loading.visibility = View.GONE
                    progress_Bar.visibility = View.GONE
                }
            }
        }).start()

        tv_location.setOnClickListener {
            val gmmIntentUri = Uri.parse("geo:0,0?q="+tv_location.text)
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
        }

        img_back.setOnClickListener {
            super.onBackPressed()
        }


    }

    fun click(showId: String?, showName: String?, detail: String?){
        if(detail == "none"){
            AlertDialog.Builder(this)
                .setTitle(showName)
                .setMessage("無詳細內容")
                .setNegativeButton("確認", null)
                .show()
        }else{
            AlertDialog.Builder(this)
                .setTitle(showName)
                .setMessage(detail)
                .setNegativeButton("確認", null)
                .show()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        Thread(Runnable {
            run{
                for(i in 0..100){
                    if(latitude != 0.0){
                        mMap = googleMap
                        val location = LatLng(latitude, longitude)
                        runOnUiThread {
                            mMap.uiSettings.isScrollGesturesEnabled = false
                            mMap.uiSettings.isTiltGesturesEnabled = false
                            mMap.uiSettings.isZoomGesturesEnabled = false
                            mMap.uiSettings.isRotateGesturesEnabled = false
                            mMap.addMarker(MarkerOptions().position(location))
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16f))
                        }
                        break
                    } else {
                        Thread.sleep(100)
                        continue
                    }
                }
            }
        }).start()
    }
}