package com.network.fiesta

import android.app.ActivityOptions
import android.content.Intent
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
import kotlinx.android.synthetic.main.activity_menu_create.*
import kotlinx.android.synthetic.main.activity_menu_create.btn_addgroup
import kotlinx.android.synthetic.main.activity_menu_create.btn_done
import kotlinx.android.synthetic.main.activity_menu_create.et_groupmember
import kotlinx.android.synthetic.main.activity_menu_create.et_groupname
import kotlinx.android.synthetic.main.activity_menu_create.sliding_layout
import kotlinx.android.synthetic.main.activity_menu_create.view_member
import kotlinx.android.synthetic.main.activity_menu_create.view_search
import kotlinx.android.synthetic.main.activity_menu_group.*
import org.json.JSONArray

class SideMenuCreate : AppCompatActivity() {

    var token = ""
    var authId = ""
    var select = ""
    var selectName = ""
    val isSelect = ArrayList<Boolean>()
    val Authority = ArrayList<String>()
    val groupName = ArrayList<String>()
    val groupId = ArrayList<String>()
    lateinit var userData: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_create)
        val NameArray = ArrayList<String>()
        val AuthIdArray = ArrayList<String>()
        val BitmapArray = ArrayList<Bitmap>()
        userData = getSharedPreferences("Data", MODE_PRIVATE)
        token = userData.getString("token", "token")!!
        authId = userData.getString("authId","-1")!!
        GroupView.layoutManager = LinearLayoutManager(this)
        GroupView.adapter = TextAdapter("載入中...")
        sliding_layout.setTouchEnabled(false)

        btn_Next.setOnClickListener {
            if (select!=""){
                val tempArray = ArrayList<String>()
                tempArray.add(authId.toString())
                val intent= Intent(this, CreateActActivity::class.java)
                intent.putExtra("authId",tempArray.toString())
                intent.putExtra("groupId",select)
                intent.putExtra("groupName",selectName)
                val options = ActivityOptions.makeCustomAnimation(this,R.anim.slide_out_left,R.anim.slide_in_right)
                startActivity(intent, options.toBundle())
                finish()
            }else{
                Toast.makeText(this,"請選擇群組",Toast.LENGTH_SHORT).show()
            }
        }

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
                                val temp = API(this@SideMenuCreate.applicationContext, token).userSearch(s.toString())
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
                                            view_search.layoutManager = LinearLayoutManager(this@SideMenuCreate)
                                            view_search.adapter = GroupSearchAdapter(this@SideMenuCreate, Search, AuthId, Photo, ::adduser)
                                        }
                                    } else {
                                        runOnUiThread {
                                            view_search.layoutManager = LinearLayoutManager(this@SideMenuCreate)
                                            view_search.adapter = TextAdapter("查無資料")
                                        }
                                    }
                                } else {
                                    runOnUiThread {
                                        view_search.layoutManager = LinearLayoutManager(this@SideMenuCreate)
                                        view_search.adapter = TextAdapter("查無資料")
                                    }
                                }
                            }
                        }).start()
                    } else {
                        view_search.layoutManager = LinearLayoutManager(this@SideMenuCreate)
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
            if (et_groupname.text.toString() != "") {
                Thread(Runnable {
                    run {
                        val tempArray = ArrayList<String>()
                        tempArray.add(authId)
                        tempArray += AuthIdArray
                        val Data = API(
                            this.applicationContext,
                            token
                        ).findGroupName(et_groupname.text.toString())
                        if (Data.getString("code") == "001") {
                            API(this.applicationContext, token).uploadGroup(
                                tempArray.toString(),
                                et_groupname.text.toString()
                            )
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
                                Toast.makeText(this, "群組名稱重複", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }).start()
            } else {
                Toast.makeText(this, "請填寫群組名稱", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun click(groupId: String,position: Int) {
        select = groupId
        selectName = groupName[position]
        isSelect.clear()
        for(i in 0..this.groupId.size-1){
            if(i == position){
                isSelect.add(true)
            }else{
                isSelect.add(false)
            }
        }
        GroupView.adapter = GroupSelectAdapter(this, groupName, this.groupId, isSelect, ::click)
    }

    override fun onResume() {
        super.onResume()
        Thread(Runnable {
            run{
                Authority.clear()
                groupName.clear()
                groupId.clear()
                val Data = API(this.applicationContext,token).getGroupByUser(authId)
                if (Data.getString("code")=="001"){
                    val temp = Data.getJSONArray("result")
                    for(i in 0..temp.length()-1){
                        val time = temp.getJSONObject(i).getString("timeStatus")
                        if(time == "1"){
                            Authority.add(temp.getJSONObject(i).getString("Authority"))
                            groupName.add(temp.getJSONObject(i).getString("groupName"))
                            groupId.add(temp.getJSONObject(i).getString("groupId"))
                        }
                    }
                    for (i in 1..groupId.size){
                        isSelect.add(false)
                    }
                    runOnUiThread(Runnable {
                        if(groupId.toString()!=""){
                            GroupView.adapter = GroupSelectAdapter(this, groupName, groupId, isSelect, ::click)
                        }else{
                            GroupView.adapter = TextAdapter("暫無資料")
                        }
                    })
                }else if(Data.getString("code")=="020"){
                    runOnUiThread {
                        Toast.makeText(this,"請先驗證信箱",Toast.LENGTH_SHORT).show()
                        GroupView.adapter = TextAdapter("請先驗證信箱")
                        btn_addgroup.visibility = View.GONE
                    }
                }else{
                    runOnUiThread {
                        Toast.makeText(this,"連線逾時",Toast.LENGTH_SHORT).show()
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
