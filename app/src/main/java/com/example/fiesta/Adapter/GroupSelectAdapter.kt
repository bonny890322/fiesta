package com.network.fiesta

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import org.json.JSONArray

class GroupSelectAdapter(val Parent: Activity,val NameArray: ArrayList<String>, val groupIdArray: ArrayList<String>, val isSelect: ArrayList<Boolean>, val click: (groupIde: String, position: Int) -> Unit): RecyclerView.Adapter<GroupSelectAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.row_select_group, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return NameArray.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tv_name.text = NameArray[position]
        holder.tv_member.text = NameArray[position]
        if(isSelect[position]==true){
            holder.background.setBackgroundResource(R.drawable.shape_round_corner_orange_28)
            holder.tv_name.setTextColor(Parent.resources.getColor(R.color.white))
        }else{
            holder.background.setBackgroundResource(R.drawable.shape_round_corner_white_28)
            holder.tv_name.setTextColor(Parent.resources.getColor(R.color.black))
        }
        holder.itemView.setOnClickListener {
            click(groupIdArray[position],position)
        }
    }


    inner class ViewHolder(View: View) : RecyclerView.ViewHolder(View) {
        val tv_name = View.findViewById<TextView>(R.id.tv_name)
        val tv_member = View.findViewById<TextView>(R.id.tv_member)
        val background = View.findViewById<ConstraintLayout>(R.id.background)
    }
}