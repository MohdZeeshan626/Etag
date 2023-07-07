package com.max360group.cammax360.views.dialgofragments

import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.PropertyDetail
import com.max360group.cammax360.repository.models.Owners
import com.max360group.cammax360.repository.models.UserOwner
import com.max360group.cammax360.viewmodels.BaseViewModel
import com.max360group.cammax360.viewmodels.OwnerViewModel
import com.max360group.cammax360.views.adapters.ExistingOwnerListAdapter
import com.max360group.cammax360.views.adapters.NewPropertyListAdapter
import com.max360group.cammax360.views.dialgofragments.AddPropertyDialogFragment.Companion.BUNDLE_PROPERTY_LIST
import com.max360group.cammax360.views.fragments.OwnerPropertiesFragment.Companion.INTENT_BROADCAST_TYPE
import com.max360group.cammax360.views.fragments.OwnerPropertiesFragment.Companion.INTENT_OWNER_PROPERTY
import com.max360group.cammax360.views.fragments.OwnerPropertiesFragment.Companion.mPropertyList
import com.max360group.cammax360.views.fragments.PropertiesOwnerFragment.Companion.INTENT_PROPERTIES_OWNER
import kotlinx.android.synthetic.main.dialog_add_new_property.*
import kotlinx.android.synthetic.main.dialog_add_new_property.ivCancel
import kotlinx.android.synthetic.main.dialog_add_new_property.btnSubmit

class AddOwnerDialogFragment : BaseDialogFragment(), View.OnClickListener,
    ExistingOwnerListAdapter.AddOwnerListener {

    companion object {
        const val BUNDLE_OWNER = "owner"
        const val BUNDLE_OWNER_LIST = "OwnerLIST"
        const val BUNDLE_SELECTED_OWNER_LIST = "selectedOwnerList"

        fun newInstance(
            mOwners: ArrayList<UserOwner>,
            mSelectedPropertiesId: ArrayList<String>
        ): AddOwnerDialogFragment {
            val args = Bundle()
            args.putParcelableArrayList(BUNDLE_OWNER_LIST, mOwners)
            args.putStringArrayList(BUNDLE_SELECTED_OWNER_LIST, mSelectedPropertiesId)
            val fragment = AddOwnerDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private val mCreateOwnerViewModel by lazy {
        ViewModelProvider(this).get(OwnerViewModel::class.java)
    }

    private val mOwnersList = ArrayList<UserOwner>()
    private var mAllOwners = ArrayList<UserOwner>()
    private var mPropertiesIds = ArrayList<String>()

    private val mExistingOwnerListAdapter by lazy {
        ExistingOwnerListAdapter(this)
    }

    override val isFullScreenDialog: Boolean
        get() = false

    override val layoutId: Int
        get() = R.layout.dialog_add_owner

    override fun init() {
        //Get arguments
        mAllOwners =
            arguments?.getParcelableArrayList<UserOwner>(BUNDLE_OWNER_LIST) as ArrayList<UserOwner>
        mPropertiesIds =
            arguments?.getStringArrayList(BUNDLE_SELECTED_OWNER_LIST) as ArrayList<String>

        //Set click listener
        btnSubmit.setOnClickListener(this)
        tvProperty.setOnClickListener(this)
        ivCancel.setOnClickListener(this)

        //Set adapter
        rvPropertyList.adapter = mExistingOwnerListAdapter

        //Update adapter
        if (mPropertiesIds.isNotEmpty()) {
            for (i in mAllOwners.indices) {
                if (!mPropertiesIds.contains(mAllOwners[i].id)) {
                    mOwnersList.add(mAllOwners[i])
                }
            }
            mExistingOwnerListAdapter.updateData(mOwnersList)
            mOwnersList.clear()
        } else {
            mExistingOwnerListAdapter.updateData(mAllOwners)
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
                        Intent(INTENT_PROPERTIES_OWNER).putExtra(
                            BUNDLE_OWNER,
                            mOwnersList
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

    override fun onItemClick(property: UserOwner) {
        rvPropertyList.visibility = View.GONE
        if (mPropertiesIds.contains(property.id)) {
            showMessage(null, getString(R.string.st_owner_already_added))
        } else {
            mOwnersList.add(property)
            mPropertiesIds.add(property.id)
            val entryChip1 = getChip(
                chipGroupProperty,
                property.firstName,
                property.id,
                true,
                object : ChipClickCallback {
                    override fun onChipRemoved(string: String?, id: String?) {
                        for (data in mOwnersList) {
                            if (data.id == id) {
                                mOwnersList.remove(data)
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