package com.max360group.cammax360.views.calender

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.max360group.cammax360.R
import com.max360group.cammax360.views.activities.inflate
import kotlinx.android.synthetic.main.calender_cell.view.*
import kotlinx.android.synthetic.main.event_cell.view.*


class EventsListingAdapter(var context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val eventsList = ArrayList<Event>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ListViewHolder(parent.inflate(R.layout.event_cell))
    }

    override fun getItemCount(): Int {
        return eventsList.size
    }

    fun updateData(events: java.util.ArrayList<Event>) {
        eventsList.clear()
        eventsList.addAll(events)
        notifyDataSetChanged()

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, i: Int) {
        (holder as ListViewHolder).bindListView(i)

    }

    private inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindListView(i: Int) {
          itemView.tvTitle.text=eventsList[absoluteAdapterPosition].name
          itemView.tvDate.text=eventsList[absoluteAdapterPosition].eventDate
            itemView.setOnClickListener {
                (context as EventsListingListener).onItemClick(eventsList[absoluteAdapterPosition].id)
            }
        }
    }
    interface EventsListingListener{
        fun onItemClick(id:String)
    }

}