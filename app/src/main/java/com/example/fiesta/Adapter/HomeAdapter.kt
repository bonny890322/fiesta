package com.network.fiesta

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray


class HomeAdapter(val MenuArray: JSONArray,val Parent:Activity):RecyclerView.Adapter<HomeAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val Menu: View = LayoutInflater.from(parent.context).inflate(R.layout.row_menu,parent,false)
        return ViewHolder(Menu)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val temp = MenuArray.getJSONObject(position)
        holder.tv_title.text = temp.getString("act_Name")

        holder.tv_date.text = temp.getString("startTime")
        if(temp.getString("Photo")!="None"){
            Thread(Runnable {
                run{
                    try {
                        val bitmap = API(Parent.applicationContext).SearchIMG(temp.getString("Photo"))
                        Parent.runOnUiThread {
                            holder.ImgView.setImageBitmap(bitmap)
                        }
                    }catch (e:Exception){
                        Parent.runOnUiThread {
                            holder.ImgView.setImageResource(R.drawable.ui_fiestalogo)
                        }
                    }
                }
            }).start()
        }

        val Id = temp.getString("Id")
        try {
            var tag = ""
            val tmp = temp.getJSONArray("Tag")
            for (i in 0..tmp.length() - 1) {
                if (i == tmp.length() - 1) {
                    tag += " #" +tmp[i].toString()
                } else {
                    tag += "#" + tmp[i].toString()
                }
            }
            holder.tv_tag.text = tag
        } catch (e: Exception) {
            holder.tv_tag.text = temp.getString("Tag")
        }
        holder.tv_groupname.text = temp.getString("groupName")
        holder.tv_address.text = temp.getString("Location")
        Thread(Runnable {
            run{
                if (temp.getString("groupPhoto") != "None" && temp.getString("groupPhoto") != "") {
                    val bitmap = API(Parent.applicationContext).SearchIMG(temp.getString("groupPhoto"))
                    Parent.runOnUiThread {
                        holder.img_group.setImageBitmap(bitmap)
                    }
                }else{
                    Parent.runOnUiThread {
                        holder.img_group.setImageResource(R.drawable.ui_fiestalogo)
                    }
                }

            }
        }).start()
        holder.view.setOnClickListener {
            val intent = Intent(holder.view.context, TicketBuyActivity::class.java).putExtra("Id", Id)
            Parent.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return MenuArray.length()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    class ViewHolder(View: View) : RecyclerView.ViewHolder(View) {
        val tv_title: TextView = View.findViewById(R.id.tv_title)
        val tv_tag: TextView = View.findViewById(R.id.tv_tag)
        val tv_date: TextView = View.findViewById(R.id.tv_date)
        val ImgView: ImageView = View.findViewById(R.id.img)
        val img_group: ImageView = View.findViewById(R.id.img_group)
        val tv_groupname: TextView = View.findViewById(R.id.tv_groupname)
        val tv_address: TextView = View.findViewById(R.id.tv_address)
        val view = View
    }
}