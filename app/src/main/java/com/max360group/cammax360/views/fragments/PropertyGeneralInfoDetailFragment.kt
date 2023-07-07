package com.max360group.cammax360.views.fragments

import android.os.Bundle
import android.view.View
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.OwnerAddressModel
import com.max360group.cammax360.repository.models.PropertyDetail
import com.max360group.cammax360.repository.models.PropertyRecord
import com.max360group.cammax360.repository.models.UserOwner
import com.max360group.cammax360.viewmodels.BaseViewModel
import com.max360group.cammax360.views.activities.BaseAppCompactActivity
import com.max360group.cammax360.views.activities.doFragmentTransaction
import com.max360group.cammax360.views.adapters.*
import kotlinx.android.synthetic.main.layout_owner_general_info.*

class PropertyGeneralInfoDetailFragment : BaseFragment(), View.OnClickListener {

    private val mAddressListAdapter by lazy {
        AddressListDetailAdapter(this)
    }

    private var mAddressList=ArrayList<OwnerAddressModel>()

    override val layoutId: Int
        get() = R.layout.layout_property_general_info_detail

    override fun init(savedInstanceState: Bundle?) {
        //Set adapter
        rvAddress.adapter = mAddressListAdapter
    }

    override val viewModel: BaseViewModel?
        get() = null

    override fun observeProperties() {
    }

    fun getOwnerData(property: PropertyDetail) {
        mAddressList.clear()
        mAddressList.add(property.primaryAddress)
        mAddressList.add(property.billingAddress)
        mAddressList.addAll(property.addresses!!)
        mAddressListAdapter.updateData(mAddressList)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
        }

    }
}