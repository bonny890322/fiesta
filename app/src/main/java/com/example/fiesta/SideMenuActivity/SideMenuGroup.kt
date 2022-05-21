package com.network.fiesta

import android.content.SharedPreferences
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import kotlinx.android.synthetic.main.activity_menu_group.*

class SideMenuGroup : AppCompatActivity() {
    var token = ""
    var authId = ""
    lateinit var userData:SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_group)

        val NameArray = ArrayList<String>()
        val AuthIdArray = ArrayList<String>()
        val BitmapArray = ArrayList<Bitmap>()
        userData = getSharedPreferences("Data", MODE_PRIVATE)
        token = userData.getString("token", "token")!!
        authId = userData.getString("authId","-1")!!
        groupList.layoutManager = LinearLayoutManager(this)
        groupList.adapter = TextAdapter("載入中")
        sliding_layout.setTouchEnabled(false)

        btn_addgroup.setOnClickListener {
            sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED)

            fun deluser(authid: String) {
                val positon = AuthIdArray.indexOf(authid)
                NameArray.removeAt(positon)
                AuthIdArray.removeAt(positon)
                BitmapArray.removeAt(positon)
                view_member.adapter!!.notifyItemRemoved(positon)
                if(AuthIdArray.size == 0){
                    view_member.adapter = TextAdapter("無")
                }
            }

            fun adduser(name: String, authid: String, bitmap:Bitmap) {
                val userData = getSharedPreferences("Data", MODE_PRIVATE)
                val userauthId = userData.getString("authId", "-1")
                if (authid != userauthId) {
                    var temp = true
                    for (i in 0..AuthIdArray.size - 1) {
                        if (AuthIdArray[i] == authid) {
                            temp = false
                            break
                        }
                    }
                    if (temp) {
                        NameArray.add(name)
                        AuthIdArray.add(authid)
                        BitmapArray.add(bitmap)
                        view_member.adapter = GroupAddAdapter(NameArray, AuthIdArray, BitmapArray, ::deluser)
                    } else {
                        Toast.makeText(this, "已經加入清單囉!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "你不用將自己加入成員之中!", Toast.LENGTH_SHORT).show()
                }
            }

            et_groupmember.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable) {}
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    if (s.toString() != "") {
                        Thread(Runnable {
                            run {
                                val temp = API(this@SideMenuGroup.applicationContext, token).userSearch(s.toString())
                                if (temp.getString("code") == "001") {
                                    val Data = temp.getJSONArray("result")
                                    var Search = ""
                                    var AuthId = ""
                                    var Photo = ""
                                    for (i in 0..Data.length() - 1) {
                                        if (s.toString() == Data.getJSONObject(i).getString("userId").toString()) {
                                            Search = Data.getJSONObject(i).getString("userId").toString()
                                            AuthId = Data.getJSONObject(i).getString("Id").toString()
                                            Photo = Data.getJSONObject(i).getString("Photo").toString()
                                        }
                                    }
                                    if (Search != "") {
                                        runOnUiThread {
                                            view_search.layoutManager = LinearLayoutManager(this@SideMenuGroup)
                                            view_search.adapter = GroupSearchAdapter(this@SideMenuGroup, Search, AuthId, Photo, ::adduser)
                                        }
                                    } else {
                                        runOnUiThread {
                                            view_search.layoutManager = LinearLayoutManager(this@SideMenuGroup)
                                            view_search.adapter = TextAdapter("查無資料")
                                        }
                                    }
                                } else {
                                    runOnUiThread {
                                        view_search.layoutManager = LinearLayoutManager(this@SideMenuGroup)
                                        view_search.adapter = TextAdapter("查無資料")
                                    }
                                }
                            }
                        }).start()
                    } else {
                        view_search.layoutManager = LinearLayoutManager(this@SideMenuGroup)
                        view_search.adapter = TextAdapter("")
                    }
                }
            })

            if(AuthIdArray.size != 0){
                view_member.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                view_member.adapter = GroupAddAdapter(NameArray, AuthIdArray, BitmapArray, ::deluser)
            }else{
                view_member.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                view_member.adapter = TextAdapter("無")
            }

        }

        btn_done.setOnClickListener {
            if(et_groupname.text.toString() != ""){
                Thread(Runnable {
                    run {
                        val tempArray = ArrayList<String>()
                        tempArray.add(authId)
                        tempArray += AuthIdArray
                        val Data = API(this.applicationContext,token).findGroupName(et_groupname.text.toString())
                        if (Data.getString("code") == "001") {
                            API(this.applicationContext,token).uploadGroup(tempArray.toString(), et_groupname.text.toString())
                            sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED)
                            NameArray.clear()
                            AuthIdArray.clear()
                            BitmapArray.clear()
                            runOnUiThread {
                                et_groupname.setText("")
                                et_groupmember.setText("")
                                view_member.adapter!!.notifyDataSetChanged()
                                view_search.adapter!!.notifyDataSetChanged()
                                onResume()
                            }
                        } else {
                            runOnUiThread {
                                Toast.makeText(this,"群組名稱重複",Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }).start()
            }else{
                Toast.makeText(this, "請填寫群組名稱", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Thread(Runnable {
            run{
                val temp = API(this.applicationContext,token).getGroupByUser(authId)
                if(temp.getString("code")=="001"){
                    val Data = temp.getJSONArray("result")
                    runOnUiThread {
                        groupList.adapter = GroupAdapter(this, Data)
                    }
                }else if(temp.getString("code")=="020"){
                    runOnUiThread {
                        Toast.makeText(this,"請先驗證信箱",Toast.LENGTH_SHORT).show()
                        groupList.adapter = TextAdapter("請先驗證信箱")
                        btn_addgroup.visibility = View.GONE
                    }
                }
            }
        }).start()
    }

    override fun onBackPressed() {
        if ((sliding_layout.getPanelState() === SlidingUpPanelLayout.PanelState.EXPANDED || sliding_layout.getPanelState() === SlidingUpPanelLayout.PanelState.ANCHORED)) {
            sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED)
        } else {
            super.onBackPressed()
        }
    }
}
