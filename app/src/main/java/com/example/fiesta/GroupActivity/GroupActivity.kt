package com.network.fiesta

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.activity_group.*
import kotlinx.android.synthetic.main.bottomsheet_group.view.*
import kotlinx.android.synthetic.main.dialog_edit.view.*
import kotlinx.android.synthetic.main.dialog_group.view.*
import kotlinx.android.synthetic.main.dialog_group_member.view.*
import org.json.JSONArray

class GroupActivity : AppCompatActivity() {
    var token = ""
    var groupId = ""
    var authId = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)
        val temp = getSharedPreferences("Data", MODE_PRIVATE)
        token = temp.getString("token", "token")!!
        authId = temp.getString("authId","authId")!!
        groupId = getIntent().getStringExtra("groupId")
        val Authority = getIntent().getStringExtra("Authority")

        img_loading.visibility = View.VISIBLE
        progress_Bar.visibility = View.VISIBLE

        Thread(Runnable {
            run {
                val temp1 = API(this.applicationContext,token).getGroupByGroupId(groupId)
                if (temp1.getString("code") == "001") {
                    val data = temp1.getJSONArray("result").getJSONObject(0)
                    val Data1 = JSONArray()
                        .put("主辦單位名稱")
                        .put("主辦單位聯絡地址")
                        .put("主辦單位聯絡電話")
                        .put("主辦單位聯絡電子郵件")
                        .put("群組成員")
                    val Data2 = JSONArray()
                        .put(data.getString("groupName"))
                        .put(data.getString("Address"))
                        .put(data.getString("Phone"))
                        .put(data.getString("Mail"))
                        .put("點擊查看群組成員")

                    if(data.getString("Photo")!="None"){
                        try {
                            val temp = API(this.applicationContext).SearchIMG(data.getString("Photo"))
                            runOnUiThread {
                                img.setImageBitmap(temp)
                                btn_left.visibility = View.VISIBLE
                                btn_delete.visibility = View.VISIBLE
                                img_camera.visibility = View.VISIBLE
                            }
                        }catch (e:Exception){
                            runOnUiThread {
                                img.setImageResource(R.drawable.ui_fiestalogo)
                                btn_left.visibility = View.VISIBLE
                                btn_delete.visibility = View.VISIBLE
                                img_camera.visibility = View.VISIBLE
                            }
                        }
                    }else{
                        runOnUiThread {
                            img.setImageResource(R.drawable.ui_fiestalogo)
                            btn_left.visibility = View.VISIBLE
                            btn_delete.visibility = View.VISIBLE
                            img_camera.visibility = View.VISIBLE
                        }
                    }

                    runOnUiThread {
                        view.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                        view.adapter = AdvanceEditAdapter(this,Data1,Data2,::click)
                    }
                }

                btn_left.setOnClickListener {
                    if(Authority=="3"){
                        AlertDialog.Builder(this)
                            .setTitle("注意")
                            .setMessage("創建群組者若離開群組\n將會刪除群組")
                            .setNegativeButton("否",null)
                            .setPositiveButton("是"){dialog, which ->
                                if(API(this.applicationContext,token).deleteGroup(groupId).getString("code") == "001"){
                                    finish()
                                }else{
                                    Toast.makeText(this,"離開失敗 請稍後在試",Toast.LENGTH_SHORT).show()
                                }
                                dialog.cancel()
                            }.show()
                    }else{
                        AlertDialog.Builder(this)
                            .setTitle("離開群組?")
                            .setMessage("離開群組將不能主動加入")
                            .setNegativeButton("否",null)
                            .setPositiveButton("是"){dialog, which ->
                                if(API(this.applicationContext,token).deleteGroupMember(groupId,authId).getString("code")=="001"){
                                    finish()
                                }else{
                                    Toast.makeText(this,"離開失敗 請稍後在試",Toast.LENGTH_SHORT).show()
                                }
                                dialog.cancel()
                            }.show()
                    }
                }
                runOnUiThread {
                    img_loading.visibility = View.GONE
                    progress_Bar.visibility = View.GONE
                }
            }
        }).start()

        img.setOnClickListener {
            val sheetDialog = BottomSheetDialog(this)
            val view = LayoutInflater.from(this).inflate(R.layout.bottomsheet_group,null)
            sheetDialog.setContentView(view)
            val parent = view.parent as ViewGroup
            parent.setBackgroundResource(android.R.color.transparent)
            view.button1.setOnClickListener {
                takeImageFromAlbumWithIntent()
                sheetDialog.dismiss()
            }
            view.button_cancel.setOnClickListener {
                sheetDialog.dismiss()
            }
            sheetDialog.show()
        }

        if (Authority=="3"){
            btn_delete.setOnClickListener {
                AlertDialog.Builder(this)
                    .setTitle("確定要刪除群組嗎?")
                    .setNegativeButton("否",null)
                    .setPositiveButton("是"){dialog, which ->
                        AlertDialog.Builder(this)
                            .setTitle("注意")
                            .setMessage("這將會導致該群組所舉辦的所有活動被刪除")
                            .setNegativeButton("否",null)
                            .setPositiveButton("是"){dialog, which ->
                                if(API(this.applicationContext,token).deleteGroup(groupId).getString("code") == "001"){
                                    finish()
                                }else{
                                    Toast.makeText(this,"刪除失敗 請稍後在試",Toast.LENGTH_SHORT).show()
                                }
                                dialog.cancel()
                            }.show()
                        dialog.cancel()
                    }.show()
            }
        }else{
            btn_delete.setOnClickListener {
                Toast.makeText(this,"你的權限不足\n無法刪除該群組",Toast.LENGTH_SHORT).show()
            }
        }

    }
    fun click(title: String ,text: TextView){
        when(title){
            "主辦單位名稱" -> {
                val myDialog = LayoutInflater.from(this).inflate(R.layout.dialog_personal_edit,null)
                val d = AlertDialog.Builder(this).setView(myDialog).setTitle(title)
                val myAlertDialog = d.show()
                myDialog.et_text.setText(text.text)
                myDialog.findViewById<Button>(R.id.btn_cancel).setOnClickListener{
                    myAlertDialog.dismiss()
                }
                myDialog.findViewById<Button>(R.id.btn_done).setOnClickListener {
                    text.text = myDialog.et_text.text
                    Thread(Runnable {
                        run {
                            API(this.applicationContext,token).updateGroup(groupId,groupName = text.text.toString())
                        }
                    }).start()
                    myAlertDialog.dismiss()
                }
            }
            "主辦單位聯絡地址" -> {
                val myDialog = LayoutInflater.from(this).inflate(R.layout.dialog_personal_edit,null)
                val d = AlertDialog.Builder(this).setView(myDialog).setTitle(title)
                val myAlertDialog = d.show()
                myDialog.et_text.setText(text.text)
                myDialog.findViewById<Button>(R.id.btn_cancel).setOnClickListener{
                    myAlertDialog.dismiss()
                }
                myDialog.findViewById<Button>(R.id.btn_done).setOnClickListener {
                    text.text = myDialog.et_text.text
                    Thread(Runnable {
                        run {
                            API(this.applicationContext,token).updateGroup(groupId,Address = text.text.toString())
                        }
                    }).start()
                    myAlertDialog.dismiss()
                }
            }
            "主辦單位聯絡電話" -> {
                val myDialog = LayoutInflater.from(this).inflate(R.layout.dialog_personal_edit,null)
                val d = AlertDialog.Builder(this).setView(myDialog).setTitle(title)
                val myAlertDialog = d.show()
                myDialog.et_text.inputType = InputType.TYPE_CLASS_NUMBER
                myDialog.et_text.setText(text.text)
                myDialog.findViewById<Button>(R.id.btn_cancel).setOnClickListener{
                    myAlertDialog.dismiss()
                }
                myDialog.findViewById<Button>(R.id.btn_done).setOnClickListener {
                    text.text = myDialog.et_text.text
                    Thread(Runnable {
                        run {
                            API(this.applicationContext,token).updateGroup(groupId,Phone = text.text.toString())
                        }
                    }).start()
                    myAlertDialog.dismiss()
                }
            }
            "主辦單位聯絡電子郵件" -> {
                val myDialog = LayoutInflater.from(this).inflate(R.layout.dialog_personal_edit,null)
                val d = AlertDialog.Builder(this).setView(myDialog).setTitle(title)
                val myAlertDialog = d.show()
                myDialog.et_text.setText(text.text)
                myDialog.findViewById<Button>(R.id.btn_cancel).setOnClickListener{
                    myAlertDialog.dismiss()
                }
                myDialog.findViewById<Button>(R.id.btn_done).setOnClickListener {
                    text.text = myDialog.et_text.text
                    Thread(Runnable {
                        run {
                            API(this.applicationContext,token).updateGroup(groupId,Mail = text.text.toString())
                        }
                    }).start()
                    myAlertDialog.dismiss()
                }
            }
            "群組成員" -> {
                val myDialog = LayoutInflater.from(this).inflate(R.layout.dialog_group_member, null)
                val d = AlertDialog.Builder(this).setView(myDialog)
                val myAlertDialog = d.show()
                Thread(Runnable {
                    run {
                        val temp = API(this.applicationContext,token).selectGroupMember(groupId)
                        if (temp.getString("code") == "001") {
                            runOnUiThread {
                                myDialog.view.layoutManager = LinearLayoutManager(this)
                                myDialog.view.adapter = MemberAdapter(this, temp.getJSONArray("result"), groupId, token, authId)
                            }
                        } else {
                            Toast.makeText(this, "發生未知的錯誤", Toast.LENGTH_SHORT).show()
                        }
                    }
                }).start()
                myDialog.btn_add.setOnClickListener {
                    myAlertDialog.dismiss()
                    val myDialog = LayoutInflater.from(this).inflate(R.layout.dialog_group, null)
                    val d = AlertDialog.Builder(this).setView(myDialog)
                    val myAlertDialog = d.show()
                    myDialog.memberView.visibility = View.GONE
                    myDialog.findViewById<Button>(R.id.btn_done).visibility = View.GONE
                    myDialog.textView15.visibility = View.GONE
                    myDialog.imageView4.visibility = View.GONE
                    myDialog.txt_groupname.visibility = View.GONE
                    myDialog.checkBox.visibility = View.GONE
                    myDialog.findViewById<Button>(R.id.btn_cancel).visibility = View.GONE
                    myDialog.img_help.visibility = View.GONE

                    myDialog.txt_membername.addTextChangedListener(object : TextWatcher {
                        override fun afterTextChanged(s: Editable) {}
                        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                            if (s.toString() != ""){
                                Thread(Runnable {
                                    run {
                                        val temp = API(this@GroupActivity.applicationContext,token).userSearch(s.toString())
                                        if(temp.getString("code") == "001"){
                                            val Data = temp.getJSONArray("result")
                                            var Search = ""
                                            var AuthId = ""
                                            var Photo = ""
                                            for (i in 0..Data.length()-1) {
                                                if(s.toString() == Data.getJSONObject(i).getString("userId").toString()){
                                                    Search = Data.getJSONObject(i).getString("userId").toString()
                                                    AuthId = Data.getJSONObject(i).getString("Id").toString()
                                                    Photo = Data.getJSONObject(i).getString("Photo").toString()
                                                }
                                            }
                                            if(Search != ""){
                                                runOnUiThread {
                                                    myDialog.searchView.layoutManager = LinearLayoutManager(this@GroupActivity)
                                                    myDialog.searchView.adapter = GroupSearchAdapter(this@GroupActivity, Search, AuthId, Photo, ::adduser)
                                                }
                                            }else{
                                                runOnUiThread {
                                                    myDialog.searchView.layoutManager = LinearLayoutManager(this@GroupActivity)
                                                    myDialog.searchView.adapter= TextAdapter("查無資料")
                                                }
                                            }
                                        }else{
                                            runOnUiThread {
                                                myDialog.searchView.layoutManager = LinearLayoutManager(this@GroupActivity)
                                                myDialog.searchView.adapter= TextAdapter("查無資料")
                                            }
                                        }
                                    }
                                }).start()
                            }else{
                                myDialog.searchView.layoutManager = LinearLayoutManager(this@GroupActivity)
                                myDialog.searchView.adapter = TextAdapter("")
                            }
                        }
                    })
                }
            }
            else -> {}
        }
    }

    fun adduser(name:String, authid:String, bitmap: Bitmap){
        val temp = API(this.applicationContext,token).addGroupMember(groupId,authid).getString("code")
        when(temp){
            "001" -> { Toast.makeText(this,"新增成功",Toast.LENGTH_SHORT).show() }
            "023" -> { Toast.makeText(this,"此成員已在群組中",Toast.LENGTH_SHORT).show() }
            else -> {}
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
            if(requestCode==200){
                val bitmap = MediaStore.Images.Media.getBitmap(resolver, data?.data)
                Thread(Runnable {
                    run{
                        val temp = API(this.applicationContext,token).uploadImg("group", groupId , bitmap)
                        if(temp.getString("code") == "001"){
                            runOnUiThread {
                                img.setImageBitmap(bitmap)
                            }
                        }
                    }
                }).start()
            }
        }catch (e:Exception){}
    }
}
