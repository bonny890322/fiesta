package com.network.fiesta

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_select_tag.*
import org.json.JSONArray
import java.lang.Exception

class CreateTagActivity : AppCompatActivity() {
    val addArray = ArrayList<String>()
    var Data = JSONArray()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_tag)

        try {
            tag_view.layoutManager = GridLayoutManager(this,4)

            val token = getSharedPreferences("Data", MODE_PRIVATE).getString("token", "token")!!
            val intent = getIntent()
            //val selected_tag = intent.getStringExtra("selected_tag")
            val authId = intent.getStringExtra("authId")
            val groupName = intent.getStringExtra("groupName")
            val groupId = intent.getStringExtra("groupId")
            val activityname = intent.getStringExtra("activityname")
            val datestart = intent.getStringExtra("datestart")
            val dateend = intent.getStringExtra("dateend")
            val timeStatus = intent.getStringExtra("timeStatus")
            val content = intent.getStringExtra("content")
            val people = intent.getStringExtra("people")
            val bitmapUri = intent.getStringExtra("bitmapUri")

            /*
            if(selected_tag != ""){
                val myTags = selected_tag.split(",")
                for(i in 0 until myTags.size){
                    addArray.add(myTags[i])
                }
            }

             */

            Thread(Runnable {
                run {
                    Data = API(this.applicationContext,token).getTag().getJSONArray("result").getJSONArray(0)
                    val test = API(this).SearchIMG("https://imgur.com/iNGnle2.jpg")
                    val ImgArray = JSONArray()
                    for(i in 0..Data.length()){
                        ImgArray.put(test)
                    }
                    runOnUiThread {
                        tag_view.adapter = CreateSelectTagAdapter(this,Data,ImgArray,addArray,::addTag,::delTag)
                    }
                }
            }).start()

            btn_next.setOnClickListener{
                var Data = ""
                for (i in 0..addArray.size-1){
                    if(i!=addArray.size-1){
                        Data+=addArray.get(i)+","
                    }else{
                        Data+=addArray.get(i)
                    }
                }
                if(Data!=""){
                    val intent = Intent(this, CreateLocationActivity::class.java)
                    intent.putExtra("authId", authId)
                        .putExtra("groupName", groupName)
                        .putExtra("datestart", datestart)
                        .putExtra("activityname", activityname)
                        .putExtra("dateend", dateend)
                        .putExtra("groupId", groupId)
                        .putExtra("content", content)
                        .putExtra("people", people)
                        .putExtra("timeStatus", timeStatus)
                        .putExtra("sort",Data)
                    if (bitmapUri != "") {
                        intent.putExtra("bitmapUri", bitmapUri)
                    }
                    val options = ActivityOptions.makeCustomAnimation(this,R.anim.slide_out_left,R.anim.slide_in_right)
                    startActivity(intent, options.toBundle())
                }else{
                    Toast.makeText(this,"請選擇至少一個標籤",Toast.LENGTH_SHORT).show()
                }
            }
        }catch (e:Exception){
            Toast.makeText(this,resources.getText(R.string.connect_fail),Toast.LENGTH_SHORT).show()
        }
    }

    private fun addTag(TagName: String) {
        addArray.add(TagName)
    }

    private fun delTag(TagName: String) {
        addArray.remove(TagName)
    }


}