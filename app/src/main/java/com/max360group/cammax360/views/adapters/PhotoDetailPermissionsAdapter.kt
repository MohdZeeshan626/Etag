package com.max360group.cammax360.views.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.JobMembers
import com.max360group.cammax360.repository.models.model.Users
import com.max360group.cammax360.views.activities.inflate
import kotlinx.android.synthetic.main.item_new_photo_permissions.view.*
import kotlinx.android.synthetic.main.item_new_photo_permissions.view.tvName
import kotlinx.android.synthetic.main.load_conversation_layout.view.*

class PhotoDetailPermissionsAdapter(var mFragment: androidx.fragment.app.Fragment) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mList= ArrayList<Users>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ListViewHolder(parent.inflate(R.layout.item_new_photo_permissions))
        }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun updateData(list: List<Users>){
        mList.clear()
        mList.addAll(list)
        notifyDataSetChanged()
    }

    fun getUpdatedList():List<Users>{
        return mList
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, i: Int) {
        (holder as ListViewHolder).bindListView(i)
    }

    private inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindListView(position: Int) {
            //Set data
            (mList[adapterPosition].name).also { itemView.tvName.text = it }
            itemView.tvEmail.text=mList[adapterPosition].email
            itemView.sdvUserImage.setImageResource(R.drawable.ic_user_thumb)

            //Expand collapse
            if (mList[adapterPosition].isChecked) {
                itemView.clPermissionsView.visibility = View.VISIBLE
                itemView.ivAction.setImageResource(R.drawable.ic_up_arrow)

            } else {
                itemView.ivAction.setImageResource(R.drawable.ic_down_arrow)
                itemView.clPermissionsView.visibility = View.GONE
            }

            //Set adapter
            itemView.rvPermissions.adapter = PhotoPermissionCategoryAdapter(mFragment,mList[adapterPosition]
                .permissions!!)

            //Select all permissions
            itemView.cbPermissions.setOnClickListener {
                if (itemView.cbPermissions.isChecked){
                    itemView.cbPermissions.isChecked=false
                    mList[adapterPosition].permissions.base=1
                    mList[adapterPosition].permissions.comments=1
                    mList[adapterPosition].permissions.members=1
                }else{
                    itemView.cbPermissions.isChecked=true
                    mList[adapterPosition].permissions.base=2
                    mList[adapterPosition].permissions.comments=2
                    mList[adapterPosition].permissions.members=2
                }
                notifyDataSetChanged()
            }

            //Set click listener
            itemView.ivAction.setOnClickListener {
                mList[adapterPosition].isChecked = !mList[adapterPosition].isChecked
                notifyDataSetChanged()
            }

        }
    }

}
