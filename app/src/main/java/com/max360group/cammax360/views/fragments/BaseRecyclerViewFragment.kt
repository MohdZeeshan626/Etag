package com.max360group.cammax360.views.fragments

import android.os.Bundle
import android.os.Handler
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.max360group.cammax360.R
import com.max360group.cammax360.views.utils.DefaultDividerItemDecoration
import kotlinx.android.synthetic.main.fragment_base_recycler_view.*


/**
 * Created by Mukesh on 20/3/18.
 */
abstract class BaseRecyclerViewFragment : BaseFragment() {

    protected val mDividerItemDecoration by lazy {
        DefaultDividerItemDecoration(
            LinearLayoutManager.VERTICAL,
            ContextCompat.getDrawable(
                activityContext,
                R.drawable.drawable_recyclerview_divider
            )!!
        )
    }

    override fun init(savedInstanceState: Bundle?) {
        // Set SwipeRefreshLayout
        if (null != swipeRefreshLayout) {
            val value = TypedValue()
            requireContext().theme.resolveAttribute(R.attr.primaryTextColor, value, true)

            swipeRefreshLayout!!.setColorSchemeResources(
                value.resourceId, value.resourceId,
                value.resourceId, value.resourceId
            )
            swipeRefreshLayout!!.setOnRefreshListener { onPullDownToRefresh() }
        }

        // Set RecylerView
        recyclerView.layoutManager =
            if (null == layoutManager) LinearLayoutManager(activity) else (layoutManager)

        if (isShowRecyclerViewDivider) {
            recyclerView.addItemDecoration(mDividerItemDecoration)
        }

        recyclerView.adapter = recyclerViewAdapter
        setData(savedInstanceState)

        // Observe SwipeRefreshLayout
        viewModel?.isShowSwipeRefreshLayout()?.observe(viewLifecycleOwner, Observer {
            if (it!!) {
                showSwipeRefreshLoader()
            } else {
                hideSwipeRefreshLoader()
            }
        })

        // Observe retrofit errors
        viewModel?.getRetrofitErrorDataMessage()?.observe(viewLifecycleOwner, Observer {
            showNoDataText(it?.errorResId, it?.errorMessage)
        })
    }

    private fun showNoDataText(resId: Int? = null, message: String? = null) {
        if (null == resId && null == message) {
            hideNoDataText()
        } else {
            if (getDefaultAdapterCount() < recyclerViewAdapter?.itemCount!!) {
                showMessage(resId, message)
            } else {
                tvNoData?.visibility = View.VISIBLE
                tvNoData?.text = message ?: getString(resId!!)
            }
        }
    }

    private fun getDefaultAdapterCount(): Int {
        return 1
    }

    private fun hideNoDataText() {
        tvNoData?.visibility = View.GONE
    }

    private fun showSwipeRefreshLoader() {
        swipeRefreshLayout?.post {
            if (null != swipeRefreshLayout) {
                swipeRefreshLayout!!.isRefreshing = true
            }
        }
    }

    private fun hideSwipeRefreshLoader() {
        Handler().postDelayed({
            if (null != swipeRefreshLayout && swipeRefreshLayout!!.isRefreshing) {
                swipeRefreshLayout!!.isRefreshing = false
            }
        }, 50)
    }

    abstract fun setData(savedInstanceState: Bundle?)

    abstract val recyclerViewAdapter: RecyclerView.Adapter<*>?

    abstract val layoutManager: RecyclerView.LayoutManager?

    abstract val isShowRecyclerViewDivider: Boolean

    abstract fun onPullDownToRefresh()

}