package com.max360group.cammax360.views.adapters

import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.PropertyDetail
import com.max360group.cammax360.repository.models.Owners
import com.max360group.cammax360.repository.models.UserOwner
import com.max360group.cammax360.views.activities.inflate
import kotlinx.android.synthetic.main.layout_new_property_list.view.*

open class ExistingOwnerListAdapter(var mFragment: Fragment) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val mList= mutableListOf<UserOwner>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ListViewHolder(parent.inflate(R.layout.layout_existing_owner_list))

    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun updateData(list:List<UserOwner>){
        mList.clear()
        mList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ListViewHolder).bindListView(position)
    }

    private inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindListView(position: Int) {
            itemView.tvName.text=mList[position].firstName
            itemView.setOnClickListener {
                (mFragment as AddOwnerListener).onItemClick(mList[position])
            }

        }
    }
    interface AddOwnerListener{
        fun onItemClick(property: UserOwner)
    }

}