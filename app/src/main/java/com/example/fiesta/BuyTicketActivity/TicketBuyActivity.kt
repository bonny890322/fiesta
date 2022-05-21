package com.network.fiesta

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_ticket_buy.*
import kotlinx.android.synthetic.main.activity_ticket_buy_scrolling.*
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class TicketBuyActivity : AppCompatActivity() , OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var act_Id = ""
    var token = ""
    var authId = ""
    var fromurl = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_ticket_buy)
        setSupportActionBar(findViewById(R.id.toolbar))
        findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout).title = title
        val data: Uri? = intent?.data
        img_loading.visibility = View.VISIBLE
        progress_Bar.visibility = View.VISIBLE
        token = getSharedPreferences("Data", MODE_PRIVATE).getString("token", "token")!!
        authId = getSharedPreferences("Data", MODE_PRIVATE).getString("authId","authId")!!
        val regex = Regex("Activity/.*/")
        val matchResult = regex.find(data.toString())?.value
        if(matchResult!=null){
            fromurl=true
            act_Id = matchResult?.split("/")!![1]
            Log.e("test",act_Id)
            act_Id = "315"
        }else{
            act_Id = getIntent().getStringExtra("Id")
        }
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        Thread(Runnable {
            run {
                val temp = API(this,token).getActivityById(act_Id)
                if(temp.getString("code")=="001"){
                    val Data = temp.getJSONArray("result").getJSONObject(0)
                    val temp = API(this,token).getGroupByGroupId(Data.getString("groupId"))
                    if(temp.getString("code")=="001"){
                        if(temp.getJSONArray("result").getJSONObject(0).getString("Photo") != "None"){
                            val bitmap = API(this.applicationContext).SearchIMG(Data.getString("Photo"))
                            runOnUiThread{
                                img_group.setImageBitmap(bitmap)
                            }
                        }
                        runOnUiThread {
                            tv_group.text = temp.getJSONArray("result").getJSONObject(0).getString("groupName")
                        }
                        //設定活動圖片
                        if (Data.getString("Photo") != "None") {
                            val bitmap = API(this.applicationContext).SearchIMG(Data.getString("Photo"))
                            runOnUiThread{
                                findViewById<AppBarLayout>(R.id.app_bar).background = BitmapDrawable(resources, bitmap)
                            }
                        }
                        if(Data.getString("peopleMaxium") == "None" || Data.getString("joinedCount") == "None"){
                            runOnUiThread {
                                tv_status.text = "誰設定的人數欄位是空的"
                                tv_status.setTextColor(resources.getColor(R.color.red))
                            }
                        }else{
                            if (Data.getString("peopleMaxium").toInt() - Data.getString("joinedCount").toInt() <= 0) {
                                runOnUiThread {
                                    tv_status.text = "已額滿"
                                    line.setColorFilter(resources.getColor(R.color.red))
                                }
                            }else{
                                runOnUiThread {
                                    tv_status.text = "購票"
                                    line.setColorFilter(resources.getColor(R.color.orange))
                                    if(token!="token"){
                                        line.setOnClickListener {
                                            scrollView.fullScroll(View.FOCUS_DOWN)
                                            app_bar.setExpanded(false)
                                            currentFocus!!.clearFocus()
                                        }
                                    }else{
                                        tv_status.setOnClickListener {
                                            runOnUiThread {
                                                Toast.makeText(this,"請先登入", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        try {
                            var showArray = JSONArray()
                            val temp2 = API(this,token).getShow(act_Id)
                            if( temp2.getString("code")== "001"){
                                showArray = temp2.getJSONArray("result")
                            }else if(temp2.getString("code") == "013"){

                            }else{
                                this.runOnUiThread {
                                    Toast.makeText(this,R.string.connect_fail,Toast.LENGTH_SHORT).show()
                                }
                            }
                            val timetemp = API(this).getTime()
                            if(timetemp.getString("code")=="001"){
                                val realTime = timetemp.getString("result")
                                val dateFormat = SimpleDateFormat("yyyy-MM-dd")
                                val c = Calendar.getInstance()
                                val starttime = Data.getString("startTime")
                                val endtime = Data.getString("endTime")
                                val eventStart = dateFormat.parse(starttime)
                                val eventEnd = dateFormat.parse(endtime)
                                val timediffs = eventEnd.time - eventStart.time
                                val days = TimeUnit.MILLISECONDS.toDays(timediffs).toInt()
                                this.runOnUiThread {
                                    for (i in 0..days){
                                        tabLayout.addTab(tabLayout.newTab().setText("第${i+1}天"))
                                    }
                                    view.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
                                    tabLayout.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(view))
                                    if (showArray.length() == 0) {
                                        view.visibility = View.GONE
                                        tabLayout.visibility = View.INVISIBLE
                                        tv_show.visibility = View.VISIBLE
                                    } else {
                                        view.adapter = TicketBuyShowPagerAdapter(this , days, showArray, eventStart, realTime, ::click)
                                    }
                                }
                            }else{
                                this.runOnUiThread {
                                    Toast.makeText(this,R.string.connect_fail,Toast.LENGTH_SHORT).show()
                                }
                            }
                        }catch (e: Exception) {
                            try {
                                this.runOnUiThread {
                                    Toast.makeText(this, "發生意外之錯誤", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) { }
                        }

                        val temp1 = API(this.applicationContext,token).getTicket(act_Id)
                        if(temp1.getString("code")=="001"){
                            runOnUiThread {
                                ticketView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                                ticketView.adapter = TicketBuyTicektAdapter(temp1.getJSONArray("result"),::buyticket)
                            }
                        }else{
                            runOnUiThread {
                                if(Data.getString("Price") == "None"){
                                    ticketView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                                    ticketView.adapter = TicketBuyTicektAdapter(JSONArray().put(
                                        JSONObject().put("ticketKinds", "一般票").put("Price", "0")
                                    ),::buyticket)
                                }else{
                                    ticketView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                                    ticketView.adapter = TicketBuyTicektAdapter(JSONArray().put(
                                        JSONObject().put("ticketKinds", "一般票").put("Price", Data.getString("Price"))
                                    ),::buyticket)
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
                        }catch (e: Exception){ }
                        runOnUiThread {
                            tv_tag.text = tag
                            tv_title.text = Data.getString("act_Name")
                            tv_date.text = Data.getString("startTime") + " ~ " + Data.getString("endTime")
                            tv_description.text = Data.getString("act_Description")
                            tv_location.text = Data.getString("Location")
                            img_loading.visibility = View.GONE
                            progress_Bar.visibility = View.GONE
                        }
                    }else{
                        runOnUiThread {
                            Toast.makeText(this,R.string.connect_fail,Toast.LENGTH_SHORT).show()
                        }
                    }
                }else{
                    runOnUiThread {
                        Toast.makeText(this,R.string.connect_fail,Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }).start()

        tv_location.setOnClickListener {
            val gmmIntentUri = Uri.parse("geo:0,0?q="+tv_location.text)
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
        }

    }

    fun buyticket(position: Int){
        Thread(Runnable {
            run {
                val temp = API(this,token).getActivityById(act_Id)
                if(temp.getString("code")=="001"){
                    val Data = temp.getJSONArray("result").getJSONObject(0)
                    val temp = API(this,token).getGroupByGroupId(Data.getString("groupId"))
                    var ticketarray =""
                    var ticketKinds = ""
                    var bitmapUri : Bitmap? = null
                    if(temp.getString("code")=="001"){
                        val temp2 = API(this.applicationContext,token).getTicket(act_Id)
                        if(temp2.getString("code")=="001"){
                            ticketKinds = temp2.getJSONArray("result").getJSONObject(position).getString("ticketKinds")
                            ticketarray = ticketKinds + " NT." + temp2.getJSONArray("result").getJSONObject(position).getString("Price")
                        }else{
                                if(Data.getString("Price") == "None"){
                                    ticketarray = "一般票 NT.0"
                                }else{
                                    ticketarray = "一般票 NT.0" + Data.getString("Price")
                                }
                            ticketKinds = "一般票"
                        }

                        runOnUiThread {
                            val act_Name = Data.getString("act_Name")
                            val location = Data.getString("Location")
                            var date = Data.getString("startTime") + " ~\n" + Data.getString("endTime")
                            if(Data.getString("startTime").split(" ")[0]==Data.getString("endTime").split(" ")[0]){
                                date = Data.getString("startTime") + " ~ " + Data.getString("endTime").split(" ")[1]
                            }

                            val intent = Intent(this, BuyTicketHealthActivity::class.java)
                            intent.putExtra("authId", authId)
                                .putExtra("act_Id",act_Id)
                                .putExtra("ticketSort",ticketarray)
                                .putExtra("ticketKinds",ticketKinds)
                                .putExtra("token",token)
                                .putExtra("date",date)
                                .putExtra("location",location)
                                .putExtra("act_Name",act_Name)
                                .putExtra("bitmapUri",Data.getString("Photo"))

                            startActivity(intent)
                        }
                    }else{
                        runOnUiThread {
                            Toast.makeText(this,R.string.connect_fail,Toast.LENGTH_SHORT).show()
                        }
                    }
                }else{
                    runOnUiThread {
                        Toast.makeText(this,R.string.connect_fail,Toast.LENGTH_SHORT).show()
                    }
                }

            }
        }).start()




//        if(token!="token"){
//            lateinit var Data:JSONObject
//
//            lateinit var temp1:JSONObject
//
//
//            Thread(Runnable {
//                run {
//                    val temp = API(this,token).getActivityById(act_Id)
//                    if(temp.getString("code")=="001"){
//                        Data = temp.getJSONArray("result").getJSONObject(0)
//                    }
//                    Log.e("token",token)
//                    temp1 = API(this.applicationContext,token).getTicket(act_Id)
//                    Log.e("temp1",temp1.toString())
//                }
//            }).start()


//            var p = 0
//            if (temp1.getString("code") == "001") {
//                val data = temp1.getJSONArray("result")
//                //val myDialog = LayoutInflater.from(this).inflate(R.layout.dialog_edit2, null)
//                //val d = AlertDialog.Builder(this).setView(myDialog).setTitle("票券資訊:")
//                //val myAlertDialog = d.show()
//                var ticketKinds = ""
//                var act_Name = ""
//                var date = ""
//                var location = ""
//
//                val ticketarray = ArrayList<String>()
//                for(i in 0..data.length() - 1){
//                    if(data.getJSONObject(i).getString("Remainder").toInt() <= 0){
//                        ticketarray.add(data.getJSONObject(i).getString("ticketKinds") +": " + "已售完")
//                    }else{
//                        ticketarray.add(data.getJSONObject(i).getString("ticketKinds") +" " + data.getJSONObject(i).getString("Price")+"元")
//                    }
//
//                }
//
//
//                Thread(Runnable {
//                    run{
//                        ticketKinds = data.getJSONObject(position).getString("ticketKinds")
//                        act_Name = Data.getString("act_Name")
//                        date = Data.getString("startTime") + " ~ " + Data.getString("endTime")
//                        location = Data.getString("Location")
//                    }
//                }).start()
//
//                val intent = Intent(this, BuyTicketActivity::class.java)
//                intent.putExtra("authId", authId)
//                    .putExtra("act_Id",act_Id)
//                    .putExtra("ticketSort",ticketarray[position])
//                    .putExtra("ticketKinds",ticketKinds)
//                    .putExtra("token",token)
//                    .putExtra("date",date)
//                    .putExtra("location",location)
//
//                startActivity(intent)

                /*
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
                                    Toast.makeText(this,"此票種已售完", Toast.LENGTH_LONG).show()
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
                                                            Toast.makeText(this, "購買成功", Toast.LENGTH_LONG).show()
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
                */
//            }else{
//                var Price = "0"
//                //val myDialog = LayoutInflater.from(this).inflate(R.layout.dialog_edit2, null)
//                //val d = AlertDialog.Builder(this).setView(myDialog).setTitle("票券資訊:")
//                //val myAlertDialog = d.show()
//                val data = temp1.getJSONArray("result")
//                var ticketKinds = ""
//                var act_Name = ""
//                var date = ""
//                var location = ""
//
//                Thread(Runnable {
//                    run{
//                        val temp = API(this,token).getActivityById(act_Id)
//                        val Data = temp.getJSONArray("result").getJSONObject(0)
//                        ticketKinds = data.getJSONObject(position).getString("ticketKinds")
//                        act_Name = Data.getString("act_Name")
//                        date = Data.getString("startTime") + " ~ " + Data.getString("endTime")
//                        location = Data.getString("Address")
//                    }
//                }).start()
//
//                val ticketarray = ArrayList<String>()
//                ticketarray.add("一般票" + Price + "元")
//                val intent = Intent(this, BuyTicketActivity::class.java)
//                intent.putExtra("authId", authId)
//                    .putExtra("act_Id",act_Id)
//                    .putExtra("ticketSort",ticketarray[position])
//                    .putExtra("ticketKinds",ticketKinds)
//                    .putExtra("token",token)
//                    .putExtra("date",date)
//                    .putExtra("location",location)
//                startActivity(intent)
//
//                /*
//                myDialog.sp_ticket.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, ticketarray)
//
//                myDialog.btn_cancel.setOnClickListener {
//                    myAlertDialog.dismiss()
//                }
//                myDialog.btn_done.setOnClickListener {
//                    AlertDialog.Builder(this)
//                        .setTitle("一個人一場活動只能購買一張票券\n確定要購買嗎?")
//                        .setNegativeButton("否", null)
//                        .setPositiveButton("是") { dialog, which ->
//                            Thread(Runnable {
//                                run {
//                                    val temp = API(this.applicationContext,token).buyTicket(act_Id, authId, myDialog.et_text.text.toString())
//                                    if (temp.getString("code") == "001") {
//                                        runOnUiThread {
//                                            Toast.makeText(this, "購買成功", Toast.LENGTH_SHORT).show()
//                                        }
//                                    } else if (temp.getString("code") == "018") {
//                                        runOnUiThread {
//                                            Toast.makeText(this, "已購買此活動票券\n請至選單->票據查看", Toast.LENGTH_LONG).show()
//                                        }
//                                    } else {
//                                        runOnUiThread {
//                                            Toast.makeText(this, "請先驗證信箱", Toast.LENGTH_SHORT).show()
//                                        }
//                                    }
//                                    myAlertDialog.dismiss()
//                                }
//                            }).start()
//                        }.show()
//                }
//                */
//            }
//        }else{
//            Toast.makeText(this,"請先登入", Toast.LENGTH_SHORT).show()
//        }
   }

/*
    fun buyticket2(){
        //todo
        if(token!="token"){
            val temp1 = API(this.applicationContext,token).getTicket(act_Id)
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
                                    Toast.makeText(this,"此票種已售完", Toast.LENGTH_LONG).show()
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
                                                            Toast.makeText(this, "購買成功", Toast.LENGTH_LONG).show()
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
                var Price = "0"
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
                                    val temp = API(this.applicationContext,token).buyTicket(act_Id, authId, myDialog.et_text.text.toString())
                                    if (temp.getString("code") == "001") {
                                        runOnUiThread {
                                            Toast.makeText(this, "購買成功", Toast.LENGTH_SHORT).show()
                                        }
                                    } else if (temp.getString("code") == "018") {
                                        runOnUiThread {
                                            Toast.makeText(this, "已購買此活動票券\n請至選單->票據查看", Toast.LENGTH_LONG).show()
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
            }
        }else{
            Toast.makeText(this,"請先登入", Toast.LENGTH_SHORT).show()
        }
    }
*/

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

    override fun onBackPressed() {
        if(fromurl){
            val intent = Intent(this, MenuActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }else{
            super.onBackPressed()
        }
    }
}