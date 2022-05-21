package com.network.fiesta

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.android.synthetic.main.activity_signup.et_userId
import kotlinx.android.synthetic.main.activity_signup.img_loading
import kotlinx.android.synthetic.main.activity_signup.progress_Bar

class SingUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val intent = getIntent()
        val school = intent.getStringExtra("school")
        val userEmail = intent.getStringExtra("email")
        val department = intent.getStringExtra("department")

        txt_privacy.setOnClickListener {
            val intent = Intent(this, SettingPrivacyActivity::class.java)
            startActivity(intent)
        }

        tv_back.setOnClickListener {
            finish()
        }

        btn_singup.setOnClickListener {
            val userName = et_userName.text.toString()
            val nickName = et_nickName.text.toString()
            val userId = et_userId.text.toString()
            val userPassword1 = et_password1.text.toString()
            val userPassword2 = et_password2.text.toString()
            val phone = et_phone.text.toString()
            if (btn_argee.isChecked) {
                if (!userName.isEmpty() && !userId.isEmpty() && !userPassword1.isEmpty() && !userPassword2.isEmpty() && !nickName.isEmpty()) {
                    if (userPassword1 == userPassword2) {
                        img_loading.visibility = View.VISIBLE
                        progress_Bar.visibility = View.VISIBLE
                        if (userPassword1 != userId && userPassword1 != userName && userPassword1 != nickName && userPassword1 != userEmail && userPassword1 != phone) {

                            Thread(Runnable {
                                var text = ""
                                run {
                                    var Data = API(this.applicationContext).userSingUp(userId, userName, nickName, userPassword1, userEmail, school = school, department = department, Phone = phone)
                                    if (Data.getString("code") == "001") {
                                        val Data = API(this.applicationContext).mailConfirm(userId)
                                        if (Data.getString("code") == "001") {
                                            text = "註冊成功!請檢查信箱以驗證帳戶"
                                            val intent = Intent(this, LoginActivity::class.java)
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                            startActivity(intent)
                                        } else {
                                            text = "註冊成功!"
                                            val intent = Intent(this, LoginActivity::class.java)
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                            startActivity(intent)
                                        }
                                    } else if(Data.getString("code") == "005"){
                                        text = "帳號重複"
                                    } else if(Data.getString("code") == "008"){
                                        text = "學校電子郵件錯誤"
                                    } else {
                                        text = "發生不明錯誤"
                                    }
                                }
                                runOnUiThread {
                                    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
                                }
                            }).start()
                        } else {
                            Toast.makeText(this, "密碼請勿與其他欄位相同", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "密碼輸入不相符", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "輸入內容請勿留白", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "請勾選隱私政策", Toast.LENGTH_SHORT).show()
            }
            img_loading.visibility = View.GONE
            progress_Bar.visibility = View.GONE

        }
    }
}
