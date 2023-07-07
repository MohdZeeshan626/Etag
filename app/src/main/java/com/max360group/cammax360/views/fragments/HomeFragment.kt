package com.max360group.cammax360.views.fragments


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.DashBoardMenuModel
import com.max360group.cammax360.utils.ApplicationGlobal.Companion.organisationLogo
import com.max360group.cammax360.viewmodels.BaseViewModel
import com.max360group.cammax360.viewmodels.DashboardViewModel
import com.max360group.cammax360.views.activities.BaseAppCompactActivity
import com.max360group.cammax360.views.activities.doFragmentTransaction
import com.max360group.cammax360.views.adapters.SideBarItemsAdapter
import com.max360group.cammax360.views.calender.MonthlyActivity
import com.max360group.cammax360.views.fragments.AccountsFragment.Companion.BUNDLE_NAVIGATION
import kotlinx.android.synthetic.main.layout_side_var.*
import kotlinx.android.synthetic.main.load_data_layout.view.*
import kotlinx.android.synthetic.main.load_organisation_layout.view.*
import kotlinx.android.synthetic.main.toolbar.*


class HomeFragment : BaseFragment(), SideBarItemsAdapter.HomeListener, View.OnClickListener {

    companion object {
        const val INTENT_HOME = "home"
        const val INTENT_DO_TRANSACTION = "transaction"
    }

    private val mSideBarItemsAdapter by lazy {
        SideBarItemsAdapter(this)
    }

    private val mDashboardViewModel by lazy {
        ViewModelProvider(this).get(DashboardViewModel::class.java)
    }

    var mMenuList = ArrayList<DashBoardMenuModel>()

    override val layoutId: Int
        get() = R.layout.fragment_dashboard

    override fun init(savedInstanceState: Bundle?) {
        //Set toolbar
        // Set toolbar
        toolbar.navigationIcon =
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_menu_burger)

        toolbar.setNavigationOnClickListener {
            initSlideAnim()
        }

        //Set organisation logo
        initUpdateOrganisation()

        //Set image
        ivToolbarUserImage.setImageURI(mUserPrefsManager.loginedUser!!.picURL)

        // Register receiver for updating profile
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(
                mGetUpdateAddressReceiver,
                IntentFilter(INTENT_HOME)
            )

        //Initialize local broadcast to refreshFragment
        LocalBroadcastManager.getInstance(activityContext)
            .registerReceiver(
                mUpdateProfileReceiver,
                IntentFilter(EditProfileFragment.INTENT_PROFILE)
            )

        //Set click listener
        ivToolbarUserImage.setOnClickListener(this)
        ivToolbarSearch.setOnClickListener(this)
        ivToolbarRightIcon.setOnClickListener(this)
        ivToolbarRightIconBell.setOnClickListener(this)


        //Set adapter
        rvMenuList.adapter = mSideBarItemsAdapter

        //Get items
        mDashboardViewModel.getMenu()

