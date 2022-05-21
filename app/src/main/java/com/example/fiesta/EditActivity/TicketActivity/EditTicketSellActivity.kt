package com.network.fiesta

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_edit_ticket_sell.*


class EditTicketSellActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_ticket_sell)

        val act_Id = getIntent().getStringExtra("act_Id")
        val token = getSharedPreferences("Data", MODE_PRIVATE).getString("token", "token")!!
        view.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        view.adapter = TextAdapter("載入中")

        val temp1 = API(this.applicationContext,token).getActivityById(act_Id)
        if (temp1.getString("code") == "001") {
            val data = temp1.getJSONArray("result").getJSONObject(0)
            val num = (data.getString("peopleMaxium").toInt() - data.getString("joinedCount").toInt()).toString()
            tv_num.text =  num
            tv_max.text = data.getString("peopleMaxium")
            val temp2 = API(this.applicationContext,token).getTicket(act_Id)
            if (temp2.getString("code") == "001") {
                val data = temp2.getJSONArray("result")
                val name = arrayListOf<String>()
                val num = arrayListOf<String>()
                for (i in 0..data.length()-1){
                    name.add(data.getJSONObject(i).getString("ticketKinds"))
                    num.add(data.getJSONObject(i).getString("Remainder"))
                }
                view.adapter = EditTicketSellAdapter(name,num)
            }else{
                view.adapter = EditTicketSellAdapter(arrayListOf("一般票"),arrayListOf(num))
            }
        }else{
            Toast.makeText(this,"網路連線失敗",Toast.LENGTH_SHORT).show()
        }

    }
}
