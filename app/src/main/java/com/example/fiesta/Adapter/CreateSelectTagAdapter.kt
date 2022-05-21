package com.network.fiesta

import android.app.Activity
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray

class CreateSelectTagAdapter(val Parent: Activity, val TagArray: JSONArray, val ImgArray: JSONArray, val addArray: ArrayList<String>, val addTag: (TagName: String) -> Unit, val delTag: (TagName: String) -> Unit) : RecyclerView.Adapter<CreateSelectTagAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreateSelectTagAdapter.ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.row_tagitem, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return TagArray.length()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.tv_tagname.text = TagArray.getString(position)
        holder.img_tag.setImageBitmap(ImgArray.get(position) as Bitmap)
        for (i in 0..addArray.size - 1) {
            if (addArray.get(i) == holder.tv_tagname.text.toString()) {
                holder.img_border.visibility = View.VISIBLE
            }
        }
        holder.img_tag.setOnClickListener {
            var temp = true

            if (addArray.size < 4) {
                for (i in 0..addArray.size - 1) {
                    if (addArray.get(i) == holder.tv_tagname.text.toString()) {
                        temp = false
                        break
                    }
                }
                if (temp) {
                    addTag(TagArray.getString(position))
                    holder.img_border.visibility = View.VISIBLE
                } else {
                    delTag(TagArray.getString(position))
                    holder.img_border.visibility = View.INVISIBLE
                }
            } else {
                for (i in 0..addArray.size - 1) {
                    if (addArray.get(i) == holder.tv_tagname.text.toString()) {
                        delTag(TagArray.getString(position))
                        holder.img_border.visibility = View.INVISIBLE
                    } else {
                        Toast.makeText(Parent, "標籤個數已達上限!", Toast.LENGTH_SHORT).show()
                    }
                }

            }
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv_tagname = itemView.findViewById<TextView>(R.id.tv_tagname)
        val img_tag = itemView.findViewById<PrettyImageView>(R.id.img_tag)
        val img_border = itemView.findViewById<ImageView>(R.id.img_border)
    }

}