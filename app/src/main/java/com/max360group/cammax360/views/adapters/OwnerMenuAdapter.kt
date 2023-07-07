package com.max360group.cammax360.views.adapters

import android.graphics.Color
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.color.MaterialColors
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.Details
import com.max360group.cammax360.views.activities.inflate
import com.max360group.cammax360.views.interfaces.CreateOwnerInterface
import kotlinx.android.synthetic.main.load_data_layout.view.tvName
import kotlinx.android.synthetic.main.load_job_detail_menu_layout.view.*


class OwnerMenuAdapter(var mFragment: Fragment) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mList= mutableListOf<Int>()
    private var mPosition=0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ListViewHolder(parent.inflate(R.layout.load_owner_menu_layout))
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun updateData(list:List<Int>){
        mList.clear()
        mList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, i: Int) {
        (holder as ListViewHolder).bindListView(i)
    }

    fun updatePosition(position:Int){
        mPosition=position
        notifyDataSetChanged()
    }

    private inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindListView(i: Int) {
            itemView.tvName.text=mFragment.getText(mList[absoluteAdapterPosition])

            if (absoluteAdapterPosition==mPosition){
                itemView.tvName.setTextColor(
                    MaterialColors.getColor(
                        mFragment.requireContext(),
                        R.attr.primaryTextColor,
                        Color.BLACK
                    ))
                itemView.view.visibility=View.VISIBLE

            }else{
                itemView.tvName.setTextColor(ContextCompat.getColor(mFragment.requireContext(),R.color.colorHeaderTitle))
                itemView.view.visibility=View.GONE
            }

            itemView.setOnClickListener {
                mPosition=absoluteAdapterPosition
                (mFragment as CreateOwnerInterface).onMenuClick(absoluteAdapterPosition)
                notifyDataSetChanged()
            }
        }
    }
}