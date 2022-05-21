package com.network.fiesta

import android.app.Activity
import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray

class MemberAdapter(val Parent: Activity, val Data: JSONArray, val groupId: String, val token: String, val authId: String) : RecyclerView.Adapter<MemberAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.row_member, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return Data.length()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (Data.getJSONObject(position).getString("authId") != authId) {
            holder.authId = Data.getJSONObject(position).getString("authId")
            Thread(Runnable {
                run {
                    if (Data.getJSONObject(position).getString("Photo") != "None") {
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
                }
            }).start()
            holder.tv_name.text = Data.getJSONObject(position).getString("nickName")
            holder.btn_kick.setOnClickListener {
                AlertDialog.Builder(Parent)
                    .setTitle("確定要踢出成員?")
                    .setNegativeButton("否", null)
                    .setPositiveButton("是") { dialog, which ->
                        API(Parent.applicationContext,token).deleteGroupMember(groupId, holder.authId)
                        Data.remove(position)
                        this.notifyDataSetChanged()
                    }.show()
            }
        } else {
            holder.img.visibility = View.GONE
            holder.tv_name.visibility = View.GONE
            holder.btn_kick.visibility = View.GONE
        }
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img = itemView.findViewById<ImageView>(R.id.img)
        val tv_name = itemView.findViewById<TextView>(R.id.tv_name)
        val btn_kick = itemView.findViewById<Button>(R.id.btn_kick)
        var authId = ""
    }
}