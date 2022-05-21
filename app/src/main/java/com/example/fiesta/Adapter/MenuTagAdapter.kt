package com.network.fiesta

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray

class MenuTagAdapter(val TagArray: JSONArray,val ImgArray: JSONArray):RecyclerView.Adapter<MenuTagAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val Menu: View = LayoutInflater.from(parent.context).inflate(R.layout.row_menutag,parent,false)
        return ViewHolder(Menu)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.Tag.text=TagArray.getString(position)
        holder.Img.setImageBitmap(ImgArray[position] as Bitmap?)
    }

    override fun getItemCount(): Int {
        return TagArray.length()
    }

    class ViewHolder(View: View):RecyclerView.ViewHolder(View){

        val Tag:TextView = View.findViewById(R.id.tv_tag)
        val Img:ImageView = View.findViewById(R.id.img)
        init {
            View.setOnClickListener {
                val intent = Intent(View.context, TagActivity::class.java)
                intent.putExtra("Tag", Tag.text)
                View.context.startActivity(intent)
            }
        }
    }
}