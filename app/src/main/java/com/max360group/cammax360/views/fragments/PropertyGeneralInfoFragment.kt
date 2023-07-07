package com.max360group.cammax360.views.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.*
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.viewmodels.BaseViewModel
import com.max360group.cammax360.views.activities.BaseAppCompactActivity
import com.max360group.cammax360.views.activities.doFragmentTransaction
import com.max360group.cammax360.views.adapters.AddressListAdapter
import com.max360group.cammax360.views.adapters.EmailListAdapter
import com.max360group.cammax360.views.fragments.AddAddressFragment.Companion.INTENT_ADDRESS_DATA
import com.max360group.cammax360.views.interfaces.GeneralInfoInterface
import kotlinx.android.synthetic.main.fragment_add_address.*
import kotlinx.android.synthetic.main.layout_owner_general_info.btnAddress
import kotlinx.android.synthetic.main.layout_owner_general_info.btnNewJobs
import kotlinx.android.synthetic.main.layout_owner_general_info.rvAddress
import kotlinx.android.synthetic.main.layout_property_general_info.*
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class PropertyGeneralInfoFragment : BaseFragment(), View.OnClickListener, GeneralInfoInterface {

    companion object {
        const val INTENT_GENERAL_INFO = "generalInfo"
        var mAddressList = ArrayList<OwnerAddressModel>()
    }

    private val mAddressListAdapter by lazy {
        AddressListAdapter(this)
    }

    private var mEditAddressPosition = -1
    private var isBroadCastReceived = false
    private var isPreFillAddress = false

    override val layoutId: Int
        get() = R.layout.layout_property_general_info

    override fun init(savedInstanceState: Bundle?) {
        // Register receiver for updating profile
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(
                mGetUpdateDataBroadcastReceiver,
                IntentFilter(INTENT_GENERAL_INFO)
            )

        //Set adapter
        rvAddress.adapter = mAddressListAdapter

        //Update list
        if (!isPreFillAddress) {
            mAddressList.clear()
            mAddressList.add(OwnerAddressModel(name = getString(R.string.st_primary)))
            mAddressList.add(OwnerAddressModel(name = getString(R.string.st_billing)))
            mAddressListAdapter.updateData(mAddressList)
        }

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

    fun getEditableDetail(property: PropertyDetail) {
        mAddressList.clear()
        mAddressList.add(property.primaryAddress)
        mAddressList.add(property.billingAddress)
        mAddressList.addAll(property.addresses!!)
        mAddressListAdapter.updateData(mAddressList)
    }

    fun getJobLocation(mLatitude: Double, mLongitude: Double) {
        isPreFillAddress = true
        try {
            val mOwnerAddressModel = OwnerAddressModel()
            mOwnerAddressModel.name = getString(R.string.st_primary)
            mOwnerAddressModel.location.coordinates = mutableListOf(mLatitude, mLongitude)
            //Get location formation
            val gcd = Geocoder(context, Locale.getDefault())
            val addresses: List<Address> = gcd.getFromLocation(mLatitude, mLongitude, 1)
            if (addresses.isNotEmpty()) {
                mOwnerAddressModel.formatted = GeneralFunctions.getAddress(
                    mLatitude,
                    mLongitude,
                    requireContext()
                )

                mOwnerAddressModel.state = addresses[0].adminArea
                mOwnerAddressModel.city = if (addresses[0].subLocality != null) {
                    addresses[0].subLocality
                } else {
                    addresses[0].adminArea
                }
                mOwnerAddressModel.country = addresses[0].countryName
            } else {
                // do your stuff
            }

            //Update list and adapter
            mAddressList.clear()
            mAddressList.add(mOwnerAddressModel)
            mAddressList.add(OwnerAddressModel(name = getString(R.string.st_billing)))
            mAddressListAdapter.updateData(mAddressList)
        } catch (e: java.lang.Exception) {
        }


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
                //Call api
                try {
                    if (!isBroadCastReceived) {
                        isBroadCastReceived = true
                        val mAddress =
                            p1?.getParcelableExtra<OwnerAddressModel>(INTENT_ADDRESS_DATA)
                        if (mEditAddressPosition != -1) {
                            mAddressList[mEditAddressPosition] = mAddress!!
                            mAddressListAdapter.updateData(mAddressList)
                        } else {
                            mAddressList.add(mAddress!!)
                            mAddressListAdapter.updateData(mAddressList)
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