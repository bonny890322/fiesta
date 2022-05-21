package com.network.fiesta

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.listener.OnTimeSelectListener
import kotlinx.android.synthetic.main.activity_create_act.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class CreateActActivity : AppCompatActivity() {

    private var bitmapUri : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_act)

        val intent = getIntent()
        val authId = intent.getStringExtra("authId")
        val groupName = intent.getStringExtra("groupName")
        val groupId = intent.getStringExtra("groupId")
        val timeStatus = intent.getStringExtra("timeStatus")//看group是否為一次性

        et_host.setText(groupName)

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
        lateinit var realtime:String
        Thread(Runnable {
            run{
                realtime  = API(this.applicationContext).getTime().getString("result")
            }
        }).start()

        img_image.setOnClickListener {
            takeImageFromAlbumWithIntent()
        }

        DateStart.setOnClickListener{
            val ime = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            ime.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
            val startDate = Calendar.getInstance()
            val endDate = Calendar.getInstance()
            val time = realtime.split(" ")
            val year = time[0].split("-")[0].toInt()
            val month = time[0].split("-")[1].toInt()
            val day = time[0].split("-")[2].toInt()
            startDate.set(year,month-1,day)
            endDate.set(year+1,11,31)
            val pvTime = TimePickerBuilder(this, object : OnTimeSelectListener {
                override fun onTimeSelect(date: Date, v: View?) {
                    DateStart.setText(dateFormat.format(date))
                    DateEnd.setText("")
                }
            }).setType(booleanArrayOf(true, true, true, true, true, false))
                .setRangDate(startDate,endDate)
                .setCancelText("取消")
                .setSubmitText("確定")
                .setContentTextSize(20)
                .setTitleSize(22)
                .setOutSideCancelable(false)
                .setLabel("", "", "", "時", "分", "")
                .isCenterLabel(false)
                .build()
            pvTime.show()
        }

        DateEnd.setOnClickListener{
            if(DateStart.text.toString() == ""){
                Toast.makeText(this,"請先選取開始時間", Toast.LENGTH_SHORT).show()
            }else{
                val ime = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                ime.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
                val time = DateStart.text.toString().split(" ")
                val year = time[0].split("-")[0].toInt()
                val month = time[0].split("-")[1].toInt()
                val day = time[0].split("-")[2].toInt()
                val startDate = Calendar.getInstance()
                val endDate = Calendar.getInstance()
                startDate.set(year,month-1,day)
                endDate.set(year+1,11,31)

                val pvTime = TimePickerBuilder(this, object : OnTimeSelectListener {
                    override fun onTimeSelect(date: Date, v: View?) {
                        DateEnd.setText(dateFormat.format(date))
                    }
                }).setType(booleanArrayOf(true, true, true, true, true, false))
                    .setRangDate(startDate,endDate)
                    .setCancelText("取消")
                    .setSubmitText("確定")
                    .setContentTextSize(20)
                    .setTitleSize(22)
                    .setOutSideCancelable(false)
                    .setLabel("", "", "", "時", "分", "")
                    .isCenterLabel(false)
                    .build()
                pvTime.show()
            }
        }

        btn_Next.setOnClickListener {
            if (DateStart.text.toString() != "" && DateEnd.text.toString() != "" && et_name.text.toString() !="" && et_description.text.toString() != "") {
                val intent = Intent(this, CreateTagActivity::class.java)
                intent.putExtra("authId", authId)
                    .putExtra("groupName", groupName)
                    .putExtra("groupId",groupId)
                    .putExtra("activityname",et_name.text.toString())
                    .putExtra("datestart", DateStart.text.toString())
                    .putExtra("dateend", DateEnd.text.toString())
                    .putExtra("timeStatus",timeStatus)
                    .putExtra("content",et_description.text.toString())
                    .putExtra("people",et_people.text.toString())
                if (bitmapUri != null) {
                    intent.putExtra("bitmapUri", bitmapUri.toString())
                }
                val options = ActivityOptions.makeCustomAnimation(this,R.anim.slide_out_left,R.anim.slide_in_right)
                startActivity(intent, options.toBundle())
            }else{
                Toast.makeText(this,"請輸入活動名稱、時間", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun takeImageFromAlbumWithIntent() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 200)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val resolver = this.contentResolver
        try{
            if (requestCode == 200) {
                if (data != null) {
                    bitmapUri = data.data
                    val bitmap = MediaStore.Images.Media.getBitmap(resolver, data?.data)
                    if (bitmap.toString() != "") {
                        img_image.setImageBitmap(bitmap)
                        img_image.clearColorFilter()
                    }
                }
            }
        }catch (e: Exception){}
    }

}