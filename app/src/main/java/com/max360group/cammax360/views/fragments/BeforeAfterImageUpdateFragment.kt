package com.max360group.cammax360.views.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.makeramen.roundedimageview.RoundedImageView
import com.max360group.cammax360.R
import com.max360group.cammax360.editorclasses.EditImageActivity
import com.max360group.cammax360.repository.models.JobMembers
import com.max360group.cammax360.repository.models.model.BeforeAfterImageModel
import com.max360group.cammax360.repository.models.model.Permissions
import com.max360group.cammax360.repository.models.model.User
import com.max360group.cammax360.utils.Constants
import com.max360group.cammax360.viewmodels.BaseViewModel
import com.max360group.cammax360.viewmodels.JobMediaViewModel
import com.max360group.cammax360.views.activities.BaseAppCompactActivity
import com.max360group.cammax360.views.activities.HomeActivity
import com.max360group.cammax360.views.activities.doFragmentTransaction
import com.max360group.cammax360.views.adapters.ImagesAdapter
import com.max360group.cammax360.views.dialgofragments.CameraDialogFragment.CameraDialogFragment
import com.max360group.cammax360.views.dialgofragments.MediaTagsDialogFragment
import com.max360group.cammax360.views.dialgofragments.MediaTagsDialogFragment.Companion.BUNDLE_IMAGE_AFTER
import com.max360group.cammax360.views.dialgofragments.MediaTagsDialogFragment.Companion.BUNDLE_IMAGE_BEFORE
import com.max360group.cammax360.views.dialgofragments.MediaTagsDialogFragment.Companion.BUNDLE_IMAGE_SIMPLE
import com.max360group.cammax360.views.fragments.PhotoDetailsPermissions.Companion.PARAM_SUBKIND
import com.max360group.cammax360.views.interfaces.PictureListener
import kotlinx.android.synthetic.main.fragment_before_after_update.*
import kotlinx.android.synthetic.main.toolbar.*

import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.views.calender.MonthlyActivity
import java.io.*


