package com.max360group.cammax360.views.adapters

import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.DashBoardMenuModel
import com.max360group.cammax360.views.activities.inflate
import kotlinx.android.synthetic.main.dialog_fragment_camera.view.*
import kotlinx.android.synthetic.main.load_data_layout.view.*
import kotlinx.android.synthetic.main.load_data_layout.view.tvName
import kotlinx.android.synthetic.main.load_organisation_layout.view.*
import kotlinx.android.synthetic.main.load_tags_layout.view.*


class TagsAdapter(var mFragment: Fragment) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mList= mutableListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ListViewHolder(parent.inflate(R.layout.load_tags_layout))
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun updateData(list:List<String>){
        mList.clear()
        mList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, i: Int) {
        (holder as ListViewHolder).bindListView(i)

    }


    private inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindListView(i: Int) {
            itemView.tvTag.text=mList[adapterPosition]
        }
    }
}