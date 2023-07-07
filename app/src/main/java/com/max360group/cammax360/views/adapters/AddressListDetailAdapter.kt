package com.max360group.cammax360.views.adapters

import android.graphics.Color
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.color.MaterialColors
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.Details
import com.max360group.cammax360.repository.models.OwnerAddressModel
import com.max360group.cammax360.repository.models.PhoneNumberModel
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.views.activities.inflate
import kotlinx.android.synthetic.main.layout_address.view.*
import kotlinx.android.synthetic.main.layout_address.view.ivMap
import kotlinx.android.synthetic.main.layout_address_detail.view.*
import kotlinx.android.synthetic.main.load_job_detail_menu_layout.view.*
class AddressListDetailAdapter(var mFragment: Fragment) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val mList = mutableListOf<OwnerAddressModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ListViewHolder(parent.inflate(R.layout.layout_address_detail))
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun updateData(addresses: ArrayList<OwnerAddressModel>) {
        mList.clear()
        mList.addAll(addresses)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, i: Int) {
        (holder as ListViewHolder).bindListView(i)
    }

    private inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindListView(i: Int) {
            itemView.tvFullAddressType.text = mList[absoluteAdapterPosition].name
            itemView.tvFullAddress.text = mList[absoluteAdapterPosition].formatted

            if (mList[absoluteAdapterPosition].location.coordinates!!.isNotEmpty()){
                itemView.ivMap.setImageURI(
                    GeneralFunctions.getStaticMap(
                        mList[absoluteAdapterPosition].location.coordinates!![0],
                        mList[absoluteAdapterPosition].location.coordinates!![1]
                    )
                )
            }
        }
    }
}