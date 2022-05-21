package com.network.fiesta

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.Switch
import android.widget.Toast
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_edit_ticket_status.*
import kotlinx.android.synthetic.main.activity_edit_ticket_status.et_search
import kotlinx.android.synthetic.main.activity_edit_ticket_status.ticketView
import kotlinx.android.synthetic.main.dialog_edit_ticket_status.view.*
import org.json.JSONObject
import kotlinx.android.synthetic.main.activity_edit_ticket_status.tabLayout

class EditTicketStatusActivity : AppCompatActivity() {
    val nickName = ArrayList<String>()
    val ticketKinds = ArrayList<String>()
    val ticketStatus = ArrayList<String>()
    val Notes = ArrayList<String>()
    val authId = ArrayList<String>()
    val mail = ArrayList<String>()
    var totalArray = mutableListOf<JSONObject>()

    var token = ""
    var act_Id = ""

    val Ticket_Kinds = ArrayList<String>()

    //var ticketArray = mutableListOf<kotlin.collections.mutableListOf<JSONObject>>()
    //var normalArray = mutableListOf<JSONObject>()
    //var studentArray = mutableListOf<JSONObject>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_ticket_status)

        act_Id= getIntent().getStringExtra("act_Id")
        token = getSharedPreferences("Data", MODE_PRIVATE).getString("token", "token")!!
        img_loading.visibility = android.view.View.VISIBLE
        progress_Bar.visibility = android.view.View.VISIBLE

