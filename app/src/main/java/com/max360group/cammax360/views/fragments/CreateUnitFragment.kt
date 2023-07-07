package com.max360group.cammax360.views.fragments

import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.*
import com.max360group.cammax360.utils.ApplicationGlobal
import com.max360group.cammax360.utils.Constants
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.viewmodels.BaseViewModel
import com.max360group.cammax360.viewmodels.PropertyViewModel
import com.max360group.cammax360.viewmodels.UnitViewModel
import com.max360group.cammax360.views.activities.BaseAppCompactActivity
import com.max360group.cammax360.views.activities.doFragmentTransaction
import com.max360group.cammax360.views.adapters.*
import com.max360group.cammax360.views.calender.MonthlyActivity
import com.max360group.cammax360.views.fragments.OwnerPropertiesFragment.Companion.INTENT_UNIT_RECORD
import com.max360group.cammax360.views.fragments.PropertiesUnitFragment.Companion.BROADCAST_UNIT_INTENT
import com.max360group.cammax360.views.fragments.PropertiesUnitFragment.Companion.BROADCAST_UNIT_RECORD
import com.max360group.cammax360.views.interfaces.CreateOwnerInterface
import com.max360group.cammax360.views.interfaces.CreatePropertyInterface
import kotlinx.android.synthetic.main.fragment_create_unit.btnSave
import kotlinx.android.synthetic.main.fragment_create_owner.etFirstName
import kotlinx.android.synthetic.main.fragment_create_owner.rvMenuAdapter
import kotlinx.android.synthetic.main.fragment_create_owner.viewPager
import kotlinx.android.synthetic.main.fragment_create_unit.sdvUserImage
import kotlinx.android.synthetic.main.fragment_create_unit.ivCameraPicker
import kotlinx.android.synthetic.main.fragment_create_unit.*
import kotlinx.android.synthetic.main.fragment_create_unit.etComment
import kotlinx.android.synthetic.main.fragment_create_unit.spnrType
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_integration.*
import java.io.File


