package com.max360group.cammax360.views.fragments


import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.ThumbnailUtils.createVideoThumbnail
import android.os.*
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.anilokcun.uwmediapicker.UwMediaPicker
import com.anilokcun.uwmediapicker.model.UwMediaPickerMediaModel
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.JobMembers
import com.max360group.cammax360.repository.models.model.Permissions
import com.max360group.cammax360.repository.models.model.User
import com.max360group.cammax360.repository.models.model.VideosModel
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.utils.GeneralFunctions.MEDIA_TYPE_VEDIO
import com.max360group.cammax360.utils.MarshMallowPermissions
import com.max360group.cammax360.viewmodels.BaseViewModel
import com.max360group.cammax360.viewmodels.JobMediaViewModel
import com.max360group.cammax360.views.activities.BaseAppCompactActivity
import com.max360group.cammax360.views.activities.doFragmentTransaction
import com.max360group.cammax360.views.adapters.VideosAdapter
import com.max360group.cammax360.views.interfaces.VideoListener
import com.priyankvasa.android.cameraviewex.Image
import com.priyankvasa.android.cameraviewex.Modes
import com.priyankvasa.android.cameraviewex.VideoSize
import kotlinx.android.synthetic.main.fragment_video_recorder.*
import kotlinx.coroutines.*
import java.io.*
import kotlin.coroutines.CoroutineContext


