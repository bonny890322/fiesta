package com.network.fiesta

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.dialog_lotter.view.*

class LotteAdapter(val Parent: Activity, val prizeArray: ArrayList<String>, val lotteIdArray: ArrayList<String>, val accountIdArray: ArrayList<String>, val actId: String?, val refresh:() -> Unit) : RecyclerView.Adapter<LotteAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LotteAdapter.ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.row_lotte, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return  prizeArray.size
    }

    override fun onBindViewHolder(holder: LotteAdapter.ViewHolder, position: Int) {
            holder.prize.text = prizeArray[position]
            if (accountIdArray[position] != "None") {
                Thread(Runnable {
                    run {
                        Parent.runOnUiThread {
                            var name = API(Parent.applicationContext,holder.token).getLottery(actId).getJSONArray("result").getJSONObject(0).getString("nickName")
                            var ticketKinds = API(Parent.applicationContext,holder.token).getLottery(actId).getJSONArray("result").getJSONObject(0).getString("ticketKinds")
                            var Mail = API(Parent.applicationContext,holder.token).getLottery(actId).getJSONArray("result").getJSONObject(0).getString("Mail")
                            //holder.lottery.text = name.toString()
                            holder.get.isEnabled = false
                            holder.get.text = "已抽出"
                            holder.itemView.setOnClickListener{

                                val myDialog = LayoutInflater.from(Parent).inflate(R.layout.dialog_lotter,null)
                                val d = AlertDialog.Builder(Parent).setView(myDialog)
                                val myAlertDialog = d.show()
                                myDialog.txt_name.setText(name)
                                if(ticketKinds == "null"){
                                    myDialog.txt_ticket.setText("一般票")
                                }
                                myDialog.txt_mail.setText(Mail)

                                Parent.runOnUiThread {
                                    myDialog.btn_cancel.setOnClickListener {

                                        Thread(Runnable {
                                            run {
                                                val Data = API(Parent.applicationContext,holder.token).getLottery(actId)
                                                if (Data.getString("code") == "001") {
                                                    name = Data.getJSONArray("result")
                                                        .getJSONObject(0).getString("nickName")
                                                    ticketKinds = Data.getJSONArray("result").getJSONObject(0).getString("ticketKinds")
                                                    Mail = Data.getJSONArray("result").getJSONObject(0).getString("Mail")
                                                    Parent.runOnUiThread {
                                                        myDialog.txt_name.setText(name)
                                                        if (ticketKinds == "null") {
                                                            myDialog.txt_ticket.setText("一般票")
                                                        }
                                                        myDialog.txt_mail.setText(Mail)
                                                    }
                                                }else if (Data.getString("code") == "021") {
                                                    Parent.runOnUiThread {
                                                        Toast.makeText(Parent, "活動尚無人進場", Toast.LENGTH_SHORT).show()
                                                    }
                                                } else if (Data.getString("code") == "013") {
                                                    Parent.runOnUiThread {
                                                        Toast.makeText(Parent, "活動尚無人參加", Toast.LENGTH_SHORT).show()
                                                    }
                                                } else {
                                                    Parent.runOnUiThread {
                                                        Toast.makeText(Parent, "網路連接發生問題", Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                            }
                                        }).start()

                                    }
                                    myDialog.btn_done.setOnClickListener {
                                        myAlertDialog.dismiss()
                                        Thread(Runnable {
                                            run {
                                                API(Parent.applicationContext, holder.token).updateLotte(lotteIdArray[position], name)
                                            }
                                        }).start()
                                    }
                                            }

                            }

                        }
                    }
                }).start()
            }



            holder.get.setOnClickListener {

                AlertDialog.Builder(Parent)
                    .setMessage("確定要抽出中獎者嗎")
                    .setNegativeButton("取消", null)
                    .setPositiveButton("確定") { dialog, which ->
                        dialog.cancel()
                        Thread(Runnable {
                            run {

                                var Data = API(Parent.applicationContext,holder.token).getLottery(actId)
                                if (Data.getString("code") == "001") {
                                    var name = Data.getJSONArray("result").getJSONObject(0).getString("nickName")
                                    var ticketKinds = Data.getJSONArray("result").getJSONObject(0).getString("ticketKinds")
                                    var Mail = Data.getJSONArray("result").getJSONObject(0).getString("Mail")

                                    //跳出得獎者dialog
                                    Parent.runOnUiThread {
                                        val myDialog = LayoutInflater.from(Parent).inflate(R.layout.dialog_lotter,null)
                                        val d = AlertDialog.Builder(Parent).setView(myDialog)
                                        val myAlertDialog = d.show()

                                        myDialog.txt_name.setText(name)
                                        if(ticketKinds == "null"){
                                            myDialog.txt_ticket.setText("一般票")
                                        }
                                        myDialog.txt_mail.setText(Mail)

                                        myDialog.btn_cancel.setOnClickListener{
                                            Thread(Runnable {
                                                run {
                                                    if (Data.getString("code") == "001") {
                                                        name = Data.getJSONArray("result")
                                                            .getJSONObject(0).getString("nickName")
                                                        ticketKinds = Data.getJSONArray("result").getJSONObject(0).getString("ticketKinds")
                                                        Mail = Data.getJSONArray("result").getJSONObject(0).getString("Mail")
                                                        Parent.runOnUiThread {
                                                            myDialog.txt_name.setText(name)
                                                            if (ticketKinds == "null") {
                                                                myDialog.txt_ticket.setText("一般票")
                                                            }
                                                            myDialog.txt_mail.setText(Mail)
                                                        }
                                                    }else if (Data.getString("code") == "021") {
                                                        Parent.runOnUiThread {
                                                            Toast.makeText(Parent, "活動尚無人進場", Toast.LENGTH_SHORT).show()
                                                        }
                                                    } else if (Data.getString("code") == "013") {
                                                        Parent.runOnUiThread {
                                                            Toast.makeText(Parent, "活動尚無人參加", Toast.LENGTH_SHORT).show()
                                                        }
                                                    } else {
                                                        Parent.runOnUiThread {
                                                            Toast.makeText(Parent, "網路連接發生問題", Toast.LENGTH_SHORT).show()
                                                        }
                                                    }
                                                }
                                            }).start()
                                        }
                                        myDialog.btn_done.setOnClickListener{
                                            myAlertDialog.dismiss()
                                            Thread(Runnable {
                                                run {
                                            API(Parent.applicationContext,holder.token).updateLotte(lotteIdArray[position], Data.getJSONArray("result").getJSONObject(0).getString("authId"))
                                                }
                                            }).start()
                                            //holder.lottery.text = name.toString()
                                            holder.get.isEnabled = false
                                            holder.get.text = "已抽出"
                                        }
                                    }



                                } else if (Data.getString("code") == "021") {
                                    Parent.runOnUiThread {
                                        Toast.makeText(Parent, "活動尚無人進場", Toast.LENGTH_SHORT).show()
                                    }
                                } else if (Data.getString("code") == "013") {
                                    Parent.runOnUiThread {
                                        Toast.makeText(Parent, "活動尚無人參加", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Parent.runOnUiThread {
                                        Toast.makeText(Parent, "網路連接發生問題", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }).start()

                    }
                    .show()
            }

            holder.delete.setOnClickListener {
                AlertDialog.Builder(Parent)
                    .setMessage("確定要刪除獎項嗎")
                    .setNegativeButton("取消", null)
                    .setPositiveButton("確定") { dialog, which ->
                        dialog.cancel()
                        Thread(Runnable {
                            run {
                                if (API(Parent.applicationContext,holder.token).deleteLotte(lotteIdArray[position]).getString("code") == "001") {
                                    refresh()
                                } else {
                                    Parent.runOnUiThread {
                                        Toast.makeText(Parent, "網路連接發生問題", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }).start()
                    }
                    .show()
            }
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val prize = view.findViewById<TextView>(R.id.txt_prize)
        val get = view.findViewById<Button>(R.id.btn_get)
        //val lottery = view.findViewById<TextView>(R.id.txt_lottery)
        val delete = view.findViewById<Button>(R.id.btn_delete)
        val token = Parent.getSharedPreferences("Data", Context.MODE_PRIVATE).getString("token", "token")!!
    }
}