class CreateUnitFragment : BasePictureOptionsFragment(), CreateOwnerInterface,
    View.OnClickListener, CreatePropertyInterface {

    companion object {
        const val PARAM_TYPE_UNIT = "unitTypes"
        const val PARAM_PROPERTY_ID = "propertyId"
        const val PARAM_UNIT_ID = "unitId"

        fun newInstance(propertyId: String = "", unitId: String = ""): CreateUnitFragment {
            val args = Bundle()
            args.putString(PARAM_PROPERTY_ID, propertyId)
            args.putString(PARAM_UNIT_ID, unitId)
            val fragment = CreateUnitFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private val mOwnerMenuAdapter by lazy {
        OwnerMenuAdapter(this)
    }

    private val mPropertyAdapter by lazy {
        IntegrationTypeAdapter(this)
    }

    private val mUnitViewModel by lazy {
        ViewModelProvider(this).get(UnitViewModel::class.java)
    }

    private val mPropertyViewModel by lazy {
        ViewModelProvider(this).get(PropertyViewModel::class.java)
    }

    private var currentPageIndex = 0
    private var mCurrentTabIndex=0
    private var mImageFile = ""
    private var mPropertyId = ""
    private var mUnitId = ""
    private var mUnitRecord = UnitRecord()
    private var mSkip = 0
    var addresses = ArrayList<OwnerAddressModel>()
    private var mTypesList = ArrayList<String>()

    override val layoutId: Int
        get() = R.layout.fragment_create_unit

    override fun setData(savedInstanceState: Bundle?) {
        // Set toolbar
        toolbar.navigationIcon =
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_back_primary)
        ivToolbarUserImage.setImageURI(mUserPrefsManager.loginedUser!!.picURL)

        Glide
            .with(requireContext())
            .load(ApplicationGlobal.organisationLogo)
            .placeholder(R.drawable.ic_mimi_logo)
            .into(ivToolbarLeftIcon)

        //Get arguments
        mPropertyId = arguments?.getString(PARAM_PROPERTY_ID, "").toString()
        mUnitId = arguments?.getString(PARAM_UNIT_ID, "").toString()

        //Set adapter
        rvMenuAdapter.adapter = mOwnerMenuAdapter


        // Set view pager
        val tabsList = ArrayList<Tab>()

        tabsList.add(
            Tab(
                tabFragment = UnitGeneralInfoFragment(),
                tabName = getString(R.string.st_general_info)
            )
        )
        tabsList.add(
            Tab(
                tabFragment = NotesHistoryFragment(),
                tabName = getString(R.string.st_properties)
            )
        )

        tabsList.add(
            Tab(
                tabFragment = AccessFragment(),
                tabName = getString(R.string.st_properties)
            )
        )
        tabsList.add(
            Tab(
                tabFragment = IntegrationFragment(),
                tabName = getString(R.string.st_properties)
            )
        )

        viewPager.adapter = TabsAdapter(
            fragmentManager = childFragmentManager,
            tabsList = tabsList
        )

        viewPager.offscreenPageLimit = 4

        // Set view pager change listener
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                currentPageIndex = position
                mOwnerMenuAdapter.updatePosition(position)
                rvMenuAdapter.smoothScrollToPosition(position)
                switchFragment(position)
            }
        })

        //Get tabs
        mUnitViewModel.getMenuTabs()
        //Call api
        mPropertyViewModel.getIntegration(PARAM_TYPE_UNIT, mSkip)
        if (mUnitId.isNotBlank()) {
            mUnitViewModel.getUnitDetail(mUnitId)
            tvCreateUnit.text = getString(R.string.st_update_unit)
            btnSave.text = getString(R.string.update)
        }


        //Set click listener
        btnSave.setOnClickListener(this)
        ivCameraPicker.setOnClickListener(this)
        spnrType.setOnClickListener(this)
        flView.setOnClickListener(this)
        ivToolbarUserImage.setOnClickListener(this)
        ivToolbarRightIcon.setOnClickListener(this)
        ivToolbarRightIconBell.setOnClickListener(this)
    }

    override fun onGettingImageFile(file: File) {
        sdvUserImage.setImageURI(GeneralFunctions.getLocalImageFile(file))
        mImageFile = file.absolutePath
    }

    override fun onGettingMultipleImages(list: List<String>) {

    }

    override fun onStartCameraDialogFragment(isOpen: Boolean) {

    }

    override val viewModel: BaseViewModel
        get() = mUnitViewModel

    override fun observeProperties() {
        mUnitViewModel.onGetTabMenu().observe(this, Observer {
            mOwnerMenuAdapter.updateData(it)
        })

        mPropertyViewModel.onGetIntegration().observe(this, Observer {
            if (it.isNotEmpty()) {
                mPropertyAdapter.updateData(it, mSkip)
            }
        })

        mUnitViewModel.onCreateUnit().observe(this, Observer {
            //Send broadcast to update photo detail
            LocalBroadcastManager.getInstance(requireContext())
                .sendBroadcast(
                    Intent(OwnerPropertiesFragment.INTENT_OWNER_PROPERTY).putExtra(
                        INTENT_UNIT_RECORD,
                        it
                    ).putExtra(OwnerPropertiesFragment.INTENT_BROADCAST_TYPE, 1)
                )

            //Send broadcast to update photo detail
            LocalBroadcastManager.getInstance(requireContext())
                .sendBroadcast(
                    Intent(BROADCAST_UNIT_INTENT).putExtra(
                        BROADCAST_UNIT_RECORD,
                        it
                    )
                )
            (requireContext() as BaseAppCompactActivity).onBackPressed()
        })

        mPropertyViewModel.onGetIntegrationAll().observe(this, Observer {
            for (i in it.unitTypes!!.indices) {
                if (mUnitRecord.unitTypes!!.contains(it.unitTypes[i].id)) {
                    clIntegrationRoot.visibility = View.GONE
                    mTypesList.add(it.unitTypes[i].id)
                    val entryChip1 = getChip(
                        chipGroupUnit,
                        it.unitTypes[i].name,
                        it.unitTypes[i].id,
                        true,
                        object : ChipClickCallback {
                            override fun onChipRemoved(string: String?, id: String?) {
                                if (mTypesList.contains(id)) {
                                    mTypesList.remove(id)
                                }
                            }

                        })
                    chipGroupUnit.addView(entryChip1)
                }
            }
        })

        mUnitViewModel.onUnitDetail().observe(this, Observer {
            mUnitRecord = it
            sdvUserImage.setImageURI(it.picURL)
            etFirstName.setText(it.name)
            etBathroom.setText(it.bathrooms.toString())
            etBedroom.setText(it.bedrooms.toString())
            etSqft.setText(it.squareFootage.toString())
            etComment.setText(it.comments)
            mImageFile = it.pic

            //Update the viewpager fragments data
            val adapter = viewPager.adapter as TabsAdapter

            //set data in general info
            val fragmentGeneralInfo = adapter.getItem(0) as UnitGeneralInfoFragment
            fragmentGeneralInfo.getEditableDetail(mUnitRecord)

            //set data in general info
            val notesHistory = adapter.getItem(1) as NotesHistoryFragment
            notesHistory.getEditableDetail(mUnitRecord.id, NotesHistoryFragment.TYPE_UNIT)

            val accessFrag = adapter.getItem(2) as AccessFragment
            accessFrag.getEditableDetail(mUnitRecord.access)

            //Get types
            mPropertyViewModel.getIntegrationAll()

        })

    }

    override fun onMenuClick(position: Int) {
        viewPager.currentItem = position
        //For tab scrolling on click
        if (position!=0){
            if (position > mCurrentTabIndex) {
                rvMenuAdapter.smoothScrollToPosition(position + 1)
            } else {
                rvMenuAdapter.smoothScrollToPosition(position - 1)
            }
        }
        mCurrentTabIndex = position
    }

    override fun onSendInviteClick() {
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btnSave -> {
                addresses.clear()
                addresses.addAll(UnitGeneralInfoFragment.mAddressList)

                //Call api
                if (mUnitId.isNotBlank()) {
                    mUnitViewModel.editUnit(
                        mUnitId,
                        mPropertyId,
                        mImageFile,
                        etFirstName.text.toString().trim(),
                        mTypesList,
                        etBathroom.text.toString().trim(),
                        etBedroom.text.toString().trim(),
                        etSqft.text.toString().trim(),
                        AuthIntegrationAdapter.IS_RM_SYNC,
                        addresses,
                        NotesHistoryFragment.mNotesList,
                        AccessListAdapter.mList
                    )
                } else {
                    mUnitViewModel.createUnit(
                        mPropertyId,
                        mImageFile,
                        etFirstName.text.toString().trim(),
                        mTypesList,
                        etBathroom.text.toString().trim(),
                        etBedroom.text.toString().trim(),
                        etSqft.text.toString().trim(),
                        AuthIntegrationAdapter.IS_RM_SYNC,
                        addresses,
                        NotesHistoryFragment.mNotesList,
                        AccessListAdapter.mList
                    )
                }
            }

            R.id.ivCameraPicker -> {
                showPictureOptionsBottomSheet(Constants.LOCAL_STORAGE_BASE_PATH_FOR_USER_PHOTOS)
            }

            R.id.spnrType -> {
                rvIntegrations.adapter = mPropertyAdapter
                if (clIntegrationRoot.isVisible) {
                    clIntegrationRoot.visibility = View.GONE
                } else {
                    clIntegrationRoot.visibility = View.VISIBLE
                }
            }
            R.id.flView -> {
                clIntegrationRoot.visibility = View.GONE

            }
            R.id.ivToolbarUserImage -> {
                (activityContext as BaseAppCompactActivity).doFragmentTransaction(
                    fragment = AccountsFragment.newInstance(AccountsFragment.BUNDLE_NORMAl_VIEWS),
                    containerViewId = R.id.flFragContainerMain,
                    enterAnimation = R.animator.slide_right_in,
                    popExitAnimation = R.animator.slide_right_out
                )
            }
            R.id.ivToolbarRightIcon -> {
                startActivity(Intent(requireContext(), MonthlyActivity::class.java))
            }
            R.id.ivToolbarRightIconBell -> {
                (activityContext as BaseAppCompactActivity).doFragmentTransaction(
                    fragment = NotificationFragment(),
                    containerViewId = R.id.flFragContainerMain,
                    enterAnimation = R.animator.slide_right_in,
                    popExitAnimation = R.animator.slide_right_out
                )
            }
        }
    }

    fun switchFragment(position: Int) {
        when (position) {
            3 -> {
                //Set data by default 1st frag of viewpager
                val index: Int = viewPager.currentItem
                val adapter = viewPager.adapter as TabsAdapter
                val fragment = adapter.getCurrentVisibleFragment(index) as IntegrationFragment
                fragment.syncRm(false)
            }

            1 -> {
                val index: Int = viewPager.currentItem
                val adapter = viewPager.adapter as TabsAdapter
                val notesFragment = adapter.getItem(index) as NotesHistoryFragment
                notesFragment.getEditableDetail(mUnitRecord.id, NotesHistoryFragment.TYPE_UNIT)
            }
        }
    }

    override fun onLoadMore() {
        mSkip += 10
        mPropertyViewModel.getIntegration(PARAM_TYPE_UNIT, mSkip)

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
        chip.isCloseIconVisible = shouldShowClose
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

    override fun onIntegrationTypeClick(integrationData: IntegrationData) {
        if (mTypesList.contains(integrationData.id)) {
            showMessage(null, getString(R.string.st_type_is_already_added))
        } else {
            clIntegrationRoot.visibility = View.GONE
            mTypesList.add(integrationData.id)
            val entryChip1 = getChip(
                chipGroupUnit,
                integrationData.name,
                integrationData.id,
                true,
                object : ChipClickCallback {
                    override fun onChipRemoved(string: String?, id: String?) {
                        if (mTypesList.contains(id)) {
                            mTypesList.remove(id)
                        }
                    }

                })
            chipGroupUnit.addView(entryChip1)
        }
    }

}