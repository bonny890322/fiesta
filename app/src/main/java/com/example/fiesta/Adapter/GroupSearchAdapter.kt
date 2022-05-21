package com.network.fiesta

import android.app.Activity
import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.drawToBitmap
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_menu_group.*

class GroupSearchAdapter(val Parent: Activity, val Name: String, val AuthId: String,val Photo: String, val click:(name:String, authid:String, bitmap:Bitmap ) -> Unit): RecyclerView.Adapter<GroupSearchAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.row_groupsearch, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return 1
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tv_name.text = Name
        holder.AuthId = AuthId
        if (Photo != "None") {
            Thread(Runnable {
                run{
                    try {
                        holder.bitmap  = API(Parent.applicationContext).SearchIMG(Photo)!!
                        Parent.runOnUiThread {
                            holder.img.setImageBitmap(holder.bitmap)
                            holder.isBitmap = true
                        }
                    }catch (e:Exception){
                        Parent.runOnUiThread {
                            holder.img.setImageResource(R.drawable.ui_fiestalogo)
                        }
                    }
                }
            }).start()
        }
        holder.btn_select.setOnClickListener {
            Parent.findViewById<ImageView>(R.id.img_loading).visibility = View.VISIBLE
            Parent.findViewById<ProgressBar>(R.id.progress_Bar).visibility = View.VISIBLE
            if(Photo != "None"){
                Thread(Runnable {
                    run{
                        while (true){
                            if(holder.isBitmap){
                                Parent.runOnUiThread {
                                    click(holder.tv_name.text.toString(), AuthId, holder.bitmap)
                                    Parent.findViewById<ImageView>(R.id.img_loading).visibility = View.GONE
                                    Parent.findViewById<ProgressBar>(R.id.progress_Bar).visibility = View.GONE
                                }
                                break
                            }else{
                                Thread.sleep(200)
                            }
                        }
                    }
                }).start()
            }else{
                Parent.runOnUiThread {
                    click(holder.tv_name.text.toString(), AuthId, holder.bitmap)
                    Parent.findViewById<ImageView>(R.id.img_loading).visibility = View.GONE
                    Parent.findViewById<ProgressBar>(R.id.progress_Bar).visibility = View.GONE
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv_name = itemView.findViewById<TextView>(R.id.tv_name)
        val img = itemView.findViewById<ImageView>(R.id.img)
        val btn_select = itemView.findViewById<Button>(R.id.btn_select)
        lateinit var bitmap: Bitmap
        var AuthId = ""
        var isBitmap = false
    }
}