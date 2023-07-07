package com.max360group.cammax360.views.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.Owners
import com.max360group.cammax360.repository.models.UserOwner
import com.max360group.cammax360.viewmodels.BaseViewModel
import com.max360group.cammax360.viewmodels.PropertyViewModel
import com.max360group.cammax360.views.adapters.*
import kotlinx.android.synthetic.main.layout_owner_properties.*

class PropertyOwnerDetailFragment : BaseFragment(), View.OnClickListener{

    private val mOwnerListDetailAdapter by lazy {
        OwnerListDetailAdapter(this)
    }

    override val layoutId: Int
        get() = R.layout.layout_owner_properties_detail

    override fun init(savedInstanceState: Bundle?) {
        //Set adapter
        rvProperty.adapter = mOwnerListDetailAdapter

    }

    override val viewModel: BaseViewModel?
        get() = null

    override fun observeProperties() {

    }

    fun getDetailInfo(userOwners: List<UserOwner>?) {
        mOwnerListDetailAdapter.updateData(userOwners!!)

    }

    override fun onClick(p0: View?) {
        when (p0?.id) {

        }
    }
}