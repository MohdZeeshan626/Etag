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
import com.max360group.cammax360.repository.models.EmailsModel
import com.max360group.cammax360.repository.models.PhoneNumberModel
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.views.activities.inflate
import kotlinx.android.synthetic.main.layout_phone_list_detail.view.*
import kotlinx.android.synthetic.main.load_data_layout.view.tvName
import kotlinx.android.synthetic.main.load_job_detail_menu_layout.view.*


class PhoneListDetailAdapter(var mFragment: Fragment) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mList = mutableListOf<PhoneNumberModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ListViewHolder(parent.inflate(R.layout.layout_phone_list_detail))
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun updateData(phoneNumbers: List<PhoneNumberModel>) {
        mList.clear()
        mList.addAll(phoneNumbers)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, i: Int) {
        (holder as ListViewHolder).bindListView(i)
    }

    private inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindListView(i: Int) {
            itemView.tvName.text = mList[absoluteAdapterPosition].name
            itemView.tvPhone.text = mList[absoluteAdapterPosition].phoneNumber
            itemView.cbDefault.isChecked = mList[absoluteAdapterPosition].default
            itemView.cbTextMessage.isChecked = mList[absoluteAdapterPosition].textMessage

            itemView.tvPhone.setOnClickListener {
                GeneralFunctions.phoneCall(
                    mList[absoluteAdapterPosition].phoneNumber,
                    mFragment.requireContext()
                )
            }
        }
    }
}