class BeforeAfterImageUpdateFragment : BasePictureOptionsFragment(), PictureListener,
    View.OnClickListener {

    companion object {
        const val BUNDLE_IMAGE_LIST = "image list"
        const val BUNDLE_POSITION = "position"
        const val BUNDLE_JOB_ID = "jobId"
        const val BUNDLE_LOCAL_ID = "localId"
        const val INTENT_IMAGE = "imageIntent"
        const val INTENT_FILTER_INTENT = "imageFilterIntent"
        const val INTENT_AFTER_IMAGE = "afterImage"
        const val BUNDLE_USERS = "users"
        const val BEFORE_IMAGE = 0
        const val AFTER_IMAGE = 1
        const val SIMPLE_IMAGE = 2

        fun newInstance(
            position: Int,
            mImageList: ArrayList<BeforeAfterImageModel>,
            mJobId: String,
            mLocalId:String,
            mUser: ArrayList<User>?
        ): BeforeAfterImageUpdateFragment {
            val mFragment = BeforeAfterImageUpdateFragment()
            val bundle = Bundle()
            bundle.putInt(BUNDLE_POSITION, position)
            bundle.putString(BUNDLE_JOB_ID, mJobId)
            bundle.putString(BUNDLE_LOCAL_ID, mLocalId)
            bundle.putParcelableArrayList(BUNDLE_IMAGE_LIST, mImageList)
            bundle.putParcelableArrayList(BUNDLE_USERS, mUser)
            mFragment.arguments = bundle
            return mFragment
        }
    }

    private val mJobMediaViewModel by lazy {
        ViewModelProvider(this).get(JobMediaViewModel::class.java)
    }

    private var mImageList = ArrayList<BeforeAfterImageModel>()
    private var mMembersList = mutableListOf<JobMembers>()
    private var mPosition = 0
    private var isBeforeImage = true
    private var isEditImage = 0
    private var mJobId = ""
    private var mLocalId = ""
    private var mUser: ArrayList<User>? =
        ArrayList()

    private val mImagesAdapter by lazy {
        ImagesAdapter(this)
    }

    override val layoutId: Int
        get() = R.layout.fragment_before_after_update


    override fun setData(savedInstanceState: Bundle?) {
        //Get arguments
        arguments?.let {
            mImageList =
                it.getParcelableArrayList<BeforeAfterImageModel>(BUNDLE_IMAGE_LIST) as ArrayList<BeforeAfterImageModel>
            mUser =
                it.getParcelableArrayList<User>(BUNDLE_USERS) as ArrayList<User>
            mPosition = it.getInt(BUNDLE_POSITION)
            mJobId = it.getString(BUNDLE_JOB_ID).toString()
            mLocalId = it.getString(BUNDLE_LOCAL_ID).toString()
        }

        // Set toolbar
        toolbar.navigationIcon =
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_back_primary)
        ivToolbarUserImage.setImageURI(mUserPrefsManager.loginedUser!!.picURL)

        //Initialize local broadcast to refreshFragment
        LocalBroadcastManager.getInstance(activityContext)
            .registerReceiver(
                mUpdateProfileBroadcastReceiver,
                IntentFilter(INTENT_IMAGE)
            )

        //Initialize local broadcast to refreshFragment
        LocalBroadcastManager.getInstance(activityContext)
            .registerReceiver(
                mFilterBroadcastReceiver,
                IntentFilter(INTENT_FILTER_INTENT)
            )

        //Set adapter
        rvGallary.adapter = mImagesAdapter

        //Set data
        initSetData()

        //Set click listener
        ivAfterCameraAndEdit.setOnClickListener(this)
        ivEditBeforeAndCamera.setOnClickListener(this)
        ivBeforeUploadAndCancel.setOnClickListener(this)
        ivAfterUploadCancel.setOnClickListener(this)
        ivCamera.setOnClickListener(this)
        ivTagAfter.setOnClickListener(this)
        ivTagsBefore.setOnClickListener(this)
        ivTagsSimple.setOnClickListener(this)
        ivVisibilityAfter.setOnClickListener(this)
        ivVisibilityBefore.setOnClickListener(this)
        ivVisibilitySimple.setOnClickListener(this)
        ivSimpleCancel.setOnClickListener(this)
        ivDone.setOnClickListener(this)
        ivEditSimple.setOnClickListener(this)
        ivToolbarRightIcon.setOnClickListener(this)
        ivToolbarRightIconBell.setOnClickListener(this)

    }

    override fun onGettingImageFile(file: File) {
        if (!isBeforeImage) {
            val afterImage = file.absolutePath
            mImageList[mPosition].afterImage = afterImage
            mImageList[mPosition].afterImageName = "After${mPosition + 1}"
            mImagesAdapter.updateData(mImageList, mPosition)
            ivAfterCameraAndEdit.setImageResource(R.drawable.ic_edit)
            ivAfterUploadCancel.setImageResource(R.drawable.ic_cancel)
            setImage()
        } else {
            val beforeImage = file.absolutePath
            mImageList[mPosition].beforeImage = beforeImage
            mImageList[mPosition].beforeImageName = "Before${mPosition + 1}"
            mImagesAdapter.updateData(mImageList, mPosition)
            ivAfterCameraAndEdit.setImageResource(R.drawable.ic_edit)
            ivAfterUploadCancel.setImageResource(R.drawable.ic_cancel)
            setImage()
        }

        //Send broadcast
        LocalBroadcastManager.getInstance(requireContext())
            .sendBroadcast(
                Intent(INTENT_IMAGE).putExtra(
                    BUNDLE_IMAGE_LIST, mImageList
                )
            )

    }

    override fun onGettingMultipleImages(list: List<String>) {

    }

    override fun onStartCameraDialogFragment(isOpen: Boolean) {

    }

    override val viewModel: BaseViewModel?
        get() = mJobMediaViewModel

    override fun observeProperties() {
        mJobMediaViewModel.onMediaUpdated().observe(this, Observer {
            //Send broadcast
            LocalBroadcastManager.getInstance(requireContext())
                .sendBroadcast(
                    Intent(JobDetailsFragment.BUNDLE_JOB_DETAIL_INTENT)
                )
            (requireContext() as BaseAppCompactActivity).onBackPressed()
            (requireContext() as BaseAppCompactActivity).onBackPressed()
        })
    }

    private fun initSetData() {
        if (mImageList[mPosition].simpleImage.isNotEmpty()) {
            clSimpleView.visibility = View.VISIBLE
            clBeforeAfter.visibility = View.GONE
            setImage(sdvSimpleImage, mImageList[mPosition].simpleImage)

        } else {
            clSimpleView.visibility = View.GONE
            clBeforeAfter.visibility = View.VISIBLE
            setImage(sdvImageBefore, mImageList[mPosition].beforeImage)

        }

        //Update adapter
        mImagesAdapter.updateData(mImageList, mPosition)
    }

    override fun onImageClick(position: Int) {
        mPosition = position
        setImage()
    }

    override fun onDeleteImage(position: Int) {
        mImageList.removeAt(position)
        if (mImageList.isEmpty()) {
            //Send broadcast
            LocalBroadcastManager.getInstance(requireContext())
                .sendBroadcast(
                    Intent(INTENT_IMAGE).putExtra(
                        BUNDLE_IMAGE_LIST, mImageList
                    )
                )
            (requireContext() as BaseAppCompactActivity).onBackPressed()
        } else {
            mPosition = position
            if (mPosition == mImageList.size) {
                mPosition = 0
                mImagesAdapter.updateData(mImageList, mPosition)
                setImage()
            } else {
                setImage()
                mImagesAdapter.updateData(mImageList, mPosition)
            }

            //Send broadcast
            LocalBroadcastManager.getInstance(requireContext())
                .sendBroadcast(
                    Intent(INTENT_IMAGE).putExtra(
                        BUNDLE_IMAGE_LIST, mImageList
                    )
                )
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ivAfterCameraAndEdit -> {
                //For pick image
                if (mImageList[mPosition].afterImage.isBlank()) {
                    isBeforeImage = false
                    (activityContext as BaseAppCompactActivity).doFragmentTransaction(
                        fragment = CameraDialogFragment.newInstance(
                            CameraDialogFragment.BUNDLE_AFTER,
                            mImageList[mPosition].beforeImage,
                            mJobId,mLocalId
                        ),
                        containerViewId = R.id.flFragContainerMain,
                        enterAnimation = R.animator.slide_right_in,
                        popExitAnimation = R.animator.slide_right_out
                    )
                } else {
                    //For edit the image
                    isEditImage = AFTER_IMAGE
                    var intent = Intent(requireContext(), EditImageActivity::class.java)
                    intent.putExtra("IMAGE", mImageList[mPosition].afterImage)
                    startActivity(intent)
                }
            }
            R.id.ivEditBeforeAndCamera -> {
                //For pick image
                if (mImageList[mPosition].beforeImage.isBlank()) {
                    isBeforeImage = true
                    (activityContext as BaseAppCompactActivity).doFragmentTransaction(
                        fragment = CameraDialogFragment.newInstance(
                            CameraDialogFragment.BUNDLE_AFTER,
                            mImageList[mPosition].afterImage,
                            mJobId,mLocalId
                        ),
                        containerViewId = R.id.flFragContainerMain,
                        enterAnimation = R.animator.slide_right_in,
                        popExitAnimation = R.animator.slide_right_out
                    )
                } else {
                    //For edit the image
                    isEditImage = BEFORE_IMAGE
                    var intent = Intent(requireContext(), EditImageActivity::class.java)
                    intent.putExtra("IMAGE", mImageList[mPosition].beforeImage)
                    startActivity(intent)
                }
            }

            R.id.ivAfterUploadCancel -> {
                if (mImageList[mPosition].afterImage.isNotBlank()) {
                    if (mImageList[mPosition].beforeImage.isBlank()) {
                        mImageList.removeAt(mPosition)
                        if (mImageList.isEmpty()) {
                            (requireContext() as BaseAppCompactActivity).onBackPressed()
                        } else {
                            if (mPosition == mImageList.size) {
                                mPosition = 0
                                mImagesAdapter.updateData(mImageList, mPosition)
                                setImage()
                            } else {
                                setImage()
                                mImagesAdapter.updateData(mImageList, mPosition)
                            }
                        }
                    } else {
                        mImageList[mPosition].afterImage = ""
                        setImage()

                    }
                } else {
                    isBeforeImage = false
                    showGallaryImage(Constants.LOCAL_STORAGE_BASE_PATH_FOR_USER_PHOTOS)
                }

                //Send broadcast
                LocalBroadcastManager.getInstance(requireContext())
                    .sendBroadcast(
                        Intent(INTENT_IMAGE).putExtra(
                            BUNDLE_IMAGE_LIST, mImageList
                        )
                    )

            }
            R.id.ivBeforeUploadAndCancel -> {
                if (mImageList[mPosition].beforeImage.isNotBlank()) {
                    if (mImageList[mPosition].afterImage.isBlank()) {
                        mImageList.removeAt(mPosition)
                        if (mImageList.isEmpty()) {
                            (requireContext() as BaseAppCompactActivity).onBackPressed()
                        } else {
                            if (mImageList.size == mPosition) {
                                mPosition = 0
                                mImagesAdapter.updateData(mImageList, mPosition)
                                setImage()

                            } else {
                                setImage()
                                mImagesAdapter.updateData(mImageList, mPosition)

                            }
                        }
                    } else {
                        mImageList[mPosition].beforeImage = ""
                        setImage()
                    }
                } else {
                    isBeforeImage = true
                    showGallaryImage(Constants.LOCAL_STORAGE_BASE_PATH_FOR_USER_PHOTOS)
                }

                //Send broadcast
                LocalBroadcastManager.getInstance(requireContext())
                    .sendBroadcast(
                        Intent(INTENT_IMAGE).putExtra(
                            BUNDLE_IMAGE_LIST, mImageList
                        )
                    )
            }

            R.id.ivSimpleCancel -> {
                mImageList.removeAt(mPosition)
                if (mImageList.isEmpty()) {
                    (requireContext() as BaseAppCompactActivity).onBackPressed()
                } else {
                    if (mImageList.size == mPosition) {
                        mPosition = 0
                        mImagesAdapter.updateData(mImageList, mPosition)
                        setImage()

                    } else {
                        setImage()
                        mImagesAdapter.updateData(mImageList, mPosition)

                    }
                }

                //Send broadcast
                LocalBroadcastManager.getInstance(requireContext())
                    .sendBroadcast(
                        Intent(INTENT_IMAGE).putExtra(
                            BUNDLE_IMAGE_LIST, mImageList
                        )
                    )

            }

            R.id.ivCamera -> {
                //Send broadcast
                LocalBroadcastManager.getInstance(requireContext())
                    .sendBroadcast(
                        Intent(INTENT_IMAGE).putExtra(
                            BUNDLE_IMAGE_LIST, mImageList
                        )
                    )
                (requireContext() as BaseAppCompactActivity).onBackPressed()
            }

            R.id.ivTagsSimple -> {
                MediaTagsDialogFragment.newInstance(mImageList, mPosition, BUNDLE_IMAGE_SIMPLE)
                    .show(childFragmentManager, "")
            }

            R.id.ivVisibilitySimple -> {
                (activityContext as BaseAppCompactActivity).doFragmentTransaction(
                    fragment = PhotoDetailsPermissions.newInstance(
                        mJobId,
                        mImageList,
                        mPosition,
                        BUNDLE_IMAGE_SIMPLE
                    ),
                    containerViewId = R.id.flFragContainerMain,
                    enterAnimation = R.animator.slide_right_in,
                    popExitAnimation = R.animator.slide_right_out
                )

            }

            R.id.ivTagsBefore -> {
                MediaTagsDialogFragment.newInstance(mImageList, mPosition, BUNDLE_IMAGE_BEFORE)
                    .show(childFragmentManager, "")
            }
            R.id.ivTagAfter -> {
                MediaTagsDialogFragment.newInstance(mImageList, mPosition, BUNDLE_IMAGE_AFTER)
                    .show(childFragmentManager, "")
            }

            R.id.ivVisibilityAfter -> {
                (activityContext as BaseAppCompactActivity).doFragmentTransaction(
                    fragment = PhotoDetailsPermissions.newInstance(
                        mJobId,
                        mImageList,
                        mPosition,
                        BUNDLE_IMAGE_AFTER
                    ),
                    containerViewId = R.id.flFragContainerMain,
                    enterAnimation = R.animator.slide_right_in,
                    popExitAnimation = R.animator.slide_right_out
                )
            }
            R.id.ivVisibilityBefore -> {
                (activityContext as BaseAppCompactActivity).doFragmentTransaction(
                    fragment = PhotoDetailsPermissions.newInstance(
                        mJobId,
                        mImageList,
                        mPosition,
                        BUNDLE_IMAGE_BEFORE
                    ),
                    containerViewId = R.id.flFragContainerMain,
                    enterAnimation = R.animator.slide_right_in,
                    popExitAnimation = R.animator.slide_right_out
                )
            }

            R.id.ivDone -> {
                //Save the dummy image
                for (i in mImageList.indices) {
                    if (mImageList[i].beforeImage.isNotEmpty()) {
                        if (mImageList[i].afterImage.isEmpty()) {
                            mImageList[i].afterImage =
                                GeneralFunctions.saveToSDCard(
                                    R.drawable.iv_grey_bg,
                                    "Placeholder_${System.currentTimeMillis()}.jpg",
                                    requireContext()
                                )!!
                        }
                    }

                    if (mImageList[i].afterImage.isNotEmpty()) {
                        if (mImageList[i].beforeImage.isEmpty()) {
                            mImageList[i].beforeImage =
                                GeneralFunctions.saveToSDCard(
                                    R.drawable.iv_grey_bg,
                                    "Placeholder_${System.currentTimeMillis()}.jpg",
                                    requireContext()
                                )!!
                        }
                    }
                }
                if (GeneralFunctions.isInternetConnected(requireContext())){
                    mJobMediaViewModel.addJobMedia(mJobId, mImageList, mUser!!)
                }else{
                    mJobMediaViewModel.saveJobMediaLocal(mJobId,mLocalId, mImageList, mUser)
                }
            }
            R.id.ivEditSimple -> {
                isEditImage = SIMPLE_IMAGE
                var intent = Intent(requireContext(), EditImageActivity::class.java)
                intent.putExtra("IMAGE", mImageList[mPosition].simpleImage)
                startActivity(intent)
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


    private fun setImage() {
        if (mImageList[mPosition].simpleImage.isNotEmpty()) {
            clSimpleView.visibility = View.VISIBLE
            clBeforeAfter.visibility = View.GONE
            setImage(sdvSimpleImage, mImageList[mPosition].simpleImage)

        } else {
            clSimpleView.visibility = View.GONE
            clBeforeAfter.visibility = View.VISIBLE
            setImage(sdvImageBefore, mImageList[mPosition].beforeImage)
            setImage(sdvImageAfter, mImageList[mPosition].afterImage)

            if (mImageList[mPosition].afterImage.isNotBlank()) {
                ivAfterCameraAndEdit.setImageResource(R.drawable.ic_edit)
                ivAfterUploadCancel.setImageResource(R.drawable.ic_cancel)
            } else {
                ivAfterCameraAndEdit.setImageResource(R.drawable.ic_camera_intent)
                ivAfterUploadCancel.setImageResource(R.drawable.ic_upload)
            }

            if (mImageList[mPosition].beforeImage.isNotBlank()) {
                ivEditBeforeAndCamera.setImageResource(R.drawable.ic_edit)
                ivBeforeUploadAndCancel.setImageResource(R.drawable.ic_cancel)
            } else {
                ivEditBeforeAndCamera.setImageResource(R.drawable.ic_camera_intent)
                ivBeforeUploadAndCancel.setImageResource(R.drawable.ic_upload)
            }

        }
    }

    private fun setImage(view: RoundedImageView, image: String) {
        Glide
            .with(requireContext())
            .load(image)
            .centerCrop()
            .placeholder(R.color.colorPrimaryText)
            .into(view)

    }

    private val mFilterBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, p1: Intent?) {
            // Get data
            try {
                var mImage = p1?.getStringExtra(INTENT_AFTER_IMAGE)
                when (isEditImage) {
                    SIMPLE_IMAGE -> {
                        sdvSimpleImage.setImageURI(Uri.parse(mImage))
                        mImageList[mPosition].simpleImage = mImage!!
                        mImagesAdapter.updateData(mImageList, mPosition)
                    }
                    AFTER_IMAGE -> {
                        mImageList[mPosition].afterImage = mImage!!
                        mImagesAdapter.updateData(mImageList, mPosition)
                        setImage()
                    }
                    BEFORE_IMAGE -> {
                        mImageList[mPosition].beforeImage = mImage!!
                        mImagesAdapter.updateData(mImageList, mPosition)
                        setImage()
                    }
                }


                //Send broadcast
                LocalBroadcastManager.getInstance(requireContext())
                    .sendBroadcast(
                        Intent(INTENT_IMAGE).putExtra(
                            BUNDLE_IMAGE_LIST, mImageList
                        )
                    )
            } catch (e: Exception) {

            }

        }
    }

    private val mUpdateProfileBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, p1: Intent?) {
            // Get data
            try {
                if (!isBeforeImage) {
                    val afterImage = p1!!.getStringExtra(INTENT_AFTER_IMAGE)
                    mImageList[mPosition].afterImage = afterImage!!
                    mImageList[mPosition].afterImageName = "After${mPosition + 1}"
                    mImagesAdapter.updateData(mImageList, mPosition)
                    ivAfterCameraAndEdit.setImageResource(R.drawable.ic_edit)
                    ivAfterUploadCancel.setImageResource(R.drawable.ic_cancel)
                    setImage()
                } else {
                    val beforeImage = p1!!.getStringExtra(INTENT_AFTER_IMAGE)
                    mImageList[mPosition].beforeImage = beforeImage!!
                    mImageList[mPosition].beforeImageName = "Before${mPosition + 1}"
                    mImagesAdapter.updateData(mImageList, mPosition)
                    ivAfterCameraAndEdit.setImageResource(R.drawable.ic_edit)
                    ivAfterUploadCancel.setImageResource(R.drawable.ic_cancel)
                    setImage()
                }

                //Send broadcast
                LocalBroadcastManager.getInstance(requireContext())
                    .sendBroadcast(
                        Intent(INTENT_IMAGE).putExtra(
                            BUNDLE_IMAGE_LIST, mImageList
                        )
                    )

            } catch (e: Exception) {

            }

        }
    }

}