package com.network.fiesta

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_tag.*

class TagActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tag)

        val intent = getIntent()
        val Tag = intent.getStringExtra("Tag")
        tv_search.text = Tag
        Thread(Runnable {
            run {
                val temp = API(this.applicationContext).getActByTag(Tag)
                if (temp.getString("code") == "001") {
                    val Data = temp.getJSONArray("result")
                    runOnUiThread {
                        img_loading.visibility = View.GONE
                        progress_Bar.visibility = View.GONE
                        view.layoutManager = LinearLayoutManager(this)
                        view.adapter = HomeAdapter(Data, this)
                    }
                } else if (temp.getString("code") == "013") {
                    runOnUiThread {
                        img_loading.visibility = View.GONE
                        progress_Bar.visibility = View.GONE
                        view.layoutManager = LinearLayoutManager(this)
                        view.adapter = TextAdapter("查無資料")
                    }
                } else {
                    runOnUiThread {
                        img_loading.visibility = View.GONE
                        progress_Bar.visibility = View.GONE
                        view.layoutManager = LinearLayoutManager(this)
                        view.adapter = TextAdapter("連線逾時")
                        Toast.makeText(this, "網路連線逾時", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }).start()

        img_back.setOnClickListener {
            super.onBackPressed()
        }
    }
}
