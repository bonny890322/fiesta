package com.network.fiesta

import android.app.ActivityOptions
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import kotlinx.android.synthetic.main.activity_create_detail.*
import java.lang.Exception

class CreateDetailActivity : AppCompatActivity() {

    var activityname = ""
    var content = ""
    var authId = ""
    var groupId:String? = ""
    var groupName:String? = ""
    var sort = ""
    var datestart = ""
    var dateend = ""
    var location = ""
    var people = ""
    var location_latitude = ""
    var location_longitude = ""
    var token = ""
    var viewStatus = ""
    var deadline = ""
    var bitmapUri = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_detail)

        val intent = getIntent()
        try {
            authId = intent.getStringExtra("authId")
            datestart = intent.getStringExtra("datestart")
            dateend = intent.getStringExtra("dateend")
            location = intent.getStringExtra("location")
            location_latitude = intent.getStringExtra("latitude")
            location_longitude = intent.getStringExtra("longitude")
            token = getSharedPreferences("Data", MODE_PRIVATE).getString("token", "token")!!
            groupId = intent.getStringExtra("groupId")
            groupName = intent.getStringExtra("groupName")
            activityname = intent.getStringExtra("activityname")
            sort = intent.getStringExtra("sort")
            people = intent.getStringExtra("people")
            content = intent.getStringExtra("content")
            bitmapUri = intent.getStringExtra("bitmapUri")
            val timestatus = intent.getStringExtra("timeStatus")
            if(timestatus=="true"){
                deadline = dateend
            }
        }catch (e: Exception){ }
        btn_cancel.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("取消創建活動?")
                .setNegativeButton("否",null)
                .setPositiveButton("是"){dialog, which ->
                    val intent = Intent(this, MenuActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                }.show()
        }
        btn_finish.setOnClickListener{
            img_loading.visibility= View.VISIBLE
            progress_Bar.visibility= View.VISIBLE
            Thread(Runnable {
                run{
                    if(groupId == null){
                        val Data = API(this.applicationContext,token).uploadGroup(authId, groupName, deadline).getJSONArray("result").getJSONObject(0)
                        val temp = API(this.applicationContext,token).uploadActivity(Data.getString("groupId"),sort,activityname,people,location,datestart,dateend,"0",content,location_latitude,location_longitude,"0")
                        val Id = temp.getJSONArray("result").getJSONObject(0).getString("act_Id")
                        if (bitmapUri!="") {
                            API(this.applicationContext,token).uploadImg("act",Id, MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(bitmapUri)))
                        }
                    }else{
                        val temp = API(this.applicationContext,token).uploadActivity(groupId,sort,activityname,people,location,datestart,dateend,"0",content,location_latitude,location_longitude,"0")
                        val Id = temp.getJSONArray("result").getJSONObject(0).getString("act_Id")
                        if (bitmapUri!="") {
                            API(this.applicationContext,token).uploadImg("act", Id, MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(bitmapUri)))
                        }
                    }
                    runOnUiThread {
                        val intent = Intent(this, MenuActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        val options = ActivityOptions.makeCustomAnimation(this,R.anim.slide_out_left,R.anim.slide_in_right)
                        startActivity(intent, options.toBundle())
                    }
                }
            }).start()
        }
    }

}