package com.network.fiesta

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_buy_ticket.*
import kotlinx.android.synthetic.main.activity_buy_ticket_detail.*
import kotlinx.android.synthetic.main.activity_buy_ticket_detail.btn_done
import kotlinx.android.synthetic.main.activity_buy_ticket_detail.tv_ticketSort

class BuyTicketDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy_ticket_detail)

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
                val Id = getSharedPreferences("Data", AppCompatActivity.MODE_PRIVATE).getString("authId", "authId")!!
                try {
                    val temp = Base64.decode(API(this.applicationContext,token).QRCode(act_Id,Id), 0)
                    val bitmap = BitmapFactory.decodeByteArray(temp, 0, temp.size)
                    runOnUiThread {
                        qrcode.setImageBitmap(bitmap)
                        img_loading.visibility = View.GONE
                        progress_Bar.visibility = View.GONE
                    }
                }catch (e:Exception){
                    runOnUiThread {
                        Toast.makeText(this,R.string.connect_fail, Toast.LENGTH_SHORT).show()
                        img_loading.visibility = View.GONE
                        progress_Bar.visibility = View.GONE
                    }
                }
            }
        }).start()

        img_act.setImageBitmap(bitmapUri as Bitmap?)
        tv_ticketSort.text = ticketSort
        tv_title.text = act_Name
        tv_location.text = location
        tv_date.text = date

        btn_done.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }
    }
}