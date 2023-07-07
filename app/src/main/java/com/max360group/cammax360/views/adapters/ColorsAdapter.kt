package com.max360group.cammax360.views.adapters

import android.graphics.Color
import android.graphics.PorterDuff
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.max360group.cammax360.R
import com.max360group.cammax360.utils.ColorTheme
import com.max360group.cammax360.views.activities.inflate
import kotlinx.android.synthetic.main.load_colors_layout.view.*


class ColorsAdapter(var mFragment: Fragment) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mList = ColorTheme.getThemeColor()
    private var mPositionColor=""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ListViewHolder(parent.inflate(R.layout.load_colors_layout))
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun updateData(primary: String?) {
        mPositionColor=primary!!
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, i: Int) {
        (holder as ListViewHolder).bindListView(i)

    }


    private inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindListView(i: Int) {

            if (mList[adapterPosition].primary==mPositionColor){
                itemView.ivSelection.visibility=View.VISIBLE
            }else{
                itemView.ivSelection.visibility=View.GONE
            }

            itemView.ivColor.background.setColorFilter(
                Color.parseColor(mList[adapterPosition].primary!!),
                PorterDuff.Mode.SRC_ATOP
            )

            itemView.setOnClickListener {
                mPositionColor= mList[adapterPosition].primary.toString()
                (mFragment as AccountDetailsListener).onThemeClick(adapterPosition,mList[adapterPosition].primary!!,
                    mList[adapterPosition].primaryLight!!)
                notifyDataSetChanged()
            }

        }
    }

    interface AccountDetailsListener{
        fun onThemeClick(position: Int, colorPrimary: String,colorLight:String)
    }
}