package com.network.fiesta

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.dialog_group_member.view.*
import org.json.JSONArray

class GroupAdapter(val Parent:Activity, val Data:JSONArray): RecyclerView.Adapter<GroupAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.row_group, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return Data.length()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tv_title.text = Data.getJSONObject(position).getString("groupName")

        val temp = Parent.getSharedPreferences("Data", AppCompatActivity.MODE_PRIVATE)
        val token = temp.getString("token", "token")!!

        val temp2 = API(Parent.applicationContext,token).selectGroupMember(Data.getJSONObject(position).getString("groupId"))
        if (temp2.getString("code") == "001") {
            var nickname = ""
            val temp3 = temp2.getJSONArray("result")
            for(i in 0..temp3.length()-1){
                nickname += temp3.getJSONObject(i).getString("nickName")
                if(i != temp3.length()-1){
                    nickname += "、"
                }
            }

            holder.txt_member.setText(nickname)
        } else {
            Toast.makeText(Parent, "發生未知的錯誤", Toast.LENGTH_SHORT).show()
        }

        /*
        if(Data.getJSONObject(position).getString("timeStatus") != "1") {
            holder.tv_status.text = "是"
        }else{
            holder.tv_status.text = "否"
        }
        if(Data.getJSONObject(position).getString("Photo") != "None"){
            Thread(Runnable {
                run{
                    try {
                        val bitmap = API(Parent.applicationContext).SearchIMG(Data.getJSONObject(position).getString("Photo"))
                        Parent.runOnUiThread {
                            holder.img.setImageBitmap(bitmap)
                        }
                    }catch (e:Exception){
                        Parent.runOnUiThread {
                            holder.img.setImageResource(R.drawable.ui_fiestalogo)
                        }
                    }
                }
            }).start()
        }

         */
        holder.itemView.setOnClickListener {
            val intent = Intent(Parent, GroupActivity::class.java)
                .putExtra("groupId",Data.getJSONObject(position).getString("groupId"))
                .putExtra("Authority",Data.getJSONObject(position).getString("Authority"))
            Parent.startActivity(intent)
        }
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv_title = itemView.findViewById<TextView>(R.id.tv_title)
        val txt_member = itemView.findViewById<TextView>(R.id.txt_member)
        //val img = itemView.findViewById<ImageView>(R.id.img)
    }
}