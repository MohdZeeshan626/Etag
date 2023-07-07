package com.max360group.cammax360.views.adapters

import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.PropertyDetail
import com.max360group.cammax360.views.activities.inflate
import kotlinx.android.synthetic.main.layout_property_list.view.*
import kotlinx.android.synthetic.main.layout_property_list.view.tvAddress

open class PropertyListAdapter(var mFragment: Fragment) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mList = mutableListOf<PropertyDetail>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ListViewHolder(parent.inflate(R.layout.layout_property_list))
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun updateData(list: List<PropertyDetail>) {
        mList.clear()
        mList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, i: Int) {
        (holder as ListViewHolder).bindListView(i)
    }

    private inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindListView(i: Int) {
            (mList[absoluteAdapterPosition].name + "(" + mList[absoluteAdapterPosition].shortName + ")").also {
                itemView.tvPropertyName.text = it
            }
            itemView.tvAddress.text = mList[absoluteAdapterPosition].primaryAddress.formatted
            (": "+mList[absoluteAdapterPosition].propertyUnits!!.size.toString()).also { itemView.tvUnitObtained.text = it }

            itemView.sdvPropertyImage.setImageURI(mList[absoluteAdapterPosition].picURL)

            itemView.ivOpenUnit.setOnClickListener {
                if (itemView.rvUnits.isVisible) {
                    itemView.rvUnits.visibility = View.GONE
                    itemView.ivOpenUnit.setImageResource(R.drawable.ic_expand_action)
                } else {
                    itemView.rvUnits.visibility = View.VISIBLE
                    itemView.ivOpenUnit.setImageResource(R.drawable.ic_collapse_action)
                }
            }

            val mUnitListAdapter = UnitListAdapter(mFragment,mList[absoluteAdapterPosition].propertyUnits!!)
            itemView.rvUnits.adapter = mUnitListAdapter

            itemView.btnAdd.setOnClickListener {
                (mFragment as PropertyListener).onAddClick(mList[absoluteAdapterPosition].id)
            }

            itemView.ivDelete.setOnClickListener {
                (mFragment as PropertyListener).onDeleteProperty(absoluteAdapterPosition)
            }
        }
    }

    interface PropertyListener {
        fun onAddClick(id: String)
        fun onDeleteProperty(position: Int)
    }

}