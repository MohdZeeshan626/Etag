package com.app.pukka.views.adapters

import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.analytics.FirebaseAnalytics.Event.VIEW_ITEM

import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.AutoCompletePlaceModel
import com.max360group.cammax360.repository.models.Prediction
import com.max360group.cammax360.repository.models.PropertyAndLocationViewModel
import com.max360group.cammax360.repository.models.PropertyLocationData
import com.max360group.cammax360.views.activities.inflate
import com.max360group.cammax360.views.adapters.JobsAdapter
import kotlinx.android.synthetic.main.layout_property_list.view.*
import kotlinx.android.synthetic.main.layout_property_list.view.sdvPropertyImage
import kotlinx.android.synthetic.main.layout_property_list.view.tvPropertyName
import kotlinx.android.synthetic.main.layout_property_search.view.*
import kotlinx.android.synthetic.main.layout_search_location.view.*
import kotlinx.android.synthetic.main.layout_search_location.view.tvAddress

class JobSearchLocationAdapter(var mContext: Fragment) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_PROPERTY = 1
        private const val VIEW_LOCATION = 2
    }

    private var mList = mutableListOf<PropertyLocationData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_LOCATION) {
            ListViewHolder(parent.inflate(R.layout.layout_search_location))
        } else {
            PropertyListViewHolder(parent.inflate(R.layout.layout_property_search))
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (VIEW_PROPERTY == getItemViewType(position)) {
            (holder as PropertyListViewHolder).bindListView(position)
        } else {
            (holder as ListViewHolder).bindListView(position)
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (mList[position].isLocation) {
            VIEW_LOCATION
        } else {
            VIEW_PROPERTY
        }

    }

    fun updateData(list: List<PropertyLocationData>) {
        mList.clear()
        mList.addAll(list)
        notifyDataSetChanged()
    }

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindListView(position: Int) {
            itemView.btnCreateProperty.visibility=View.VISIBLE
            itemView.tvLocation.text = mList[absoluteAdapterPosition].locationDesc
            itemView.tvAddress.text = mList[absoluteAdapterPosition].secondary_text

            //Set click listener
            itemView.setOnClickListener {
                (mContext as SearchListener).onLocationClick(
                    mList[absoluteAdapterPosition].placeId,
                    mList[absoluteAdapterPosition].locationDesc
                )
            }

            itemView.btnCreateProperty.setOnClickListener {
                (mContext as SearchListener).onCreateProperty(mList[absoluteAdapterPosition].placeId)
            }
        }
    }

    inner class PropertyListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindListView(position: Int) {
            itemView.sdvPropertyImage.setImageURI(mList[absoluteAdapterPosition].propertyImage)
            itemView.tvPropertyName.text=mList[absoluteAdapterPosition].propertyName
            itemView.tvAddress.text=mList[absoluteAdapterPosition].propertyAddress.formatted

            //Set property unit
            if (mList[absoluteAdapterPosition].propertyUnits!!.isNotEmpty()){
                itemView.tvUnitName.text=mList[absoluteAdapterPosition].propertyUnits!![0].name
                itemView.tvUnitAddress.text=mList[absoluteAdapterPosition].propertyUnits!![0].primaryAddress.formatted
            }

            itemView.setOnClickListener {
                (mContext as SearchListener).onPropertyClick(mList[absoluteAdapterPosition])
            }
        }
    }

    interface SearchListener {
        fun onLocationClick(placeId: String, address: String)
        fun onPropertyClick(mPropertyLocationData:PropertyLocationData)
        fun onCreateProperty(placeId:String)
    }
}