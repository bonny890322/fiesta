package com.network.fiesta

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_menu_edit.tabLayout
import kotlinx.android.synthetic.main.activity_menu_edit.ticketView
import org.json.JSONArray
import org.json.JSONObject

class SideMenuEdit : AppCompatActivity() {
    var token = ""
    var activityArray = mutableListOf<JSONObject>()
    var expArray = mutableListOf<JSONObject>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_edit)

        token = getSharedPreferences("Data", MODE_PRIVATE).getString("token", "token")!!

    }

    override fun onResume() {
        super.onResume()
        reload()
    }

    fun reload() {
        Thread(Runnable {
            run {
                if (activityArray.size == 0) {
                    val data = this.getSharedPreferences("Data", Context.MODE_PRIVATE)
                    val authId = data.getString("authId", "error")
                    val temp1 = API(this.applicationContext, token).getEditActivity(authId)

                    if (temp1.getString("code") == "001") {
                        val Data = temp1.getJSONArray("result")
                        for (i in 0..Data.length() - 1) {
                            activityArray.add(Data.getJSONObject(i))
                        }
                    } else if (temp1.getString("code") == "013") {
                    } else if (temp1.getString("code") == "020") {
                        runOnUiThread {
                            Toast.makeText(this, "請先驗證信箱", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this, R.string.connect_fail, Toast.LENGTH_SHORT).show()
                        }
                    }
                    //過期
                    val temp2 = API(this.applicationContext, token).getExpActivity()
                    if (temp2.getString("code") == "001") {
                        for (i in 0..temp2.getJSONArray("result").length() - 1) {
                            expArray.add(temp2.getJSONArray("result").getJSONObject(i))
                        }
                    } else if (temp2.getString("code") == "013") {
                    } else if (temp2.getString("code") == "020") {
                    } else {
                        runOnUiThread {
                            Toast.makeText(this, R.string.connect_fail, Toast.LENGTH_SHORT).show()
                        }
                    }
                    runOnUiThread {
                        ticketView.adapter = EditPagerAdapter(this, activityArray, expArray)
                        ticketView.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
                        tabLayout.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(ticketView))
                    }


                } else {
                    activityArray.clear()
                    expArray.clear()
                    val data = this.getSharedPreferences("Data", Context.MODE_PRIVATE)
                    val authId = data.getString("authId", "error")
                    val temp1 = API(this.applicationContext, token).getEditActivity(authId)

                    if (temp1.getString("code") == "001") {
                        val Data = temp1.getJSONArray("result")
                        for (i in 0..Data.length() - 1) {
                            activityArray.add(Data.getJSONObject(i))
                        }
                    } else if (temp1.getString("code") == "013") {
                    } else if (temp1.getString("code") == "020") {
                        runOnUiThread {
                            Toast.makeText(this, "請先驗證信箱", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this, R.string.connect_fail, Toast.LENGTH_SHORT).show()
                        }
                    }
                    //過期
                    val temp2 = API(this.applicationContext, token).getExpActivity()
                    if (temp2.getString("code") == "001") {
                        for (i in 0..temp2.getJSONArray("result").length() - 1) {
                            expArray.add(temp2.getJSONArray("result").getJSONObject(i))
                        }
                    } else if (temp2.getString("code") == "013") {
                    } else if (temp2.getString("code") == "020") {
                    } else {
                        runOnUiThread {
                            Toast.makeText(this, R.string.connect_fail, Toast.LENGTH_SHORT).show()
                        }
                    }
                    runOnUiThread {
                        ticketView.adapter!!.notifyDataSetChanged()
                    }
                }
            }
        }).start()
    }

}
