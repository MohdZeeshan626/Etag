package com.max360group.cammax360.views.adapters

import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.PermissionsBitValues
import com.max360group.cammax360.views.activities.inflate
import kotlinx.android.synthetic.main.item_add_members_jobs.view.*
import kotlinx.android.synthetic.main.load_member_permission_type_layout.view.tvName


class PhotoPermissionTypeAdapter(
    var mFragment: Fragment
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mShowFullDetail = true
    private var mList = mutableListOf<String>("All", "View", "Add", "Edit", "Delete", "TimeLine")
    private var mPermissionsBitValues = PermissionsBitValues()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ListViewHolder(parent.inflate(R.layout.load_photo_permission_type_layout))
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun updateData(isShowFullDetail: Boolean) {
        mShowFullDetail = isShowFullDetail
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, i: Int) {
        (holder as ListViewHolder).bindListView(i)

    }


    private inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindListView(i: Int) {


            itemView.tvName.text = mList[adapterPosition]
            itemView.cbTermsPrivacy.setOnClickListener {
                itemView.cbTermsPrivacy.isChecked = !itemView.cbTermsPrivacy.isChecked
            }
        }
    }

    interface AccountListener {
        fun onItemClick()
    }
}