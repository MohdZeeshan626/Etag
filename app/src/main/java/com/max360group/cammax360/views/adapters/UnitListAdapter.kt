package com.max360group.cammax360.views.adapters

import android.content.Context
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.Job
import com.max360group.cammax360.repository.models.UnitRecord
import com.max360group.cammax360.repository.models.UserX
import com.max360group.cammax360.repository.preferences.UserPrefsManager
import com.max360group.cammax360.utils.Constants
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.views.activities.inflate
import com.max360group.cammax360.views.interfaces.JobsListener
import kotlinx.android.synthetic.main.layout_unit_list.view.*
import kotlinx.android.synthetic.main.load_jobs_layout.view.*
import kotlinx.android.synthetic.main.load_jobs_layout.view.tvDate
import kotlinx.android.synthetic.main.load_jobs_layout.view.tvJobName
import kotlinx.android.synthetic.main.row_load_more.view.*

open class UnitListAdapter(var mFragment: Fragment, var mList: List<UnitRecord>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return  ListViewHolder(parent.inflate(R.layout.layout_unit_list))
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, i: Int) {
        (holder as ListViewHolder).bindListView(i)
    }

    private inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindListView(i: Int) {
            itemView.tvJobName.text=mList[absoluteAdapterPosition].name
            itemView.tvDate.text=mList[absoluteAdapterPosition].primaryAddress.formatted

            itemView.cbUnitSelect.isChecked = mList[absoluteAdapterPosition].isChecked

            itemView.cbUnitSelect.setOnClickListener {
                if (itemView.cbUnitSelect.isChecked){
                    itemView.cbUnitSelect.isChecked=false
                    mList[absoluteAdapterPosition].isChecked=false
                }else{
                    itemView.cbUnitSelect.isChecked=true
                    mList[absoluteAdapterPosition].isChecked=true
                }
            }
        }
    }
}