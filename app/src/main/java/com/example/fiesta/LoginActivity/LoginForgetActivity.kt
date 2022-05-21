package com.network.fiesta

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login_forget.*


class LoginForgetActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_forget)


        btn_send.setOnClickListener {
            if(et_userId.text.toString()!=""){
                img_loading.visibility = View.VISIBLE
                progress_Bar.visibility = View.VISIBLE
                Thread(Runnable {
                    run {
                        if (API(this.applicationContext).forgetPassword(et_userId.text.toString()).getString("code") == "001") {
                            runOnUiThread {
                                img_loading.visibility = View.GONE
                                progress_Bar.visibility = View.GONE
                                Toast.makeText(this, "密碼重設信已寄至您的信箱", Toast.LENGTH_SHORT).show()
                                btn_send.isEnabled = false
                                btn_send.text = "60"
                            }
                            for (i in 0..60) {
                                if (btn_send.text.toString() == "0") {
                                    runOnUiThread {
                                        btn_send.text = "發送重設信件"
                                        btn_send.isEnabled = true
                                    }
                                    break
                                } else {
                                    runOnUiThread {
                                        btn_send.text =
                                            (btn_send.text.toString().toInt() - 1).toString()
                                    }
                                    Thread.sleep(1000)
                                }
                            }
                        } else {
                            runOnUiThread {
                                img_loading.visibility = View.GONE
                                progress_Bar.visibility = View.GONE
                                Toast.makeText(this, "發生未知的錯誤", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }).start()
            }else{
                Toast.makeText(this, "請輸入帳號", Toast.LENGTH_SHORT).show()
            }
        }

        tv_login.setOnClickListener {
            super.onBackPressed()
        }
    }

}
