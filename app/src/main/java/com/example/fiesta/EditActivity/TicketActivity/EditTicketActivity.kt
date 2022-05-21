package com.network.fiesta

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_edit_ticket.*

class EditTicketActivity : AppCompatActivity() {
    lateinit var act_Name: String
    val data = mutableListOf<Int>()
    var act_Id = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_ticket)

        act_Id = getIntent().getStringExtra("act_Id")
        act_Name = getIntent().getStringExtra("act_Name")
        tv_title.text = act_Name

        img_qrcode.setOnClickListener {
            val intent = Intent(this, EditTicketQRCodeActivity::class.java)
            intent.putExtra("act_Id", act_Id)
            startActivity(intent)
        }
        img_ticketsell.setOnClickListener {
            val intent = Intent(this, EditTicketStatusActivity::class.java)
                .putExtra("act_Id", act_Id)
            startActivity(intent)
        }
        img_edit.setOnClickListener{
            val intent = Intent(this, EditActivity::class.java)
            intent.putExtra("act_Id", act_Id)
            startActivity(intent)
        }
        img_lotto.setOnClickListener{
            val intent = Intent(this, EditLottteActivity::class.java)
                .putExtra("act_Id", act_Id)
            startActivity(intent)
        }
        img_feedback.setOnClickListener{
            val intent = Intent(this, EditTicketFeedbackActivity::class.java)
                .putExtra("act_Id", act_Id)
                .putExtra("act_Name", act_Name)
            startActivity(intent)
        }

    }
}
