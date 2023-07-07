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
import com.max360group.cammax360.repository.models.PropertyDetail
import com.max360group.cammax360.repository.models.OwnerProperty
import com.max360group.cammax360.repository.models.UnitRecord
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.viewmodels.BaseViewModel
import com.max360group.cammax360.viewmodels.OwnerViewModel
import com.max360group.cammax360.views.activities.BaseAppCompactActivity
import com.max360group.cammax360.views.activities.doFragmentTransaction
import com.max360group.cammax360.views.adapters.*
import com.max360group.cammax360.views.dialgofragments.AddPropertyDialogFragment
import com.max360group.cammax360.views.dialgofragments.AddPropertyDialogFragment.Companion.BUNDLE_PROPERTY
import com.max360group.cammax360.views.fragments.CreatePropertyFragment.Companion.BUNDLE_CREATE_OWNER
import kotlinx.android.synthetic.main.layout_owner_properties.*

class OwnerPropertiesFragment : BaseFragment(), View.OnClickListener,
    PropertyListAdapter.PropertyListener {

    companion object {
        const val INTENT_OWNER_PROPERTY = "ownerProperty"
        const val INTENT_BROADCAST_TYPE = "broadcastType"
        const val INTENT_UNIT_RECORD = "unitRecord"
        const val INTENT_PROPERTY = "property"
        var mPropertyList = ArrayList<PropertyDetail>()
    }

    private var isBroadcastListen = true
    private var mPropertiesListAll = ArrayList<PropertyDetail>()
    private var mEditPropertiesList = ArrayList<OwnerProperty>()
    private var mSelectedPropertiesId = ArrayList<String>()

    private val mPropertyListAdapter by lazy {
        PropertyListAdapter(this)
    }

    private val mCreateOwnerViewModel by lazy {
        ViewModelProvider(this).get(OwnerViewModel::class.java)
    }

    override val layoutId: Int
        get() = R.layout.layout_owner_properties

    override fun init(savedInstanceState: Bundle?) {
        // Register receiver for updating profile
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(
                mGetUpdateDataBroadcastReceiver,
                IntentFilter(INTENT_OWNER_PROPERTY)
            )
        mPropertyList.clear()

        //Set adapter
        rvProperty.adapter = mPropertyListAdapter

        //Set on click listener
        btnNewProperty.setOnClickListener(this)
        btnExistingProperty.setOnClickListener(this)

        //Get properties
        if (GeneralFunctions.isInternetConnected(requireContext())) {
            mCreateOwnerViewModel.getProperties()
        } else {
            mCreateOwnerViewModel.getPropertiesFromLocal()
        }

    }

    override val viewModel: BaseViewModel
        get() = mCreateOwnerViewModel

    override fun observeProperties() {
        mCreateOwnerViewModel.onGetPropertiesData().observe(this, Observer {
            mPropertyList.clear()
            mPropertiesListAll.clear()
            mPropertiesListAll.addAll(it.records!!)

            //Set editable data if comes for edit detail
            setEditableData()
        })
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btnNewProperty -> {
                (activityContext as BaseAppCompactActivity).doFragmentTransaction(
                    fragment = CreatePropertyFragment.newInstance("", from = BUNDLE_CREATE_OWNER),
                    containerViewId = R.id.flFragContainerMain,
                    enterAnimation = R.animator.slide_right_in,
                    popExitAnimation = R.animator.slide_right_out
                )
            }
            R.id.btnExistingProperty -> {
                mSelectedPropertiesId.clear()
                for (i in mPropertyList.indices) {
                    mSelectedPropertiesId.add(mPropertyList[i].id)
                }
                AddPropertyDialogFragment.newInstance(mPropertiesListAll, mSelectedPropertiesId)
                    .show(childFragmentManager, "")
            }
        }
    }

    override fun onAddClick(id: String) {
        (activityContext as BaseAppCompactActivity).doFragmentTransaction(
            fragment = CreateUnitFragment.newInstance(id),
            containerViewId = R.id.flFragContainerMain,
            enterAnimation = R.animator.slide_right_in,
            popExitAnimation = R.animator.slide_right_out
        )
    }

    override fun onDeleteProperty(position: Int) {
        mPropertyList.removeAt(position)
        mPropertyListAdapter.updateData(mPropertyList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Register receiver for updating profile
        LocalBroadcastManager.getInstance(requireContext())
            .unregisterReceiver(mGetUpdateDataBroadcastReceiver)
    }

    fun getEditableDetail(properties: List<OwnerProperty>?) {
        mEditPropertiesList.clear()
        mEditPropertiesList.addAll(properties!!)

        //Get properties
        mCreateOwnerViewModel.getProperties()
    }

    private fun setEditableData() {
        for (i in mPropertiesListAll.indices) {
            for (j in mEditPropertiesList.indices) {
                if (mEditPropertiesList[j].propertyId == mPropertiesListAll[i].id) {

                    //To filter out the selected units of selected property
                    for (k in mPropertiesListAll[i].propertyUnits!!.indices) {
                        mPropertiesListAll[i].propertyUnits!![k].isChecked =
                            mEditPropertiesList[j].propertyUnits!!.contains(mPropertiesListAll[i].propertyUnits!![k].id)
                    }
                    mPropertyList.add(mPropertiesListAll[i])
                }
            }
        }
        mPropertyListAdapter.updateData(mPropertyList)
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
                                p1.getParcelableArrayListExtra<PropertyDetail>(BUNDLE_PROPERTY) as ArrayList<PropertyDetail>
                            mPropertyList.addAll(mData)
                            mPropertyListAdapter.updateData(mPropertyList)


                        } else if (p1?.getIntExtra(INTENT_BROADCAST_TYPE, 0) == 2) {
                            val mData =
                                p1.getParcelableExtra<PropertyDetail>(INTENT_PROPERTY)
                            mPropertyList.add(mData!!)
                            mPropertyListAdapter.updateData(mPropertyList)
                        } else {
                            //Get from create new unit
                            val mUnit = p1?.getParcelableExtra<UnitRecord>(INTENT_UNIT_RECORD)
                            for (i in mPropertyList.indices) {
                                if (mUnit!!.propertyId == mPropertyList[i].id) {
                                    mPropertyList[i].propertyUnits!!.add(
                                        UnitRecord(
                                            id = mUnit.id,
                                            name = mUnit.name,
                                            primaryAddress = mUnit.primaryAddress!!
                                        )
                                    )
                                    mPropertyListAdapter.updateData(mPropertyList)
                                    break
                                }
                            }
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
}