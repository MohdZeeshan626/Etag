package com.max360group.cammax360.views.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.View
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.*
import com.max360group.cammax360.utils.ApplicationGlobal
import com.max360group.cammax360.utils.Constants.LOCAL_STORAGE_BASE_PATH_FOR_USER_PHOTOS
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.viewmodels.BaseViewModel
import com.max360group.cammax360.viewmodels.OwnerViewModel
import com.max360group.cammax360.views.activities.BaseAppCompactActivity
import com.max360group.cammax360.views.activities.doFragmentTransaction
import com.max360group.cammax360.views.adapters.AccessListAdapter.Companion.mList
import com.max360group.cammax360.views.adapters.AuthIntegrationAdapter.Companion.RM_DISPLAY
import com.max360group.cammax360.views.adapters.OwnerMenuAdapter
import com.max360group.cammax360.views.adapters.TabsAdapter
import com.max360group.cammax360.views.calender.MonthlyActivity
import com.max360group.cammax360.views.dialgofragments.SaveSendInviteDialogFragment
import com.max360group.cammax360.views.fragments.NotesHistoryFragment.Companion.TYPE_OWNER
import com.max360group.cammax360.views.fragments.NotesHistoryFragment.Companion.mNotesList
import com.max360group.cammax360.views.fragments.OwnerGeneralInfoFragment.Companion.mAddressList
import com.max360group.cammax360.views.fragments.OwnerGeneralInfoFragment.Companion.mEmailList
import com.max360group.cammax360.views.fragments.OwnerGeneralInfoFragment.Companion.mPhoneList
import com.max360group.cammax360.views.fragments.OwnerPropertiesFragment.Companion.mPropertyList
import com.max360group.cammax360.views.fragments.OwnersFragment.Companion.INTENT_OWNER_FRAGMENT
import com.max360group.cammax360.views.fragments.PropertiesOwnerFragment.Companion.INTENT_OWNER_RECORD
import com.max360group.cammax360.views.interfaces.CreateOwnerInterface
import kotlinx.android.synthetic.main.fragment_create_owner.*
import kotlinx.android.synthetic.main.fragment_create_owner.etComment
import kotlinx.android.synthetic.main.fragment_create_owner.etEmail
import kotlinx.android.synthetic.main.fragment_create_owner.etFirstName
import kotlinx.android.synthetic.main.fragment_create_owner.etLastName
import kotlinx.android.synthetic.main.fragment_create_owner.etTaxId
import kotlinx.android.synthetic.main.fragment_create_owner.rvMenuAdapter
import kotlinx.android.synthetic.main.fragment_create_owner.sdvUserImage
import kotlinx.android.synthetic.main.fragment_create_owner.viewPager
import kotlinx.android.synthetic.main.fragment_owner_detail.*
import kotlinx.android.synthetic.main.load_owners_layout.view.*
import kotlinx.android.synthetic.main.toolbar.*
import java.io.File


