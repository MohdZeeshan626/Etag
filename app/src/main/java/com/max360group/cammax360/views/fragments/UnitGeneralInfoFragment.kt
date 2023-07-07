package com.max360group.cammax360.views.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.OwnerAddressModel
import com.max360group.cammax360.repository.models.PropertyRecord
import com.max360group.cammax360.repository.models.UnitRecord
import com.max360group.cammax360.viewmodels.BaseViewModel
import com.max360group.cammax360.views.activities.BaseAppCompactActivity
import com.max360group.cammax360.views.activities.doFragmentTransaction
import com.max360group.cammax360.views.adapters.AddressListAdapter
import com.max360group.cammax360.views.fragments.AddAddressFragment.Companion.INTENT_ADDRESS_DATA
import com.max360group.cammax360.views.interfaces.GeneralInfoInterface
import kotlinx.android.synthetic.main.layout_owner_general_info.*
import kotlinx.android.synthetic.main.layout_owner_general_info.rvAddress
import java.lang.Exception

class UnitGeneralInfoFragment : BaseFragment(), View.OnClickListener, GeneralInfoInterface {

    companion object {
        const val INTENT_GENERAL_INFO = "generalInfo"
        var mAddressList = ArrayList<OwnerAddressModel>()
    }

    private val mAddressListAdapter by lazy {
        AddressListAdapter(this)
    }

    private var mEditAddressPosition = -1
    private var isBroadCastReceived = false

    override val layoutId: Int
        get() = R.layout.layout_unit_general_info

    override fun init(savedInstanceState: Bundle?) {
        // Register receiver for updating profile
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(
                mGetUpdateDataBroadcastReceiver,
                IntentFilter(INTENT_GENERAL_INFO)
            )

        //Set adapter
        rvAddress.adapter = mAddressListAdapter

        mAddressList.clear()
        mAddressList.add(OwnerAddressModel(name = "Primary"))
        mAddressListAdapter.updateData(mAddressList, 1)

        //Set click listener
        btnAddress.setOnClickListener(this)
    }

    override val viewModel: BaseViewModel?
        get() = null

    override fun observeProperties() {
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btnAddress -> {
                mEditAddressPosition = -1
                (activityContext as BaseAppCompactActivity).doFragmentTransaction(
                    fragment = AddAddressFragment.newInstance(OwnerAddressModel()),
                    containerViewId = R.id.flFragContainerMain,
                    enterAnimation = R.animator.slide_right_in,
                    popExitAnimation = R.animator.slide_right_out
                )
            }
        }
    }

    override fun onDeleteEmail(position: Int) {
    }

    override fun onDeletePhone(position: Int) {
    }

    override fun onDeleteAddress(position: Int) {
        mAddressList.removeAt(position)
        mAddressListAdapter.updateData(mAddressList)
    }

    override fun onEditAddress(position: Int, address: OwnerAddressModel) {
        mEditAddressPosition = position
        (activityContext as BaseAppCompactActivity).doFragmentTransaction(
            fragment = AddAddressFragment.newInstance(address),
            containerViewId = R.id.flFragContainerMain,
            enterAnimation = R.animator.slide_right_in,
            popExitAnimation = R.animator.slide_right_out
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Register receiver for updating profile
        LocalBroadcastManager.getInstance(requireContext())
            .unregisterReceiver(mGetUpdateDataBroadcastReceiver)
    }

    fun getEditableDetail(unitRecord: UnitRecord) {
        mAddressList.clear()
        mAddressList.add(unitRecord.primaryAddress)
        mAddressList.addAll(unitRecord.addresses!!)
        mAddressListAdapter.updateData(mAddressList)
    }


    private val mGetUpdateDataBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, p1: Intent?) {
            context?.let {
                //Call api
                try {
                    if (!isBroadCastReceived) {
                        isBroadCastReceived = true
                        val mAddress =
                            p1?.getParcelableExtra<OwnerAddressModel>(INTENT_ADDRESS_DATA)
                        if (mEditAddressPosition != -1) {
                            mAddressList[mEditAddressPosition] = mAddress!!
                            mAddressListAdapter.updateData(mAddressList, 1)
                        } else {
                            mAddressList.add(mAddress!!)
                            mAddressListAdapter.updateData(mAddressList, 1)
                        }
                    }
                    Handler().postDelayed({
                        isBroadCastReceived = false
                    }, 1000)
                } catch (e: Exception) {

                }
            }
        }
    }
}