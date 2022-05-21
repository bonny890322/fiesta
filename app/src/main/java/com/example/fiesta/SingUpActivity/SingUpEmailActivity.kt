package com.network.fiesta

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_singup_email.*
import kotlin.collections.ArrayList

class SingUpEmailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_singup_email)

        val school = ArrayList<String>()
        val schoolclass = ArrayList<String>()
        val schooleng = ArrayList<String>()

        Thread(Runnable {
            run {
                val temp = API(this.applicationContext).getSchoolData()
                school.add("--請選擇學校--")
                if (temp.getString("code") == "001") {
                    val Data = temp.getJSONArray("result")
                    for (i in 0..Data.getJSONArray(0).length() - 2) {
                        schooleng.add(Data.getJSONArray(0).getString(i))
                    }
                    for (i in 0..Data.getJSONArray(1).length() - 2) {
                        school.add(Data.getJSONArray(1).getString(i))
                    }
                }
                runOnUiThread {
                    sp_school.adapter = ArrayAdapter(this, R.layout.spinner_item, school)
                }
            }
        }).start()

        switch_school.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                view_school.visibility = View.VISIBLE
            }else{
                view_school.visibility = View.GONE
                tv_emailaddress.text = ""
            }
        }

        sp_school.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if (position != 0) {
//                    tv_emailaddress.text = "@${schooleng.get(position - 1)}.edu.tw"
                    Thread(Runnable {
                        run{
                            schoolclass.clear()
                            schoolclass.add("--請選擇系所--")
                            val temp = API(this@SingUpEmailActivity.applicationContext).getSchoolClassData(school.get(position))
                            if(temp.getString("code") == "001"){
                                val Data = temp.getJSONArray("result")
                                for(i in 0..Data.length()-1){
                                    schoolclass.add(Data.getString(i))
                                }
                                runOnUiThread {
                                    sp_class.adapter = ArrayAdapter(this@SingUpEmailActivity, R.layout.spinner_item, schoolclass)
                                }
                            }else{
                                runOnUiThread {
                                    Toast.makeText(this@SingUpEmailActivity,R.string.connect_fail,Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }).start()
                } else {
                    tv_emailaddress.text = ""
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        btn_continue.setOnClickListener {
            if(switch_school.isChecked){
                if (et_email.text.toString() != "" && sp_school.selectedItemPosition != 0 && sp_class.selectedItemPosition != 0) {
                    val intent = Intent(this, SingUpActivity::class.java)
                    intent.putExtra("school", sp_school.selectedItem.toString())
                    intent.putExtra("department", sp_class.selectedItem.toString())
                    intent.putExtra("email", et_email.text.toString())
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "請輸入及選擇完整資訊", Toast.LENGTH_SHORT).show()
                }
            }else{
                if (et_email.text.toString() != "" ) {
                    val intent = Intent(this, SingUpActivity::class.java)
                    intent.putExtra("school", "")
                    intent.putExtra("schoolclass", "")
                    intent.putExtra("email", et_email.text.toString())
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "請輸入及選擇完整資訊", Toast.LENGTH_SHORT).show()
                }
            }

        }

        tv_back.setOnClickListener {
            finish()
        }
    }

}
