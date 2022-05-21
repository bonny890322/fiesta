package com.network.fiesta

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_edit_lottte.*
import kotlinx.android.synthetic.main.dialog_lotte.view.*

class EditLottteActivity : AppCompatActivity() {

    val prizeArray: ArrayList<String> = ArrayList()
    val lotteIdArray: ArrayList<String> = ArrayList()
    val accountIdArray: ArrayList<String> = ArrayList()
    var act_Id = ""
    var token = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_lottte)

        val intent = getIntent()
        act_Id = intent.getStringExtra("act_Id")
        token = getSharedPreferences("Data", MODE_PRIVATE).getString("token", "token")!!

        Thread(Runnable {
            run{
                val lotte = API(this.applicationContext,token).getLotte(act_Id)
                if(lotte.getString("code") == "001"){

                    val Data = lotte.getJSONArray("result")
                    for(i in 0..Data.length()-1){
                        prizeArray.add(Data.getJSONObject(i).getString("Prize"))
                        lotteIdArray.add(Data.getJSONObject(i).getString("Id"))
                        accountIdArray.add(Data.getJSONObject(i).getString("accountId"))
                    }
                    runOnUiThread {
                        lotteView.layoutManager = GridLayoutManager(this,2)
                        lotteView.adapter = LotteAdapter(this, prizeArray, lotteIdArray, accountIdArray, act_Id, ::refresh)
                    }
                }
            }
        }).start()

        btn_add.setOnClickListener{
            val myDialog = LayoutInflater.from(this).inflate(R.layout.dialog_lotte,null)
            val d = AlertDialog.Builder(this).setView(myDialog)
            val myAlertDialog = d.show()
            myDialog.btn_cancel.setOnClickListener{
                myAlertDialog.dismiss()
            }
            myDialog.btn_done.setOnClickListener{
                myAlertDialog.dismiss()

                if(myDialog.txt_prize.text.toString() != ""){
                    Thread(Runnable {
                        run{
                            API(this.applicationContext,token).uploadLotte(myDialog.txt_prize.text.toString(),act_Id)
                            refresh()
                        }
                    }).start()

                    lotteView.layoutManager = GridLayoutManager(this,2)
                    lotteView.adapter = LotteAdapter(this, prizeArray, lotteIdArray, accountIdArray, act_Id, ::refresh)

                }else{
                    Toast.makeText(this,"請輸入資料", Toast.LENGTH_SHORT).show()
                }

            }
        }
    }

    fun refresh(){
        val temp = API(this.applicationContext,token).getLotte(act_Id)
        prizeArray.clear()
        lotteIdArray.clear()
        accountIdArray.clear()
        if(temp.getString("code") == "001") {
            val lotte = temp.getJSONArray("result")
            if(lotte.length()!=0){
                for (i in 0..lotte.length() - 1) {
                    prizeArray.add(lotte.getJSONObject(i).getString("Prize"))
                    lotteIdArray.add(lotte.getJSONObject(i).getString("Id"))
                    accountIdArray.add(lotte.getJSONObject(i).getString("accountId"))
                }
            }
            runOnUiThread {
                lotteView.layoutManager = GridLayoutManager(this,2)
                lotteView.adapter = LotteAdapter(this, prizeArray, lotteIdArray, accountIdArray, act_Id, ::refresh)
            }
        }else if(temp.getString("code") == "013"){
            runOnUiThread{
                lotteView.layoutManager = GridLayoutManager(this,2)
                lotteView.adapter = LotteAdapter(this, prizeArray, lotteIdArray, accountIdArray, act_Id, ::refresh)
            }
        }else{
            runOnUiThread{
                Toast.makeText(this, "網路連接失敗", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
