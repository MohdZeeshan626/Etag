package com.max360group.cammax360.views.dialgofragments

import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.PropertyDetail
import com.max360group.cammax360.viewmodels.BaseViewModel
import com.max360group.cammax360.viewmodels.OwnerViewModel
import com.max360group.cammax360.views.adapters.NewPropertyListAdapter
import com.max360group.cammax360.views.fragments.OwnerPropertiesFragment.Companion.INTENT_BROADCAST_TYPE
import com.max360group.cammax360.views.fragments.OwnerPropertiesFragment.Companion.INTENT_OWNER_PROPERTY
import kotlinx.android.synthetic.main.dialog_add_new_property.*
import kotlinx.android.synthetic.main.dialog_add_new_property.ivCancel
import kotlinx.android.synthetic.main.dialog_add_new_property.btnSubmit

class AddPropertyDialogFragment : BaseDialogFragment(), View.OnClickListener,
    NewPropertyListAdapter.AddPropertyListener {

    companion object {
        const val BUNDLE_PROPERTY = "property"
        const val BUNDLE_PROPERTY_LIST = "propertyLIST"
        const val BUNDLE_SELECTED_PROPERTY_LIST = "selectedPropertyList"

        fun newInstance(
            mProperties: ArrayList<PropertyDetail>,
            mSelectedPropertiesId: ArrayList<String>
        ): AddPropertyDialogFragment {
            val args = Bundle()
            args.putParcelableArrayList(BUNDLE_PROPERTY_LIST, mProperties)
            args.putStringArrayList(BUNDLE_SELECTED_PROPERTY_LIST, mSelectedPropertiesId)
            val fragment = AddPropertyDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private val mCreateOwnerViewModel by lazy {
        ViewModelProvider(this).get(OwnerViewModel::class.java)
    }

    private val mPropertiesList = ArrayList<PropertyDetail>()
    private var mAllProperties = ArrayList<PropertyDetail>()
    private var mPropertiesIds = ArrayList<String>()

    private val mNewPropertyListAdapter by lazy {
        NewPropertyListAdapter(this)
    }

    override val isFullScreenDialog: Boolean
        get() = false

    override val layoutId: Int
        get() = R.layout.dialog_add_new_property

    override fun init() {
        //Get arguments
        mAllProperties =
            arguments?.getParcelableArrayList<PropertyDetail>(BUNDLE_PROPERTY_LIST) as ArrayList<PropertyDetail>
        mPropertiesIds =
            arguments?.getStringArrayList(BUNDLE_SELECTED_PROPERTY_LIST) as ArrayList<String>

        //Set click listener
        btnSubmit.setOnClickListener(this)
        tvProperty.setOnClickListener(this)
        ivCancel.setOnClickListener(this)

        //Set adapter
        rvPropertyList.adapter = mNewPropertyListAdapter

        //Update adapter
        if (mPropertiesIds.isNotEmpty()) {
            for (i in mAllProperties.indices) {
                if (!mPropertiesIds.contains(mAllProperties[i].id)) {
                    mPropertiesList.add(mAllProperties[i])
                }
            }
            mNewPropertyListAdapter.updateData(mPropertiesList)
            mPropertiesList.clear()
        } else {
            mNewPropertyListAdapter.updateData(mAllProperties)

        }


    }

    override val viewModel: BaseViewModel
        get() = mCreateOwnerViewModel

    override fun observeProperties() {
    }

    // Set chipView
    private fun getChip(
        entryChipGroup: ChipGroup,
        text: String?,
        id: String?,
        shouldShowClose: Boolean,
        chipClickCallback: ChipClickCallback
    ): Chip {
        val chip = Chip(requireContext())
        val paddingDp = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 10f,
            resources.displayMetrics
        ).toInt()
        if (shouldShowClose) {
            chip.setPadding(paddingDp, paddingDp, paddingDp * 3, paddingDp)
        } else {
            chip.setPadding(paddingDp, paddingDp, paddingDp, paddingDp)
        }
        chip.text = text
        chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        chip.isCloseIconEnabled = shouldShowClose
        chip.setChipBackgroundColorResource(R.color.colorDivider)
        chip.setOnCloseIconClickListener {
            chipClickCallback.onChipRemoved(text, id)
            entryChipGroup.removeView(chip)
        }
        //
        return chip
    }

    // Remove chipView
    interface ChipClickCallback {
        fun onChipRemoved(string: String?, id: String?)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btnSubmit -> {
                //Send broadcast to update photo detail
                LocalBroadcastManager.getInstance(requireContext())
                    .sendBroadcast(
                        Intent(INTENT_OWNER_PROPERTY).putExtra(
                            BUNDLE_PROPERTY,
                            mPropertiesList
                        ).putExtra(INTENT_BROADCAST_TYPE,0)
                    )
                dismiss()

            }

            R.id.ivCancel -> {
                dismiss()
            }

            R.id.tvProperty -> {
                if (rvPropertyList.isVisible) {
                    rvPropertyList.visibility = View.GONE
                } else {
                    rvPropertyList.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onItemClick(property: PropertyDetail) {
        rvPropertyList.visibility = View.GONE
        if (mPropertiesIds.contains(property.id)) {
            showMessage(null, "This property is already added")
        } else {
            mPropertiesList.add(property)
            mPropertiesIds.add(property.id)
            val entryChip1 = getChip(
                chipGroupProperty,
                property.name,
                property.id,
                true,
                object : ChipClickCallback {
                    override fun onChipRemoved(string: String?, id: String?) {
                        for (data in mPropertiesList) {
                            if (data.id == id) {
                                mPropertiesList.remove(data)
                                mPropertiesIds.remove(id)
                                break
                            }
                        }
                    }

                })
            chipGroupProperty.addView(entryChip1)
        }

    }
}