        //first fragment transition
        ivToolbarSearch.visibility = View.VISIBLE
        doFragmentTransaction(JobsFragment())

    }

    override val viewModel: BaseViewModel?
        get() = mDashboardViewModel

    override fun observeProperties() {
        mDashboardViewModel.onGetMenu().observe(this, Observer {
            mMenuList.clear()
            mMenuList.addAll(it)
            mSideBarItemsAdapter.updateData(mMenuList, 0)
        })
    }

    private fun initSlideAnim() {
        if (clSideView.visibility == View.GONE) {
            val open: Animation =
                AnimationUtils.loadAnimation(requireContext(), R.anim.left_to_right)
            clSideView.visibility = View.VISIBLE
            clSideView.startAnimation(open)
            toolbar.navigationIcon =
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_back_primary)

        } else {
            var close =
                AnimationUtils.loadAnimation(requireContext(), R.anim.right_to_left)
            clSideView.visibility = View.GONE
            clSideView.startAnimation(close)
            toolbar.navigationIcon =
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_menu_burger)
        }
    }

    override fun onItemClick(name: String) {
        initType(name, true)
    }

    private fun initType(name: String, mFromSideMenu: Boolean) {
        when (name) {
            getString(R.string.st_dashboard) -> {
                ivToolbarSearch.visibility = View.GONE
                ivToolbarRightIcon.visibility = View.VISIBLE
                doFragmentTransaction(DashBoardFragment())
            }
            getString(R.string.st_account) -> {
                ivToolbarSearch.visibility = View.GONE
                ivToolbarRightIcon.visibility = View.VISIBLE
                doFragmentTransaction(AccountsFragment.newInstance(BUNDLE_NAVIGATION))

            }
            getString(R.string.st_jobs) -> {
                ivToolbarSearch.visibility = View.VISIBLE
                ivToolbarRightIcon.visibility = View.VISIBLE
                doFragmentTransaction(JobsFragment())

            }
            getString(R.string.st_owners) -> {
                ivToolbarSearch.visibility = View.VISIBLE
                ivToolbarRightIcon.visibility = View.VISIBLE
                doFragmentTransaction(OwnersFragment())

            }
            getString(R.string.st_properties) -> {
                ivToolbarSearch.visibility = View.VISIBLE
                ivToolbarRightIcon.visibility = View.VISIBLE
                doFragmentTransaction(PropertiesFragment())

            }
        }

        //Check to listen from side bar
        if (mFromSideMenu) {
            initSlideAnim()
        } else {
            for (i in mMenuList.indices) {
                if (mMenuList[i].name == name) {
                    mSideBarItemsAdapter.updateData(mMenuList, i)
                    break
                }
            }
        }
    }

    private fun doFragmentTransaction(fragment: Fragment) {
        (activityContext as BaseAppCompactActivity).doFragmentTransaction(
            fragManager = childFragmentManager,
            fragment = fragment,
            containerViewId = R.id.flNavContainer,
            enterAnimation = R.animator.slide_right_in,
            popExitAnimation = R.animator.scale_fade_out
        )
    }

    private val mGetUpdateAddressReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, p1: Intent?) {
            context?.let {
                try {
                    val name = p1!!.getStringExtra(INTENT_DO_TRANSACTION)
                    initType(name!!, false)

                } catch (e: Exception) {
                }

            }

        }
    }

    private val mUpdateProfileReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, p1: Intent?) {
            // Get data
            try {
                ivToolbarUserImage.setImageURI(mUserPrefsManager.loginedUser!!.picURL)
                initUpdateOrganisation()
            } catch (e: Exception) {

            }

        }
    }

    private fun initUpdateOrganisation() {
        if (mUserPrefsManager.getAccount.isNotBlank()) {
            if (mUserPrefsManager.loginedUser!!.accounts!!.isNotEmpty()) {
                for (i in mUserPrefsManager.loginedUser!!.accounts!!.indices) {
                    if (mUserPrefsManager.getAccount == mUserPrefsManager.loginedUser!!.accounts!![i].primaryUserId!!.id) {
                        Glide
                            .with(requireContext())
                            .load(mUserPrefsManager.loginedUser!!.accounts!![i].primaryUserId!!.account!!.logoURL)
                            .placeholder(R.drawable.ic_mimi_logo)
                            .into(ivToolbarLeftIcon)
                        organisationLogo =
                            mUserPrefsManager.loginedUser!!.accounts!![i].primaryUserId!!.account!!.logoURL!!
                    }
                }
            }
        } else {
            if (mUserPrefsManager.loginedUser!!.accounts!!.isNotEmpty()) {
                Glide
                    .with(requireContext())
                    .load(mUserPrefsManager.loginedUser!!.accounts!![0].primaryUserId!!.account!!.logoURL)
                    .placeholder(R.drawable.ic_mimi_logo)
                    .into(ivToolbarLeftIcon)
                organisationLogo =
                    mUserPrefsManager.loginedUser!!.accounts!![0].primaryUserId!!.account!!.logoURL!!
            }
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ivToolbarUserImage -> {
                initType(getString(R.string.st_account), false)
            }

            R.id.ivToolbarSearch -> {
                val mFragment =
                    childFragmentManager.findFragmentById(R.id.flNavContainer)

                when (mFragment) {
                    is JobsFragment -> {
                        mFragment.showSearchView()
                    }
                    is OwnersFragment -> {
                        mFragment.showSearchView()
                    }
                    is PropertiesFragment -> {
                        mFragment.showSearchView()
                    }
                    else -> {

                    }
                }
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
}