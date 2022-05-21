package com.network.fiesta

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_login_tag.*
import org.json.JSONArray
import kotlin.collections.ArrayList

class LoginTagActivity : AppCompatActivity() {
    val addArray = ArrayList<String>()
    var Data = JSONArray()
    var token : String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_tag)

        lateinit var intent:Intent
        val data = getSharedPreferences("Data", Context.MODE_PRIVATE)
        val userId = data.getString("userId", "userId")
        val userPassword = data.getString("userPassword", "userPassword")
        token = data.getString("token", "token")!!
        tag_view.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        tag_view.adapter = TextAdapter("載入中...")
        userTag(userId,userPassword)

        btn_next.setOnClickListener {
            Thread(Runnable {
                run {
                    if(addArray.size!=0){
                        var tags = ""
                        for (i in 0..addArray.size - 1) {
                            if (i != addArray.size - 1) {
                                tags += addArray.get(i) + ","
                            } else {
                                tags += addArray.get(i)
                            }
                        }
                        intent = Intent(this, MenuActivity::class.java)
                        val temp = API(this.applicationContext,token).userTagUpdate(userId, tags)
                        if(temp.getString("code")!="001"){
                            runOnUiThread {
                                Toast.makeText(this,"網路連接失敗",Toast.LENGTH_SHORT).show()
                            }
                        }else{
                            getSharedPreferences("ShareData", Context.MODE_PRIVATE)
                                .edit()
                                .clear()
                                .putBoolean("FirstTime",false)
                                .commit()
                            runOnUiThread {
                                startActivity(intent)
                                finish()
                            }
                        }
                    }else{
                        runOnUiThread {
                            Toast.makeText(this,"請選擇至少一個標籤",Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }).start()

        }
    }

    private fun userTag(userId: String?,userPassword: String?){
        Thread(Runnable {
            run {
                val temp1 = API(this.applicationContext,token).getTag()
                if(temp1.getString("code")=="001"){
                    Data = temp1.getJSONArray("result").getJSONArray(0)
                }else{
                    Toast.makeText(this,"網路連接失敗",Toast.LENGTH_SHORT).show()
                }

                val temp2 = API(this.applicationContext,token).getUserData(userId, userPassword)
                if (temp2.getString("code") == "001") {
                    val temp3 = temp2.getJSONArray("result").getJSONObject(0).get("Tag")
                    if(temp3.toString() != "None"){
                        val Tag = temp2.getJSONArray("result").getJSONObject(0).getJSONArray("Tag")
                        for (i in 0..Tag.length() - 1) {
                            addArray.add(Tag.getString(i))
                        }
                    }
                    val test = API(this).SearchIMG("https://imgur.com/iNGnle2.jpg")
                    val ImgArray = JSONArray()
                    for(i in 0..temp1.getJSONArray("result").getJSONArray(0).length()){
                        ImgArray.put(test)
                    }
                    runOnUiThread {
                        tag_view.layoutManager = GridLayoutManager(this,4)
                        tag_view.adapter = LoginTagAdapter(this,temp1.getJSONArray("result").getJSONArray(0),ImgArray,addArray,::addTag,::delTag)
                    }
                } else {
                    Toast.makeText(this, "網路連接失敗", Toast.LENGTH_SHORT).show()
                }
            }
        }).start()
    }

    private fun addTag(TagName: String) {
        addArray.add(TagName)
    }

    private fun delTag(TagName: String) {
        addArray.remove(TagName)
    }
}
