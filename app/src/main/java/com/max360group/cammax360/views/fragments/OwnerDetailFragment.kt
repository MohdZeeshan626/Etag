package com.max360group.cammax360.views.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.Tab
import com.max360group.cammax360.repository.models.UserOwner
import com.max360group.cammax360.utils.ApplicationGlobal
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.viewmodels.BaseViewModel
import com.max360group.cammax360.viewmodels.OwnerViewModel
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
import kotlinx.android.synthetic.main.load_owners_layout.view.*
import kotlinx.android.synthetic.main.toolbar.*
import java.io.File
import java.lang.Exception

class OwnerDetailFragment : BaseFragment(), CreateOwnerInterface, View.OnClickListener {

    companion object {
        const val BUNDLE_OWNER_ID = "ownerId"
        const val BUNDLE_OWNER_LOCAL_ID = "ownerLocalId"

        fun newInstance(ownerId: String, ownerLocalId: Int): OwnerDetailFragment {
            val args = Bundle()
            args.putString(BUNDLE_OWNER_ID, ownerId)
            args.putInt(BUNDLE_OWNER_LOCAL_ID, ownerLocalId)
            val fragment = OwnerDetailFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private val mOwnerMenuAdapter by lazy {
        OwnerMenuAdapter(this)
    }

    private val mCreateOwnerViewModel by lazy {
        ViewModelProvider(this).get(OwnerViewModel::class.java)
    }

    private var currentPageIndex = 0
    private var mCurrentTabIndex = 0
    private var mOwnerId = ""
    private var mOwnerLocalId = 0
    private var mUserOwner = UserOwner()
    private var composeEmail = ""

    override val layoutId: Int
        get() = R.layout.fragment_owner_detail

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
                IntentFilter(OwnersFragment.INTENT_OWNER_FRAGMENT)
            )

        //Get arguments
        mOwnerId = arguments?.getString(BUNDLE_OWNER_ID).toString()
        mOwnerLocalId = arguments?.getInt(BUNDLE_OWNER_LOCAL_ID)!!

        //Set adapter
        rvMenuAdapter.adapter = mOwnerMenuAdapter

        //Call api
        if (GeneralFunctions.isInternetConnected(requireContext())) {
            mCreateOwnerViewModel.getOwnerDetail(mOwnerId)
        } else {
            mCreateOwnerViewModel.getOwnerDetailFromLocal(mOwnerLocalId)
        }


        // Set view pager
        val tabsList = ArrayList<Tab>()
        tabsList.add(
            Tab(
                tabFragment = GeneralInfoDetailFragment(),
                tabName = getString(R.string.st_general_info)
            )
        )
        tabsList.add(
            Tab(
                tabFragment = OwnerPropertiesDetailFragment(),
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
        tabsList.add(
            Tab(
                tabFragment = IntegrationDetailFragment(),
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
        mCreateOwnerViewModel.getMenuTabs()

        //Set click listener
        btnMenu.setOnClickListener(this)
        ivToolbarUserImage.setOnClickListener(this)
        ivToolbarRightIcon.setOnClickListener(this)
        ivToolbarRightIconBell.setOnClickListener(this)
        etEmail.setOnClickListener(this)

    }

    override val viewModel: BaseViewModel
        get() = mCreateOwnerViewModel

    override fun observeProperties() {
        mCreateOwnerViewModel.onGetTabMenu().observe(this, Observer {
            mOwnerMenuAdapter.updateData(it)
        })

        mCreateOwnerViewModel.onGetOwnerData().observe(this, Observer {
            mUserOwner = it.record
            if (it.record.picURL.startsWith("https://")) {
                sdvUserImage.setImageURI(it.record.picURL)
            } else {
                sdvUserImage.setImageURI(GeneralFunctions.getLocalImageFile(File(it.record.picURL)))
            }

            etFirstName.text = it.record.firstName
            etLastName.text = it.record.lastName
            etEmail.text = it.record.email
            etTaxId.text = it.record.taxId
            etComment.text = it.record.comments
            composeEmail = it.record.email!!

            //Set data by default 1st frag of viewpager
            val adapter = viewPager.adapter as TabsAdapter
            val fragment = adapter.getItem(0) as GeneralInfoDetailFragment
            fragment.getOwnerData(mUserOwner)
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
            0 -> {
                val index: Int = viewPager.currentItem
                val adapter = viewPager.adapter as TabsAdapter
                val fragment = adapter.getCurrentVisibleFragment(index) as GeneralInfoDetailFragment
                fragment.getOwnerData(mUserOwner)
            }
            1 -> {
                val index: Int = viewPager.currentItem
                val adapter = viewPager.adapter as TabsAdapter
                val fragment =
                    adapter.getCurrentVisibleFragment(index) as OwnerPropertiesDetailFragment
                fragment.getOwnerProperties(mUserOwner.properties)
            }
            2 -> {
                val index: Int = viewPager.currentItem
                val adapter = viewPager.adapter as TabsAdapter
                val fragment = adapter.getCurrentVisibleFragment(index) as NotesHistoryFragment
                fragment.getEditableDetail(mOwnerId, NotesHistoryFragment.TYPE_OWNER)
            }
            3 -> {
                val index: Int = viewPager.currentItem
                val adapter = viewPager.adapter as TabsAdapter
                val fragment = adapter.getCurrentVisibleFragment(index) as AccessDetailFragment
                fragment.getDetailInfo(mUserOwner.access)
            }
            4 -> {
                val index: Int = viewPager.currentItem
                val adapter = viewPager.adapter as TabsAdapter
                val fragment = adapter.getCurrentVisibleFragment(index) as IntegrationDetailFragment
                fragment.getRmData(mUserOwner.rm)
            }
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btnMenu -> {
                (activityContext as BaseAppCompactActivity).doFragmentTransaction(
                    fragment = CreateOwnerFragment.newInstance(mOwnerId, mOwnerLocalId),
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
            R.id.etEmail -> {
                GeneralFunctions.email(composeEmail, requireContext())
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
                    if (mOwnerId.isNotBlank()) {
                        mCreateOwnerViewModel.getOwnerDetail(mOwnerId)
                    }

                } catch (e: Exception) {
                }
            }
        }
    }
}