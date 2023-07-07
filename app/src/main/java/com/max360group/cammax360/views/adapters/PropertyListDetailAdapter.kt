package com.max360group.cammax360.views.adapters

import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.PropertyDetail
import com.max360group.cammax360.views.activities.inflate
import kotlinx.android.synthetic.main.layout_property_list.view.ivOpenUnit
import kotlinx.android.synthetic.main.layout_property_list.view.rvUnits
import kotlinx.android.synthetic.main.layout_property_list_detail.view.*
import kotlinx.android.synthetic.main.load_jobs_layout.view.tvJobName

open class PropertyListDetailAdapter(var mFragment: Fragment) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mList= mutableListOf<PropertyDetail>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ListViewHolder(parent.inflate(R.layout.layout_property_list_detail))
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun updateData(list: List<PropertyDetail>){
        mList.clear()
        mList.addAll(list)
        notifyDataSetChanged()

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, i: Int) {
        (holder as ListViewHolder).bindListView(i)
    }

    private inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindListView(i: Int) {
            itemView.ivSdvImage.setImageURI(mList[absoluteAdapterPosition].picURL)
         itemView.tvJobName.text=mList[absoluteAdapterPosition].name
         itemView.tvLocation.text=mList[absoluteAdapterPosition].primaryAddress.formatted
            (": "+mList[absoluteAdapterPosition].propertyUnits!!.size.toString()).also { itemView.tvUnitObtained.text = it }

            itemView.ivOpenUnit.setOnClickListener {
                if (itemView.rvUnits.isVisible) {
                    itemView.rvUnits.visibility = View.GONE
                    itemView.ivOpenUnit.setImageResource(R.drawable.ic_expand_action)
                } else {
                    itemView.rvUnits.visibility = View.VISIBLE
                    itemView.ivOpenUnit.setImageResource(R.drawable.ic_collapse_action)
                }
            }

            val  mUnitListAdapter = UnitListDetailAdapter(mFragment,mList[absoluteAdapterPosition].propertyUnits)
            itemView.rvUnits.adapter = mUnitListAdapter

        }
    }

}