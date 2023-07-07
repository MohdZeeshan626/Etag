package com.max360group.cammax360.views.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.PropertyDetail
import com.max360group.cammax360.repository.models.OwnerProperty
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.viewmodels.BaseViewModel
import com.max360group.cammax360.viewmodels.OwnerViewModel
import com.max360group.cammax360.views.adapters.*
import kotlinx.android.synthetic.main.layout_owner_properties.*

class OwnerPropertiesDetailFragment : BaseFragment(){

    private val mPropertyListAdapter by lazy {
        PropertyListDetailAdapter(this)
    }
    private val mCreateOwnerViewModel by lazy {
        ViewModelProvider(this).get(OwnerViewModel::class.java)
    }

    private var mPropertyList=ArrayList<PropertyDetail>()
    private var mPropertyListAll=ArrayList<PropertyDetail>()
    private var mSelectedProperty=ArrayList<OwnerProperty>()

    override val layoutId: Int
        get() = R.layout.layout_owner_properties_detail

    override fun init(savedInstanceState: Bundle?) {
        //Set adapter
        rvProperty.adapter = mPropertyListAdapter

        //Get properties
        if (GeneralFunctions.isInternetConnected(requireContext())){
            mCreateOwnerViewModel.getProperties()
        }else{
            mCreateOwnerViewModel.getPropertiesFromLocal()
        }
    }

    override val viewModel: BaseViewModel?
        get() = mCreateOwnerViewModel

    override fun observeProperties() {
        mCreateOwnerViewModel.onGetPropertiesData().observe(this, Observer {
            mPropertyListAll.clear()
            mPropertyListAll.addAll(it.records!!)
        })
    }

    fun getOwnerProperties(properties: List<OwnerProperty>?) {
        mSelectedProperty.clear()
        mSelectedProperty.addAll(properties!!)

        initFilterOutData(mPropertyListAll)

    }

    private fun initFilterOutData(records: List<PropertyDetail>?) {
        mPropertyList.clear()
        for (i in records!!.indices){
            for (j in mSelectedProperty.indices){

                //To filter out the selected property
                if (mSelectedProperty[j].propertyId==records[i].id) {
                    val mProperty=PropertyDetail()
                    mProperty.id=records[i].id
                    mProperty.name=records[i].name
                    mProperty.primaryAddress=records[i].primaryAddress
                    mProperty.shortName=records[i].shortName

                    //To filter out the selected units of selected property
                    for (k in records[i].propertyUnits!!.indices){
                        if (mSelectedProperty[j].propertyUnits!!.contains(records[i].propertyUnits!![k].id)){
                            mProperty.propertyUnits!!.add(records[i].propertyUnits!![k])
                        }
                    }
                    mPropertyList.add(mProperty)
                }
            }
        }
        mPropertyListAdapter.updateData(mPropertyList)
    }

}