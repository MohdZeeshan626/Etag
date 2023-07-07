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
import com.max360group.cammax360.repository.models.UserX
import com.max360group.cammax360.repository.preferences.UserPrefsManager
import com.max360group.cammax360.utils.Constants
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.views.activities.inflate
import com.max360group.cammax360.views.interfaces.JobsListener
import com.max360group.cammax360.views.utils.JobsConstants
import kotlinx.android.synthetic.main.load_jobs_image.view.*
import kotlinx.android.synthetic.main.load_jobs_layout.view.*
import kotlinx.android.synthetic.main.row_load_more.view.*

class PropertyJobsAdapter(var mFragment: Fragment) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_ITEM = 1
        private const val VIEW_MORE = 2
        const val LIMIT = 10
    }

    protected val mUserPrefsManager: UserPrefsManager by lazy { UserPrefsManager(mFragment.requireContext()) }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_ITEM) {
            ListViewHolder(parent.inflate(R.layout.load_property_jobs_layout))
        } else {
            MoreViewHolder(parent.inflate(R.layout.row_load_more))

        }

    }

    override fun getItemCount(): Int {
        return 5
    }


    override fun getItemViewType(position: Int): Int {
        return VIEW_ITEM
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
        }
    }



    private inner class MoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindListView(position: Int) {

        }
    }
}