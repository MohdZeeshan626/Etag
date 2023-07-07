package com.app.pukka.views.adapters

import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.analytics.FirebaseAnalytics.Event.VIEW_ITEM

import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.AutoCompletePlaceModel
import com.max360group.cammax360.repository.models.Prediction
import com.max360group.cammax360.views.activities.inflate
import com.max360group.cammax360.views.adapters.JobsAdapter
import kotlinx.android.synthetic.main.layout_search_location.view.*

class SearchLocationAdapter(var mContext: Fragment) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mList = mutableListOf<Prediction>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return   ListViewHolder(parent.inflate(R.layout.layout_search_location))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ListViewHolder).bindListView(position)

    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun updateData(list: List<Prediction>) {
        mList.clear()
        mList.addAll(list)
        notifyDataSetChanged()
    }

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindListView(position: Int) {
            itemView.tvLocation.text=mList[absoluteAdapterPosition].description
            itemView.tvAddress.text=mList[absoluteAdapterPosition].structured_formatting.secondary_text

            //Set click listener
            itemView.setOnClickListener {
                (mContext as SearchListener).onLocationClick(mList[absoluteAdapterPosition].place_id,mList[absoluteAdapterPosition].description)
            }
        }
    }


    interface SearchListener {
        fun onLocationClick(placeId: String, address: String)
    }
}