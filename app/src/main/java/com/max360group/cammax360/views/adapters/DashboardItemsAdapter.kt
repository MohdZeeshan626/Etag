package com.max360group.cammax360.views.adapters

import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.DashBoardMenuModel
import com.max360group.cammax360.views.activities.inflate
import kotlinx.android.synthetic.main.load_data_layout.view.*


class DashboardItemsAdapter(var mFragment: Fragment) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mList= ArrayList<DashBoardMenuModel>()
    private var mPosition=0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ListViewHolder(parent.inflate(R.layout.load_dashboard_items_layout))
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, i: Int) {
        (holder as ListViewHolder).bindListView(i)

    }

    fun updateData(list: List<DashBoardMenuModel>) {
        mList.clear()
        mList.addAll(list)
        mList.removeAt(0)
        notifyDataSetChanged()

    }


    private inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindListView(i: Int) {
            itemView.tvName.text=mList[adapterPosition].name
                itemView.ivIcon.setImageResource(mList[adapterPosition].icon)

            itemView.setOnClickListener {
                (mFragment as DashboardInterface).onItemClick(mList[adapterPosition].name)
            }

        }
    }

    interface DashboardInterface{
        fun onItemClick(name:String)
    }


}