package com.max360group.cammax360.views.fragments

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.UnitRecord
import com.max360group.cammax360.viewmodels.BaseViewModel
import com.max360group.cammax360.viewmodels.OwnerViewModel
import com.max360group.cammax360.viewmodels.PropertyViewModel
import com.max360group.cammax360.views.activities.BaseAppCompactActivity
import com.max360group.cammax360.views.activities.doFragmentTransaction
import com.max360group.cammax360.views.adapters.*
import com.max360group.cammax360.views.dialgofragments.AlertDialogFragment
import kotlinx.android.synthetic.main.layout_properties_unit.*

class PropertiesUnitFragment : BaseFragment(), View.OnClickListener,
    PropertyUnitListAdapter.UnitListener {

    companion object {
        const val BROADCAST_UNIT_INTENT = "unitIntent"
        const val BROADCAST_UNIT_RECORD = "unitRecord"
         var mUnitList = ArrayList<UnitRecord>()
    }

    private val mPropertyUnitListAdapter by lazy {
        PropertyUnitListAdapter(this)
    }

    private val mCreateOwnerViewModel by lazy {
        ViewModelProvider(this).get(OwnerViewModel::class.java)
    }

    private val mPropertyViewModel by lazy {
        ViewModelProvider(this).get(PropertyViewModel::class.java)
    }


    private var isBroadcastListen = true
    private var mUnitId=""
    private var mUnitPosition=-1


    override val layoutId: Int
        get() = R.layout.layout_properties_unit

    override fun init(savedInstanceState: Bundle?) {
        // Register receiver for updating profile
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(
                mGetUpdateDataBroadcastReceiver,
                IntentFilter(BROADCAST_UNIT_INTENT)
            )

        //Set on click listener
        btnAddUnit.setOnClickListener(this)

        //Set adapter
        mUnitList.clear()
        rvUnits.adapter = mPropertyUnitListAdapter
    }

    override val viewModel: BaseViewModel
        get() = mCreateOwnerViewModel

    override fun observeProperties() {
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btnAddUnit -> {
                (activityContext as BaseAppCompactActivity).doFragmentTransaction(
                    fragment = CreateUnitFragment.newInstance(""),
                    containerViewId = R.id.flFragContainerMain,
                    enterAnimation = R.animator.slide_right_in,
                    popExitAnimation = R.animator.slide_right_out
                )
            }
        }
    }

    override fun onEdit(unitRecord: UnitRecord, position: Int) {
        mUnitPosition=position
        (activityContext as BaseAppCompactActivity).doFragmentTransaction(
            fragment = CreateUnitFragment.newInstance(unitRecord.propertyId,unitId = unitRecord.id),
            containerViewId = R.id.flFragContainerMain,
            enterAnimation = R.animator.slide_right_in,
            popExitAnimation = R.animator.slide_right_out
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (Activity.RESULT_OK == resultCode && 1234 == requestCode) {
            when (intent?.getIntExtra(AlertDialogFragment.INTENT_SUBMIT, 0)) {
                AlertDialogFragment.VALUE_TRUE -> {
                    mUnitList.removeAt(mUnitPosition)
                    mPropertyUnitListAdapter.updateFunction(mUnitList)
                    mPropertyViewModel.deletePropertyUnit(mUnitId,false)
                    mUnitPosition=-1
                }
            }
        }
    }

    override fun onDelete(position: Int, id: String) {
        mUnitId=id
        mUnitPosition=position
        val mAlertDialogFragment = AlertDialogFragment.newInstance(
            getString(R.string.st_delete_unit_message),
            getString(R.string.delete_confirmation)
        )
        mAlertDialogFragment.setTargetFragment(this, 1234)
        mAlertDialogFragment.show(
            parentFragmentManager,
            getString(R.string.dialog)
        )
    }

    fun getEditableDetail(units: List<UnitRecord>?) {
        mUnitList.clear()
        mUnitList.addAll(units!!)
        mPropertyUnitListAdapter.updateFunction(mUnitList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Register receiver for updating profile
        LocalBroadcastManager.getInstance(requireContext())
            .unregisterReceiver(mGetUpdateDataBroadcastReceiver)
    }

    private val mGetUpdateDataBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, p1: Intent?) {
            context?.let {
                if (isBroadcastListen) {
                    isBroadcastListen = false
                    try {
                        //Get from add property dialog
                        val mData =
                            p1?.getParcelableExtra<UnitRecord>(BROADCAST_UNIT_RECORD)
                        if (mUnitPosition!=-1){
                            mUnitList[mUnitPosition] = mData!!
                            mPropertyUnitListAdapter.updateFunction(mUnitList)
                            mUnitPosition=-1
                        }else{
                            mUnitList.add(mData!!)
                            mPropertyUnitListAdapter.updateFunction(mUnitList)
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