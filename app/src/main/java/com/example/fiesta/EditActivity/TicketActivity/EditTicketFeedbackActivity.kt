package com.network.fiesta

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_edit_ticket_feedback.*
import java.lang.Exception

class EditTicketFeedbackActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_ticket_feedback)
        val act_Id = getIntent().getStringExtra("act_Id")
        val act_Name = getIntent().getStringExtra("act_Name")
        val token = getSharedPreferences("Data", MODE_PRIVATE).getString("token", "token")!!

        Thread(Runnable {
            run {
                val temp = API(this.applicationContext,token).getActivityScore(act_Id)
                val temp2 = API(this.applicationContext,token).getShow(act_Id)
                val Stars = ArrayList<Float>()
                val Light = ArrayList<Float>()
                val Moveline = ArrayList<Float>()
                val Music = ArrayList<Float>()
                val Price = ArrayList<Float>()
                val Site = ArrayList<Float>()
                val Staff = ArrayList<Float>()
                val Vibe = ArrayList<Float>()
                if (temp.getString("code") == "001") {
                    val Data = temp.getJSONArray("result")
                    if (Data.length() != 0) {
                        for (i in 0..Data.length() - 1) {
                            try{ Stars.add(Data.getJSONObject(i).getString("Stars").toFloat()) }catch (e:Exception) {}
                            try{ Light.add(Data.getJSONObject(i).getString("Light").toFloat()) }catch (e:Exception) {}
                            try{ Moveline.add(Data.getJSONObject(i).getString("Moveline").toFloat()) }catch (e:Exception) {}
                            try{ Music.add(Data.getJSONObject(i).getString("Music").toFloat()) }catch (e:Exception) {}
                            try{ Price.add(Data.getJSONObject(i).getString("Price").toFloat()) }catch (e:Exception) {}
                            try{ Site.add(Data.getJSONObject(i).getString("Site").toFloat()) }catch (e:Exception) {}
                            try{ Staff.add(Data.getJSONObject(i).getString("Staff").toFloat()) }catch (e:Exception) {}
                            try{ Vibe.add(Data.getJSONObject(i).getString("Vibe").toFloat()) }catch (e:Exception) {}
                        }
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
                        temp2.add(Stars.sum()/Stars.size)
                        temp2.add(Light.sum()/Light.size)
                        temp2.add(Moveline.sum()/Moveline.size)
                        temp2.add(Music.sum()/Music.size)
                        temp2.add(Price.sum()/Price.size)
                        temp2.add(Site.sum()/Site.size)
                        temp2.add(Staff.sum()/Staff.size)
                        temp2.add(Vibe.sum()/Vibe.size)

                        runOnUiThread {
                            tv_title.text = act_Name
                            view.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                            view.adapter = EditTicketRatingAdapter(temp1,temp2,false)
                        }
                    } else {
                        runOnUiThread {
                            tv_detail.visibility = View.GONE
                            tv_title.text = "目前無人評分 :("
                            Toast.makeText(this, "目前無人評分", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this, "網路連線失敗", Toast.LENGTH_SHORT).show()
                    }
                }
                if(temp2.getString("code") == "001"){
                    if(temp2.getJSONArray("result").length() != 0){
                        runOnUiThread {
                            tv_showdetail.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }).start()

        tv_detail.setOnClickListener {
            val intent = Intent(this, EditTicketFeedbackDetailActivity::class.java)
                .putExtra("type","star")
                .putExtra("act_Id",act_Id)
            startActivity(intent)
        }

        tv_showdetail.setOnClickListener {
            val intent = Intent(this, EditTicketFeedbackDetailActivity::class.java)
                .putExtra("type","show")
                .putExtra("act_Id",act_Id)
            startActivity(intent)
        }
    }
}
