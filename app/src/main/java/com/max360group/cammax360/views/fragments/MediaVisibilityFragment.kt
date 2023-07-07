package com.max360group.cammax360.views.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.JobMediaList
import com.max360group.cammax360.repository.models.JobUser
import com.max360group.cammax360.repository.models.MediaUsers
import com.max360group.cammax360.repository.models.model.Users
import com.max360group.cammax360.utils.ApplicationGlobal
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.viewmodels.BaseViewModel
import com.max360group.cammax360.viewmodels.MediaDetailViewModel
import com.max360group.cammax360.views.activities.BaseAppCompactActivity
import com.max360group.cammax360.views.activities.doFragmentTransaction
import com.max360group.cammax360.views.adapters.MediaListAdapter
import com.max360group.cammax360.views.adapters.PhotoDetailPermissionsAdapter
import com.max360group.cammax360.views.calender.MonthlyActivity
import com.max360group.cammax360.views.interfaces.PhotoDetailPermissionsListener
import kotlinx.android.synthetic.main.fragment_media_visibility.*
import kotlinx.android.synthetic.main.toolbar.*

class MediaVisibilityFragment : BaseFragment(),View.OnClickListener,
    PhotoDetailPermissionsListener {

    companion object {
        const val BUNDLE_MEDIA = "media"
        const val BUNDLE_MEDIA_KIND = "kind"
        const val BUNDLE_PHOTO_TYPE = "photoType"//for before and after (0 and 1) by default its 0

        fun newInstance(mMedia: JobMediaList?, kind: String,photoType:Int=0): MediaVisibilityFragment {
            val mMediaVisibilityFragment = MediaVisibilityFragment()
            val bundle = Bundle()
            bundle.putParcelable(BUNDLE_MEDIA, mMedia)
            bundle.putString(BUNDLE_MEDIA_KIND, kind)
            bundle.putInt(BUNDLE_PHOTO_TYPE, photoType)
            mMediaVisibilityFragment.arguments = bundle

            return mMediaVisibilityFragment
        }
    }

    private val mPhotoDetailPermissionsAdapter by lazy {
        PhotoDetailPermissionsAdapter(this)
    }

    private val mMediaAdapter by lazy {
        MediaListAdapter(this)
    }

    private val mMediaDetailViewModel by lazy {
        ViewModelProvider(this).get(MediaDetailViewModel::class.java)
    }

    lateinit var mJobMediaList:JobMediaList
    private var mKind = ""
    private var mType=0

    override val layoutId: Int
        get() = R.layout.fragment_media_visibility

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

        //Get arguments
        mJobMediaList = arguments?.getParcelable<JobMediaList>(BUNDLE_MEDIA)!!
        mKind = arguments?.getString(BUNDLE_MEDIA_KIND)!!
        mType = arguments?.getInt(BUNDLE_PHOTO_TYPE)!!

        rvJobsList.adapter=mPhotoDetailPermissionsAdapter

        //Get photo detail
        if (GeneralFunctions.isInternetConnected(requireContext())) {
            rvMedia.adapter=mMediaAdapter
            mMediaDetailViewModel.getPhotoDetail(
                mJobMediaList!!.jobId,
                mJobMediaList!!.id, mKind, mJobMediaList!!.medias!![mType].id
            )
        }else{

        }

        //Set on click listener
        btnSave.setOnClickListener(this)
        ivToolbarUserImage.setOnClickListener(this)
        ivToolbarRightIcon.setOnClickListener(this)
        ivToolbarRightIconBell.setOnClickListener(this)

    }

    override val viewModel: BaseViewModel?
        get() = mMediaDetailViewModel

    override fun observeProperties() {
        mMediaDetailViewModel.onGetMediaDetail().observe(this, Observer {
           var mUsers=ArrayList<Users>()
           var mMediaUsers=ArrayList<MediaUsers>()

            //Remove my self
            mMediaUsers.clear()
            mMediaUsers.addAll(it.medias!![0].users)
            for (i in mMediaUsers.indices) {
                if (mMediaUsers[i].userId.id == mUserPrefsManager.loginedUser!!.id) {
                    mMediaUsers.removeAt(i)
                    break
                }
            }

            for ( i in mMediaUsers.indices){
                var users=Users()
                users.permissions=mMediaUsers[i].permissions
                users.name=mMediaUsers[i].userId.firstName
                users.email=mMediaUsers[i].userId.email
                users.primaryUserId=mMediaUsers[i].primaryUserId
                users.userId=mMediaUsers[i].userId.id
                mUsers.add(users)
            }
            mPhotoDetailPermissionsAdapter.updateData(mUsers)
            mMediaAdapter.updateData(it)
        })


        mMediaDetailViewModel.onShowShimmer().observe(this, Observer {
            if (it){
                rvMedia.showShimmerAdapter()
                rvJobsList.showShimmerAdapter()
            }else{
                rvMedia.hideShimmerAdapter()
                rvJobsList.hideShimmerAdapter()
            }
        })
    }

    override fun onClick(p0: View?) {
      when(p0?.id){
          R.id.btnSave->{
              mMediaDetailViewModel.updateMediaPermissions(mJobMediaList.medias!![mType].id,mKind,
              mPhotoDetailPermissionsAdapter.getUpdatedList()
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

    override fun updateData(position: Int) {
        mPhotoDetailPermissionsAdapter.notifyDataSetChanged()
    }
}