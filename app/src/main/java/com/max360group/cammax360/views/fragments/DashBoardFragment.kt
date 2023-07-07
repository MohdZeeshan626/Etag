package com.max360group.cammax360.views.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.max360group.cammax360.R
import com.max360group.cammax360.viewmodels.BaseViewModel
import com.max360group.cammax360.viewmodels.DashboardViewModel
import com.max360group.cammax360.views.activities.BaseAppCompactActivity
import com.max360group.cammax360.views.activities.doFragmentTransaction
import com.max360group.cammax360.views.adapters.DashboardItemsAdapter
import com.max360group.cammax360.views.fragments.HomeFragment.Companion.INTENT_HOME
import kotlinx.android.synthetic.main.toolbar.*

class DashBoardFragment : BaseRecyclerViewFragment(), DashboardItemsAdapter.DashboardInterface {

    private val mDashboardItemsAdapter  by lazy {
        DashboardItemsAdapter(this)
    }

    private val mDashboardViewModel by lazy {
        ViewModelProvider(this).get(DashboardViewModel::class.java)
    }

    override val layoutId: Int
        get() = R.layout.fragment_base_dashboard_view


    override fun setData(savedInstanceState: Bundle?) {
        //Set  toolbar
        toolbar.visibility=View.GONE

        //Get items
        mDashboardViewModel.getMenu()
    }

    override val viewModel: BaseViewModel?
        get() = mDashboardViewModel

    override fun observeProperties() {
        mDashboardViewModel.onGetMenu().observe(this, Observer {
            mDashboardItemsAdapter.updateData(it)
        })
    }

    override val recyclerViewAdapter: RecyclerView.Adapter<*>?
        get() = mDashboardItemsAdapter

    override val layoutManager: RecyclerView.LayoutManager?
        get() = GridLayoutManager(requireContext(),2)

    override val isShowRecyclerViewDivider: Boolean
        get() = false

    override fun onPullDownToRefresh() {

    }

    override fun onItemClick(name: String) {
        // Send broadcast to select tab
        LocalBroadcastManager.getInstance(requireContext())
            .sendBroadcast(
                Intent(
                    INTENT_HOME
                ).putExtra(HomeFragment.INTENT_DO_TRANSACTION, name))
        }

    }