class RecordVideoFragment : BaseFragment(), CoroutineScope,
    View.OnClickListener, VideoListener {

    companion object {
        const val BUNDLE_JOB_ID = "jobId"
        const val BUNDLE_LOCAL_ID = "localId"

        fun newInstance(mJobId: String, mLocalId: String): RecordVideoFragment {
            val mRecordVideoFragment = RecordVideoFragment()
            val bundle = Bundle()
            bundle.putString(BUNDLE_JOB_ID, mJobId)
            bundle.putString(BUNDLE_LOCAL_ID, mLocalId)
            mRecordVideoFragment.arguments = bundle

            return mRecordVideoFragment
        }
    }

    private var selectedVideos: List<String>? = null
    private var mJobId = ""
    private var mLocalId = ""
    private var mMembersList = mutableListOf<JobMembers>()
    private var mUser: ArrayList<User>? = ArrayList()
    private val job: Job = SupervisorJob()

    private val mVideosAdapter by lazy {
        VideosAdapter(this)
    }

    private val mJobMediaViewModel by lazy {
        ViewModelProvider(this).get(JobMediaViewModel::class.java)
    }

    private var mList = ArrayList<VideosModel>()

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    private lateinit var mMediaFile: File
    private val customHandler = Handler()
    private var startTime = 0L
    var timeInMilliseconds = 0L
    var timeSwapBuff = 0L
    var updatedTime = 0L

    override val layoutId: Int
        get() = R.layout.fragment_video_recorder

    override fun init(savedInstanceState: Bundle?) {
        //Get arguments
        mJobId = arguments?.getString(BUNDLE_JOB_ID).toString()
        mLocalId = arguments?.getString(BUNDLE_LOCAL_ID).toString()

        //Initialize local broadcast to refreshFragment
        LocalBroadcastManager.getInstance(activityContext)
            .registerReceiver(
                mUpdateProfileBroadcastReceiver,
                IntentFilter(VideoPreviewFragment.INTENT_VIDEO)
            )


        //Set click listener
        ivCapture.setOnClickListener(this)
        ivCancel.setOnClickListener(this)
        ivGallary.setOnClickListener(this)
        ivDone.setOnClickListener(this)

        // Check for permissions
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        setUpCamera()
        cameraView.start()

        //Set adapter
        rvVideoList.adapter = mVideosAdapter

        //Call api
        mJobMediaViewModel.getJobMembers(PhotoDetailsPermissions.PARAM_SUBKIND, mJobId)

    }

    private fun setUpCamera() {
        with(cameraView) {

            // Callback on main (UI) thread
            addCameraOpenedListener {
                // Check for camera view group visibility and set camera modes
                if (View.GONE == cameraView.visibility) {
                    // Enable visibility of CameraView
                    cameraView.visibility = View.VISIBLE
                    ivCapture.visibility = View.VISIBLE

                    // Set camera mode
                    // enable only video capture mode
                    cameraView.setCameraMode(Modes.CameraMode.VIDEO_CAPTURE)

                    cameraView.enableCameraMode(Modes.CameraMode.VIDEO_CAPTURE)


                }
            }
            // enable only video capture mode
            cameraView.setCameraMode(Modes.CameraMode.VIDEO_CAPTURE)

            cameraView.enableCameraMode(Modes.CameraMode.VIDEO_CAPTURE)
            // Callback on main (UI) thread
            addCameraErrorListener { t, errorLevel ->
                Log.e("error", "setUpCamera: $t  $errorLevel")
            }


            // Callback on main (UI) thread
            addPictureTakenListener { image: Image -> launch { saveImageDataToFile(image) } }

            // Callback on main (UI) thread
            addVideoRecordStoppedListener { isSuccess: Boolean ->
                ivCapture.isActivated = false
                if (isSuccess) {
                    // Update the gallery thumbnail with latest video recorded
                    val thumb = createVideoThumbnail(
                        mMediaFile.absolutePath,
                        MediaStore.Images.Thumbnails.MINI_KIND
                    )

                    mList.add(
                        VideosModel(
                            video = mMediaFile.absolutePath,
                            thumbail = GeneralFunctions.bitmapToFile(
                                thumb!!,
                                requireContext()
                            )!!.absolutePath,
                            videoName = "Video${mList.size + 1}"
                        )
                    )
                    mVideosAdapter.updateData(mList, -1)


                }
            }

            // Callback on main (UI) thread
            addCameraClosedListener { }
        }
    }


    private suspend fun saveImageDataToFile(image: Image): File {
        mMediaFile = GeneralFunctions.createFile(activityContext, MEDIA_TYPE_VEDIO)

        runCatching {
            withContext(Dispatchers.IO) {
                BufferedOutputStream(mMediaFile.outputStream()).use { it.write(image.data) }
            }
        }
            .onFailure {}
            .onSuccess {

            }

        return mMediaFile
    }


    override val viewModel: BaseViewModel?
        get() = mJobMediaViewModel

    override fun observeProperties() {
        mJobMediaViewModel.onGetMembers().observe(this, Observer {
            mMembersList.clear()
            mMembersList.addAll(it)

            //Remove my self from the list
            for (i in mMembersList.indices) {
                if (mMembersList[i].id == mUserPrefsManager.loginedUser!!.id) {
                    mMembersList.removeAt(i)
                    break
                }
            }

            for (i in mMembersList.indices) {
                mUser!!.add(
                    User(
                        userId = mMembersList[i].id,
                        primaryUserId = mMembersList[i].accounts[0].primaryUserId,
                        permissions = Permissions(
                            base = mMembersList[i].accounts[0].permissions.jobs!!.mediaPhotos,
                            comments = mMembersList[i].accounts[0].permissions.jobs!!.comments,
                            members = mMembersList[i].accounts[0].permissions.jobs!!.members
                        )
                    )
                )
            }
        })

        mJobMediaViewModel.onMediaUpdated().observe(this, androidx.lifecycle.Observer {
            //Send broadcast
            LocalBroadcastManager.getInstance(requireContext())
                .sendBroadcast(
                    Intent(JobDetailsFragment.BUNDLE_JOB_DETAIL_INTENT)
                )
            (requireContext() as BaseAppCompactActivity).onBackPressed()
        })
    }

    @SuppressLint("MissingPermission")
    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ivCapture -> {
                if (cameraView.isVideoRecording) {
                    cameraView.stopVideoRecording()

                    tvTimeCount.visibility = View.GONE
                    ivCancel.visibility = View.VISIBLE
                    ivDone.visibility = View.VISIBLE
                    ivCapture.setImageResource(R.drawable.ic_image_capture)
                } else {
                    //View
                    tvTimeCount.visibility = View.VISIBLE
                    ivCancel.visibility = View.GONE
                    ivDone.visibility = View.GONE
                    initCountDown()

                    ivCapture.setImageResource(R.drawable.ic_recording)

                    mMediaFile = GeneralFunctions.createFile(
                        activityContext, GeneralFunctions.MEDIA_TYPE_VEDIO
                    )
                    if (ActivityCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.CAMERA
                        ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.RECORD_AUDIO
                        ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return
                    }
                    cameraView.startVideoRecording(mMediaFile) {
                        videoFrameRate = 30
                        // maxDuration = 4000
                        videoStabilization = true
                        videoSize = VideoSize.Max4x3
                    }
                }
            }
            R.id.ivCancel -> {
                (requireContext() as BaseAppCompactActivity).onBackPressed()
            }
            R.id.ivDone -> {
                if (GeneralFunctions.isInternetConnected(requireContext())) {
                    mJobMediaViewModel.addJobVideo(mJobId, mList, mUser!!)
                } else {
                    mJobMediaViewModel.saveVideoInLocal(mJobId, mLocalId, mList, mUser)
                }

            }

            R.id.ivGallary -> {
                UwMediaPicker
                    .with(this)
                    .setGalleryMode(UwMediaPicker.GalleryMode.VideoGallery)
                    .setGridColumnCount(2)
                    .setMaxSelectableMediaCount(5)
                    .setLightStatusBar(true)
                    .enableImageCompression(true)
                    .setCompressionMaxWidth(1280F)
                    .setCompressionMaxHeight(720F)
                    .setCompressFormat(Bitmap.CompressFormat.JPEG)
                    .setCompressionQuality(50)
                    .setCompressedFileDestinationPath(GeneralFunctions.createFile(activityContext, MEDIA_TYPE_VEDIO).absolutePath)
                    .setCancelCallback {  }
                    .launch(::onMediaSelected)
            }
        }
    }

    private fun onMediaSelected(selectedMediaList: List<UwMediaPickerMediaModel>?) {
        if (selectedMediaList != null) {
             selectedVideos = selectedMediaList.map { it.mediaPath }
            if (selectedVideos!!.size > 5) {
                showMessage(null, getString(R.string.st_video_limit), true)
            } else {
                ivDone.visibility = View.VISIBLE
                for (i in selectedVideos!!.indices) {
                    //Create video thumbnail
                    val thumb = createVideoThumbnail(
                        selectedVideos!![i],
                        MediaStore.Images.Thumbnails.MINI_KIND
                    )

                    mList.add(
                        VideosModel(
                            video = selectedVideos!![i],
                            thumbail = GeneralFunctions.bitmapToFile(
                                thumb!!,
                                requireContext()
                            )!!.absolutePath,
                            videoName = "Video${mList!!.size + 1}"
                        )
                    )
                }
                mVideosAdapter.updateData(mList, -1)
            }
        } else {
            Toast.makeText(requireContext(), "Unexpected Error", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onResume() {
        super.onResume()
        checkForRecordAudio()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTimer()
    }

    private fun checkForPermissions(isRequestPermissions: Boolean = true) {
        mMarshMallowPermissions.checkPermissionsForCameraCapture
            .let {
                if (it.isEmpty()) {
                    if (ActivityCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.CAMERA
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return
                    }
                    cameraView.start()
                } else {
                    if (isRequestPermissions) {
                        mMarshMallowPermissions.reqPermissionsForCameraCapture()
                    } else {
                        showMessage(
                            R.string.enable_camera_capture_permission, null,
                            false
                        )
                    }
                }
            }
    }

    private fun initCountDown() {
        startTime = SystemClock.uptimeMillis()
        customHandler.postDelayed(updateTimerThread, 0)
    }

    private val updateTimerThread: Runnable = object : Runnable {
        override fun run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime
            updatedTime = timeSwapBuff + timeInMilliseconds
            var secs = (updatedTime / 1000).toInt()
            var mins = secs / 60
            secs %= 60
            val milliseconds = (updatedTime % 1000).toInt()
            tvTimeCount.text = (java.lang.String.format("%02d", mins) + ":"
                    + java.lang.String.format("%02d", secs))
            customHandler.postDelayed(this, 0)
        }
    }

    private fun stopTimer() {
        timeSwapBuff += timeInMilliseconds
        customHandler.removeCallbacks(updateTimerThread)
    }

    private fun checkForRecordAudio(isRequestPermissions: Boolean = true) {
        if (mMarshMallowPermissions.isRecordAudioPermission) {
            checkForPermissions()
        } else {
            mMarshMallowPermissions.requestPermissionForAudio()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            MarshMallowPermissions.RQ_CAMERA_CAPTURE_PERMISSION ->
                checkForPermissions(false)
        }
    }


    override fun onPause() {
        cameraView.stop()
        super.onPause()
    }

    override fun onDestroyView() {
        cameraView.destroy()
        super.onDestroyView()
    }

    override fun onVideoClick(position: Int) {
        (activityContext as BaseAppCompactActivity).doFragmentTransaction(
            fragment = VideoPreviewFragment.newInstance(position, mList, mJobId, mLocalId, mUser),
            containerViewId = R.id.flFragContainerMain,
            enterAnimation = R.animator.slide_right_in,
            popExitAnimation = R.animator.slide_right_out
        )
        stopTimer()
    }

    override fun onDeleteImage(position: Int) {
        mList.removeAt(position)
        mVideosAdapter.updateData(mList, -1)
        if (mList.isEmpty()) {
            ivDone.visibility = View.GONE
        }
    }

    private val mUpdateProfileBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, p1: Intent?) {
            // Get data
            try {
                mList = p1!!.getParcelableArrayListExtra<VideosModel>(
                    VideoPreviewFragment.INTENT_VIDEO_LIST
                ) as ArrayList<VideosModel>
                mVideosAdapter.updateData(mList, -1)

            } catch (e: Exception) {

            }

        }
    }

}