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
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.views.activities.inflate
import kotlinx.android.synthetic.main.layout_email_detail.view.*
import kotlinx.android.synthetic.main.load_data_layout.view.tvName
import kotlinx.android.synthetic.main.load_job_detail_menu_layout.view.*


class EmailListDetailAdapter(var mFragment: Fragment) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val mList = mutableListOf<EmailsModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ListViewHolder(parent.inflate(R.layout.layout_email_detail))
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun updateData(emails: List<EmailsModel>) {
        mList.clear()
        mList.addAll(emails)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, i: Int) {
        (holder as ListViewHolder).bindListView(i)

    }

    private inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindListView(i: Int) {
            itemView.tvNameLevel.text = mList[absoluteAdapterPosition].type
            itemView.tvEmail.text = mList[absoluteAdapterPosition].email

            itemView.tvEmail.setOnClickListener {
                GeneralFunctions.email(
                    mList[absoluteAdapterPosition].email,
                    mFragment.requireContext()
                )
            }
        }
    }
}