package com.max360group.cammax360.views.fragments

import android.app.DownloadManager
import android.app.ProgressDialog
import android.content.*
import android.content.Context.DOWNLOAD_SERVICE
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.exoplayer2.offline.DownloadService
import com.max360group.cammax360.R
import com.max360group.cammax360.editorclasses.EditImageActivity
import com.max360group.cammax360.repository.models.JobMediaList
import com.max360group.cammax360.utils.Constants
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.viewmodels.BaseViewModel
import com.max360group.cammax360.viewmodels.MediaDetailViewModel
import com.max360group.cammax360.views.activities.BaseAppCompactActivity
import com.max360group.cammax360.views.activities.doFragmentTransaction
import com.max360group.cammax360.views.calender.MonthlyActivity
import com.max360group.cammax360.views.dialgofragments.CameraDialogFragment.CameraDialogFragment
import com.max360group.cammax360.views.utils.JobsConstants
import kotlinx.android.synthetic.main.fragment_before_after_update.*
import kotlinx.android.synthetic.main.fragment_edit_photo.*
import kotlinx.android.synthetic.main.fragment_edit_photo.ivDone
import kotlinx.android.synthetic.main.fragment_edit_photo.sdvImage
import kotlinx.android.synthetic.main.fragment_photo_detail.*
import kotlinx.android.synthetic.main.load_gallary_layout.view.*
import kotlinx.android.synthetic.main.media_tags_dialog.*
import kotlinx.android.synthetic.main.toolbar.*
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.concurrent.Executors


class EditPhotoFragment : BasePictureOptionsFragment(), View.OnClickListener {

    companion object {
        const val INTENT_PHOTO_DETAIL = "photoDetail"
        const val INTENT_PHOTO_TYPE = "photoType"//

        fun newInstance(mJobMediaList: JobMediaList, mType: Int): EditPhotoFragment {
            val mEditPhotoFragment = EditPhotoFragment()
            val bundle = Bundle()
            bundle.putParcelable(INTENT_PHOTO_DETAIL, mJobMediaList)
            bundle.putInt(INTENT_PHOTO_TYPE, mType)
            mEditPhotoFragment.arguments = bundle

            return mEditPhotoFragment
        }
    }

    private val mMediaDetailViewModel by lazy {
        ViewModelProvider(this).get(MediaDetailViewModel::class.java)
    }

    lateinit var mJobMediaList: JobMediaList
    private var mMediaFile = ""
    private var mPhotoType = 0

    override val layoutId: Int
        get() = R.layout.fragment_edit_photo

    override fun setData(savedInstanceState: Bundle?) {
        // Set toolbar
        toolbar.navigationIcon =
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_back_primary)
        ivToolbarUserImage.setImageURI(mUserPrefsManager.loginedUser!!.picURL)

        //Get arguments
        mJobMediaList = arguments?.getParcelable<JobMediaList>(INTENT_PHOTO_DETAIL)!!
        mPhotoType = arguments?.getInt(INTENT_PHOTO_TYPE)!!

        //Initialize local broadcast to refreshFragment
        LocalBroadcastManager.getInstance(activityContext)
            .registerReceiver(
                mUpdateProfileBroadcastReceiver,
                IntentFilter(BeforeAfterImageUpdateFragment.INTENT_IMAGE)
            )

        //Initialize local broadcast to refreshFragment
        LocalBroadcastManager.getInstance(activityContext)
            .registerReceiver(
                mFilterBroadcastReceiver,
                IntentFilter(BeforeAfterImageUpdateFragment.INTENT_FILTER_INTENT)
            )


