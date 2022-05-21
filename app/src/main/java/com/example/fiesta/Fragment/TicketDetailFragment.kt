package com.network.fiesta

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_ticket_detail.*
import org.json.JSONObject

class TicketDetailFragment(val act_Id:String) : Fragment() {
    var Id = ""
    var token = ""
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_ticket_detail, container, false )
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Id = activity!!.getSharedPreferences("Data", AppCompatActivity.MODE_PRIVATE).getString("authId", "authId")!!
        token = activity!!.getSharedPreferences("Data", AppCompatActivity.MODE_PRIVATE).getString("token", "token")!!
        var exp = true
        img_loading.visibility = View.VISIBLE
        progress_Bar.visibility = View.VISIBLE

        Thread(Runnable {
            run {
                val temp = API(context,token).selectbuyTicket(Id)
                var result = JSONObject()
                if (temp.getString("code") == "001") {
                    for(i in 0..temp.getJSONArray("result").length()-1){
                        if(temp.getJSONArray("result").getJSONObject(i).getString("act_Id") == act_Id){
                            exp = false
                            result = temp.getJSONArray("result").getJSONObject(i)
                            if (result.getString("Photo") != "None") {
                                try {
                                    val bitmap = API(context).SearchIMG(result.getString("Photo"))
                                    activity!!.runOnUiThread {
                                        img.setImageBitmap(bitmap)
                                    }
                                }catch (e:Exception){
                                    activity!!.runOnUiThread {
                                        img.setImageResource(R.drawable.ui_fiestalogo)
                                    }
                                }
                            }
                            activity!!.runOnUiThread {
                                tv_title.text = result.getString("act_Name")
                                tv_date.text = result.getString("startTime")+" ~ "+result.getString("endTime")
                                tv_location.text = result.getString("Location")
                                tv_text.text = result.getString("act_Description")
                                frameLayout.visibility = View.VISIBLE
                                tv_text.setOnClickListener {
                                    tv_text.toggle()
                                }
                            }
                        }
                    }
                    btn_create.setOnClickListener {
                        img_loading.visibility = View.VISIBLE
                        progress_Bar.visibility = View.VISIBLE
                        Thread(Runnable {
                            run{
                                try {
                                    val temp = Base64.decode(API(context,token).QRCode(act_Id,Id), 0)
                                    val bitmap = BitmapFactory.decodeByteArray(temp, 0, temp.size)
                                    activity!!.runOnUiThread {
                                        qrcode.setImageBitmap(bitmap)
                                        img_loading.visibility = View.GONE
                                        progress_Bar.visibility = View.GONE
                                    }
                                }catch (e:Exception){
                                    activity!!.runOnUiThread {
                                        Toast.makeText(activity!!,R.string.connect_fail,Toast.LENGTH_SHORT).show()
                                        img_loading.visibility = View.GONE
                                        progress_Bar.visibility = View.GONE
                                    }
                                }
                            }
                        }).start()
                    }

                }else if(temp.getString("code")=="012"){

                }else{
                    activity!!.runOnUiThread {
                        Toast.makeText(activity!!,"網路連線失敗", Toast.LENGTH_SHORT).show()
                    }
                }
                if(exp){
                    val temp2 = API(context,token).selectexpTicket()
                    if(temp2.getString("code")=="001") {
                        for(i in 0..temp2.getJSONArray("result").length()-1){
                            if(temp2.getJSONArray("result").getJSONObject(i).getString("act_Id") == act_Id){
                                result = temp2.getJSONArray("result").getJSONObject(i)
                                if (result.getString("Photo") != "None") {
                                    val bitmap = API(context).SearchIMG(result.getString("Photo"))
                                    activity!!.runOnUiThread {
                                        img.setImageBitmap(bitmap)
                                    }
                                }
                                activity!!.runOnUiThread {
                                    tv_title.text = result.getString("act_Name")
                                    tv_date.text = result.getString("startTime")+" ~ "+result.getString("endTime")
                                    tv_location.text = result.getString("Location")
                                    tv_text.text = result.getString("act_Description")
                                    btn_create.text = "填寫活動反饋"
                                    temp1.visibility = View.GONE
                                    frameLayout.visibility = View.VISIBLE
                                    tv_text.setOnClickListener {
                                        tv_text.toggle()
                                    }
                                }
                            }
                        }
                        btn_create.setOnClickListener {
                            val intent = Intent(activity!!, TicketFeedBack::class.java)
                                .putExtra("act_Id", act_Id)
                                .putExtra("authId", Id)
                                .putExtra("token", token)
                            startActivity(intent)
                        }
                    }
                }
            }
            activity!!.runOnUiThread {
                img_loading.visibility = View.GONE
                progress_Bar.visibility = View.GONE
            }
        }).start()
    }

    override fun onResume() {
        super.onResume()
        Handler().postDelayed({
            val temp3 = API(context,token).getActivityScoreByAuth(Id)
            if(temp3.getString("code")=="001"){
                val result = temp3.getJSONArray("result")
                for(i in 0..result.length()-1){
                    if(result.getJSONObject(i).getString("act_Id") == act_Id){
                        btn_create.isEnabled = false
                        break
                    }
                }
            }
        },0)
    }
}