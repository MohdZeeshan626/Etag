package com.max360group.cammax360.views.fragments

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.max360group.cammax360.R
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.viewmodels.BaseViewModel
import com.max360group.cammax360.views.activities.BaseAppCompactActivity
import com.max360group.cammax360.views.activities.doFragmentTransaction
import com.max360group.cammax360.viewmodels.OwnerViewModel
import com.max360group.cammax360.views.adapters.OwnersAdapter
import com.max360group.cammax360.views.dialgofragments.AlertDialogFragment
import com.max360group.cammax360.views.dialgofragments.SendInviteDialogFragment
import kotlinx.android.synthetic.main.fragment_job.btnNewJobs
import kotlinx.android.synthetic.main.fragment_job.tvNoData
import kotlinx.android.synthetic.main.fragment_job.collapsingToolbarLayout
import kotlinx.android.synthetic.main.fragment_owner.*
import java.lang.Exception

class OwnersFragment : BaseRecyclerViewFragment(), View.OnClickListener,
    OwnersAdapter.OwnerListener {

    companion object {
        const val INTENT_OWNER_FRAGMENT = "ownerFragment"
    }

    private val mOwnersAdapter by lazy {
        OwnersAdapter(this)
    }

    private val mOwnerViewModel by lazy {
        ViewModelProvider(this).get(OwnerViewModel::class.java)
    }

    private var mSKip = 0
    private var mOwnerId=""
    private var mOwnerLocalId=0

    override fun setData(savedInstanceState: Bundle?) {
        // Register receiver for updating profile
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(
                mGetUpdateDataBroadcastReceiver,
                IntentFilter(INTENT_OWNER_FRAGMENT))

        //Set on click listener
        btnNewJobs.setOnClickListener(this)

        //Call api
        if (GeneralFunctions.isInternetConnected(requireContext())) {
            mOwnerViewModel.getOwners(skip = mSKip)
        } else {
            mOwnerViewModel.getOwnersFromDataBase()
        }

        //Search text listener
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                //Call api
                if (GeneralFunctions.isInternetConnected(requireContext())) {
                    mOwnerViewModel.getOwners(s.toString(),mSKip)
                } else {
                    mOwnerViewModel.getSearchOwners(s.toString())
                }
            }
        })
    }

    override val recyclerViewAdapter: RecyclerView.Adapter<*>
        get() = mOwnersAdapter

    override val layoutManager: RecyclerView.LayoutManager
        get() = LinearLayoutManager(requireContext())

    override val isShowRecyclerViewDivider: Boolean
        get() = false

    override fun onPullDownToRefresh() {
        //Call api
        mSKip = 0
        if (GeneralFunctions.isInternetConnected(requireContext())) {
            mOwnerViewModel.getOwners(skip = mSKip)
        } else {
            mOwnerViewModel.getOwnersFromDataBase()
        }
    }

    override val layoutId: Int
        get() = R.layout.fragment_owner

    override val viewModel: BaseViewModel
        get() = mOwnerViewModel

    override fun observeProperties() {
        mOwnerViewModel.onGetOwnersList().observe(this, Observer {
            if (it.isEmpty()){
                tvNoData.visibility=View.VISIBLE
            }else{
                tvNoData.visibility=View.GONE
            }
            mOwnersAdapter.updateData(it, mSKip)
        })
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btnNewJobs -> {
                (activityContext as BaseAppCompactActivity).doFragmentTransaction(
                    fragment = CreateOwnerFragment.newInstance(""),
                    containerViewId = R.id.flFragContainerMain,
                    enterAnimation = R.animator.slide_right_in,
                    popExitAnimation = R.animator.slide_right_out
                )
            }
        }
    }

    override fun onItemClick(id: String,ownerLocalId:Int) {
        (activityContext as BaseAppCompactActivity).doFragmentTransaction(
            fragment = OwnerDetailFragment.newInstance(id,ownerLocalId),
            containerViewId = R.id.flFragContainerMain,
            enterAnimation = R.animator.slide_right_in,
            popExitAnimation = R.animator.slide_right_out
        )
    }

    override fun onLoadMore() {
        //Call api
        mSKip += 10
        mOwnerViewModel.getOwners(skip = mSKip, showLoader = false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (Activity.RESULT_OK == resultCode && 1234 == requestCode) {
            when (intent?.getIntExtra(AlertDialogFragment.INTENT_SUBMIT, 0)) {
                AlertDialogFragment.VALUE_TRUE -> {
                    //Call api
                    if (GeneralFunctions.isInternetConnected(requireContext())) {
                        mOwnerViewModel.deleteOwner(mOwnerId)
                    } else {
                        mOwnerViewModel.deleteOwnerFromLocal(mOwnerId,mOwnerLocalId)
                    }

                }
            }
        }
    }

    override fun onDeleteOwner(id: String, ownerLocalId: Int) {
        mOwnerId=id
        mOwnerLocalId=ownerLocalId
        val mAlertDialogFragment = AlertDialogFragment.newInstance(
            getString(R.string.st_delete_owner_message),
            getString(R.string.delete_confirmation)
        )
        mAlertDialogFragment.setTargetFragment(this, 1234)
        mAlertDialogFragment.show(
            parentFragmentManager,
            getString(R.string.dialog)
        )
    }

    override fun onStatus(id: String, localId: Int, status: Boolean) {
        if (GeneralFunctions.isInternetConnected(requireContext())){
            mOwnerViewModel.blockOwner(id,status)
        }else{
            mOwnerViewModel.updateStateFromLocal(localId,status)
        }
    }

    override fun onSendInvite(id: String, localId:Int,sendInvite: Boolean) {
        SendInviteDialogFragment(id,localId,sendInvite).show(childFragmentManager,"")
    }

    override fun onEditDetail(id: String, ownerLocalId: Int) {
        (activityContext as BaseAppCompactActivity).doFragmentTransaction(
            fragment = CreateOwnerFragment.newInstance(id,ownerLocalId),
            containerViewId = R.id.flFragContainerMain,
            enterAnimation = R.animator.slide_right_in,
            popExitAnimation = R.animator.slide_right_out
        )
    }

    fun showSearchView() {
        if (etSearch.visibility == View.VISIBLE) {
            etSearch.visibility = View.GONE
            collapsingToolbarLayout.visibility = View.VISIBLE
        } else {
            etSearch.visibility = View.VISIBLE
            collapsingToolbarLayout.visibility = View.GONE
        }
    }

    private val mGetUpdateDataBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, p1: Intent?) {
            context?.let {
                //Call api
                try {
                    mSKip = 0
                    //Call api
                    if (GeneralFunctions.isInternetConnected(requireContext())) {
                        mOwnerViewModel.getOwners(skip = mSKip)
                    } else {
                        mOwnerViewModel.getOwnersFromDataBase()
                    }
                } catch (e: Exception) {
                }
            }
        }
    }
}