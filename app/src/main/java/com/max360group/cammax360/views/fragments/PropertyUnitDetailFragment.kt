package com.max360group.cammax360.views.fragments

import android.os.Bundle
import android.view.View
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.UnitRecord
import com.max360group.cammax360.viewmodels.BaseViewModel
import com.max360group.cammax360.views.adapters.*
import kotlinx.android.synthetic.main.layout_owner_properties.*

class PropertyUnitDetailFragment : BaseFragment(), View.OnClickListener{

    private val mPropertyUnitListDetailAdapter by lazy {
        PropertyUnitListDetailAdapter(this)
    }

    override val layoutId: Int
        get() = R.layout.layout_owner_properties_detail

    override fun init(savedInstanceState: Bundle?) {
        //Set adapter
        rvProperty.adapter = mPropertyUnitListDetailAdapter
    }

    override val viewModel: BaseViewModel?
        get() = null

    override fun observeProperties() {
    }

    fun getDetailInfo(propertyUnits: List<UnitRecord>?) {
        mPropertyUnitListDetailAdapter.updateData(propertyUnits!!)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {

        }
    }
}