package com.network.fiesta

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray

class AdvanceEditAdapter( val Parent: Activity,val Data1: JSONArray, val Data2: JSONArray, val click:(title:String, text:TextView) -> Unit ): RecyclerView.Adapter<AdvanceEditAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.row_personal_edit, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return Data1.length()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tv_title.text = Data1.getString(position)
        holder.tv_text.text = Data2.getString(position)
        run{
            holder.tv_text.setOnClickListener{
                click(holder.tv_title.text.toString(),holder.tv_text)
            }
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tv_title = view.findViewById<TextView>(R.id.tv_title)
        val tv_text = view.findViewById<TextView>(R.id.tv_text)
    }

}