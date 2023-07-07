package com.max360group.cammax360.views.calender

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.max360group.cammax360.R
import com.max360group.cammax360.views.activities.inflate
import kotlinx.android.synthetic.main.calender_cell.view.*
import java.time.LocalDate


class CalenderAdapter(var context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val daysList = ArrayList<LocalDate>()
    private val eventsList = ArrayList<Event>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ListViewHolder(parent.inflate(R.layout.calender_cell))
    }

    override fun getItemCount(): Int {
        return daysList.size
    }

    fun updateData(days: ArrayList<LocalDate>, events: List<Event>) {
        daysList.clear()
        eventsList.clear()
        daysList.addAll(days)
        eventsList.addAll(events)
        notifyDataSetChanged()

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, i: Int) {
        (holder as ListViewHolder).bindListView(i)

    }

    private inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindListView(i: Int) {
            val date: LocalDate = daysList[absoluteAdapterPosition]
            itemView.cellDayText.text = date.dayOfMonth.toString()

            if (date == CalendarUtils.selectedDate) {
                itemView.cellDayText.background = ContextCompat.getDrawable(
                    context,
                    R.drawable.drawable_small_rounded_corners_solid
                )

            } else {
                itemView.cellDayText.background = null

            }

            if (date.month == CalendarUtils.selectedDate!!.month)
                itemView.cellDayText.setTextColor(
                    Color.BLACK
                ) else itemView.cellDayText.setTextColor(Color.LTGRAY)

            if (eventsList.isNotEmpty()) {
                for (j in eventsList.indices) {
                    itemView.rvEvent.visibility = View.VISIBLE
                    val calendarAdapter = EventListAdapter(context, Event.eventsForDate(date))
                    itemView.rvEvent.adapter = calendarAdapter
                }
            }

            itemView.setOnClickListener {
                (context as CalenderListener).onItemClick(
                    absoluteAdapterPosition,
                    daysList[absoluteAdapterPosition],Event.eventsForDate(date).size
                )
            }

        }
    }

    interface CalenderListener {
        fun onItemClick(position: Int, date: LocalDate, size: Int)
    }

}