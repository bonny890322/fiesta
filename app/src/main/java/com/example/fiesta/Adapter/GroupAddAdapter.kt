package com.network.fiesta

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.lang.Exception

class GroupAddAdapter(val NameArray: ArrayList<String>, val AuthIdArray: ArrayList<String>, val BitmapArray: ArrayList<Bitmap>, val click:(authid:String) -> Unit): RecyclerView.Adapter<GroupAddAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.row_groupmember, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return NameArray.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tv_name.text = NameArray[position]
        holder.AuthId = AuthIdArray[position]
        try{
            holder.img.setImageBitmap(BitmapArray[position])
        }catch (e:Exception){ }

        holder.btn_delete.setOnClickListener {
            click(holder.AuthId)
        }
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv_name = itemView.findViewById<TextView>(R.id.tv_name)
        val img = itemView.findViewById<PrettyImageView>(R.id.img)
        val btn_delete = itemView.findViewById<Button>(R.id.btn_delete)
        var AuthId = ""
    }
}