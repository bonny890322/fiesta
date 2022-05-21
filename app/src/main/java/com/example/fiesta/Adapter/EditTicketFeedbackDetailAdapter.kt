package com.network.fiesta

import android.app.Activity
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_menu_personal_edit.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class EditTicketFeedbackDetailAdapter(val Parent: Activity, val stars: ArrayList<String>, val detail: ArrayList<String>, val date: ArrayList<String>, val nickName: ArrayList<String>, val Photo: ArrayList<String>): RecyclerView.Adapter<EditTicketFeedbackDetailAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.row_ticket_feedback_detail, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return stars.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        Thread(Runnable {
            run{
                try {
                    holder.bitmap  = API(Parent.applicationContext).SearchIMG(Photo.get(position))!!
                    Parent.runOnUiThread {
                        holder.img.setImageBitmap(holder.bitmap)
                    }
                }catch (e:Exception){
                    Parent.runOnUiThread {
                        holder.img.setImageResource(R.drawable.ui_fiestalogo)
                    }
                }
            }
        }).start()


            holder.rb_stars.rating = stars.get(position).toFloat()
            holder.et_detail.setText(detail.get(position))
            val day = date.get(position).split("-")
            holder.txt_date.setText(day[1] + "月" + day[2] +"日")
            holder.txt_name.setText(nickName.get(position))
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var bitmap: Bitmap
        val rb_stars = itemView.findViewById<RatingBar>(R.id.rb_stars)
        val et_detail = itemView.findViewById<TextView>(R.id.et_detail)
        val txt_date = itemView.findViewById<TextView>(R.id.txt_date)
        val txt_name = itemView.findViewById<TextView>(R.id.txt_name)
        val img = itemView.findViewById<ImageView>(R.id.img)
    }
}