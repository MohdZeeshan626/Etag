package com.max360group.cammax360.views.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.View
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.max360group.cammax360.R
import com.max360group.cammax360.viewmodels.BaseViewModel
import com.max360group.cammax360.viewmodels.JobsViewModel
import com.max360group.cammax360.views.activities.BaseAppCompactActivity
import com.max360group.cammax360.views.activities.doFragmentTransaction
import com.max360group.cammax360.views.adapters.JobsAdapter
import com.max360group.cammax360.views.dialgofragments.CameraDialogFragment.CameraDialogFragment
import com.max360group.cammax360.views.dialgofragments.CameraDialogFragment.CameraDialogFragment.Companion.BUNDLE_SIMPLE
import com.max360group.cammax360.views.interfaces.JobsListener
import kotlinx.android.synthetic.main.fragment_job.*
import kotlinx.coroutines.Job
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.max360group.cammax360.services.CheckInternetService
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.views.fragments.SelectLocationFragment.Companion.BUNDLE_CREATE
import java.lang.Exception

class JobsFragment : BaseRecyclerViewFragment(), View.OnClickListener, JobsListener {

    private val mJobsAdapter by lazy {
        JobsAdapter(this)
    }

    private val mJobsViewModel by lazy {
        ViewModelProvider(this).get(JobsViewModel::class.java)
    }

    private var mSkip = 0
    private var mViewType = true
    private var mSortValue = "{\"endDate\":\"1\"}"//default value
    private var mSortBy = 1//default value
    private var mSortKind = "endDate"//default value


