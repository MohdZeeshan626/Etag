package com.max360group.cammax360.views.fragments

import android.os.Bundle
import android.view.View
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.OwnerAddressModel
import com.max360group.cammax360.repository.models.UserOwner
import com.max360group.cammax360.viewmodels.BaseViewModel
import com.max360group.cammax360.views.activities.BaseAppCompactActivity
import com.max360group.cammax360.views.activities.doFragmentTransaction
import com.max360group.cammax360.views.adapters.*
import kotlinx.android.synthetic.main.layout_owner_general_info.*

class GeneralInfoDetailFragment : BaseFragment(), View.OnClickListener {

    private val mEmailListAdapter by lazy {
        EmailListDetailAdapter(this)
    }
    private val mPhoneListAdapter by lazy {
        PhoneListDetailAdapter(this)
    }

    private val mAddressListAdapter by lazy {
        AddressListDetailAdapter(this)
    }

    private var mAddressList=ArrayList<OwnerAddressModel>()

    override val layoutId: Int
        get() = R.layout.layout_general_info_detail

    override fun init(savedInstanceState: Bundle?) {
        //Set adapter
        rvEmails.adapter = mEmailListAdapter
        rvPhones.adapter = mPhoneListAdapter
        rvAddress.adapter = mAddressListAdapter
    }

    override val viewModel: BaseViewModel?
        get() = null

    override fun observeProperties() {
    }

    fun getOwnerData(userOwner: UserOwner) {
        mEmailListAdapter.updateData(userOwner.emails!!)
        mPhoneListAdapter.updateData(userOwner.phoneNumbers!!)

        mAddressList.clear()
        mAddressList.add(userOwner.primaryAddress)
        mAddressList.add(userOwner.billingAddress)
        mAddressList.addAll(userOwner.addresses!!)
        mAddressListAdapter.updateData(mAddressList)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btnAddress -> {
                (activityContext as BaseAppCompactActivity).doFragmentTransaction(
                    fragment = AddAddressFragment(),
                    containerViewId = R.id.flFragContainerMain,
                    enterAnimation = R.animator.slide_right_in,
                    popExitAnimation = R.animator.slide_right_out
                )
            }
        }

    }
}