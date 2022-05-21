package com.network.fiesta

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_remind.*

class RemindActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_remind)


        val temp = arrayOf(
            arrayOf(
                "Title",
                "Date"
            ), arrayOf(
                "asdzxcsvrytjhtterwqeds",
                "2020/02/02 Sun 18:00~20:00"
            )
        )
        View.adapter = RemindAdapter(temp)
        View.layoutManager = LinearLayoutManager(this)

        img_back.setOnClickListener {
            super.onBackPressed()
        }
    }
}
