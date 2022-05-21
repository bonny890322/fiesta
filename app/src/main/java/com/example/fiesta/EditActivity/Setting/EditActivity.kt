package com.network.fiesta

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_advance_edit.*
import kotlinx.android.synthetic.main.dialog_edit.view.btn_cancel
import kotlinx.android.synthetic.main.dialog_edit.view.btn_done
import kotlinx.android.synthetic.main.dialog_edit.view.et_text
import org.json.JSONArray
import kotlin.Exception

class EditActivity : AppCompatActivity() {
    lateinit var act_Id: String
    var token = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_advance_edit)

        act_Id = getIntent().getStringExtra("act_Id")
        token = getSharedPreferences("Data", MODE_PRIVATE).getString("token", "token")!!
    }

    override fun onResume() {
        super.onResume()
        img_loading.visibility = View.VISIBLE
        progress_Bar.visibility = View.VISIBLE
        Thread(Runnable {
            run{
                val Data = API(this.applicationContext,token).getActivityById(act_Id).getJSONArray("result").getJSONObject(0)
                val Data1 = JSONArray()
                    .put("活動名稱")
                    .put("活動標籤")
                    .put("地址")
                    .put("始末時間")
                    .put("票種、價格、人數")
                    .put("活動簡介")
                var tag = ""
                try {
                    val temp = Data.getJSONArray("Tag")
                    for (i in 0..temp.length() - 1) {
                        if (i == temp.length() - 1) {
                            tag += " #" +temp[i].toString()
                        } else {
                            tag += "#" + temp[i].toString()
                        }
                    }
                } catch (e: Exception) { }
                val Data2 = JSONArray()
                    .put(Data.getString("act_Name"))
                    .put(tag)
                    .put(Data.getString("Location"))
                    .put("從"+Data.getString("startTime") + "\n到" + Data.getString("endTime"))
                    .put("人數:"+Data.getString("peopleMaxium"))
                    .put(Data.getString("act_Description"))
                if(Data.getString("Photo")!="None"){
                    try {
                        val temp = API(this.applicationContext).SearchIMG(Data.getString("Photo"))
                        runOnUiThread {
                            img.setImageBitmap(temp)
                        }
                    }catch (e:Exception){
                        runOnUiThread {
                            img.setImageResource(R.drawable.ui_fiestalogo)
                        }
                    }
                }

                if(Data.getString("viewStatus") == "0"){
                    //未發佈
                    switch_public.setOnCheckedChangeListener { _, isChecked ->
                        if(isChecked){
                            try {
                                AlertDialog.Builder(this)
                                    .setTitle("發佈活動?")
                                    .setMessage("活動發佈後將只剩下內容可做更改")
                                    .setNegativeButton("取消") { dialog, which ->
                                        switch_public.isChecked = false
                                    }
                                    .setPositiveButton("確認") { dialog, which ->
                                        if(API(this.applicationContext,token).updateActivity(act_Id, viewStatus = "true")!!.getString("code")=="001"){
                                            runOnUiThread{
                                                Toast.makeText(this, "發佈成功",Toast.LENGTH_SHORT).show()
                                                switch_public.isEnabled = false
                                            }
                                        }else{
                                            Toast.makeText(this, "發佈失敗",Toast.LENGTH_SHORT).show()
                                        }
                                    }.show()
                            } catch (e: Exception) {
                                runOnUiThread {
                                    Toast.makeText(this, R.string.connect_fail, Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }else{
                    //已發佈
                    runOnUiThread {
                        switch_public.isChecked = true
                        switch_public.isEnabled = false
                    }
                }

                runOnUiThread {
                    img.setOnClickListener {
                        if (switch_public.isEnabled) {
                            takeImageFromAlbumWithIntent()
                        } else {
                            Toast.makeText(this, "已發佈之活動無法修改", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                btn_delete.setOnClickListener {
                    if(switch_public.isEnabled){
                        AlertDialog.Builder(this)
                            .setTitle("確定要刪除此活動嗎")
                            .setNegativeButton("否", null)
                            .setPositiveButton("是") { dialog, which ->
                                Thread(Runnable {
                                    run {
                                        if (API(this.applicationContext,token).deleteActivity(act_Id).getString("code") == "001") {
                                            kotlin.run {
                                                runOnUiThread {
                                                    Toast.makeText(this, "刪除成功", Toast.LENGTH_SHORT).show()
                                                    dialog.cancel()
                                                    super.onBackPressed()
                                                }
                                            }
                                        } else {
                                            runOnUiThread {
                                                Toast.makeText(this, "刪除失敗 請稍後在試", Toast.LENGTH_SHORT).show()
                                                dialog.cancel()
                                            }
                                        }
                                    }
                                }).start()
                            }.show()
                    }else{
                        AlertDialog.Builder(this)
                            .setTitle("已發佈之活動無法刪除")
                            .setMessage("如欲修改任何無法修改之事項\n請聯絡我們")
                            .setPositiveButton("確定",null)
                            .setNegativeButton("Email聯絡"){dialog, which ->

                                val TO = arrayOf("fiestaconfirmmail@gmail.com")
                                val emailIntent = Intent(Intent.ACTION_SEND)
                                emailIntent.type = "text/plain"

                                emailIntent.putExtra(Intent.EXTRA_EMAIL, TO)
                                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "活動問題")
                                emailIntent.putExtra(Intent.EXTRA_TEXT, "#活動編號: ${act_Id}\n  您的問題：\n")

                                try {
                                    startActivity(Intent.createChooser(emailIntent, "Send mail..."))
                                } catch (ex: android.content.ActivityNotFoundException) { }
                            }.show()
                    }
                }

                runOnUiThread {
                    tv_title.text = Data.getString("act_Name")
                    editView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                    editView.adapter = AdvanceEditAdapter(this,Data1,Data2,::click)
                    img_loading.visibility = View.GONE
                    progress_Bar.visibility = View.GONE
                }
            }
        }).start()
    }

    fun click(title: String ,text: TextView){
        img_loading.visibility = View.VISIBLE
        progress_Bar.visibility = View.VISIBLE
        when(title){
            "活動名稱" -> {
                if(switch_public.isEnabled){
                    val myDialog = LayoutInflater.from(this).inflate(R.layout.dialog_personal_edit,null)
                    val d = AlertDialog.Builder(this).setView(myDialog).setTitle(title)
                    val myAlertDialog = d.show()
                    myDialog.et_text.setText(text.text)
                    myDialog.btn_cancel.setOnClickListener{
                        myAlertDialog.dismiss()
                    }
                    myDialog.btn_done.setOnClickListener {
                        text.text = myDialog.et_text.text
                        Thread(Runnable {
                            run {
                                API(this.applicationContext,token).updateActivity(act_Id, act_Name = text.text.toString())
                            }
                        }).start()
                        myAlertDialog.dismiss()
                    }
                }else{
                    Toast.makeText(this,"已發佈活動之$title 無法修改",Toast.LENGTH_SHORT).show()
                }
            }
            "活動標籤" -> {
                if(switch_public.isEnabled) {
                    intent = Intent(this, LoginTagActivity::class.java)
                        .putExtra("msg", act_Id)
                    startActivity(intent)
                }else{
                    Toast.makeText(this,"已發佈活動之$title 無法修改",Toast.LENGTH_SHORT).show()
                }
            }
            "地址" -> {
                if (switch_public.isEnabled){
                    intent = Intent(this, EditMapActivity::class.java)
                        .putExtra("Id", act_Id)
                    startActivity(intent)
                }else{
                    Toast.makeText(this,"已發佈活動之$title 無法修改",Toast.LENGTH_SHORT).show()
                }
            }
            "始末時間" -> {
                if(switch_public.isEnabled) {
                    intent = Intent(this, EditAdvanceTimeActivity::class.java)
                        .putExtra("Id", act_Id)
                    startActivity(intent)
                }else{
                    Toast.makeText(this,"已發佈活動之$title 無法修改",Toast.LENGTH_SHORT).show()
                }
            }
            "票種、價格、人數" -> {
                if(switch_public.isEnabled) {
                    intent = Intent(this, EditAdvancePriceActivity::class.java)
                        .putExtra("act_Id", act_Id)
                    startActivity(intent)
                }else{
                    Toast.makeText(this,"已發佈活動之$title 無法修改",Toast.LENGTH_SHORT).show()
                }
            }
            "活動簡介" -> {
                val myDialog = LayoutInflater.from(this).inflate(R.layout.dialog_edit, null)
                val d = AlertDialog.Builder(this).setView(myDialog).setTitle(title)
                val myAlertDialog = d.show()
                myDialog.et_text.setText(text.text)
                myDialog.btn_cancel.setOnClickListener {
                    myAlertDialog.dismiss()
                }
                myDialog.btn_done.setOnClickListener {
                    text.text = myDialog.et_text.text
                    Thread(Runnable {
                        run {
                            API(this.applicationContext,token).updateActivity(act_Id, act_Description = text.text.toString())
                        }
                    }).start()
                    myAlertDialog.dismiss()
                }
            }
            else -> {}
        }
        img_loading.visibility = View.GONE
        progress_Bar.visibility = View.GONE
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
            if(requestCode==200){
                val bitmap = MediaStore.Images.Media.getBitmap(resolver, data?.data)
                Thread(Runnable {
                    run{
                        val temp = API(this.applicationContext,token).uploadImg("act", act_Id , bitmap)
                        runOnUiThread {
                            img_loading.visibility = View.VISIBLE
                            progress_Bar.visibility = View.VISIBLE
                        }
                        if(temp.getString("code") == "001"){
                            runOnUiThread {
                                img.setImageBitmap(bitmap)
                                img_loading.visibility = View.GONE
                                progress_Bar.visibility = View.GONE
                            }
                        }
                    }
                }).start()
            }
        }catch (e:Exception){ }
    }
}
