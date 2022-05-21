package com.network.fiesta

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_buy_ticket.*
import kotlinx.android.synthetic.main.activity_ticket_buy_scrolling.*

class BuyTicketActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy_ticket)


        val ticketSort = intent.getStringExtra("ticketSort")
        val act_Id = intent.getStringExtra("act_Id")
        val authId = intent.getStringExtra("authId")
        val ticketKinds = intent.getStringExtra("ticketKinds")
        val token = intent.getStringExtra("token")
        val date = intent.getStringExtra("date")
        val location = intent.getStringExtra("location")
        val act_Name = intent.getStringExtra("act_Name")
        val bitmapUri = intent.getStringExtra("bitmapUri")

        Thread(Runnable {
            run{
                val bitmap = API(this.applicationContext).SearchIMG(bitmapUri)
                runOnUiThread {
                    tv_img.setImageBitmap(bitmap)
                    tv_ticketSort.text = ticketSort
                    txt_title.text = act_Name
                    txt_location.text = location
                    txt_date.text = date
                }
            }
        }).start()

        btn_done.setOnClickListener {
            Thread(Runnable {
                run {
                    val temp = API(this.applicationContext, token).buyTicket(act_Id,authId,tv_note.text.toString(),ticketKinds)
                    runOnUiThread {
                        val intent = Intent(this, BuyTicketDetailActivity::class.java)
                        intent.putExtra("authId", authId)
                            .putExtra("ticketSort", ticketSort)
                            .putExtra("ticketKinds", ticketKinds)
                            .putExtra("token", token)
                            .putExtra("date", date)
                            .putExtra("location", location)
                            .putExtra("act_Name", act_Name)
                        startActivity(intent)
                    }
                }
            }).start()
        }


    }


}