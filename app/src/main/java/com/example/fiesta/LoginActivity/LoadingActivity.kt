package com.network.fiesta

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId


class LoadingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_loading)

        var deviceToken: String? = ""

        Handler().postDelayed({

            //生成設備令牌
            FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        return@OnCompleteListener
                    }
                    // Get new Instance ID token
                    deviceToken = task.result?.token
                    getSharedPreferences("deviceToken",Context.MODE_PRIVATE).edit().clear().putString("deviceToken",deviceToken).apply()
                    Log.e("deviceToken",deviceToken)
                })

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Create channel to show notifications.
                val channelId = "default_notification_channel_id"
                val channelName = "通知"
                val notificationManager =
                    getSystemService(
                        NotificationManager::class.java
                    )
                notificationManager.createNotificationChannel(
                    NotificationChannel(
                        channelId,
                        channelName, NotificationManager.IMPORTANCE_LOW
                    )
                )
            }


            val Data = getSharedPreferences("Data", Context.MODE_PRIVATE)
            val userId = Data.getString("userId", "")
            val userPassword = Data.getString("userPassword", "")
            if (userId != "" && userPassword != "") {
                Thread(Runnable {
                    run {
                        var Data = API(this.applicationContext).getUserData(userId, userPassword)
                        when (Data.getString("code")) {
                            "001" -> {
                                Data = Data.getJSONArray("result").getJSONObject(0)
                                getSharedPreferences("Data", Context.MODE_PRIVATE)
                                    .edit()
                                    .clear()
                                    .putString("userName", Data.getString("userName"))
                                    .putString("nickName", Data.getString("nickName"))
                                    .putString("Mail_1", Data.getString("Mail_1"))
                                    .putString("userId", Data.getString("userId"))
                                    .putString("userPassword", userPassword)
                                    .putString("authId", Data.getString("Id"))
                                    .putString("token", Data.getString("token"))
                                    .putString("Photo", Data.getString("Photo"))
                                    .apply()
                                    API(this.applicationContext,Data.getString("token")).userUpdate(Data.getString("userId"),deviceToken = deviceToken)
                                    val intent = Intent(this, MenuActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                    startActivity(intent)
                                    finish()
                            }
                            "002" -> {
                                runOnUiThread {
                                    Toast.makeText(this, "查無帳號", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this, LoginActivity::class.java)
                                    getSharedPreferences("Data",Context.MODE_PRIVATE).edit().clear().apply()
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                    startActivity(intent)
                                    finish()
                                }
                            }
                            "003" -> {
                                runOnUiThread {
                                    Toast.makeText(this, "密碼錯誤", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this, LoginActivity::class.java)
                                    getSharedPreferences("Data",Context.MODE_PRIVATE).edit().clear().apply()
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                    startActivity(intent)
                                    finish()
                                }
                            }
                            "000" -> {
                                runOnUiThread {
                                    Toast.makeText(this, "網路連接失敗", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this, LoginActivity::class.java)
                                    getSharedPreferences("Data",Context.MODE_PRIVATE).edit().clear().apply()
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                    startActivity(intent)
                                    finish()
                                }
                            }
                            else -> {
                                runOnUiThread {
                                    val intent = Intent(this, MenuActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                    startActivity(intent)
                                    finish()
                                }
                            }
                        }

                    }
                }).start()
            }else{
                val intent= Intent(this, MenuActivity::class.java)
                startActivity(intent)
                finish()
            }
        },(Math.random() * (2000 - 1000) + 1000).toLong())
    }

}
