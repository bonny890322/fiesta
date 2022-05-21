package com.network.fiesta


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_ticket_feedback.*
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.util.Log
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*


class TicketFeedBack : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ticket_feedback)
        val act_Id = getIntent().getStringExtra("act_Id")
        val Id = getIntent().getStringExtra("authId")
        val token = getIntent().getStringExtra("token")

        var temp1 = mutableListOf<String>()
        temp1.add("活動總體評分")
        temp1.add("燈光")
        temp1.add("動線")
        temp1.add("音樂")
        temp1.add("售價")
        temp1.add("地點")
        temp1.add("工作人員")
        temp1.add("氛圍")

        var temp2 = mutableListOf<Float>()
        for (i in 0..temp1.size - 1) {
            temp2.add(-1f)
        }

        view.layoutManager = LinearLayoutManager(this)
        view.adapter = EditTicketRatingAdapter(temp1, temp2, true)

        btn_done.setOnClickListener {
            if(temp2.get(0)!= -1.0f){
                val dff = SimpleDateFormat("yyyy-MM-dd")
                dff.setTimeZone(TimeZone.getTimeZone("Asia/Taipei"))
                val Date = dff.format(Date())

                if(API(this.applicationContext,token).uploadActivityScore(
                        act_Id,
                        Id,
                        Date,
                        Detail = et_detail.text.toString(),
                        Stars = temp2.get(0),
                        Light = temp2.get(1),
                        Moveline = temp2.get(2),
                        Music = temp2.get(3),
                        Price = temp2.get(4),
                        Site = temp2.get(5),
                        Staff = temp2.get(6),
                        Vibe = temp2.get(7)
                    ).getString("code")=="001"){
                    finish()
                }else{
                    Toast.makeText(this,"發生未知的錯誤",Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this,"至少需填活動總體評分",Toast.LENGTH_SHORT).show()
            }
        }
    }
}
