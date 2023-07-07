package com.max360group.cammax360.views.adapters

import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.TimeLineItems
import com.max360group.cammax360.views.activities.inflate
import kotlinx.android.synthetic.main.timeline_filter_layout.view.*


class TimelineFilterAdapter(var mFragment: Fragment) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mList = mutableListOf<TimeLineItems>()
    private var isAll = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ListViewHolder(parent.inflate(R.layout.timeline_filter_layout))
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun updateData(list: List<TimeLineItems>) {
        mList.clear()
        mList.addAll(list)
        notifyDataSetChanged()
    }

    fun getSelectedItems(): List<TimeLineItems> {
        return mList
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ListViewHolder).bindListView(position)

    }

    private inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindListView(i: Int) {
            itemView.tvName.text = mList[absoluteAdapterPosition].name

            //Check for all on first position
            if (isAll) {
                mList[absoluteAdapterPosition].isChecked = mList[0].isChecked
            }

            itemView.cbFilter.isChecked = mList[absoluteAdapterPosition].isChecked

            //Set click listener
            itemView.cbFilter.setOnClickListener {
                if (absoluteAdapterPosition==0){
                    isAll=true
                }else{
                    isAll=false
                    mList[0].isChecked=false
                }

                mList[absoluteAdapterPosition].isChecked = !mList[absoluteAdapterPosition].isChecked
                notifyDataSetChanged()
            }
        }
    }
}