package com.max360group.cammax360.views.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.*
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.viewmodels.BaseViewModel
import com.max360group.cammax360.viewmodels.PropertyViewModel
import com.max360group.cammax360.views.activities.BaseAppCompactActivity
import com.max360group.cammax360.views.activities.doFragmentTransaction
import com.max360group.cammax360.views.adapters.*
import com.max360group.cammax360.views.dialgofragments.AddOwnerDialogFragment
import com.max360group.cammax360.views.dialgofragments.AddOwnerDialogFragment.Companion.BUNDLE_OWNER
import com.max360group.cammax360.views.dialgofragments.AddPropertyDialogFragment
import kotlinx.android.synthetic.main.layout_properties_owner.*

class PropertiesOwnerFragment : BaseFragment(), View.OnClickListener,
    OwnerListAdapter.OwnersListListener {

    companion object {
        const val INTENT_PROPERTIES_OWNER = "propertiesOwner"
        const val INTENT_BROADCAST_TYPE = "broadcastType"
        const val INTENT_OWNER_RECORD = "ownerRecord"
        var mOwnersList = ArrayList<UserOwner>()
    }

    private val mPropertyViewModel by lazy {
        ViewModelProvider(this).get(PropertyViewModel::class.java)
    }

    private val mOwnersAdapter by lazy {
        OwnerListAdapter(this)
    }

    private var isBroadcastListen = true

    private var mOwnerListAll = ArrayList<UserOwner>()

    // private var mEditPropertiesList = ArrayList<OwnerProperty>()
    private var mSelectedOwnersIds = ArrayList<String>()

    override val layoutId: Int
        get() = R.layout.layout_properties_owner

    override fun init(savedInstanceState: Bundle?) {
        // Register receiver for updating profile
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(
                mGetUpdateDataBroadcastReceiver,
                IntentFilter(INTENT_PROPERTIES_OWNER)
            )

        //Set on click listener
        btnAddOwner.setOnClickListener(this)
        btnAddExisting.setOnClickListener(this)

        //Set adapter
        rvOwners.adapter = mOwnersAdapter

        //Call api
        mOwnersList.clear()
        if (GeneralFunctions.isInternetConnected(requireContext())) {
            mPropertyViewModel.getAllOwners()
        } else {
            mPropertyViewModel.getOwnersFromDataBase()
        }
    }

    override val viewModel: BaseViewModel?
        get() = mPropertyViewModel

    override fun observeProperties() {
        mPropertyViewModel.onGetAllOwners().observe(this, Observer {
            mOwnerListAll.clear()
            mOwnerListAll.addAll(it)
        })
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btnAddOwner -> {
                (activityContext as BaseAppCompactActivity).doFragmentTransaction(
                    fragment = CreateOwnerFragment.newInstance(""),
                    containerViewId = R.id.flFragContainerMain,
                    enterAnimation = R.animator.slide_right_in,
                    popExitAnimation = R.animator.slide_right_out
                )
            }

            R.id.btnAddExisting -> {
                mSelectedOwnersIds.clear()
                for (i in mOwnersList.indices) {
                    mSelectedOwnersIds.add(mOwnersList[i].id)
                }
                AddOwnerDialogFragment.newInstance(mOwnerListAll, mSelectedOwnersIds)
                    .show(childFragmentManager, "")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Register receiver for updating profile
        LocalBroadcastManager.getInstance(requireContext())
            .unregisterReceiver(mGetUpdateDataBroadcastReceiver)
    }

    fun getEditableDetail(userOwner: List<UserOwner>?) {
        mOwnersList.addAll(userOwner!!)
        mOwnersAdapter.updateData(mOwnersList)
    }

    private val mGetUpdateDataBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, p1: Intent?) {
            context?.let {
                if (isBroadcastListen) {
                    isBroadcastListen = false
                    try {
                        //Get from add property dialog
                        if (p1?.getIntExtra(INTENT_BROADCAST_TYPE, 0) == 0) {
                            val mData =
                                p1.getParcelableArrayListExtra<UserOwner>(BUNDLE_OWNER) as ArrayList<UserOwner>
                            mOwnersList.addAll(mData)
                            mOwnersAdapter.updateData(mOwnersList)


                        } else {
                            val mData =
                                p1?.getParcelableExtra<UserOwner>(INTENT_OWNER_RECORD)
                            mOwnersList.add(mData!!)
                            mOwnersAdapter.updateData(mOwnersList)
                        }
                        android.os.Handler().postDelayed({
                            isBroadcastListen = true
                        }, 300)
                    } catch (e: Exception) {

                    }
                }
            }
        }
    }

    override fun onDeleteClick(position: Int) {
        mOwnersList.removeAt(position)
        mOwnersAdapter.updateData(mOwnersList)
    }

}