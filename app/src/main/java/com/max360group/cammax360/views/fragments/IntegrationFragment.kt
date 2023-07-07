package com.max360group.cammax360.views.fragments

import android.os.Bundle
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.Rm
import com.max360group.cammax360.viewmodels.BaseViewModel
import com.max360group.cammax360.views.adapters.AuthIntegrationAdapter
import kotlinx.android.synthetic.main.layout_owner_rm_fields.*


class IntegrationFragment : BaseFragment() {
    private val mAuthIntegrationAdapter by lazy {
        AuthIntegrationAdapter(this)
    }

    var rmFields = Rm()

    override val layoutId: Int
        get() = R.layout.layout_owner_rm_fields

    override fun init(savedInstanceState: Bundle?) {
        //Set adapter
        rvIntegration.adapter = mAuthIntegrationAdapter

    }

    override val viewModel: BaseViewModel?
        get() = null

    override fun observeProperties() {
    }

    fun syncRm(isSync: Boolean = true) {
        mAuthIntegrationAdapter.updateData(rmFields,isSync)
    }

    fun getEditableDetail(rm: Rm) {
        rmFields = rm
        mAuthIntegrationAdapter.updateData(rmFields)
    }


}