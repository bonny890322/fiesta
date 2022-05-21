package com.network.fiesta

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_edit_advance_price.*
import kotlinx.android.synthetic.main.dialog_personal_edit.view.*
import kotlinx.android.synthetic.main.dialog_ticket.view.*
import org.json.JSONArray
import java.lang.Exception

class EditAdvancePriceActivity : AppCompatActivity() {

    val ticketSort: ArrayList<String> = ArrayList()
    val ticketQuantity: ArrayList<String> = ArrayList()
    val ticketPrice: ArrayList<String> = ArrayList()
    val ticketId: ArrayList<String> = ArrayList()
    var models = mutableListOf<String>()
    var token = ""
    lateinit var act_Id :String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_advance_price)

        token = getSharedPreferences("Data", MODE_PRIVATE).getString("token", "token")!!
        val intent = getIntent()
        act_Id = intent.getStringExtra("act_Id")

        ticketView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        ticketView.adapter = TextAdapter("載入中...")

        Thread(Runnable {
            run{
                val temp = API(this.applicationContext,token).getActivityById(act_Id)
                if(temp.getString("code")=="001"){
                    val Data = temp.getJSONArray("result").getJSONObject(0)
                    val Data1 = JSONArray()
                        .put("人數")
                        .put("票價")
                    val Data2 = JSONArray()
                        .put(Data.getString("peopleMaxium"))
                        .put(Data.getString("Price"))
                    if(Data.getString("Models").toString()!="None"){
                        models = Data.getString("Models").split(",").toMutableList()
                        for(i in 0..models.size-1){
                            if (models[i] == "1"){
                                runOnUiThread {
                                    switch_advance.isChecked = true
                                }
                            }
                        }
                    }
                    runOnUiThread{
                        ticketView.adapter = AdvanceEditAdapter(this,Data1,Data2,::click)
                    }
                }
            }
        }).start()

        switch_advance.setOnCheckedChangeListener{ _, isChecked ->
            Thread(Runnable {
                run {
                    if (isChecked) {
                        runOnUiThread {
                            view_advance.visibility = View.VISIBLE
                            ticketView.visibility = View.GONE
                        }
                        var temp = ""
                        if(models.size != 0){
                            for(i in 0..models.size-1){
                                if (models[i] != "1" && models[i] != ""){
                                    temp += models[i]+","
                                }
                            }
                            API(this.applicationContext,token).updateActivity(act_Id, Models = temp+"1")
                        }else{
                            API(this.applicationContext,token).updateActivity(act_Id, Models = "1")
                        }
                        refresh()
                    } else {
                        runOnUiThread {
                            view_advance.visibility = View.GONE
                            ticketView.visibility = View.VISIBLE
                        }
                        if (models.remove("1").toString() == "") {
                            API(this.applicationContext,token).updateActivity(act_Id, Models = "")
                        } else {
                            var temp = ""
                            for (i in 0..models.size - 1) {
                                if (i == models.size - 1) {
                                    temp += models[i]
                                } else {
                                    temp += models[i] + ","
                                }
                            }
                            API(this.applicationContext,token).updateActivity(act_Id, Models = temp)
                        }
                    }
                }
            }).start()

        }

        advanceTicket.layoutManager = LinearLayoutManager(this)
        advanceTicket.adapter = TicketSortAdapter(this,ticketSort,ticketQuantity,ticketPrice,ticketId,::refresh)

        btn_add.setOnClickListener{
            val myDialog = LayoutInflater.from(this).inflate(R.layout.dialog_ticket,null)
            val d = AlertDialog.Builder(this).setView(myDialog)
            val myAlertDialog = d.show()
            myDialog.findViewById<Button>(R.id.btn_cancel).setOnClickListener{
                myAlertDialog.dismiss()
            }
            myDialog.findViewById<Button>(R.id.btn_done).setOnClickListener{
                if (myDialog.txt_name.text.toString()!="" && myDialog.txt_quantity.text.toString()!="" && myDialog.txt_price.text.toString()!=""){
                    try {
                        Thread(Runnable {
                            run {
                                if (API(this.applicationContext,token).uploadTicket(act_Id, myDialog.txt_name.text.toString(), myDialog.txt_quantity.text.toString(), myDialog.txt_price.text.toString()).getString("code") == "001") {
                                    refresh()
                                    runOnUiThread {
                                        myAlertDialog.dismiss()
                                    }
                                }
                            }
                        }).start()
                    }catch (e:Exception){
                        Toast.makeText(this, "網路連接失敗", Toast.LENGTH_SHORT).show()
                    }
                    advanceTicket.adapter!!.notifyItemRangeChanged(0,ticketSort.size)
                }else{
                    Toast.makeText(this,"請輸入資料", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }
    fun refresh(){
        val temp = API(this.applicationContext,token).getTicket(act_Id)
        var peopleMaxium = 0
        ticketSort.clear()
        ticketQuantity.clear()
        ticketPrice.clear()
        ticketId.clear()
        if(temp.getString("code")=="001") {
            val ticket = temp.getJSONArray("result")
            if(ticket.length()!=0){
                for (i in 0..ticket.length() - 1) {
                    ticketSort.add(ticket.getJSONObject(i).getString("ticketKinds"))
                    ticketQuantity.add(ticket.getJSONObject(i).getString("Mounts"))
                    peopleMaxium += ticket.getJSONObject(i).getString("Mounts").toInt()
                    ticketPrice.add(ticket.getJSONObject(i).getString("Price"))
                    ticketId.add(ticket.getJSONObject(i).getString("Id"))
                }
            }
            runOnUiThread {
                advanceTicket.adapter = TicketSortAdapter(this,ticketSort,ticketQuantity,ticketPrice,ticketId,::refresh)
            }
            API(this.applicationContext,token).updateActivity(act_Id, peopleMaxium = peopleMaxium.toString())
        }else if (temp.getString("code")=="013"){
            runOnUiThread{
                advanceTicket.adapter = TextAdapter("暫無資料")
            }
            API(this.applicationContext,token).updateActivity(act_Id, peopleMaxium = "0")
        }else{
            runOnUiThread{
                Toast.makeText(this, "網路連接失敗", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun click(title: String, text: TextView) {
        when (title) {
            "人數" -> {
                val myDialog =
                    LayoutInflater.from(this).inflate(R.layout.dialog_personal_edit, null)
                val d = AlertDialog.Builder(this).setView(myDialog).setTitle(title)
                val myAlertDialog = d.show()
                myDialog.et_text.setText(text.text)
                myDialog.et_text.inputType = InputType.TYPE_CLASS_NUMBER
                myDialog.findViewById<Button>(R.id.btn_cancel).setOnClickListener {
                    myAlertDialog.dismiss()
                }
                myDialog.findViewById<Button>(R.id.btn_done).setOnClickListener {
                    text.text = myDialog.et_text.text
                    Thread(Runnable {
                        run {
                            API(this.applicationContext,token).updateActivity(act_Id, peopleMaxium = text.text.toString())
                            runOnUiThread {
                                myAlertDialog.dismiss()
                            }
                        }
                    }).start()
                }
            }
            "票價" -> {
                val myDialog =
                    LayoutInflater.from(this).inflate(R.layout.dialog_personal_edit, null)
                val d = AlertDialog.Builder(this).setView(myDialog).setTitle(title)
                val myAlertDialog = d.show()
                myDialog.et_text.setText(text.text)
                myDialog.et_text.inputType = InputType.TYPE_CLASS_NUMBER
                myDialog.findViewById<Button>(R.id.btn_cancel).setOnClickListener {
                    myAlertDialog.dismiss()
                }
                myDialog.findViewById<Button>(R.id.btn_done).setOnClickListener {
                    text.text = myDialog.et_text.text
                    Thread(Runnable {
                        run {
                            API(this.applicationContext,token).updateActivity(act_Id, Price = text.text.toString())
                            runOnUiThread {
                                myAlertDialog.dismiss()
                            }
                        }
                    }).start()
                }
            }
            else -> { }
        }
    }
}
