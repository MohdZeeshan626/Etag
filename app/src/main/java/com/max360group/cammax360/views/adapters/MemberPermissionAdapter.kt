package com.max360group.cammax360.views.adapters

import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.Jobs
import com.max360group.cammax360.repository.models.model.MembersPermissionsModel
import com.max360group.cammax360.views.activities.inflate
import kotlinx.android.synthetic.main.load_member_permission_item_layout.view.*


class MemberPermissionAdapter(var mFragment: Fragment) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mList = ArrayList<MembersPermissionsModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ListViewHolder(parent.inflate(R.layout.load_member_permission_item_layout))
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun updateData(list: List<MembersPermissionsModel>) {
        mList.clear()
        mList.addAll(list)
        notifyDataSetChanged()
    }

    fun getJobsPermission(): Jobs? {
       return mList[0].permission?.get(0)?.mJobs
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, i: Int) {
        (holder as ListViewHolder).bindListView(i)

    }

    private inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindListView(i: Int) {
            itemView.tvName.text=mList[adapterPosition].permissionName

            //Set adapter
            val mMemberPermissionAdapter =MemberPermissionCategoryAdapter(mFragment,mList[adapterPosition].permission!!)
            itemView.rvList.adapter=mMemberPermissionAdapter
        }
    }


}