    override fun setData(savedInstanceState: Bundle?) {
        //Set click listener
        ivListType.setOnClickListener(this)
        ivGridType.setOnClickListener(this)
        btnNewJobs.setOnClickListener(this)
        ivFilterUp.setOnClickListener(this)
        ivFilterDown.setOnClickListener(this)
        tvDueDate.setOnClickListener(this)

        // Register receiver for updating profile
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(
                mGetUpdateDataBroadcastReceiver,
                IntentFilter(JobDetailsFragment.BUNDLE_JOB_DETAIL_INTENT)
            )

        //Search text listener
        etSearchJob.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                //Call api
                if (GeneralFunctions.isInternetConnected(requireContext())) {
                    mJobsViewModel.getJobs(mSkip, mSortValue, s.toString())
                } else {
                    mJobsViewModel.searchJobs(s.toString())
                }
            }
        })

        //Call api
        if (GeneralFunctions.isInternetConnected(requireContext())) {
            mJobsViewModel.getJobs(mSkip, mSortValue)
        } else {
            mJobsViewModel.getJobsFromDataBase()
        }

    }

    override val recyclerViewAdapter: RecyclerView.Adapter<*>
        get() = mJobsAdapter

    override val layoutManager: RecyclerView.LayoutManager
        get() = LinearLayoutManager(requireContext())

    override val isShowRecyclerViewDivider: Boolean
        get() = false

    override fun onPullDownToRefresh() {
        mSkip = 0
        //Call api
        if (GeneralFunctions.isInternetConnected(requireContext())) {
            mJobsViewModel.getJobs(mSkip, mSortValue)
        } else {
            mJobsViewModel.getJobsFromDataBase()
        }
    }

    override val layoutId: Int
        get() = R.layout.fragment_job

    override val viewModel: BaseViewModel?
        get() = mJobsViewModel

    override fun observeProperties() {
        mJobsViewModel.onGetJobs().observe(this, Observer {
            if (it.isEmpty()) {
                if (mSkip == 0) {
                    tvNoData.visibility = View.VISIBLE
                }
            } else {
                tvNoData.visibility = View.GONE
            }
            tvJobsValue.text = it.size.toString()
            mJobsAdapter.updateData(it, mViewType, mSkip)
        })

        mJobsViewModel.onMediaCount().observe(this, Observer {
            tvJobsValue.text = it.count.toString()
            tvPhotosValue.text = it.mediasCount.photo.toString()
        })
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ivListType -> {
                mViewType = false
                ivListType.setImageResource(R.drawable.ic_list)
                ivGridType.setImageResource(R.drawable.ic_disbale_dashboard)
                mJobsAdapter.updateData(isShowFullDetail = false, mSkip = mSkip)
            }
            R.id.ivGridType -> {
                mViewType = true
                ivListType.setImageResource(R.drawable.ic_list_view)
                ivGridType.setImageResource(R.drawable.ic_dashboard_fill)
                mJobsAdapter.updateData(isShowFullDetail = true, mSkip = mSkip)
            }
            R.id.btnNewJobs -> {
                (activityContext as BaseAppCompactActivity).doFragmentTransaction(
                    fragment = SelectLocationFragment.newInstance(BUNDLE_CREATE),
                    containerViewId = R.id.flFragContainerMain,
                    enterAnimation = R.animator.slide_right_in,
                    popExitAnimation = R.animator.slide_right_out
                )
            }
            R.id.ivFilterUp -> {
                mSkip = 0
                mSortBy = 1//Asc
                //Call Api
                if (GeneralFunctions.isInternetConnected(requireContext())) {
                    mSortValue = "{\"$mSortKind\":\"$mSortBy\"}"
                    mJobsViewModel.getJobs(mSkip, mSortValue)
                } else {
                    mJobsViewModel.filterJob(mSortBy, mSortKind)
                }

                ivFilterUp.setColorFilter(ContextCompat.getColor(requireContext(), R.color.black))
                ivFilterDown.setColorFilter(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.colorDisabledText
                    )
                )
            }
            R.id.ivFilterDown -> {
                mSkip = 0
                mSortBy = -1//Desc
                //Call Api
                if (GeneralFunctions.isInternetConnected(requireContext())) {
                    mSortValue = "{\"$mSortKind\":\"$mSortBy\"}"
                    mJobsViewModel.getJobs(mSkip, mSortValue)
                } else {
                    mJobsViewModel.filterJob(mSortBy, mSortKind)
                }

                ivFilterUp.setColorFilter(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.colorDisabledText
                    )
                )
                ivFilterDown.setColorFilter(ContextCompat.getColor(requireContext(), R.color.black))
            }
            R.id.tvDueDate -> {
                popUpMenu()
            }
        }
    }

    private fun popUpMenu() {
        var wrapper: Context =
            ContextThemeWrapper(requireContext(), R.style.StylePopupMenu)
        var popUp = PopupMenu(wrapper, tvDueDate, Gravity.CENTER)
        popUp.menuInflater.inflate(R.menu.sorting_pop_up_menu, popUp.menu)
        //Set Click Listener on Popup Menu Item
        popUp.setOnMenuItemClickListener { myItem ->
            when (myItem!!.itemId) {
                R.id.actionDueDate -> {
                    tvDueDate.text = getString(R.string.st_due_date)
                    mSortKind = "endDate"
                    if (GeneralFunctions.isInternetConnected(requireContext())) {
                        mSortValue = "{\"$mSortKind\":\"$mSortBy\"}"
                        mJobsViewModel.getJobs(mSkip, mSortValue)
                    } else {
                        mJobsViewModel.filterJob(mSortBy, mSortKind)
                    }
                }

                R.id.actionTitle -> {
                    tvDueDate.text = getString(R.string.st_job_title)
                    mSortKind = "title"
                    if (GeneralFunctions.isInternetConnected(requireContext())) {
                        mSortValue = "{\"$mSortKind\":\"$mSortBy\"}"
                        mJobsViewModel.getJobs(mSkip, mSortValue)
                    } else {
                        mJobsViewModel.filterJob(mSortBy, mSortKind)
                    }
                }
            }
            true
        }
        popUp.show()
    }

    fun showSearchView() {
        if (etSearchJob.visibility == View.VISIBLE) {
            etSearchJob.visibility = View.GONE
            collapsingToolbarLayout.visibility = View.VISIBLE
        } else {
            etSearchJob.visibility = View.VISIBLE
            collapsingToolbarLayout.visibility = View.GONE
        }

    }

    override fun onItemClick(id: String, mLocalId: String) {
        (activityContext as BaseAppCompactActivity).doFragmentTransaction(
            fragment = JobDetailsFragment.newInstance(id, mLocalId),
            containerViewId = R.id.flFragContainerMain,
            enterAnimation = R.animator.slide_right_in,
            popExitAnimation = R.animator.slide_right_out
        )
    }

    override fun onCameraClick(type: Int, position: Int, id: String, mJobLocalId: Int) {
        if (type == 0) {
            (activityContext as BaseAppCompactActivity).doFragmentTransaction(
                fragment = CameraDialogFragment.newInstance(
                    BUNDLE_SIMPLE,
                    "",
                    id,
                    mJobLocalId.toString()
                ),
                containerViewId = R.id.flFragContainerMain,
                enterAnimation = R.animator.slide_right_in,
                popExitAnimation = R.animator.slide_right_out
            )
        } else {
                (activityContext as BaseAppCompactActivity).doFragmentTransaction(
                fragment = RecordVideoFragment.newInstance(id, mJobLocalId.toString()),
                containerViewId = R.id.flFragContainerMain,
                enterAnimation = R.animator.slide_right_in,
                popExitAnimation = R.animator.slide_right_out
            )
        }
}

private val mGetUpdateDataBroadcastReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context?, p1: Intent?) {
        context?.let {
            //Call api
            try {
                if (GeneralFunctions.isInternetConnected(requireContext())) {
                    mJobsViewModel.getJobs(mSkip, mSortValue)
                } else {
                    mJobsViewModel.getJobsFromDataBase()
                }

            } catch (e: Exception) {

            }
        }
    }
}

override fun onApiCall() {
    mSkip += 10
    mJobsViewModel.getJobs(mSkip, mSortValue)
}

override fun onMapCall(lat: Double, long: Double) {
    val uri = "http://maps.google.com/maps?daddr=$lat,$long (Where the party is at)"
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
    intent.setPackage("com.google.android.apps.maps")
    startActivity(intent)
}
}