        //Set image
        if (mJobMediaList.medias!![mPhotoType].mediaURL.startsWith("https://")) {
            val imageName =
                mJobMediaList.medias!![mPhotoType].media.split("${JobsConstants.JOB_KIND_PHOTO}/")

            // Check if local file exists, if it exists set file from local else downloads from server
            val file = GeneralFunctions
                .getLocalMediaFile(requireActivity(), imageName[1])
            if (file.exists()) {
                sdvImage.setImageURI(
                    GeneralFunctions.getLocalImageFile(file)
                )
            } else {
                sdvImage.setImageURI(mJobMediaList.medias!![mPhotoType].mediaURL)
            }
        } else {
            sdvImage.setImageURI(GeneralFunctions.getLocalImageFile(File(mJobMediaList.medias!![mPhotoType].mediaURL)))
        }


        //Set on click listener
        ivUpload.setOnClickListener(this)
        fileOpenCamera.setOnClickListener(this)
        ivDone.setOnClickListener(this)
        ivEffects.setOnClickListener(this)
        ivToolbarUserImage.setOnClickListener(this)
        ivToolbarRightIcon.setOnClickListener(this)
        ivToolbarRightIconBell.setOnClickListener(this)

    }

    override val viewModel: BaseViewModel?
        get() = mMediaDetailViewModel

    override fun observeProperties() {
        mMediaDetailViewModel.onSuccess().observe(this, Observer {
            //Send broadcast to update photo detail
            LocalBroadcastManager.getInstance(requireContext())
                .sendBroadcast(
                    Intent(PhotoDetailFragment.INTENT_PHOTO_DETAIL)
                )

            //Send broadcast to update photo detail
            LocalBroadcastManager.getInstance(requireContext())
                .sendBroadcast(
                    Intent(JobDetailsFragment.BUNDLE_JOB_DETAIL_INTENT)
                )
        })
    }

    override fun onGettingImageFile(file: File) {
        mMediaFile = file.absolutePath
        sdvImage.setImageURI(GeneralFunctions.getLocalImageFile(file))

    }

    override fun onGettingMultipleImages(list: List<String>) {

    }

    override fun onStartCameraDialogFragment(isOpen: Boolean) {

    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ivUpload -> {
                // Get picture
                showGallaryImage(
                    Constants.LOCAL_STORAGE_BASE_PATH_FOR_USER_PHOTOS
                )

            }
            R.id.ivDone -> {
                if (GeneralFunctions.isInternetConnected(requireContext())) {
                    mMediaDetailViewModel.updateMediaInfo(
                        id = mJobMediaList.medias!![mPhotoType].id,
                        kind = mJobMediaList.kind,
                        name = mJobMediaList.medias!![mPhotoType].name,
                        mTagsList = mJobMediaList.medias!![mPhotoType].tags!!,
                        mMedia = mJobMediaList.medias!![mPhotoType].media,
                        mEditMediaFil = mMediaFile,
                        mJobId = mJobMediaList.jobId
                    )
                } else {
                    mMediaDetailViewModel.updateMediaInfoInLocal(
                        id = mJobMediaList.mediaLocalId.toString(),
                        kind = mJobMediaList.kind,
                        name = mJobMediaList.medias!![mPhotoType].name,
                        mTagsList = mJobMediaList.medias!![mPhotoType].tags!!,
                        media = mMediaFile,
                        mediaUrl = mMediaFile,
                        mPhotoType,
                        thumbnail = mJobMediaList.medias!![mPhotoType].thumbnailURL,
                        true
                    )
                }

            }
            R.id.fileOpenCamera -> {
                (activityContext as BaseAppCompactActivity).doFragmentTransaction(
                    fragment = CameraDialogFragment.newInstance(
                        "",
                        "",
                        mJobMediaList.jobId, ""
                    ),
                    containerViewId = R.id.flFragContainerMain,
                    enterAnimation = R.animator.slide_right_in,
                    popExitAnimation = R.animator.slide_right_out
                )
            }
            R.id.ivEffects -> {
                if (mMediaFile.isNotBlank()) {
                    val intent = Intent(requireContext(), EditImageActivity::class.java)
                    intent.putExtra("IMAGE", mMediaFile)
                    startActivity(intent)
                } else {
                    if (mJobMediaList.medias!![mPhotoType].mediaURL.startsWith("https://")) {
                        val imageName =
                            mJobMediaList.medias!![mPhotoType].media.split("${JobsConstants.JOB_KIND_PHOTO}/")

                        // Check if local file exists, if it exists set file from local else downloads from server
                        val file = GeneralFunctions
                            .getLocalMediaFile(requireActivity(), imageName[1])
                        if (file.exists()) {
                            val intent = Intent(requireContext(), EditImageActivity::class.java)
                            intent.putExtra("IMAGE", GeneralFunctions.getLocalImageFile(file))
                            startActivity(intent)

                        } else {
                            downloadImageNew(mJobMediaList.medias!![mPhotoType].mediaURL)
                        }
                    } else {
                        val intent = Intent(requireContext(), EditImageActivity::class.java)
                        intent.putExtra(
                            "IMAGE",
                            GeneralFunctions.getLocalImageFile(File(mJobMediaList.medias!![mPhotoType].mediaURL))
                        )
                        startActivity(intent)
                    }
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


    private fun downloadImageNew(mWebPath: String) {
        pbProgressView.visibility = View.VISIBLE
        // Declaring a Bitmap local
        var mImage: Bitmap?
        // Declaring and initializing an Executor and a Handler
        val myExecutor = Executors.newSingleThreadExecutor()
        val myHandler = Handler(Looper.getMainLooper())

        myExecutor.execute {
            mImage = mLoad(mWebPath)
            myHandler.post {
                if (mImage != null) {
                    mSaveMediaToStorage(mImage)
                }
            }
        }
    }

    // Function to establish connection and load image
    private fun mLoad(string: String): Bitmap? {
        val url: URL = mStringToURL(string)!!
        val connection: HttpURLConnection?
        try {
            connection = url.openConnection() as HttpURLConnection
            connection.connect()
            val inputStream: InputStream = connection.inputStream
            val bufferedInputStream = BufferedInputStream(inputStream)
            return BitmapFactory.decodeStream(bufferedInputStream)
        } catch (e: IOException) {
            pbProgressView.visibility = View.GONE
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error", Toast.LENGTH_LONG).show()
        }
        pbProgressView.visibility = View.GONE
        return null
    }

    // Function to convert string to URL
    private fun mStringToURL(string: String): URL? {
        try {
            return URL(string)
        } catch (e: MalformedURLException) {
            pbProgressView.visibility = View.GONE
            e.printStackTrace()
        }
        pbProgressView.visibility = View.GONE
        return null
    }

    private fun mSaveMediaToStorage(bitmap: Bitmap?) {
        var imageUri: Uri? = null
        val filename = "${System.currentTimeMillis()}.jpg"
        var fos: OutputStream? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            requireContext().contentResolver?.also { resolver ->
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                imageUri =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            val imagesDir = GeneralFunctions.getOutputDirectory(activityContext).absolutePath
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }
        fos?.use {
            pbProgressView.visibility = View.GONE
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 30, it)
            //Call editor
            val intent = Intent(requireContext(), EditImageActivity::class.java)
            intent.putExtra(
                "IMAGE",
                GeneralFunctions.getRealPathFromURI(requireContext(), imageUri)
            )
            startActivity(intent)
        }
    }

    private val mFilterBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, p1: Intent?) {
            // Get data
            try {
                val mImage = p1?.getStringExtra(BeforeAfterImageUpdateFragment.INTENT_AFTER_IMAGE)
                mMediaFile = mImage!!
                sdvImage.setImageURI(GeneralFunctions.getLocalImageFile(File(mImage)))
            } catch (e: Exception) {

            }
        }
    }


    private val mUpdateProfileBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, p1: Intent?) {
            // Get data
            try {
                val mImage =
                    p1!!.getStringExtra(BeforeAfterImageUpdateFragment.INTENT_AFTER_IMAGE)
                mMediaFile = mImage!!
                sdvImage.setImageURI(GeneralFunctions.getLocalImageFile(File(mImage)))

            } catch (e: Exception) {

            }

        }
    }

}