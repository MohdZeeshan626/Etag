package com.max360group.cammax360.views.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.max360group.cammax360.R
import com.max360group.cammax360.viewmodels.BaseViewModel
import com.max360group.cammax360.viewmodels.CalenderViewModel
import com.max360group.cammax360.views.activities.BaseAppCompactActivity
import com.max360group.cammax360.views.activities.doFragmentTransaction
import com.max360group.cammax360.views.adapters.NotificationsAdapter
import com.max360group.cammax360.views.calender.MonthlyActivity
import kotlinx.android.synthetic.main.fragment_notifications.*
import kotlinx.android.synthetic.main.toolbar.*

class NotificationFragment : BaseRecyclerViewFragment(),
    NotificationsAdapter.NotificationListener, View.OnClickListener {

    private var mSkip = 0

    private val mCalenderViewModel by lazy {
        ViewModelProvider(this).get(CalenderViewModel::class.java)
    }

    private val mNotificationsAdapter by lazy {
        NotificationsAdapter(this)
    }

    override fun setData(savedInstanceState: Bundle?) {
        //Set toolbar
        toolbar.navigationIcon =
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_back_primary)
        ivToolbarRightIconBell.visibility = View.GONE

        //Get notifications
        mCalenderViewModel.getNotifications(mSkip)

        ivToolbarUserImage.setOnClickListener(this)
        ivToolbarRightIcon.setOnClickListener(this)
    }

    override val recyclerViewAdapter: RecyclerView.Adapter<*>?
        get() = mNotificationsAdapter

    override val layoutManager: RecyclerView.LayoutManager?
        get() = LinearLayoutManager(requireContext())

    override val isShowRecyclerViewDivider: Boolean
        get() = false

    override fun onPullDownToRefresh() {
        mSkip = 0
        //Get notifications
        mCalenderViewModel.getNotifications(mSkip)
    }

    override val layoutId: Int
        get() = R.layout.fragment_notifications

    override val viewModel: BaseViewModel?
        get() = mCalenderViewModel

    override fun observeProperties() {
        mCalenderViewModel.onGetNotifications().observe(this, Observer {
            if (it.isEmpty()) {
                tvNoData.visibility = View.VISIBLE
            } else {
                tvNoData.visibility = View.GONE
            }
            mNotificationsAdapter.updateData(it)
        })

        mCalenderViewModel.onAcceptRejectInvite().observe(this, Observer {
            mCalenderViewModel.onAcceptRejectInvite().observe(this, Observer {
                mCalenderViewModel.getNotifications(mSkip, showLoader = false)
            })
        })
    }

    override fun onLoadMore() {
        mSkip += 10
        //Get notifications
        mCalenderViewModel.getNotifications(mSkip)
    }

    override fun onInviteAccept(token: String) {
        mCalenderViewModel.inviteAcceptReject("Accepted", token)
    }

    override fun onInviteReject(token: String) {
        mCalenderViewModel.inviteAcceptReject("Rejected", token)

    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ivToolbarUserImage -> {
                (activityContext as BaseAppCompactActivity).doFragmentTransaction(
                    fragment = AccountsFragment.newInstance(AccountsFragment.BUNDLE_NORMAl_VIEWS),
                    containerViewId = R.id.flFragContainerMain,
                    enterAnimation = R.animator.slide_right_in,
                    popExitAnimation = R.animator.slide_right_out
                )
            }
            R.id.ivToolbarRightIcon -> {
                startActivity(Intent(requireContext(), MonthlyActivity::class.java))
            }
        }
    }
}