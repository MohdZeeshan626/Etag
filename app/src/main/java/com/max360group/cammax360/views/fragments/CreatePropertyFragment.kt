package com.max360group.cammax360.views.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.max360group.cammax360.utils.Constants.LOCAL_STORAGE_BASE_PATH_FOR_USER_PHOTOS
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.viewmodels.BaseViewModel
import com.max360group.cammax360.viewmodels.PropertyViewModel
import com.max360group.cammax360.views.activities.BaseAppCompactActivity
import com.max360group.cammax360.views.activities.doFragmentTransaction
import com.max360group.cammax360.views.adapters.AccessListAdapter.Companion.mList
import com.max360group.cammax360.views.adapters.IntegrationTypeAdapter
import com.max360group.cammax360.views.adapters.OwnerMenuAdapter
import com.max360group.cammax360.views.adapters.TabsAdapter
import com.max360group.cammax360.views.calender.MonthlyActivity
import com.max360group.cammax360.views.fragments.NotesHistoryFragment.Companion.mNotesList
import com.max360group.cammax360.views.fragments.OwnerPropertiesFragment.Companion.INTENT_PROPERTY
import com.max360group.cammax360.views.fragments.PropertiesFragment.Companion.INTENT_PROPERTIES
import com.max360group.cammax360.views.fragments.PropertiesOwnerFragment.Companion.mOwnersList
import com.max360group.cammax360.views.fragments.PropertiesUnitFragment.Companion.mUnitList
import com.max360group.cammax360.views.fragments.PropertyGeneralInfoFragment.Companion.mAddressList
import com.max360group.cammax360.views.interfaces.CreateOwnerInterface
import com.max360group.cammax360.views.interfaces.CreatePropertyInterface
import kotlinx.android.synthetic.main.fragment_create_owner.ivCameraPicker
import kotlinx.android.synthetic.main.fragment_create_owner.rvMenuAdapter
import kotlinx.android.synthetic.main.fragment_create_owner.sdvUserImage
import kotlinx.android.synthetic.main.fragment_create_owner.viewPager
import kotlinx.android.synthetic.main.fragment_create_property.*
import kotlinx.android.synthetic.main.fragment_create_property.chipGroupCharge
import kotlinx.android.synthetic.main.fragment_create_property.chipGroupProperty
import kotlinx.android.synthetic.main.fragment_create_property.etComment
import kotlinx.android.synthetic.main.fragment_create_property.etPropertyName
import kotlinx.android.synthetic.main.fragment_create_property.etSqFit
import kotlinx.android.synthetic.main.fragment_create_property.spnrChargeType
import kotlinx.android.synthetic.main.fragment_create_property.spnrType
import kotlinx.android.synthetic.main.fragment_property_detail.*
import kotlinx.android.synthetic.main.load_owners_layout.view.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_integration.*
import java.io.File


