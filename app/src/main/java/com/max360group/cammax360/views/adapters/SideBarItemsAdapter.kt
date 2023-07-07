package com.max360group.cammax360.views.adapters

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.color.MaterialColors
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.DashBoardMenuModel
import com.max360group.cammax360.views.activities.inflate
import kotlinx.android.synthetic.main.load_data_layout.view.*


class SideBarItemsAdapter(var mFragment: Fragment) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mList = mutableListOf<DashBoardMenuModel>()
    var mPosition = 0
    var isClick=false


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ListViewHolder(parent.inflate(R.layout.load_data_layout))
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, i: Int) {
        (holder as ListViewHolder).bindListView(i)

    }

    fun updateData(list: List<DashBoardMenuModel>, isChecked: Int) {
        mList.clear()
        mList.addAll(list)
        mPosition = isChecked
        notifyDataSetChanged()

    }


    private inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindListView(i: Int) {
            if (absoluteAdapterPosition == mPosition) {
                itemView.clRootView.setBackgroundColor(
                    MaterialColors.getColor(
                        mFragment.requireContext(),
                        R.attr.primaryTransparent,
                        Color.BLACK
                    )
                )
                itemView.tvName.setTextColor(
                    MaterialColors.getColor(
                        mFragment.requireContext(),
                        R.attr.primaryTextColor,
                        Color.BLACK
                    )
                )
                val typeface = ResourcesCompat.getFont(
                    mFragment.requireContext(),
                    R.font.font_santral_semibold
                )
                itemView.tvName.typeface = typeface

            } else {
                itemView.clRootView.setBackgroundColor(mFragment.resources.getColor(R.color.white))
                itemView.tvName.setTextColor(mFragment.resources.getColor(R.color.colorBlackTransparent))
                val typeface =
                    ResourcesCompat.getFont(mFragment.requireContext(), R.font.font_santral_reguler)
                itemView.tvName.typeface = typeface
            }
            itemView.tvName.text = mList[absoluteAdapterPosition].name
            itemView.ivIcon.setImageResource(mList[absoluteAdapterPosition].icon)

            itemView.setOnClickListener {
                if (!isClick){
                    mPosition = absoluteAdapterPosition
                    (mFragment as HomeListener).onItemClick(mList[absoluteAdapterPosition].name)
                    notifyDataSetChanged()
                    isClick=true
                }
                android.os.Handler().postDelayed({
                    isClick=false
                },1000)
            }


        }
    }

    interface HomeListener {
        fun onItemClick(name: String)

    }
}