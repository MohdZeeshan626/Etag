package com.max360group.cammax360.views.fragments

import android.os.Bundle
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.Rm
import com.max360group.cammax360.viewmodels.BaseViewModel
import com.max360group.cammax360.views.adapters.AuthDetailIntegrationAdapter
import kotlinx.android.synthetic.main.layout_owner_rm_fields.*


class IntegrationDetailFragment : BaseFragment() {
    companion object{
        var RMS_DISPLAY=""
    }

    private val mAuthIntegrationAdapter by lazy {
        AuthDetailIntegrationAdapter(this)
    }

    override val layoutId: Int
        get() = R.layout.layout_owner_rm_fields

    override fun init(savedInstanceState: Bundle?) {
        //Set adapter
        rvIntegration.adapter=mAuthIntegrationAdapter

    }

    fun getRmData(rm: Rm) {
        mAuthIntegrationAdapter.updateFunction(rm)
    }

    override val viewModel: BaseViewModel?
        get() = null

    override fun observeProperties() {

    }


}