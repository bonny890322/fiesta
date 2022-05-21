package com.network.fiesta

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_menu_personal.*

class SideMenuPersonal : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_personal)

        // 輸入用戶資料
        val Data = getSharedPreferences("Data",Context.MODE_PRIVATE)
        val ShareData = getSharedPreferences("ShareData", Context.MODE_PRIVATE)
        val token = Data.getString("token", "token")!!
        val userId = Data.getString("userId","error")
        val userPassword = Data.getString("userPassword","error")

        Thread(Runnable {
            run {
                var userData = API(this,token).getUserData(userId, userPassword)
                if (userData.getString("code") == "001") {
                    userData = userData.getJSONArray("result").getJSONObject(0)
                    runOnUiThread {
                        findViewById<TextView>(R.id.tv_userName).text = userData.getString("nickName")
                        findViewById<TextView>(R.id.tv_email).text = userData.getString("Mail_1")
                    }
                    if (userData.getString("Photo") != "None") {
                        try {
                            val bitmap = API(this).SearchIMG(userData.getString("Photo"))
                            runOnUiThread {
                                img_person.setImageBitmap(bitmap)
                            }
                        }catch (e:Exception){ }
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this, "網路連接失敗", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }).start()
        tv_personalinformation.setOnClickListener {
            val intent= Intent(this,SideMenuPersonalinformationEditActivity::class.java)
            startActivity(intent)
        }
        tv_logout.setOnClickListener {
            Data.edit().clear().commit()
            ShareData.edit().clear().commit()
            val intent= Intent(this,LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }

        val addArray = ArrayList<String>()

        Thread(Runnable {
            run {
                val temp2 = API(this,token).getUserData(userId, userPassword)
                if (temp2.getString("code") == "001") {
                    val temp3 = temp2.getJSONArray("result").getJSONObject(0).get("Tag")
                    if(temp3.toString() !="None"){
                        val Tag = temp2.getJSONArray("result").getJSONObject(0).getJSONArray("Tag")
                        for (i in 0..Tag.length() - 1) {
                            addArray.add(Tag.getString(i))
                        }
                    }
                    runOnUiThread {
                        favoritesList.layoutManager = LinearLayoutManager(this)
                        favoritesList.adapter = FavoritesAdapter(this, addArray)
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this, "網路連接失敗", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }).start()
   }
}