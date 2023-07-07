package com.max360group.cammax360.views.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.Events
import com.max360group.cammax360.utils.Constants.DATE_FORMAT_DISPLAY
import com.max360group.cammax360.utils.Constants.DATE_FORMAT_SERVER_ISO
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.views.activities.inflate
import kotlinx.android.synthetic.main.calender_event_layout.view.*
import kotlinx.android.synthetic.main.load_conversation_layout.view.tvDate


class CalenderEventListAdapter(var mFragment: Fragment) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mList = mutableListOf<Events>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ListViewHolder(parent.inflate(R.layout.calender_event_layout))
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun updateFunction(list: List<Events>) {
        mList.clear()
        mList.addAll(list)
        notifyDataSetChanged()

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, i: Int) {
        (holder as ListViewHolder).bindListView(i)
    }

    private inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindListView(i: Int) {
            (GeneralFunctions.changeUtcToLocal(
                mList[adapterPosition].startDt,
                DATE_FORMAT_SERVER_ISO, DATE_FORMAT_DISPLAY
            ) + " - " + GeneralFunctions.changeUtcToLocal(
                mList[adapterPosition].endDt,
                DATE_FORMAT_SERVER_ISO, DATE_FORMAT_DISPLAY
            )).also { itemView.tvDate.text = it }

            itemView.tvTitle.text = mList[adapterPosition].title

            //Set click listener
            itemView.setOnClickListener {
                (mFragment as CalenderEventListener).onItemClick(mList[adapterPosition].id)
            }


        }
    }

    interface CalenderEventListener {
        fun onItemClick(events: String)
    }
}