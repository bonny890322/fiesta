package com.network.fiesta

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.fiesta.Lib.TimeLine.Utils.VectorDrawableUtils
import com.github.vipulasri.timelineview.TimelineView
import com.network.fiesta.Lib.TimeLine.Model.OrderStatus
import com.network.fiesta.Lib.TimeLine.Model.TimeLineModel
import kotlinx.android.synthetic.main.row_timeline.view.*
import android.app.TimePickerDialog
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.dialog_edit_show.view.*
import java.util.*

class AdvanceTimeAdapter(val Parent: Activity, private val mFeedList: MutableList<TimeLineModel>, val click:(showId: String?, showName: String?, detail: String?) -> Unit) : RecyclerView.Adapter<AdvanceTimeAdapter.TimeLineViewHolder>() {

    private lateinit var mLayoutInflater: LayoutInflater
    private lateinit var context: Context

    override fun getItemViewType(position: Int): Int {
        return TimelineView.getTimeLineViewType(position, itemCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeLineViewHolder {

        context = parent.context
        if(!::mLayoutInflater.isInitialized) {
            mLayoutInflater = LayoutInflater.from(context)
        }
        return TimeLineViewHolder(mLayoutInflater.inflate(R.layout.row_timeline, parent, false), viewType)
    }

    override fun onBindViewHolder(holder: TimeLineViewHolder, position: Int) {

        val timeLineModel = mFeedList[position]

        when {
            timeLineModel.status == OrderStatus.INACTIVE -> {
                setMarker(holder, R.drawable.ic_marker_inactive, R.color.inactive)
            }
            timeLineModel.status == OrderStatus.ACTIVE -> {
                setMarker(holder, R.drawable.ic_marker_active, R.color.active)
            }
            else -> {
                setMarker(holder, R.drawable.ic_marker, R.color.marker)
            }
        }

        if (timeLineModel.date.isNotEmpty()) {
            holder.date.visibility = View.VISIBLE
            holder.date.text = timeLineModel.date
        } else {
            holder.date.visibility = View.GONE
        }

        holder.message.text = timeLineModel.showName

        if (timeLineModel.buttonVis == true) {
            holder.button.visibility = View.VISIBLE
            holder.button.setOnClickListener {
                click(timeLineModel.showId, timeLineModel.showName,"")
            }
        }

        holder.itemView.setOnClickListener{
            val myDialog = LayoutInflater.from(Parent).inflate(R.layout.dialog_edit_show,null)
            val d = AlertDialog.Builder(Parent).setView(myDialog)
            val myAlertDialog = d.show()
            myDialog.txt_name.setText(timeLineModel.showName)
            myDialog.txt_starttime.setText(timeLineModel.date.split(" ")[1])
            myDialog.txt_description.setText(timeLineModel.detail)

            myDialog.btn_cancel.setOnClickListener{
                myAlertDialog.dismiss()
            }

            myDialog.txt_starttime.setOnClickListener{
                val c = Calendar.getInstance()
                val hour = c.get(Calendar.HOUR_OF_DAY)
                val min = c.get(Calendar.MINUTE)

                TimePickerDialog(Parent,android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                    TimePickerDialog.OnTimeSetListener { view, hour, min->
                        myDialog.txt_starttime.setText("$hour:$min", TextView.BufferType.EDITABLE)
                    }, hour, min,false
                ).show()
            }

            myDialog.btn_done.setOnClickListener{
                myAlertDialog.dismiss()

                if(myDialog.txt_name.text.toString()!="" || myDialog.txt_starttime.text.toString() != ""|| myDialog.txt_description.text.toString() != ""){
                    Thread(Runnable {
                        run{
                            val time = myDialog.txt_starttime.text.toString().split(":")
                            val date = timeLineModel.date.split(" ")
                            API(Parent.applicationContext,holder.token).updateShow(timeLineModel.showId,myDialog.txt_name.text.toString(),myDialog.txt_description.text.toString(),date[0] + "-" + time[0] + "-" + time[1] + "-00")
                            timeLineModel.detail = myDialog.txt_description.text.toString()
                            timeLineModel.showName = myDialog.txt_name.text.toString()
                            timeLineModel.date = date[0] + " " + time[0] + ":" + time[1]
                            Parent.runOnUiThread {
                                notifyDataSetChanged()
                            }
                        }
                    }).start()
                }else{
                    Toast.makeText(Parent,"請輸入資料", Toast.LENGTH_SHORT).show()
                }
            }

            myDialog.btn_delete.setOnClickListener {
                myAlertDialog.dismiss()

                Thread(Runnable {
                    run {
                        if (API(Parent.applicationContext,holder.token).deleteShow(timeLineModel.showId).getString("code") == "001") {
                            mFeedList.removeAt(position)
                            Parent.runOnUiThread {
                                notifyDataSetChanged()
                            }
                        }
                    }
                }).start()
            }

        }
    }

    private fun setMarker(holder: TimeLineViewHolder, drawableResId: Int, colorFilter: Int) {
        holder.timeline.marker = VectorDrawableUtils.getDrawable(holder.itemView.context, drawableResId, ContextCompat.getColor(holder.itemView.context, colorFilter))
    }

    override fun getItemCount() = mFeedList.size

    inner class TimeLineViewHolder(itemView: View, viewType: Int) : RecyclerView.ViewHolder(itemView) {
        val date = itemView.text_timeline_date
        val message = itemView.text_timeline_title
        val timeline = itemView.timeline
        val button = itemView.button_timeline_score
        val token = Parent.getSharedPreferences("Data", Context.MODE_PRIVATE).getString("token", "token")!!
        init {
            timeline.initLine(viewType)
        }
    }
}