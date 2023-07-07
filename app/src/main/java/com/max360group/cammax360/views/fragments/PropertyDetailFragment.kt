package com.max360group.cammax360.views.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.PropertyDetail
import com.max360group.cammax360.repository.models.Tab
import com.max360group.cammax360.utils.ApplicationGlobal
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.viewmodels.BaseViewModel
import com.max360group.cammax360.viewmodels.PropertyViewModel
import com.max360group.cammax360.views.activities.BaseAppCompactActivity
import com.max360group.cammax360.views.activities.doFragmentTransaction
import com.max360group.cammax360.views.adapters.OwnerMenuAdapter
import com.max360group.cammax360.views.adapters.TabsAdapter
import com.max360group.cammax360.views.calender.MonthlyActivity
import com.max360group.cammax360.views.interfaces.CreateOwnerInterface
import kotlinx.android.synthetic.main.fragment_create_owner.rvMenuAdapter
import kotlinx.android.synthetic.main.fragment_create_owner.viewPager
import kotlinx.android.synthetic.main.fragment_owner_detail.*
import kotlinx.android.synthetic.main.fragment_owner_detail.btnMenu
import kotlinx.android.synthetic.main.fragment_property_detail.*
import kotlinx.android.synthetic.main.fragment_property_detail.chipGroupProperty
import kotlinx.android.synthetic.main.fragment_property_detail.etComment
import kotlinx.android.synthetic.main.fragment_property_detail.etPropertyName
import kotlinx.android.synthetic.main.fragment_property_detail.etSqFit
import kotlinx.android.synthetic.main.fragment_property_detail.sdvUserImage
import kotlinx.android.synthetic.main.fragment_property_detail.chipGroupCharge
import kotlinx.android.synthetic.main.fragment_property_detail.etLastName
import kotlinx.android.synthetic.main.load_owners_layout.view.*
import kotlinx.android.synthetic.main.toolbar.*
import java.io.File
import java.lang.Exception

class PropertyDetailFragment : BaseFragment(), CreateOwnerInterface, View.OnClickListener {

