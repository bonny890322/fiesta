package com.network.fiesta

import android.R
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.SimpleAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_edit_ticket_feedback_detail.*
import org.json.JSONArray
import org.json.JSONObject


class EditTicketFeedbackDetailActivity : AppCompatActivity() {
    var Data = JSONArray()
    val stars = ArrayList<String>()
    val detail = ArrayList<String>()
    val date = ArrayList<String>()
    val nickName = ArrayList<String>()
    val Photo = ArrayList<String>()
    var data = ArrayList<MutableMap<String, Any>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.network.fiesta.R.layout.activity_edit_ticket_feedback_detail)

        val token = getSharedPreferences("Data", MODE_PRIVATE).getString("token", "token")!!
        val act_Id = getIntent().getStringExtra("act_Id")
        if(getIntent().getStringExtra("type") == "star"){

            //查看評論

//            val scorearray = ArrayList<String>()
//            scorearray.add("全部顯示")
//            scorearray.add("5顆星")
//            scorearray.add("4顆星")
//            scorearray.add("3顆星")
//            scorearray.add("2顆星")
//            scorearray.add("1顆星")
//            scorearray.add("0顆星")

            sp_score.adapter = SimpleAdapter(this, getDat(), com.network.fiesta.R.layout.item,arrayOf("image", "image2", "image3", "image4", "image5", "text"),intArrayOf(com.network.fiesta.R.id.img, com.network.fiesta.R.id.img2, com.network.fiesta.R.id.img3, com.network.fiesta.R.id.img4, com.network.fiesta.R.id.img5, com.network.fiesta.R.id.tvv))
            sp_score.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, View: View, position: Int, id: Long) {
                    when (position) {
                        0 -> {
                            stars.clear()
                            detail.clear()
                            date.clear()
                            nickName.clear()
                            Photo.clear()
                            for (i in 0..Data.length() - 1) {
                                if(Data.getJSONObject(i).getString("Detail")!=""){
                                    stars.add(Data.getJSONObject(i).getString("Stars"))
                                    detail.add(Data.getJSONObject(i).getString("Detail"))
                                    date.add(Data.getJSONObject(i).getString("score_Date"))
                                    nickName.add(Data.getJSONObject(i).getString("nickName"))
                                    Photo.add(Data.getJSONObject(i).getString("Photo"))
                                }
                            }
                            runOnUiThread {
                                view.adapter = EditTicketFeedbackDetailAdapter(this@EditTicketFeedbackDetailActivity, stars, detail,date,nickName, Photo)
                            }
                        }
                        else -> {
                            stars.clear()
                            detail.clear()
                            date.clear()
                            nickName.clear()
                            Photo.clear()
                            for (i in 0..Data.length() - 1) {
                                if (Data.getJSONObject(i).getString("Stars") == (6 - position).toString()) {
                                    if(Data.getJSONObject(i).getString("Detail")!=""){
                                        stars.add(Data.getJSONObject(i).getString("Stars"))
                                        detail.add(Data.getJSONObject(i).getString("Detail"))
                                        date.add(Data.getJSONObject(i).getString("score_Date"))
                                        nickName.add(Data.getJSONObject(i).getString("nickName"))
                                        Photo.add(Data.getJSONObject(i).getString("Photo"))
                                    }
                                }
                            }
                            runOnUiThread {
                                view.adapter = EditTicketFeedbackDetailAdapter(this@EditTicketFeedbackDetailActivity, stars, detail, date, nickName, Photo)
                            }
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }

            Thread(Runnable {
                run {
                    val temp = API(this.applicationContext,token).getActivityScore(act_Id)
                    if(temp.getString("code")=="001"){
                        Data = temp.getJSONArray("result")
                        for (i in 0..Data.length() - 1) {
                            if(Data.getJSONObject(i).getString("Detail")!=""){
                                stars.add(Data.getJSONObject(i).getString("Stars"))
                                detail.add(Data.getJSONObject(i).getString("Detail"))
                                date.add(Data.getJSONObject(i).getString("score_Date"))
                                nickName.add(Data.getJSONObject(i).getString("nickName"))
                                Photo.add(Data.getJSONObject(i).getString("Photo"))
                            }
                        }
                        runOnUiThread {
                            view.layoutManager = LinearLayoutManager(this)
                            view.adapter = EditTicketFeedbackDetailAdapter(this@EditTicketFeedbackDetailActivity, stars, detail,date, nickName, Photo)
                        }
                    }else if(temp.getString("code")=="013"){
                        Toast.makeText(this,"暫時無人留言",Toast.LENGTH_SHORT).show()
                        runOnUiThread {
                            sp_score.visibility = View.INVISIBLE
                            view.adapter = TextAdapter("暫時無人留言")
                        }
                    }
                }
            }).start()
        }else{

            //查看Show評論

            sp_score.visibility = View.GONE
            Thread(Runnable {
                run{
                    val temp = API(this.applicationContext,token).getShow(act_Id)
                    if(temp.getString("code") == "001"){
                        for (i in 0..temp.getJSONArray("result").length()-1){
                            val showId = temp.getJSONArray("result").getJSONObject(i).getString("Id")
                            val temp =API(this.applicationContext,token).getShowScore(showId)
                            if(temp.getString("code") == "001"){
                                for (i in 0..temp.getJSONArray("result").length() - 1){
                                    stars.add(temp.getJSONArray("result").getJSONObject(i).getString("showName"))
                                    detail.add(temp.getJSONArray("result").getJSONObject(i).getString("Detail"))
                                    //date.add(temp.getJSONArray("result").getJSONObject(i).getString("score_Date"))
                                    //nickName.add(Data.getJSONObject(i).getString("nickName"))
                                    //Photo.add(Data.getJSONObject(i).getString("Photo"))
                                }
                            }
                        }
                        runOnUiThread {
                            if(!stars.isEmpty()){
                                view.layoutManager = LinearLayoutManager(this)
                                view.adapter = EditTicketFeedbackDetailShowAdapter(stars, detail)
                            }else{
                                view.layoutManager = LinearLayoutManager(this)
                                view.adapter = TextAdapter("目前無人評論")
                            }
                        }
                    }
                }
            }).start()
        }
    }

    private fun getDat(): List<MutableMap<String, Any>> {
        val map: MutableMap<String, Any> = HashMap()
        map["image"] = com.network.fiesta.R.drawable.ui_star
        map["image2"] = com.network.fiesta.R.drawable.ui_star
        map["image3"] = com.network.fiesta.R.drawable.ui_star
        map["image4"] = com.network.fiesta.R.drawable.ui_star
        map["image5"] = com.network.fiesta.R.drawable.ui_star
        map["text"] = ""
        data.add(map)
        val map1: MutableMap<String, Any> = HashMap()
        map1["image"] = com.network.fiesta.R.drawable.ui_star_full
        map1["image2"] = com.network.fiesta.R.drawable.ui_star_full
        map1["image3"] = com.network.fiesta.R.drawable.ui_star_full
        map1["image4"] = com.network.fiesta.R.drawable.ui_star_full
        map1["image5"] = com.network.fiesta.R.drawable.ui_star_full
        map1["text"] = ""
        data.add(map1)
        val map2: MutableMap<String, Any> = HashMap()
        map2["image"] = com.network.fiesta.R.drawable.ui_star_full
        map2["image2"] = com.network.fiesta.R.drawable.ui_star_full
        map2["image3"] = com.network.fiesta.R.drawable.ui_star_full
        map2["image4"] = com.network.fiesta.R.drawable.ui_star_full
        map2["image5"] = com.network.fiesta.R.drawable.ui_star
        map2["text"] = ""
        data.add(map2)
        val map3: MutableMap<String, Any> = HashMap()
        map3["image"] = com.network.fiesta.R.drawable.ui_star_full
        map3["image2"] = com.network.fiesta.R.drawable.ui_star_full
        map3["image3"] = com.network.fiesta.R.drawable.ui_star_full
        map3["image4"] = com.network.fiesta.R.drawable.ui_star
        map3["image5"] = com.network.fiesta.R.drawable.ui_star
        map3["text"] = ""
        data.add(map3)
        val map4: MutableMap<String, Any> = HashMap()
        map4["image"] = com.network.fiesta.R.drawable.ui_star_full
        map4["image2"] = com.network.fiesta.R.drawable.ui_star_full
        map4["image3"] = com.network.fiesta.R.drawable.ui_star
        map4["image4"] = com.network.fiesta.R.drawable.ui_star
        map4["image5"] = com.network.fiesta.R.drawable.ui_star
        map4["text"] = ""
        data.add(map4)
        val map5: MutableMap<String, Any> = HashMap()
        map5["image"] = com.network.fiesta.R.drawable.ui_star_full
        map5["image2"] = com.network.fiesta.R.drawable.ui_star
        map5["image3"] = com.network.fiesta.R.drawable.ui_star
        map5["image4"] = com.network.fiesta.R.drawable.ui_star
        map5["image5"] = com.network.fiesta.R.drawable.ui_star
        map5["text"] = ""
        data.add(map5)
        return data
    }

}
