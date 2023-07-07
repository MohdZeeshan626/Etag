package com.max360group.cammax360.views.fragments

import android.os.Bundle
import com.max360group.cammax360.R
import com.max360group.cammax360.viewmodels.BaseViewModel
import com.max360group.cammax360.views.adapters.PropertyJobsAdapter
import kotlinx.android.synthetic.main.fragment_property_jobs.*

class PropertyJobsFragment : BaseFragment() {

    private val mPropertyJobsAdapter by lazy {
        PropertyJobsAdapter(this)
    }

    override val layoutId: Int
        get() = R.layout.fragment_property_jobs

    override fun init(savedInstanceState: Bundle?) {
        //Set adapter
        rvJobsList.adapter=mPropertyJobsAdapter

    }

    override val viewModel: BaseViewModel?
        get() = null

    override fun observeProperties() {

    }
}