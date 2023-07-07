package com.max360group.cammax360.views.adapters

import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.Jobs
import com.max360group.cammax360.repository.models.model.JobsPermissionsValue
import com.max360group.cammax360.views.activities.inflate
import kotlinx.android.synthetic.main.load_member_permission_type_layout.view.*

class MemberPermissionTypeAdapter(
    var mFragment: Fragment,
    var mList: List<JobsPermissionsValue>,
    var mJobs: Jobs
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ListViewHolder(parent.inflate(R.layout.load_member_permission_type_layout))
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, i: Int) {
        (holder as ListViewHolder).bindListView(i)

    }


    private inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindListView(i: Int) {



        }
    }

    interface AccountListener {
        fun onItemClick()
    }
}