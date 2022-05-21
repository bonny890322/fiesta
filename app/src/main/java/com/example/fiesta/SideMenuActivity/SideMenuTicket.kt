package com.network.fiesta

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState
import kotlinx.android.synthetic.main.activity_menu_edit.*
import kotlinx.android.synthetic.main.activity_menu_ticket.*
import kotlinx.android.synthetic.main.activity_menu_ticket.tabLayout
import org.json.JSONArray
import org.json.JSONObject


class SideMenuTicket : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_ticket)

        val Id = getSharedPreferences("Data", MODE_PRIVATE).getString("authId", "authId")
        val token = getSharedPreferences("Data", MODE_PRIVATE).getString("token", "token")!!

        var activityArray = mutableListOf<JSONObject>()
        var expArray = mutableListOf<JSONObject>()

        viewpager_ticket.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout_ticket))
        tabLayout_ticket.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(viewpager_ticket))
        sliding_layout.setTouchEnabled(false)
        Thread(Runnable {
            run{
                val temp1 = API(this,token).selectbuyTicket(Id)
                val temp2 = API(this,token).selectexpTicket()
                if(temp1.getString("code")=="001" && temp2.getString("code")=="001"){
                    val activitytemp = temp1.getJSONArray("result")
                    for (i in 0..activitytemp.length()-1){
                        activityArray.add(activitytemp.getJSONObject(i))
                    }
                    val exptemp = temp2.getJSONArray("result")
                    for (i in 0..exptemp.length()-1){
                        expArray.add(exptemp.getJSONObject(i))
                    }
                    runOnUiThread {
                        viewpager_ticket.adapter = TicketPagerAdapter(this,activityArray,expArray,::checkActivity)
                    }
                }else if(temp1.getString("code") == "001" && temp2.getString("code") == "012"){
                    val activitytemp = temp1.getJSONArray("result")
                    for (i in 0..activitytemp.length()-1){
                        activityArray.add(activitytemp.getJSONObject(i))
                    }
                    runOnUiThread {
                        viewpager_ticket.adapter = TicketPagerAdapter(this,activityArray,expArray,::checkActivity)
                    }
                }else if(temp1.getString("code") == "012" && temp2.getString("code") == "001"){
                    val exptemp = temp2.getJSONArray("result")
                    for (i in 0..exptemp.length()-1){
                        expArray.add(exptemp.getJSONObject(i))
                    }
                    runOnUiThread {
                        viewpager_ticket.adapter = TicketPagerAdapter(this,activityArray,expArray,::checkActivity)
                    }
                }else if(temp1.getString("code") == "012" || temp2.getString("code") == "012"){
                    runOnUiThread {
                        viewpager_ticket.adapter = TextPagerAdapter("尚無參加活動")
                    }
                }else if(temp1.getString("code") == "020" || temp2.getString("code") == "020"){
                    runOnUiThread {
                        Toast.makeText(this,"請先驗證信箱",Toast.LENGTH_SHORT).show()
                        viewpager_ticket.adapter = TextPagerAdapter("請先驗證信箱")
                    }
                }else{
                    runOnUiThread {
                        Toast.makeText(this,"網路連接失敗",Toast.LENGTH_SHORT).show()
                        viewpager_ticket.adapter = TextPagerAdapter("網路連接失敗")
                    }
                }
            }
        }).start()

    }
    val aspter = FragmentAdapter(getSupportFragmentManager())
    fun checkActivity(act_Id:String){
        val test = mutableListOf(TicketDetailFragment(act_Id),TicketShowFragment(act_Id))
        aspter.addFragment(test)
        ticketViewPager.adapter = aspter

        ticketViewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(ticketViewPager))
        sliding_layout.setPanelState(PanelState.ANCHORED)
    }

    override fun onBackPressed() {
        if ((sliding_layout.getPanelState() === PanelState.EXPANDED || sliding_layout.getPanelState() === PanelState.ANCHORED)) {
            sliding_layout.setPanelState(PanelState.COLLAPSED)
        } else {
            super.onBackPressed()
        }
    }
}