    companion object {
        const val BUNDLE_PROPERTY_ID = "propertyId"
        const val BUNDLE_PROPERTY_LOCAL_ID = "propertyLocalId"

        fun newInstance(propertyId: String,propertyLocalId:Int): PropertyDetailFragment {
            val args = Bundle()
            args.putString(BUNDLE_PROPERTY_ID, propertyId)
            args.putInt(BUNDLE_PROPERTY_LOCAL_ID, propertyLocalId)
            val fragment = PropertyDetailFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private val mOwnerMenuAdapter by lazy {
        OwnerMenuAdapter(this)
    }

    private val mPropertyViewModel by lazy {
        ViewModelProvider(this).get(PropertyViewModel::class.java)
    }

    private var currentPageIndex = 0
    private var mCurrentTabIndex = 0
    private var mPropertyId = ""
    private var mPropertyLocalId = 0
    private var mPropertyRecord = PropertyDetail()

    override val layoutId: Int
        get() = R.layout.fragment_property_detail

    override fun init(savedInstanceState: Bundle?) {
        // Set toolbar
        toolbar.navigationIcon =
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_back_primary)
        ivToolbarUserImage.setImageURI(mUserPrefsManager.loginedUser!!.picURL)

        Glide
            .with(requireContext())
            .load(ApplicationGlobal.organisationLogo)
            .placeholder(R.drawable.ic_mimi_logo)
            .into(ivToolbarLeftIcon)

        // Register receiver for updating profile
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(
                mGetUpdateDataBroadcastReceiver,
                IntentFilter(PropertiesFragment.INTENT_PROPERTIES)
            )

        //Get arguments
        mPropertyId = arguments?.getString(BUNDLE_PROPERTY_ID).toString()
        mPropertyLocalId = arguments?.getInt(BUNDLE_PROPERTY_LOCAL_ID)!!

        //Set adapter
        rvMenuAdapter.adapter = mOwnerMenuAdapter

        // Set view pager
        val tabsList = ArrayList<Tab>()
        tabsList.add(
            Tab(
                tabFragment = PropertyGeneralInfoDetailFragment(),
                tabName = getString(R.string.st_general_info)
            )
        )
        tabsList.add(
            Tab(
                tabFragment = PropertyOwnerDetailFragment(),
                tabName = getString(R.string.st_properties)
            )
        )
        tabsList.add(
            Tab(
                tabFragment = PropertyUnitDetailFragment(),
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
                tabFragment = AccessDetailFragment(),
                tabName = getString(R.string.st_properties)
            )
        )
      /*  tabsList.add(
            Tab(
                tabFragment = PropertyJobsFragment(),
                tabName = getString(R.string.st_properties)
            )
        )*/
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
                rvMenuAdapter.smoothScrollToPosition(position)
                switchFragment(position)
            }
        })

        //Get menu tabs
        mPropertyViewModel.getDetailTabs()
        //Call api
        if (GeneralFunctions.isInternetConnected(requireContext())){
            mPropertyViewModel.getPropertyDetail(mPropertyId)
        }else{
            mPropertyViewModel.getPropertiesDetailFromLocal(mPropertyLocalId)
        }

        //Set click listener
        btnMenu.setOnClickListener(this)
        ivToolbarUserImage.setOnClickListener(this)
        ivToolbarRightIcon.setOnClickListener(this)
        ivToolbarRightIconBell.setOnClickListener(this)

    }

    override val viewModel: BaseViewModel
        get() = mPropertyViewModel

    override fun observeProperties() {
        mPropertyViewModel.onGetTabs().observe(this, Observer {
            mOwnerMenuAdapter.updateData(it)
        })

        mPropertyViewModel.onGetPropertyDetail().observe(this, Observer {
            mPropertyRecord = it
            if (it.picURL.startsWith("https://")) {
                sdvUserImage.setImageURI(it.picURL)
            } else {
                sdvUserImage.setImageURI(GeneralFunctions.getLocalImageFile(File(it.picURL)))
            }
            etPropertyName.text = it.name
            etLastName.text = it.shortName
            etSqFit.text = it.squareFootage.toString()
            etComment.text = it.comments

            //Call for get types
            if (GeneralFunctions.isInternetConnected(requireContext())){
                mPropertyViewModel.getIntegrationAll()
            }else{
                mPropertyViewModel.getAllIntegrationCommonsFromLocal()
            }

            //Update the data in all child frag
            val adapter = viewPager.adapter as TabsAdapter
            val generalInfoFrag = adapter.getItem(0) as PropertyGeneralInfoDetailFragment
            generalInfoFrag.getOwnerData(mPropertyRecord)

            val ownerFragment = adapter.getItem(1) as PropertyOwnerDetailFragment
            ownerFragment.getDetailInfo(mPropertyRecord.userOwners)

            val unitFragment = adapter.getItem(2) as PropertyUnitDetailFragment
            unitFragment.getDetailInfo(mPropertyRecord.propertyUnits)

            val notesHistory = adapter.getItem(3) as NotesHistoryFragment
            notesHistory.getEditableDetail(mPropertyRecord.id, NotesHistoryFragment.TYPE_PROPERTY)

            val accessFrag = adapter.getItem(4) as AccessDetailFragment
            accessFrag.getDetailInfo(mPropertyRecord.access)
        })

        mPropertyViewModel.onGetIntegrationAll().observe(this, Observer {
            for (i in it.propertyTypes!!.indices) {
                if (mPropertyRecord.propertyTypes!!.contains(it.propertyTypes!![i].id)) {
                    val entryChip1 = getChip(
                        it.propertyTypes!![i].name
                    )
                    chipGroupProperty.addView(entryChip1)
                }
            }

            for (i in it.chargeTypes!!.indices) {
                if (mPropertyRecord.chargeTypes!!.contains(it.chargeTypes!![i].id)) {
                    val entryChip1 = getChip(
                        it.chargeTypes!![i].name
                    )
                    chipGroupCharge.addView(entryChip1)
                }
            }
        })
    }

    fun switchFragment(position: Int){
        when(position){
            5->{
                //Set data by default 1st frag of viewpager
                val index: Int = viewPager.currentItem
                val adapter = viewPager.adapter as TabsAdapter
                val fragment = adapter.getCurrentVisibleFragment(index) as IntegrationFragment
                fragment.syncRm(false)
                fragment.syncRm(false)
            }
        }
    }

    //Set chipView
    private fun getChip(
        text: String?
    ): Chip {
        val chip = Chip(requireContext())
        val paddingDp = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 10f,
            resources.displayMetrics
        ).toInt()
        chip.setPadding(paddingDp, paddingDp, paddingDp, paddingDp)
        chip.text = text
        chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        chip.setChipBackgroundColorResource(R.color.colorDivider)
        return chip
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
            R.id.btnMenu -> {
                (activityContext as BaseAppCompactActivity).doFragmentTransaction(
                    fragment =  CreatePropertyFragment.newInstance(mPropertyId,mPropertyLocalId,from = CreatePropertyFragment.BUNDLE_CREATE_PROPERTY),
                    containerViewId = R.id.flFragContainerMain,
                    enterAnimation = R.animator.slide_right_in,
                    popExitAnimation = R.animator.slide_right_out
                )
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

    private val mGetUpdateDataBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, p1: Intent?) {
            context?.let {
                //Call api
                try {
                    //Call api
                    mPropertyViewModel.getPropertyDetail(mPropertyId)
                } catch (e: Exception) {
                }
            }
        }
    }
}