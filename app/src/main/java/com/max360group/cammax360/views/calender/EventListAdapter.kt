package com.max360group.cammax360.views.calender

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.max360group.cammax360.R
import com.max360group.cammax360.views.activities.inflate
import kotlinx.android.synthetic.main.calender_cell.view.*
import kotlinx.android.synthetic.main.calender_events.view.*
import kotlinx.android.synthetic.main.fragment_media_visibility.view.*
import kotlinx.android.synthetic.main.load_images_layout.view.*


class EventListAdapter(var context: Context, var eventsList: java.util.ArrayList<Event>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ListViewHolder(parent.inflate(R.layout.calender_events))
    }

    override fun getItemCount(): Int {
        return if (eventsList.size > 3) {
            3
        } else {
            eventsList.size
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, i: Int) {
        (holder as ListViewHolder).bindListView(i)

    }

    private inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindListView(i: Int) {
            if (absoluteAdapterPosition == 2) {
                itemView.tvEvent.text = "more...+"
            } else {
                itemView.tvEvent.text = eventsList[absoluteAdapterPosition].name
            }

        }
    }


}