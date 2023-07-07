package com.max360group.cammax360.views.adapters

import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.JobMediaList
import com.max360group.cammax360.views.activities.inflate
import com.max360group.cammax360.views.interfaces.JobsLDetailListener
import kotlinx.android.synthetic.main.load_documents_layout.view.*
import kotlinx.android.synthetic.main.load_gallary_layout.view.*


class DocumentsItemsAdapter(var mFragment: Fragment,
                            var mJobMediaList: ArrayList<JobMediaList>?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_SMALL = 3
        const val VIEW_MEDIUM = 2
        const val VIEW_LARGE = 1
        var viewType = 3
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ListViewHolder(parent.inflate(R.layout.load_documents_layout))
    }

    override fun getItemCount(): Int {
        return mJobMediaList!!.size
    }

    fun updateData(isViewType: Int) {
        viewType = isViewType
        notifyDataSetChanged()

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, i: Int) {
        (holder as ListViewHolder).bindListView(i)

    }


    private inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindListView(i: Int) {
            //Get screen Width
            val displayMetrics = DisplayMetrics()
            (mFragment).requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
            val width = displayMetrics.widthPixels

            when (viewType) {
                VIEW_LARGE -> {
                    var mWidth=width-300
                    itemView.sdvDocument.layoutParams.height = mWidth
                }
                VIEW_SMALL -> {
                    var mWidth=width/3-54
                    itemView.sdvDocument.layoutParams.height = mWidth
                }
                VIEW_MEDIUM -> {
                    var mWidth= width/2-50
                    itemView.sdvDocument.layoutParams.height = mWidth
                }

            }

            //Set data
            itemView.tvName.text= mJobMediaList!![adapterPosition].medias?.get(0)!!.name
            (mJobMediaList!![adapterPosition].creatorId.firstName + " " + mJobMediaList!![adapterPosition].creatorId.lastName).also {
                itemView.tvUserName.text = it
            }

            itemView .setOnClickListener {
                (mFragment as JobsLDetailListener).onItemClick(mJobMediaList!![adapterPosition],0)
            }

        }
    }
}