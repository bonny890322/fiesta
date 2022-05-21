package com.network.fiesta

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.activity_menu_personal_edit.*
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.TextView
import kotlinx.android.synthetic.main.bottomsheet_editimage.view.*
import java.util.*


class SideMenuPersonalinformationEditActivity : AppCompatActivity() {
    var token = ""
    var userId = ""
    var isStudent = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_personal_edit)
        val data = getSharedPreferences("Data", Context.MODE_PRIVATE)
        token = data.getString("token", "token")!!
        userId = data.getString("userId", "error")!!
        val userPassword = data.getString("userPassword", "error")

        Thread(Runnable {
            run{
                val temp = API(this.applicationContext,token).getUserData(userId, userPassword)
                if(temp.getString("code")=="001"){
                    val data = temp.getJSONArray("result").getJSONObject(0)
                    if (data.getString("Photo") != "None") {
                        try {
                            val bitmap = API(this.applicationContext).SearchIMG(data.getString("Photo"))
                            runOnUiThread {
                                img_person.setImageBitmap(bitmap)
                            }
                        }catch (e:Exception){ }
                    }
                    runOnUiThread {
                        tv_school.text = data.getString("School")
                        tv_nickName.text = data.getString("nickName")
                        et_nickName.setText(data.getString("nickName"))
                        et_userName.setText(data.getString("userName"))
                        if(data.getString("Birthday")!="None"){
                            tv_birthday.setText(data.getString("Birthday"))
                        }else{
                            tv_birthday.setText("")
                        }
                        if(data.getString("Phone")!="None"){
                            et_phone.setText(data.getString("Phone"))
                        }else{
                            et_phone.setText("")
                        }
                        val sex = ArrayList<String>()
                        sex.add("男")
                        sex.add("女")
                        sp_sex.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, sex)
                        if(data.getString("Sex")=="F"){
                            sp_sex.setSelection(1)
                        }
                        if(data.getString("Mail_1")!="None"){
                            et_email.setText(data.getString("Mail_1"))
                            if(API(this,token).getUserEmailStatus(userId).getString("code")=="001"){
                                et_email.isEnabled = false
                                img_school.setImageResource(R.drawable.ui_check)
                            }else{
                                img_school.setImageResource(R.drawable.ic_error)
                                img_school.setOnClickListener {
                                    AlertDialog.Builder(this)
                                        .setTitle("發送驗證信?")
                                        .setNegativeButton("否",null)
                                        .setPositiveButton("是"){ _, _ ->
                                            if(isStudent){
                                                Thread(Runnable {
                                                    run {
                                                        val temp = API(this,token).userUpdate(userId, Mail_1 = et_email.text.toString())
                                                        if (temp.getString("code") == "001") {
                                                            val temp = API(this).mailConfirm(userId)
                                                            if (temp.getString("code") == "001") {
                                                                runOnUiThread {
                                                                    Toast.makeText(this, "已經發送驗證信至信箱囉!", Toast.LENGTH_SHORT).show()
                                                                }
                                                            } else if (temp.getString("code") == "008") {
                                                                runOnUiThread {
                                                                    Toast.makeText(this, "E-mail請填學校信箱", Toast.LENGTH_SHORT).show()
                                                                }
                                                            }
                                                        }
                                                    }
                                                }).start()
                                            }else{
                                                Thread(Runnable {
                                                    run {
                                                        val temp = API(this,token).userUpdate(userId, Mail_2 = et_email.text.toString())
                                                        if (temp.getString("code") == "001") {
                                                            runOnUiThread {
                                                                Toast.makeText(this, "非常抱歉\n目前此功能僅限學校信箱", Toast.LENGTH_SHORT).show()
                                                            }
                                                        }
                                                    }
                                                }).start()
                                            }

                                        }.show()
                                }
                            }
                            isStudent = true
                        }else{
                            et_email.setText(data.getString("Mail_2"))
                        }
                        if (data.getString("Distance") != "None") {
                            val temp = data.getString("Distance")
                            when(temp){
                                "10" -> { seekBar.progress = 0;tv_km.text = "10km" }
                                "50" -> { seekBar.progress = 1;tv_km.text = "50km" }
                                "100" -> { seekBar.progress = 2;tv_km.text = "100km" }
                            }
                        }
                        img_tvpen.setOnClickListener {
                            et_nickName.visibility = View.VISIBLE
                            img_etpen.visibility = View.VISIBLE
                            tv_nickName.visibility = View.INVISIBLE
                            img_tvpen.visibility = View.GONE
                        }
                        img_etpen.setOnClickListener {
                            if(et_nickName.text.isNotBlank() && et_nickName.text.isNotEmpty()){
                                tv_nickName.text = et_nickName.text
                                et_nickName.visibility = View.GONE
                                img_etpen.visibility = View.GONE
                                tv_nickName.visibility = View.VISIBLE
                                img_tvpen.visibility = View.VISIBLE
                            }else{
                                et_nickName.setText(tv_nickName.text)
                                et_nickName.visibility = View.GONE
                                img_etpen.visibility = View.GONE
                                tv_nickName.visibility = View.VISIBLE
                                img_tvpen.visibility = View.VISIBLE
                            }
                            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            imm.hideSoftInputFromWindow(logo.windowToken, 0)
                        }
                        tv_birthday.setOnClickListener {
                            val c = Calendar.getInstance()
                            val year = c.get(Calendar.YEAR)
                            val month = c.get(Calendar.MONTH)
                            val day = c.get(Calendar.DAY_OF_MONTH)
                            DatePickerDialog(
                                it.context,
                                DatePickerDialog.OnDateSetListener { view, year, month, day ->
                                    tv_birthday.setText(
                                        "$year-" + (month + 1) + "-$day",
                                        TextView.BufferType.EDITABLE
                                    )
                                }, year, month, day
                            ).show()
                        }
                    }
                }else{
                    runOnUiThread {
                        Toast.makeText(this, "網路連接失敗", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }).start()

        img_person.setOnClickListener {
            val sheetDialog = BottomSheetDialog(this)
            val view = LayoutInflater.from(this).inflate(R.layout.bottomsheet_editimage,null)
            sheetDialog.setContentView(view)
            val parent = view.parent as ViewGroup
            parent.setBackgroundResource(android.R.color.transparent)
            view.button1.setOnClickListener {
                takePicture()
                sheetDialog.dismiss()
            }
            view.button2.setOnClickListener {
                takeImageFromAlbumWithIntent()
                sheetDialog.dismiss()
            }
            view.button_cancel.setOnClickListener {
                sheetDialog.dismiss()
            }
            sheetDialog.show()
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                Thread(Runnable {
                    run{
                        when (progress) {
                            0 -> { API(this@SideMenuPersonalinformationEditActivity.applicationContext,token).userUpdate(userId, Distance = "10");runOnUiThread { tv_km.text = "10km" } }
                            1 -> { API(this@SideMenuPersonalinformationEditActivity.applicationContext,token).userUpdate(userId, Distance = "50");runOnUiThread { tv_km.text = "50km" }}
                            2 -> { API(this@SideMenuPersonalinformationEditActivity.applicationContext,token).userUpdate(userId, Distance = "100");runOnUiThread { tv_km.text = "100km" } }
                        }
                    }
                }).start()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun takeImageFromAlbumWithIntent() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 200)
    }
    private fun takePicture() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, 1001)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try{
            val userId = getSharedPreferences("Data", Context.MODE_PRIVATE).getString("authId", "error")
            val resolver = this.contentResolver
            when (requestCode) {
                200 -> {
                    val bitmap = MediaStore.Images.Media.getBitmap(resolver, data?.data)
                    img_person.setImageBitmap(bitmap)
                    Thread(Runnable {
                        run {
                            API(this.applicationContext,token).uploadImg("auth", userId!!, bitmap)
                        }
                    }).start()
                }
                1001 -> {
                    val bitmap = data?.extras?.get("data") as Bitmap
                    img_person.setImageBitmap(bitmap)
                    Thread(Runnable {
                        run {
                            API(this.applicationContext,token).uploadImg("auth", userId!!, bitmap)
                        }
                    }).start()
                }
            }
        }catch (e:Exception){ }
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setTitle("確認修改個人資料?")
            .setNegativeButton("否") { dialog, _ ->
                super.onBackPressed()
            }
            .setPositiveButton("是"){ dialog, _ ->
                dialog.cancel()
                Thread(Runnable {
                    run{
                        if(sp_sex.selectedItem.toString()=="男"){
                            API(this, token).userUpdate(
                                userId,
                                userName = et_userName.text.toString(),
                                nickName = tv_nickName.text.toString(),
                                Phone = et_phone.text.toString(),
                                Birthday = tv_birthday.text.toString(),
                                Sex = "M"
                            )
                        }else{
                            API(this, token).userUpdate(
                                userId,
                                userName = et_userName.text.toString(),
                                nickName = tv_nickName.text.toString(),
                                Phone = et_phone.text.toString(),
                                Birthday = tv_birthday.text.toString(),
                                Sex = "F"
                            )
                        }
                    }
                    runOnUiThread {
                        super.onBackPressed()
                    }
                }).start()
            }.show()
    }
}