class CreateOwnerFragment : BasePictureOptionsFragment(), CreateOwnerInterface,
    View.OnClickListener {

    companion object {
        const val BUNDLE_OWNER_ID = "ownerId"
        const val BUNDLE_OWNER_LOCAL_ID = "OwnerLocalId"

        fun newInstance(id: String = "", mLocalId: Int = 0): CreateOwnerFragment {
            val args = Bundle()
            args.putString(BUNDLE_OWNER_ID, id)
            args.putInt(BUNDLE_OWNER_LOCAL_ID, mLocalId)
            val fragment = CreateOwnerFragment()
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
    private var mImageFile = ""
    var addresses = ArrayList<OwnerAddressModel>()
    private var mOwnerId = ""
    private var mOwnerLocalId = 0
    var mUserOwner = UserOwner()

    override val layoutId: Int
        get() = R.layout.fragment_create_owner

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
        mOwnerId = arguments?.getString(BUNDLE_OWNER_ID).toString()
        mOwnerLocalId = arguments?.getInt(BUNDLE_OWNER_LOCAL_ID)!!

        //Set adapter
        rvMenuAdapter.adapter = mOwnerMenuAdapter

        // Set view pager
        val tabsList = ArrayList<Tab>()

        tabsList.add(
            Tab(
                tabFragment = OwnerGeneralInfoFragment(),
                tabName = getString(R.string.st_general_info)
            )
        )
        tabsList.add(
            Tab(
                tabFragment = OwnerPropertiesFragment(),
                tabName = getString(R.string.st_properties)
            )
        )
        tabsList.add(
            Tab(
                tabFragment = NotesHistoryFragment(),
                tabName = getString(R.string.st_note_history)
            )
        )

        tabsList.add(
            Tab(
                tabFragment = AccessFragment(),
                tabName = getString(R.string.st_access)
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
                rvMenuAdapter.smoothScrollToPosition(position)

                when (position) {
                    2 -> {
                        val adapter = viewPager.adapter as TabsAdapter
                        //set data in notes
                        val notesFragment = adapter.getItem(2) as NotesHistoryFragment
                        notesFragment.getEditableDetail(mOwnerId, TYPE_OWNER)
                    }
                }

            }
        })

        //Set click listener
        ivCameraPicker.setOnClickListener(this)
        btnSave.setOnClickListener(this)
        btnUpdate.setOnClickListener(this)
        ivToolbarUserImage.setOnClickListener(this)
        ivToolbarRightIcon.setOnClickListener(this)
        ivToolbarRightIconBell.setOnClickListener(this)

        //Get tabs
        mCreateOwnerViewModel.getMenuTabs()

        //Call api
        if (mOwnerId.isNotBlank()) {
            if (GeneralFunctions.isInternetConnected(requireContext())) {
                mCreateOwnerViewModel.getOwnerDetail(mOwnerId)
            } else {
                mCreateOwnerViewModel.getOwnerDetailFromLocal(mOwnerLocalId)
            }
            btnUpdate.visibility = View.VISIBLE
            tvCreateOwner.text = getString(R.string.st_update_owner)
        } else {
            btnUpdate.visibility = View.GONE
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

    override val viewModel: BaseViewModel?
        get() = mCreateOwnerViewModel


    override fun observeProperties() {
        mCreateOwnerViewModel.onOwnerCreateSuccess().observe(this, Observer {
            (requireContext() as BaseAppCompactActivity).onBackPressed()
            //Send broadcast to update photo detail
            LocalBroadcastManager.getInstance(requireContext())
                .sendBroadcast(
                    Intent(INTENT_OWNER_FRAGMENT)
                )

            //Send broadcast to update photo detail
            LocalBroadcastManager.getInstance(requireContext())
                .sendBroadcast(
                    Intent(PropertiesOwnerFragment.INTENT_PROPERTIES_OWNER).putExtra(
                        INTENT_OWNER_RECORD,
                        it
                    ).putExtra(OwnerPropertiesFragment.INTENT_BROADCAST_TYPE, 1)
                )
        })

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
            etFirstName.setText(it.record.firstName)
            etLastName.setText(it.record.lastName)
            etEmail.setText(it.record.email)
            etTaxId.setText(it.record.taxId)
            etComment.setText(it.record.comments)
            mImageFile = it.record.pic

            //Update the viewpager fragments data
            val adapter = viewPager.adapter as TabsAdapter

            //set data in general info
            val fragmentGeneralInfo = adapter.getItem(0) as OwnerGeneralInfoFragment
            fragmentGeneralInfo.getEditableDetail(mUserOwner)

            //set data in properties
            val propertiesFragment = adapter.getItem(1) as OwnerPropertiesFragment
            propertiesFragment.getEditableDetail(mUserOwner.properties)

            //set data in notes
            val notesFragment = adapter.getItem(2) as NotesHistoryFragment
            notesFragment.getEditableDetail(mOwnerId, TYPE_OWNER)

            //set data in Access
            val accessFragment = adapter.getItem(3) as AccessFragment
            accessFragment.getEditableDetail(mUserOwner.access)

            //set data in Integration
            val rmFragment = adapter.getItem(4) as IntegrationFragment
            rmFragment.getEditableDetail(mUserOwner.rm)
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
        addresses.clear()
        addresses.addAll(mAddressList)

        //Call api
        if (GeneralFunctions.isInternetConnected(requireContext())) {
            mCreateOwnerViewModel.createOwner(
                mImageFile,
                etFirstName.text.toString().trim(),
                etLastName.text.toString().trim(),
                etEmail.text.toString().trim(),
                etTaxId.text.toString().trim(),
                etComment.text.toString().trim(),
                RM_DISPLAY,
                mEmailList,
                mPhoneList,
                addresses,
                mPropertyList,
                mNotesList,
                mList,
                true
            )
        } else {
            mCreateOwnerViewModel.createOwnerInLocal(
                mImageFile,
                etFirstName.text.toString().trim(),
                etLastName.text.toString().trim(),
                etEmail.text.toString().trim(),
                etTaxId.text.toString().trim(),
                etComment.text.toString().trim(),
                RM_DISPLAY,
                mEmailList,
                mPhoneList,
                addresses,
                mPropertyList,
                mNotesList,
                mList,
                true
            )
        }
    }

    private fun popUpMenu() {
        val wrapper: Context = ContextThemeWrapper(requireContext(), R.style.StylePopupMenu)
        val popUp = PopupMenu(wrapper, btnSave, Gravity.END)
        popUp.menuInflater.inflate(R.menu.owner_detail_pop_up_menu, popUp.menu)
        //Set Click Listener on Popup Menu Item
        popUp.setOnMenuItemClickListener { myItem ->
            when (myItem!!.itemId) {

                R.id.actionSave -> {
                    addresses.clear()
                    addresses.addAll(mAddressList)

                    //Call api
                    //Call api
                    if (GeneralFunctions.isInternetConnected(requireContext())) {
                        mCreateOwnerViewModel.createOwner(
                            mImageFile,
                            etFirstName.text.toString().trim(),
                            etLastName.text.toString().trim(),
                            etEmail.text.toString().trim(),
                            etTaxId.text.toString().trim(),
                            etComment.text.toString().trim(),
                            RM_DISPLAY,
                            mEmailList,
                            mPhoneList,
                            addresses,
                            mPropertyList,
                            mNotesList,
                            mList,
                            false
                        )
                    } else {
                        mCreateOwnerViewModel.createOwnerInLocal(
                            mImageFile,
                            etFirstName.text.toString().trim(),
                            etLastName.text.toString().trim(),
                            etEmail.text.toString().trim(),
                            etTaxId.text.toString().trim(),
                            etComment.text.toString().trim(),
                            RM_DISPLAY,
                            mEmailList,
                            mPhoneList,
                            addresses,
                            mPropertyList,
                            mNotesList,
                            mList,
                            false
                        )
                    }

                }

                R.id.actionInvite -> {
                    SaveSendInviteDialogFragment(this).show(childFragmentManager, "")
                }
            }
            true
        }
        popUp.show()
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ivCameraPicker -> {
                showPictureOptionsBottomSheet(LOCAL_STORAGE_BASE_PATH_FOR_USER_PHOTOS)
            }
            R.id.btnSave -> {
                popUpMenu()
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
            R.id.btnUpdate -> {
                addresses.clear()
                addresses.addAll(mAddressList)

                //Call api
                if (GeneralFunctions.isInternetConnected(requireContext())) {
                    mCreateOwnerViewModel.editOwner(
                        mOwnerId,
                        mImageFile,
                        etFirstName.text.toString().trim(),
                        etLastName.text.toString().trim(),
                        etEmail.text.toString().trim(),
                        etTaxId.text.toString().trim(),
                        etComment.text.toString().trim(),
                        RM_DISPLAY,
                        mEmailList,
                        mPhoneList,
                        addresses,
                        mPropertyList,
                        mNotesList,
                        mList,
                        false
                    )
                } else {
                    mCreateOwnerViewModel.updateOwnerInLocal(
                        mOwnerId,
                        mOwnerLocalId,
                        mImageFile,
                        etFirstName.text.toString().trim(),
                        etLastName.text.toString().trim(),
                        etEmail.text.toString().trim(),
                        etTaxId.text.toString().trim(),
                        etComment.text.toString().trim(),
                        RM_DISPLAY,
                        mEmailList,
                        mPhoneList,
                        addresses,
                        mPropertyList,
                        mNotesList,
                        mList,
                        false
                    )
                }

            }
        }
    }

}