class CreatePropertyFragment : BasePictureOptionsFragment(), CreateOwnerInterface,
    View.OnClickListener, CreatePropertyInterface {

    companion object {
        const val INTEGRATION_CHARGE_TYPE = "chargeTypes"
        const val INTEGRATION_PROPERTY_TYPE = "propertyTypes"
        const val BUNDLE_PROPERTY_ID = "propertyId"
        const val BUNDLE_PROPERTY_LOCAL_ID = "propertyLocalId"
        const val BUNDLE_FROM = "from"
        const val BUNDLE_CREATE_JOB = 0
        const val BUNDLE_CREATE_OWNER = 1
        const val BUNDLE_CREATE_PROPERTY = 2

        fun newInstance(propertyId: String, propertyLocalId: Int = -1,from:Int): CreatePropertyFragment {
            val args = Bundle()
            args.putString(BUNDLE_PROPERTY_ID, propertyId)
            args.putInt(BUNDLE_PROPERTY_LOCAL_ID, propertyLocalId)
            args.putInt(BUNDLE_FROM, from)
            val fragment = CreatePropertyFragment()
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

    private val mChargeAdapter by lazy {
        IntegrationTypeAdapter(this)
    }

    private val mPropertyViewModel by lazy {
        ViewModelProvider(this).get(PropertyViewModel::class.java)
    }

    private var currentPageIndex = 0
    private var mImageFile = ""
    private var mSkip = 0
    private var mType = ""
    private var mPropertyRecord = PropertyDetail()
    private var mPropertyIdList = ArrayList<String>()
    private var mChargeId = ArrayList<String>()
    private var mCurrentTabIndex = 0
    var addresses = ArrayList<OwnerAddressModel>()
    private var mPropertyId = ""
    private var mPropertyLocalId = 0
    private var navigateFrom=0


    override val layoutId: Int
        get() = R.layout.fragment_create_property

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
        mPropertyId = arguments?.getString(BUNDLE_PROPERTY_ID).toString()
        mPropertyLocalId = arguments?.getInt(BUNDLE_PROPERTY_LOCAL_ID)!!
        navigateFrom = arguments?.getInt(BUNDLE_FROM)!!


        //Set adapter
        rvMenuAdapter.adapter = mOwnerMenuAdapter

        // Set view pager
        val tabsList = ArrayList<Tab>()

        tabsList.add(
            Tab(
                tabFragment = PropertyGeneralInfoFragment(),
                tabName = getString(R.string.st_general_info)
            )
        )
        tabsList.add(
            Tab(
                tabFragment = PropertiesOwnerFragment(),
                tabName = getString(R.string.st_properties)
            )
        )

        tabsList.add(
            Tab(
                tabFragment = PropertiesUnitFragment(),
                tabName = getString(R.string.st_properties)
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

        viewPager.offscreenPageLimit = 5

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
                switchFragment(position)

                when (position) {
                    3 -> {
                        val adapter = viewPager.adapter as TabsAdapter
                        //set data in notes
                        val notesFragment =
                            adapter.getCurrentVisibleFragment(3) as NotesHistoryFragment
                        notesFragment.getEditableDetail("", NotesHistoryFragment.TYPE_PROPERTY)
                    }
                }
            }
        })

        //if comes from create job location send the lat lng from which we comes
        if (navigateFrom== BUNDLE_CREATE_JOB){
            //Update the viewpager fragments data
            val adapter = viewPager.adapter as TabsAdapter
            val fragmentGeneralInfo = adapter.getItem(0) as PropertyGeneralInfoFragment
            fragmentGeneralInfo.getJobLocation(SelectLocationFragment.mLatitude,SelectLocationFragment.mLongitude)
        }

        //Set click listener
        ivCameraPicker.setOnClickListener(this)
        btnSave.setOnClickListener(this)
        spnrType.setOnClickListener(this)
        spnrChargeType.setOnClickListener(this)
        flView.setOnClickListener(this)
        ivToolbarUserImage.setOnClickListener(this)
        ivToolbarRightIcon.setOnClickListener(this)
        ivToolbarRightIconBell.setOnClickListener(this)

        //Call api
        if (GeneralFunctions.isInternetConnected(requireContext())) {
            mPropertyViewModel.getIntegration(INTEGRATION_PROPERTY_TYPE, mSkip)
            mPropertyViewModel.getIntegration(INTEGRATION_CHARGE_TYPE, mSkip)
        } else {
            mPropertyViewModel.getIntegrationCommonsFromLocal(INTEGRATION_PROPERTY_TYPE)
            Handler(Looper.getMainLooper()).postDelayed({
                mPropertyViewModel.getIntegrationCommonsFromLocal(INTEGRATION_CHARGE_TYPE)
            }, 1000)

        }

        //Get tabs
        mPropertyViewModel.getTabs()

        if (mPropertyId.isNotBlank() || mPropertyLocalId >= 0) {
            if (GeneralFunctions.isInternetConnected(requireContext())) {
                mPropertyViewModel.getPropertyDetail(mPropertyId)
            } else {
                mPropertyViewModel.getPropertiesDetailFromLocal(mPropertyLocalId)
            }

            tvCreateProperty.text = getString(R.string.st_update_property)
            btnSave.text = getString(R.string.update)
        }
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
        get() = mPropertyViewModel

    override fun observeProperties() {
        mPropertyViewModel.onGetIntegration().observe(this, Observer {
            if (it.isNotEmpty()) {
                if (it[0].kind == INTEGRATION_PROPERTY_TYPE) {
                    mPropertyAdapter.updateData(it, mSkip)
                } else {
                    mChargeAdapter.updateData(it, mSkip)
                }
            }
        })

        mPropertyViewModel.onGetTabs().observe(this, Observer {
            mOwnerMenuAdapter.updateData(it)
        })

        mPropertyViewModel.onCreateProperty().observe(this, Observer {
            //Send broadcast to update photo detail
            LocalBroadcastManager.getInstance(requireContext())
                .sendBroadcast(
                    Intent(OwnerPropertiesFragment.INTENT_OWNER_PROPERTY).putExtra(
                        INTENT_PROPERTY,
                        it
                    ).putExtra(OwnerPropertiesFragment.INTENT_BROADCAST_TYPE, 2)
                )

            //Send broadcast to update photo detail
            LocalBroadcastManager.getInstance(requireContext())
                .sendBroadcast(
                    Intent(INTENT_PROPERTIES)
                )
            (requireContext() as BaseAppCompactActivity).onBackPressed()

        })

        mPropertyViewModel.onGetIntegrationAll().observe(this, Observer {
            for (i in it.propertyTypes!!.indices) {
                if (mPropertyRecord.propertyTypes!!.contains(it.propertyTypes!![i].id)) {
                    mPropertyIdList.add(it.propertyTypes!![i].id)
                    val entryChip1 = getChip(
                        chipGroupProperty,
                        it.propertyTypes!![i].name,
                        it.propertyTypes!![i].id,
                        true,
                        object : ChipClickCallback {
                            override fun onChipRemoved(string: String?, id: String?) {
                                if (mPropertyIdList.contains(id)) {
                                    mPropertyIdList.remove(id)
                                }
                            }

                        })
                    chipGroupProperty.addView(entryChip1)
                }
            }

            for (i in it.chargeTypes!!.indices) {
                if (mPropertyRecord.chargeTypes!!.contains(it.chargeTypes!![i].id)) {
                    mChargeId.add(it.chargeTypes!![i].id)
                    val entryChip1 = getChip(
                        chipGroupCharge,
                        it.chargeTypes!![i].name,
                        it.chargeTypes!![i].id,
                        true,
                        object : ChipClickCallback {
                            override fun onChipRemoved(string: String?, id: String?) {
                                if (mChargeId.contains(id)) {
                                    mChargeId.remove(id)
                                }
                            }

                        })
                    chipGroupCharge.addView(entryChip1)
                }
            }
        })

        mPropertyViewModel.onGetPropertyDetail().observe(this, Observer {
            mPropertyRecord = it
            if (it.picURL.startsWith("https://")) {
                sdvUserImage.setImageURI(it.picURL)
            } else {
                sdvUserImage.setImageURI(GeneralFunctions.getLocalImageFile(File(it.picURL)))
            }
            etPropertyName.setText(it.name)
            etSortName.setText(it.shortName)
            etSqFit.setText(it.squareFootage.toString())
            etTaxId.setText(it.taxId)
            etComment.setText(it.comments)
            mImageFile = it.pic

            //Get types
            if (GeneralFunctions.isInternetConnected(requireContext())) {
                mPropertyViewModel.getIntegrationAll()
            } else {
                mPropertyViewModel.getAllIntegrationCommonsFromLocal()
            }

            //Update the viewpager fragments data
            val adapter = viewPager.adapter as TabsAdapter

            //set data in general info
            val fragmentGeneralInfo = adapter.getItem(0) as PropertyGeneralInfoFragment
            fragmentGeneralInfo.getEditableDetail(mPropertyRecord)

            //set data in properties info
            val ownerFragment = adapter.getItem(1) as PropertiesOwnerFragment
            ownerFragment.getEditableDetail(mPropertyRecord.userOwners)

            //set data in property unit info
            val propertyUnit = adapter.getItem(2) as PropertiesUnitFragment
            propertyUnit.getEditableDetail(mPropertyRecord.propertyUnits)

            //set data in notes info
            val notesHistory = adapter.getItem(3) as NotesHistoryFragment
            notesHistory.getEditableDetail(mPropertyRecord.id, NotesHistoryFragment.TYPE_PROPERTY)

            //set data in property access info
            val accessFrag = adapter.getItem(4) as AccessFragment
            accessFrag.getEditableDetail(mPropertyRecord.access)
        })

        mPropertyViewModel.onPropertyUpdated().observe(this, Observer {
            //Send broadcast to update photo detail
            LocalBroadcastManager.getInstance(requireContext())
                .sendBroadcast(
                    Intent(INTENT_PROPERTIES)
                )

            (requireContext() as BaseAppCompactActivity).onBackPressed()
        })

    }

    override fun onMenuClick(position: Int) {
        viewPager.currentItem = position

        //For tab scrolling on click
        if (position != 0) {
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

    fun switchFragment(position: Int) {
        when (position) {
            5 -> {
                //Set data by default 1st frag of viewpager
                val index: Int = viewPager.currentItem
                val adapter = viewPager.adapter as TabsAdapter
                val fragment = adapter.getCurrentVisibleFragment(index) as IntegrationFragment
                fragment.syncRm(false)
            }
        }
    }


    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ivCameraPicker -> {
                showPictureOptionsBottomSheet(LOCAL_STORAGE_BASE_PATH_FOR_USER_PHOTOS)
            }

            R.id.btnSave -> {
                addresses.clear()
                addresses.addAll(mAddressList)

                //Call api
                if (GeneralFunctions.isInternetConnected(requireContext())) {
                    if (mPropertyId.isBlank()) {
                        mPropertyViewModel.createProperty(
                            mImageFile,
                            etPropertyName.text.toString().trim(),
                            etSortName.text.toString().trim(),
                            etTaxId.text.toString().trim(),
                            mPropertyIdList,
                            mChargeId,
                            etSqFit.text.toString().trim(),
                            etComment.text.toString().trim(),
                            addresses,
                            mOwnersList,
                            mUnitList,
                            mNotesList,
                            mList
                        )
                    } else {
                        mPropertyViewModel.updateProperty(
                            mPropertyId,
                            mImageFile,
                            etPropertyName.text.toString().trim(),
                            etSortName.text.toString().trim(),
                            etTaxId.text.toString().trim(),
                            mPropertyIdList,
                            mChargeId,
                            etSqFit.text.toString().trim(),
                            etComment.text.toString().trim(),
                            addresses,
                            mOwnersList,
                            mUnitList,
                            mNotesList,
                            mList
                        )
                    }
                } else {
                    if (mPropertyLocalId >= 0) {
                        mPropertyViewModel.updatePropertyInLocal(
                            mPropertyId,
                            mPropertyLocalId,
                            mImageFile,
                            etPropertyName.text.toString().trim(),
                            etSortName.text.toString().trim(),
                            etTaxId.text.toString().trim(),
                            mPropertyIdList,
                            mChargeId,
                            etSqFit.text.toString().trim(),
                            etComment.text.toString().trim(),
                            addresses,
                            mOwnersList,
                            mUnitList,
                            mNotesList,
                            mList
                        )
                    } else {
                        mPropertyViewModel.createPropertyInLocal(
                            mImageFile,
                            etPropertyName.text.toString().trim(),
                            etSortName.text.toString().trim(),
                            etTaxId.text.toString().trim(),
                            mPropertyIdList,
                            mChargeId,
                            etSqFit.text.toString().trim(),
                            etComment.text.toString().trim(),
                            addresses,
                            mOwnersList,
                            mUnitList,
                            mNotesList,
                            mList
                        )
                    }
                }
            }

            R.id.flView -> {
                clIntegrationRoot.visibility = View.GONE

            }
            R.id.spnrChargeType -> {
                mType = INTEGRATION_CHARGE_TYPE
                //Set adapter
                rvIntegrations.adapter = mChargeAdapter
                if (clIntegrationRoot.isVisible) {
                    clIntegrationRoot.visibility = View.GONE
                } else {
                    clIntegrationRoot.visibility = View.VISIBLE
                }
            }

            R.id.spnrType -> {
                mType = INTEGRATION_PROPERTY_TYPE
                rvIntegrations.adapter = mPropertyAdapter
                if (clIntegrationRoot.isVisible) {
                    clIntegrationRoot.visibility = View.GONE
                } else {
                    clIntegrationRoot.visibility = View.VISIBLE
                }
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

    override fun onLoadMore() {
        mSkip += 10
        if (mType == INTEGRATION_PROPERTY_TYPE) {
            mPropertyViewModel.getIntegration(INTEGRATION_PROPERTY_TYPE, mSkip)
        } else {
            mPropertyViewModel.getIntegration(INTEGRATION_CHARGE_TYPE, mSkip)
        }
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
        //
        return chip
    }

    // Remove chipView
    interface ChipClickCallback {
        fun onChipRemoved(string: String?, id: String?)
    }

    override fun onIntegrationTypeClick(integrationData: IntegrationData) {
        if (mType == INTEGRATION_PROPERTY_TYPE) {
            if (mPropertyIdList.contains(integrationData.id)) {
                showMessage(null, getString(R.string.st_property_already_added))
            } else {
                clIntegrationRoot.visibility = View.GONE
                mPropertyIdList.clear()
                chipGroupProperty.removeAllViews()
                mPropertyIdList.add(integrationData.id)
                val entryChip1 = getChip(
                    chipGroupProperty,
                    integrationData.name,
                    integrationData.id,
                    true,
                    object : ChipClickCallback {
                        override fun onChipRemoved(string: String?, id: String?) {
                            if (mPropertyIdList.contains(id)) {
                                mPropertyIdList.remove(id)
                            }
                        }

                    })
                chipGroupProperty.addView(entryChip1)
            }

        } else {
            if (mChargeId.contains(integrationData.id)) {
                showMessage(null, getString(R.string.st_charge_already_added))
            } else {
                clIntegrationRoot.visibility = View.GONE
                mChargeId.add(integrationData.id)
                val entryChip1 = getChip(
                    chipGroupCharge,
                    integrationData.name,
                    integrationData.id,
                    true,
                    object : ChipClickCallback {
                        override fun onChipRemoved(string: String?, id: String?) {
                            if (mChargeId.contains(id)) {
                                mChargeId.remove(id)
                            }
                        }

                    })
                chipGroupCharge.addView(entryChip1)
            }
        }
    }

}