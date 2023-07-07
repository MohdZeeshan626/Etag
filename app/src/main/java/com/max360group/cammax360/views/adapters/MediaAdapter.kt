package com.max360group.cammax360.views.adapters

import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.FilteredJobsMediaModel
import com.max360group.cammax360.repository.models.JobMediaList
import com.max360group.cammax360.utils.Constants.DATE_FORMAT_DISPLAY
import com.max360group.cammax360.utils.Constants.DATE_FORMAT_SERVER
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.views.activities.inflate
import kotlinx.android.synthetic.main.load_midea_layout.view.*
import java.util.*
import kotlin.collections.ArrayList


class MediaAdapter(var mFragment: Fragment) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mType = 3
    private var mJobMediaList = mutableListOf<FilteredJobsMediaModel>()
    private var mFilterList = ArrayList<JobMediaList>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ListViewHolder(parent.inflate(R.layout.load_midea_layout))
    }

    override fun getItemCount(): Int {
        return mJobMediaList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, i: Int) {
        (holder as ListViewHolder).bindListView(i)

    }

    fun updateViewType(type: Int) {
        mType = type
        notifyDataSetChanged()
    }

    fun updateData(list: List<FilteredJobsMediaModel>) {
        mJobMediaList.clear()
        mJobMediaList.addAll(list)
        notifyDataSetChanged()
    }


    private inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindListView(i: Int) {
            itemView.tvDate.text =mJobMediaList[adapterPosition].date!!

            itemView.cbCheckSelection.setOnClickListener {
                itemView.cbCheckSelection.isChecked = !itemView.cbCheckSelection.isChecked
            }

            itemView.ivFilterUp.setOnClickListener {
                itemView.ivFilterDown.setColorFilter(ContextCompat.getColor(mFragment.requireContext(),
                    R.color.colorCheckBoxNormal))

                itemView.ivFilterUp.setColorFilter(ContextCompat.getColor(mFragment.requireContext(),
                    R.color.black))

                //Update adapter
                updateAdapter(mJobMediaList[adapterPosition].mJobMediaList, itemView.rvList, mType)

            }

            itemView.ivFilterDown.setOnClickListener {
                itemView.ivFilterDown.setColorFilter(ContextCompat.getColor(mFragment.requireContext(),
                    R.color.black))

                itemView.ivFilterUp.setColorFilter(ContextCompat.getColor(mFragment.requireContext(),
                    R.color.colorCheckBoxNormal))

                //Reverse lis
                mFilterList.clear()
                mJobMediaList[adapterPosition].mJobMediaList?.let { it1 -> mFilterList.addAll(it1) }
                mFilterList.reverse()
                //Update adapter
                updateAdapter(mFilterList, itemView.rvList, mType)
            }

            //Update adapter
            updateAdapter(mJobMediaList[adapterPosition].mJobMediaList, itemView.rvList, mType)

        }
    }

    fun updateAdapter(mJobMediaList: ArrayList<JobMediaList>?, rvList: RecyclerView, mType: Int) {
        //Set adapter
        var mGallaryAdapter = GallaryAdapter(mFragment, mJobMediaList)
        var manager = GridLayoutManager(mFragment.requireContext(), this.mType)
        rvList.layoutManager = manager
        rvList.adapter = mGallaryAdapter
        mGallaryAdapter.updateData(mType)
    }

}