package com.max360group.cammax360.views.calender

import android.content.Context
import android.content.LocusId
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.max360group.cammax360.R
import com.max360group.cammax360.views.activities.inflate
import kotlinx.android.synthetic.main.calender_cell.view.*
import kotlinx.android.synthetic.main.calender_events.view.*
import kotlinx.android.synthetic.main.fragment_media_visibility.view.*
import kotlinx.android.synthetic.main.hours_cell.view.*
import kotlinx.android.synthetic.main.load_images_layout.view.*


class HoursAdapter(var context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


   private var mList=ArrayList<HourEvent>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ListViewHolder(parent.inflate(R.layout.hours_cell))
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun updateData(events: ArrayList<HourEvent>) {
        mList.clear()
        mList.addAll(events)
        notifyDataSetChanged()

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, i: Int) {
        (holder as ListViewHolder).bindListView(i)

    }

    private inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindListView(i: Int) {
            itemView.timeTV.text = CalendarUtils.formattedShortTime(mList[absoluteAdapterPosition].time)
            if (mList[absoluteAdapterPosition].events.isEmpty()){
                itemView.llEventView.setPadding(0,82,0,82)
                itemView.timeTV.setBackgroundColor(ContextCompat.getColor(context,R.color.white))
            }else{
                itemView.llEventView.setPadding(0,28,0,34)

                val value = TypedValue()
                context.theme.resolveAttribute(R.attr.primaryTransparent, value, true)
                itemView.timeTV.setBackgroundColor(ContextCompat.getColor(context,value.resourceId))
            }
            val mAdapter=DayEventListAdapter(context,mList[absoluteAdapterPosition].events)
            itemView.rvEvents.adapter=mAdapter


        }
    }

}