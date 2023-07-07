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
import com.max360group.cammax360.viewmodels.PropertyViewModel
import com.max360group.cammax360.views.adapters.PropertiesAdapter
import com.max360group.cammax360.views.dialgofragments.AlertDialogFragment
import com.max360group.cammax360.views.fragments.CreatePropertyFragment.Companion.BUNDLE_CREATE_PROPERTY
import kotlinx.android.synthetic.main.fragment_job.btnNewJobs
import kotlinx.android.synthetic.main.fragment_job.collapsingToolbarLayout
import kotlinx.android.synthetic.main.fragment_job.tvNoData
import kotlinx.android.synthetic.main.fragment_property.*
import java.lang.Exception

class PropertiesFragment : BaseRecyclerViewFragment(), View.OnClickListener,
    PropertiesAdapter.PropertiesListener {

    companion object {
        const val INTENT_PROPERTIES = "intentProperties"
    }

    private val mPropertiesAdapter by lazy {
        PropertiesAdapter(this)
    }

    private val mPropertyViewModel by lazy {
        ViewModelProvider(this).get(PropertyViewModel::class.java)
    }

    private var mSKip = 0
    private var mPropertyId = ""
    private var mPropertyLocalId = 0

    override fun setData(savedInstanceState: Bundle?) {
        // Register receiver for updating profile
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(
                mGetUpdateDataBroadcastReceiver,
                IntentFilter(INTENT_PROPERTIES)
            )

        //Set on click listener
        btnNewJobs.setOnClickListener(this)

        //Call api
        if (GeneralFunctions.isInternetConnected(requireContext())) {
            mPropertyViewModel.getProperties(mSKip)
        } else {
            mPropertyViewModel.getPropertiesFromLocal()
        }

        //Search text listener
        etSearchProperty.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                if (GeneralFunctions.isInternetConnected(requireContext())) {
                    mPropertyViewModel.getProperties(mSKip, s.toString())
                } else {
                    mPropertyViewModel.searchProperties(s.toString())
                }
            }
        })
    }

    override val recyclerViewAdapter: RecyclerView.Adapter<*>
        get() = mPropertiesAdapter

    override val layoutManager: RecyclerView.LayoutManager
        get() = LinearLayoutManager(requireContext())

    override val isShowRecyclerViewDivider: Boolean
        get() = false

    override fun onPullDownToRefresh() {
        //Call api
        mSKip = 0
        if (GeneralFunctions.isInternetConnected(requireContext())) {
            mPropertyViewModel.getProperties(mSKip)
        } else {
            mPropertyViewModel.getPropertiesFromLocal()
        }

    }

    override val layoutId: Int
        get() = R.layout.fragment_property

    override val viewModel: BaseViewModel?
        get() = mPropertyViewModel

    override fun observeProperties() {
        mPropertyViewModel.onGetProperties().observe(this, Observer {
            if (it.isEmpty()) {
                tvNoData.visibility = View.VISIBLE
            } else {
                tvNoData.visibility = View.GONE
            }
            mPropertiesAdapter.updateData(it, mSKip)
        })

        mPropertyViewModel.onGetSuccess().observe(this, Observer {
            //Call api
            mPropertyViewModel.getProperties(mSKip)
        })
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btnNewJobs -> {
                (activityContext as BaseAppCompactActivity).doFragmentTransaction(
                    fragment = CreatePropertyFragment.newInstance("", from = BUNDLE_CREATE_PROPERTY),
                    containerViewId = R.id.flFragContainerMain,
                    enterAnimation = R.animator.slide_right_in,
                    popExitAnimation = R.animator.slide_right_out
                )
            }
        }
    }

    override fun onItemClick(id: String, propertyLocalId: Int) {
        (activityContext as BaseAppCompactActivity).doFragmentTransaction(
            fragment = PropertyDetailFragment.newInstance(id,propertyLocalId),
            containerViewId = R.id.flFragContainerMain,
            enterAnimation = R.animator.slide_right_in,
            popExitAnimation = R.animator.slide_right_out
        )
    }

    override fun onEditClick(id: String, propertyLocalId: Int) {
        (activityContext as BaseAppCompactActivity).doFragmentTransaction(
            fragment = CreatePropertyFragment.newInstance(id,propertyLocalId,from = BUNDLE_CREATE_PROPERTY),
            containerViewId = R.id.flFragContainerMain,
            enterAnimation = R.animator.slide_right_in,
            popExitAnimation = R.animator.slide_right_out
        )
    }

    override fun onStatus(id: String, propertyLocalId: Int, status: Boolean) {
        if (GeneralFunctions.isInternetConnected(requireContext())) {
            mPropertyViewModel.blockUnblock(id, status)
        }else{
            mPropertyViewModel.updatePropertyStateInLocal(propertyLocalId,status)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (Activity.RESULT_OK == resultCode && 1234 == requestCode) {
            when (intent?.getIntExtra(AlertDialogFragment.INTENT_SUBMIT, 0)) {
                AlertDialogFragment.VALUE_TRUE -> {
                    if (GeneralFunctions.isInternetConnected(requireContext())) {
                        mPropertyViewModel.deleteProperties(mPropertyId)
                    } else {
                        mPropertyViewModel.deletePropertyFromLocal(mPropertyId,mPropertyLocalId)
                    }
                }
            }
        }
    }

    override fun onDelete(id: String, propertyLocalId: Int) {
        mPropertyId = id
        mPropertyLocalId = propertyLocalId
        val mAlertDialogFragment = AlertDialogFragment.newInstance(
            getString(R.string.st_delete_property_message),
            getString(R.string.delete_confirmation)
        )
        mAlertDialogFragment.setTargetFragment(this, 1234)
        mAlertDialogFragment.show(
            parentFragmentManager,
            getString(R.string.dialog)
        )
    }

    override fun onLoadMore() {
        //Call api
        mSKip += 10
        mPropertyViewModel.getProperties(mSKip, showLoader = false)
    }

    fun showSearchView() {
        if (etSearchProperty.visibility == View.VISIBLE) {
            etSearchProperty.visibility = View.GONE
            collapsingToolbarLayout.visibility = View.VISIBLE
        } else {
            etSearchProperty.visibility = View.VISIBLE
            collapsingToolbarLayout.visibility = View.GONE
        }
    }

    private val mGetUpdateDataBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, p1: Intent?) {
            context?.let {
                //Call api
                try {
                    mSKip = 0
                    if (GeneralFunctions.isInternetConnected(requireContext())) {
                        mPropertyViewModel.getProperties(mSKip)
                    }else{
                        mPropertyViewModel.getPropertiesFromLocal()
                    }
                } catch (e: Exception) {
                }
            }
        }
    }
}