        et_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString() != "" ){
                    for(i in 0..nickName.size - 1){
                        if(s.toString() == nickName[i]){
                            ticketView.scrollBy(i,0)
                        }
                    }

                }
            }
        })

        Thread(Runnable {
            run{
                //參加人數
                val temp1 = API(this.applicationContext,token).getActivityById(act_Id)
                if(temp1.getString("code")=="001"){
                    val Data = temp1.getJSONArray("result").getJSONObject(0)
                    runOnUiThread {
                        tv_people.text = Data.getString("joinedCount")
                        tv_left.text = (Data.getString("peopleMaxium").toInt() - Data.getString("joinedCount").toInt()).toString()
                    }
                }else{
                    runOnUiThread {
                        Toast.makeText(this,"網路連線失敗",Toast.LENGTH_SHORT).show()
                    }
                }

                //票種種類
                val temp2 = API(this.applicationContext,token).getTicket(act_Id)
                if(temp2.getString("code")=="001") {
                    val Data = temp2.getJSONArray("result")
                    Ticket_Kinds.add("全部票種")
                    for (i in 0..Data.length() - 1) {
                        if(Data.getJSONObject(i).getString("ticketKinds") == "None"){
                            Ticket_Kinds.add("一般票")
                        }else{
                            Ticket_Kinds.add(Data.getJSONObject(i).getString("ticketKinds"))
                        }

                    }
                }else if(temp2.getString("code")=="013"){
                    Ticket_Kinds.add("全部票種")
                    Ticket_Kinds.add("一般票")
                } else{
                    runOnUiThread {
                        Toast.makeText(this,"網路連線錯誤",Toast.LENGTH_SHORT).show()
                    }
                }


                /*
                val temp2 = API(this.applicationContext,token).getTicket(act_Id)
                val TicketKinds = ArrayList<String>()
                if(temp2.getString("code")=="001"){
                    val Data = temp2.getJSONArray("result")
                    TicketKinds.add("全部票種")
                    for(i in 0..Data.length()-1) {
                        TicketKinds.add(Data.getJSONObject(i).getString("ticketKinds")+": "+Data.getJSONObject(i).getString("Price"))
                    }
                    runOnUiThread {
                        //sp_ticket.visibility = View.VISIBLE
                        //sp_ticket.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, TicketKinds)
                    }
                }else if(temp2.getString("code")=="013"){

                } else{
                    runOnUiThread {
                        Toast.makeText(this,"網路連線錯誤",Toast.LENGTH_SHORT).show()
                    }
                }

                 */

                val temp3 = API(this.applicationContext,token).getActivitySellTicket(act_Id)
                if(temp3.getString("code")=="001"){
                    val Data = temp3.getJSONArray("result")

                    if(Data.length() == 0){
                        runOnUiThread {
                            //.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                            //view.adapter = TextAdapter("暫時無人參加")
                        }
                    }else{
                        for(i in 0..Data.length()-1){
                            authId.add(Data.getJSONObject(i).getString("authId"))
                            nickName.add(Data.getJSONObject(i).getString("nickName"))
                            if(Data.getJSONObject(i).getString("ticketKinds") == "None"){
                                ticketKinds.add("一般票")
                            }else{
                                ticketKinds.add(Data.getJSONObject(i).getString("ticketKinds"))
                            }

                            mail.add(Data.getJSONObject(i).getString("Mail"))
                            ticketStatus.add(Data.getJSONObject(i).getString("ticketStatus"))
                            Notes.add(Data.getJSONObject(i).getString("Notes"))
                        }
                        runOnUiThread {
                            //view.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                            //view.adapter = EditTicketStatusAdapter(authId, nickName, ticketKinds, ticketStatus, Notes, ::click)
                        }
                    }
                }



                /*sp_ticket.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, View: View, position: Int, id: Long) {
                        authId.clear()
                        nickName.clear()
                        ticketKinds.clear()
                        ticketStatus.clear()
                        Notes.clear()
                        if(position==0){
                            for(i in 0..Data.length()-1){
                                authId.add(Data.getJSONObject(i).getString("authId"))
                                nickName.add(Data.getJSONObject(i).getString("nickName"))
                                ticketKinds.add(Data.getJSONObject(i).getString("ticketKinds"))
                                ticketStatus.add(Data.getJSONObject(i).getString("ticketStatus"))
                                Notes.add(Data.getJSONObject(i).getString("Notes"))
                            }
                            runOnUiThread {
                                //view.adapter = EditTicketStatusAdapter(authId, nickName, ticketKinds, ticketStatus, Notes, ::click)
                            }
                        }else{
                            for(i in 0..Data.length()-1){
                                if(TicketKinds[position].split(":")[0] == Data.getJSONObject(i).getString("ticketKinds")){
                                    authId.add(Data.getJSONObject(i).getString("authId"))
                                    nickName.add(Data.getJSONObject(i).getString("nickName"))
                                    ticketKinds.add(Data.getJSONObject(i).getString("ticketKinds"))
                                    ticketStatus.add(Data.getJSONObject(i).getString("ticketStatus"))
                                    Notes.add(Data.getJSONObject(i).getString("Notes"))
                                }
                                runOnUiThread {
                                    if(authId.size != 0){
                                        //view.adapter = EditTicketStatusAdapter(authId, nickName, ticketKinds, ticketStatus, Notes, ::click)
                                    }else{
                                        //view.adapter = TextAdapter("暫時無人參加")
                                    }
                                }
                            }
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {}
                }

                 */

                runOnUiThread {
                    img_loading.visibility = android.view.View.GONE
                    progress_Bar.visibility = android.view.View.GONE

                    ticketView.adapter = EditTicketPagerAdapter(this,Ticket_Kinds, totalArray,::click,::click_delete,::click_switch)
                    ticketView.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
                    tabLayout.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(ticketView))
                    for(i in Ticket_Kinds){
                        tabLayout.addTab(tabLayout.newTab().setText(i))
                    }
                }
            }
        }).start()
    }

    override fun onResume() {
        super.onResume()
        reload()
    }

    private fun reload() {
        Thread(Runnable {
            run {
                if (totalArray.size == 0) {
                    //val data = this.getSharedPreferences("Data", Context.MODE_PRIVATE)
                    //val authId = data.getString("authId", "error")
                    //val temp1 = API(this.applicationContext, token).getEditActivity(authId)
                    val temp1 = API(this.applicationContext,token).getActivitySellTicket(act_Id)

                    if (temp1.getString("code") == "001") {
                        val Data = temp1.getJSONArray("result")
                        for (i in 0..Data.length() - 1) {
                            totalArray.add(Data.getJSONObject(i))
                            /*
                            if (Data.getJSONObject(i).getString("ticketKinds") == "None") {
                                normalArray.add(Data.getJSONObject(i))
                            } else {
                                studentArray.add(Data.getJSONObject(i))
                            }
                                                         */
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




                } else {
                    totalArray.clear()
                    //normalArray.clear()
                    //studentArray.clear()
                    //val data = this.getSharedPreferences("Data", Context.MODE_PRIVATE)
                    //val authId = data.getString("authId", "error")
                    //val temp1 = API(this.applicationContext, token).getEditActivity(authId)
                    val temp1 = API(this.applicationContext,token).getActivitySellTicket(act_Id)

                    if (temp1.getString("code") == "001") {
                        val Data = temp1.getJSONArray("result")
                        for (i in 0..Data.length() - 1) {
                            totalArray.add(Data.getJSONObject(i))
                            /*
                            if (Data.getJSONObject(i).getString("ticketKinds") == "None") {
                                normalArray.add(Data.getJSONObject(i))
                            } else {
                                studentArray.add(Data.getJSONObject(i))
                            }

                             */
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

                    runOnUiThread {
                        ticketView.adapter!!.notifyDataSetChanged()
                    }
                }
            }
        }).start()
    }

    fun click(AuthId: String, NickName: String, TicketKinds: String, TicketStatus: String, NOtes: String, Mail:String) {
        val myDialog = LayoutInflater.from(this).inflate(R.layout.dialog_edit_ticket_status, null)
        val d = AlertDialog.Builder(this).setView(myDialog)
        val myAlertDialog = d.show()
        //var first = true
        if (TicketKinds == "None") {
            myDialog.tv_ticketKinds.text = "一般票"
        } else {
            myDialog.tv_ticketKinds.text = TicketKinds
        }
        myDialog.tv_nickName.text = NickName
        myDialog.et_text.setText(NOtes)
        myDialog.txt_mail.setText(Mail)
        /*
        myDialog.switch_check.setOnCheckedChangeListener { buttonView, isChecked ->
            if(first){
                first = false
            }else{
                if(isChecked) {
                    AlertDialog.Builder(this)
                        .setTitle("認證用戶?")
                        .setMessage("使用手動驗票\"認證\"該用戶?")
                        .setNegativeButton("取消"){ dialog, which ->
                            first = true
                            myDialog.switch_check.isChecked = false
                        }
                        .setPositiveButton("確定") { dialog, which ->
                            Thread(Runnable {
                                run{
                                    if(API(this.applicationContext,token).checkTicket(act_Id, AuthId).getString("code")=="001"){
                                        for(i in 0..authId.size - 1){
                                            if(AuthId == authId[i]){
                                                ticketStatus.set(i,"1")
                                            }
                                        }
                                        runOnUiThread {
                                            //view.adapter!!.notifyDataSetChanged()
                                            Toast.makeText(this,"成功!",Toast.LENGTH_SHORT).show()
                                        }
                                    }else{
                                        runOnUiThread {
                                            Toast.makeText(this,"失敗!",Toast.LENGTH_SHORT).show()
                                            first = true
                                            myDialog.switch_check.isChecked = false
                                        }
                                    }
                                }
                            }).start()
                        }.show()
                }else{
                    AlertDialog.Builder(this)
                        .setTitle("取消認證用戶?")
                        .setMessage("使用手動驗票\"取消認證\"該用戶?")
                        .setNegativeButton("取消"){ dialog, which ->
                            first = true
                            myDialog.switch_check.isChecked = true
                        }
                        .setPositiveButton("確定") { dialog, which ->
                            if(API(this.applicationContext,token).undoCheckTicket(act_Id, AuthId).getString("code")=="001"){
                                for(i in 0..authId.size - 1){
                                    if(AuthId == authId[i]){
                                        ticketStatus.set(i,"0")
                                    }
                                }
                                runOnUiThread {
                                    //view.adapter!!.notifyDataSetChanged()
                                    Toast.makeText(this,"成功!",Toast.LENGTH_SHORT).show()
                                }
                            }else{
                                runOnUiThread {
                                    Toast.makeText(this,"失敗!",Toast.LENGTH_SHORT).show()
                                    first = true
                                    myDialog.switch_check.isChecked = true
                                }
                            }
                        }.show()
                }
            }
        }

         */
        /*
        if(TicketStatus == "1"){
            myDialog.switch_check.isChecked = true
        }else{
            first = false
        }

         */
        /*
        myDialog.btn_delete.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("刪除票據?")
                .setMessage("這將會讓使用者失去這張票據!")
                .setNegativeButton("取消", null)
                .setPositiveButton("確認刪除") { dialog, which ->
                    Thread(Runnable {
                        run{
                            if(API(this.applicationContext,token).deleteActivitySellTicket(act_Id, AuthId).getString("code")=="001"){
                                runOnUiThread {
                                    Toast.makeText(this,"刪除成功!",Toast.LENGTH_SHORT).show()
                                    for(i in 0..authId.size-1){
                                        if(AuthId == authId[i]){
                                            authId.removeAt(i)
                                            nickName.removeAt(i)
                                            ticketKinds.removeAt(i)
                                            ticketStatus.removeAt(i)
                                            Notes.removeAt(i)
                                        }
                                    }
                                    //view.adapter!!.notifyItemRemoved(position)
                                    tv_people.text = (tv_people.text.toString().toInt()-1).toString()
                                    tv_left.text = (tv_left.text.toString().toInt()+1).toString()
                                    myAlertDialog.dismiss()
                                }
                            }else{
                                runOnUiThread {
                                    Toast.makeText(this,"刪除失敗!",Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }).start()
                }.show()
        }

         */
        myDialog.btn_cancel.setOnClickListener {
            myAlertDialog.dismiss()
        }
        myDialog.btn_done.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("確定修改票券備註?")
                .setNegativeButton("取消", null)


                .setPositiveButton("確認") { dialog, which ->
                    if(API(this.applicationContext,token).editTicketNote(act_Id, AuthId, myDialog.et_text.text.toString()).getString("code")=="001"){
                        for(i in 0..authId.size - 1){
                            if(AuthId == authId[i]){
                                Notes.set(i,myDialog.et_text.text.toString())
                            }
                        }

                        runOnUiThread {
                            //view.adapter!!.notifyDataSetChanged()
                            Toast.makeText(this,"修改成功!",Toast.LENGTH_SHORT).show()
                            myAlertDialog.dismiss()
                        }
                    }else{
                        runOnUiThread {
                            Toast.makeText(this, "修改失敗!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }.show()
        }
    }

    fun click_delete(AuthId: String) {
        AlertDialog.Builder(this)
            .setTitle("刪除票據?")
            .setMessage("這將會讓使用者失去這張票據!")
            .setNegativeButton("取消", null)
            .setPositiveButton("確認刪除") { dialog, which ->
                Thread(Runnable {
                    run{
                        if(API(this.applicationContext,token).deleteActivitySellTicket(act_Id, AuthId).getString("code")=="001"){
                            runOnUiThread {
                                Toast.makeText(this,"刪除成功!",Toast.LENGTH_SHORT).show()
                                for(i in 0..authId.size-1){
                                    if(AuthId == authId[i]){
                                        authId.removeAt(i)
                                        nickName.removeAt(i)
                                        ticketKinds.removeAt(i)
                                        ticketStatus.removeAt(i)
                                        Notes.removeAt(i)
                                        mail.removeAt(i)
                                    }
                                }
                                //view.adapter!!.notifyItemRemoved(position)
                                tv_people.text = (tv_people.text.toString().toInt()-1).toString()
                                tv_left.text = (tv_left.text.toString().toInt()+1).toString()
                                //myAlertDialog.dismiss()
                            }
                        }else{
                            runOnUiThread {
                                Toast.makeText(this,"刪除失敗!",Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }).start()
            }.show()
    }

    var first = false

    fun click_switch(AuthId: String, isChecked: Boolean, TicketStatus: String, switch_check: Switch) {


        if(first){
            first = false
        }else{
            if(isChecked) {
                AlertDialog.Builder(this)
                    .setTitle("認證用戶?")
                    .setMessage("使用手動驗票\"認證\"該用戶?")
                    .setNegativeButton("取消"){ dialog, which ->
                        first = true
                        switch_check.isChecked = false
                    }
                    .setPositiveButton("確定") { dialog, which ->
                        Thread(Runnable {
                            run{
                                if(API(this.applicationContext,token).checkTicket(act_Id, AuthId).getString("code")=="001"){
                                    for(i in 0..authId.size - 1){
                                        if(AuthId == authId[i]){
                                            ticketStatus.set(i,"1")
                                        }
                                    }
                                    runOnUiThread {
                                        //view.adapter!!.notifyDataSetChanged()
                                        Toast.makeText(this,"成功!",Toast.LENGTH_SHORT).show()
                                    }
                                }else{
                                    runOnUiThread {
                                        Toast.makeText(this,"失敗!",Toast.LENGTH_SHORT).show()
                                        first = true
                                        switch_check.isChecked = false
                                    }
                                }
                            }
                        }).start()
                    }.show()
            }else{
                AlertDialog.Builder(this)
                    .setTitle("取消認證用戶?")
                    .setMessage("使用手動驗票\"取消認證\"該用戶?")
                    .setNegativeButton("取消"){ dialog, which ->
                        first = true
                        switch_check.isChecked = true
                    }
                    .setPositiveButton("確定") { dialog, which ->
                        if(API(this.applicationContext,token).undoCheckTicket(act_Id, AuthId).getString("code")=="001"){
                            for(i in 0..authId.size - 1){
                                if(AuthId == authId[i]){
                                    ticketStatus.set(i,"0")
                                }
                            }
                            runOnUiThread {
                                //view.adapter!!.notifyDataSetChanged()
                                Toast.makeText(this,"成功!",Toast.LENGTH_SHORT).show()
                            }
                        }else{
                            runOnUiThread {
                                Toast.makeText(this,"失敗!",Toast.LENGTH_SHORT).show()
                                first = true
                                switch_check.isChecked = true
                            }
                        }
                    }.show()
            }
        }


    }
}
