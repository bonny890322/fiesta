package com.network.fiesta

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {
    lateinit var intents: Intent
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        var passwordvisible = false

        //判斷是否第一次進入
        val ShareData = getSharedPreferences("ShareData", Context.MODE_PRIVATE)
        if (ShareData.getBoolean("FirstTime", true)) {
            intents = Intent(this, LoginTagActivity::class.java)
        } else {
            intents = Intent(this, MenuActivity::class.java)
        }

        tv_foget.setOnClickListener {
            startActivity(Intent(this, LoginForgetActivity::class.java))
        }

        img_view.setOnClickListener {
            if (passwordvisible) {
                et_password.transformationMethod = HideReturnsTransformationMethod.getInstance()
                et_password.setSelection(et_password.length())
                passwordvisible = !passwordvisible
            } else {
                et_password.transformationMethod = PasswordTransformationMethod.getInstance()
                et_password.setSelection(et_password.length())
                passwordvisible = !passwordvisible
            }
        }

        SignUp.setOnClickListener {
            val intent = Intent(this, SingUpEmailActivity::class.java)
            startActivity(intent)
        }

        btn_login.setOnClickListener {
            val userId = et_userId.text.toString()
            val userPassword = et_password.text.toString()

            if (userId != "" && userPassword != "") {
                if (userId != userPassword) {
                    img_loading.visibility = View.VISIBLE
                    progress_Bar.visibility = View.VISIBLE
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
                                    val deviceToken = getSharedPreferences("deviceToken", Context.MODE_PRIVATE).getString("deviceToken","")
                                    API(this.applicationContext,Data.getString("token")).userUpdate(Data.getString("userId"),deviceToken = deviceToken)
                                    intents.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                    startActivity(intents)
                                    finish()
                                }
                                "002" -> {
                                    runOnUiThread {
                                        Toast.makeText(this, "帳號或密碼錯誤", Toast.LENGTH_SHORT).show()
                                        this.img_loading.visibility = View.GONE
                                        this.progress_Bar.visibility = View.GONE
                                    }
                                }
                                else -> {
                                    runOnUiThread {
                                        Toast.makeText(this, "網路連接失敗", Toast.LENGTH_SHORT).show()
                                        this.img_loading.visibility = View.GONE
                                        this.progress_Bar.visibility = View.GONE
                                    }
                                }
                            }
                        }
                    }).start()
                } else {
                    Toast.makeText(this, "帳號與密碼請勿相同", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "帳號密碼請勿留白", Toast.LENGTH_SHORT).show()
            }
        }
    }
}