package com.network.fiesta

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

class TimeLineAdapter(private val mFeedList: List<TimeLineModel>, val click:(showId: String?, showName: String?, detail: String?) -> Unit) : RecyclerView.Adapter<TimeLineAdapter.TimeLineViewHolder>() {

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

        holder.itemView.setOnClickListener {
            click(timeLineModel.showId, timeLineModel.showName,timeLineModel.detail)
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
        init {
            timeline.initLine(viewType)
        }
    }

}
