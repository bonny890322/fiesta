package com.network.fiesta

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_ticket_health.*

class BuyTicketHealthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ticket_health)

        val ticketSort = intent.getStringExtra("ticketSort")
        val act_Id = intent.getStringExtra("act_Id")
        val authId = intent.getStringExtra("authId")
        val ticketKinds = intent.getStringExtra("ticketKinds")
        val token = intent.getStringExtra("token")
        val date = intent.getStringExtra("date")
        val location = intent.getStringExtra("location")
        val act_Name = intent.getStringExtra("act_Name")
        val bitmapUri = intent.getStringExtra("bitmapUri")

        btn_done.setOnClickListener {
            val intent = Intent(this, BuyTicketActivity::class.java)
            intent.putExtra("authId", authId)
                .putExtra("act_Id",act_Id)
                .putExtra("ticketSort",ticketSort)
                .putExtra("ticketKinds",ticketKinds)
                .putExtra("token",token)
                .putExtra("date",date)
                .putExtra("location",location)
                .putExtra("act_Name",act_Name)
                .putExtra("bitmapUri",bitmapUri)
        }
    }
}