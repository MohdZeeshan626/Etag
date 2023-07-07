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
import com.max360group.cammax360.views.activities.inflate
import kotlinx.android.synthetic.main.load_data_layout.view.tvName
import kotlinx.android.synthetic.main.load_job_detail_menu_layout.view.*


class DetailsMenuAdapter(var mFragment: Fragment) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mList= mutableListOf<String>("Media","Documents","Conversation","Notes","Comments","Timeline")
    private var mPosition=0
    private var mDetails:Details=Details()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ListViewHolder(parent.inflate(R.layout.load_job_detail_menu_layout))
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, i: Int) {
        (holder as ListViewHolder).bindListView(i)

    }

    fun updateData(details: Details=Details()) {
        mDetails=details
        notifyDataSetChanged()
    }

    private inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindListView(i: Int) {

            when(absoluteAdapterPosition){
                0->{
                    (mList[absoluteAdapterPosition] +"(${mDetails!!.mediaPhotosCount+mDetails!!.mediaVideosCount})").also { itemView.tvName.text = it }
                }
                1->{
                    (mList[absoluteAdapterPosition] +"(${mDetails!!.mediaDocsCount.toString()})").also { itemView.tvName.text = it }
                }
                2->{
                    (mList[absoluteAdapterPosition] +"(${mDetails!!.conversationCount.toString()})").also { itemView.tvName.text = it }
                }
                3->{
                    (mList[absoluteAdapterPosition] +"(${mDetails!!.notesCount.toString()})").also { itemView.tvName.text = it }
                }
                4->{
                    (mList[absoluteAdapterPosition] +"(${mDetails!!.commentsCount.toString()})").also { itemView.tvName.text = it }
                }

                5->{
                    ("${mList[absoluteAdapterPosition]}").also { itemView.tvName.text = it }
                }
            }

            if (absoluteAdapterPosition==mPosition){
                itemView.tvName.setTextColor(
                    MaterialColors.getColor(
                        mFragment.requireContext(),
                        R.attr.primaryTextColor,
                        Color.BLACK
                    )
                )
                itemView.view.visibility=View.VISIBLE
            }else{
                itemView.tvName.setTextColor(mFragment.resources.getColor(R.color.colorHeaderTitle))
                itemView.view.visibility=View.GONE
            }

            itemView.setOnClickListener {
                mPosition=absoluteAdapterPosition
                (mFragment as onJobDetailListener).onItemClick(absoluteAdapterPosition)
                notifyDataSetChanged()
            }
        }
    }

    interface onJobDetailListener{
        fun onItemClick(position:Int)
    }

}