package com.max360group.cammax360.views.adapters

import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.IntegrationData
import com.max360group.cammax360.repository.preferences.UserPrefsManager
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.views.activities.inflate
import com.max360group.cammax360.views.interfaces.CreatePropertyInterface
import kotlinx.android.synthetic.main.layout_new_property_list.view.*
import kotlinx.android.synthetic.main.row_load_more.view.*

open class IntegrationTypeAdapter(var mFragment: Fragment) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mList = ArrayList<IntegrationData>()

    companion object {
        private const val VIEW_ITEM = 1
        private const val VIEW_MORE = 2
        const val LIMIT = 50
    }

    private var isLoadMore=false

    protected val mUserPrefsManager: UserPrefsManager by lazy { UserPrefsManager(mFragment.requireContext()) }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_ITEM) {
            ListViewHolder(parent.inflate(R.layout.load_integration))
        } else {
            MoreViewHolder(parent.inflate(R.layout.row_load_more))
        }
    }

    override fun getItemCount(): Int {
        return mList.size+1
    }

    fun updateData(list: List<IntegrationData> , mSkip: Int=0) {
            if (mSkip == 0) {
                mList.clear()
            }
            mList.addAll(list)
            isLoadMore = list.size >= LIMIT

        notifyDataSetChanged()

    }

    override fun getItemViewType(position: Int): Int {
        return when(position){
            mList.size-> VIEW_MORE
            else-> VIEW_ITEM
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, i: Int) {
        if (VIEW_MORE == getItemViewType(i)) {
            (holder as MoreViewHolder).bindListView(i)
        } else {
            (holder as ListViewHolder).bindListView(i)
        }
    }

    private inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindListView(i: Int) {
            (mList[absoluteAdapterPosition].name+"("+mList[absoluteAdapterPosition].description+")").also { itemView.tvName.text = it }
            itemView.setOnClickListener {
                (mFragment as CreatePropertyInterface).onIntegrationTypeClick(mList[absoluteAdapterPosition])
            }
        }
    }

    private inner class MoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindListView(position: Int) {
            if (GeneralFunctions.isInternetConnected(mFragment.requireContext())) {
                if (isLoadMore) {
                    itemView.llLoadMore.visibility = View.VISIBLE
                    (mFragment as CreatePropertyInterface).onLoadMore()
                } else {
                    itemView.llLoadMore.visibility = View.GONE
                }
            }
        }
    }


}