package com.max360group.cammax360.views.dialgofragments.CameraDialogFragment

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.hardware.display.DisplayManager
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.KeyEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.datatransport.cct.internal.LogEvent
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.JobMembers
import com.max360group.cammax360.repository.models.model.*
import com.max360group.cammax360.utils.Constants
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.utils.MarshMallowPermissions
import com.max360group.cammax360.viewmodels.BaseViewModel
import com.max360group.cammax360.viewmodels.JobMediaViewModel
import com.max360group.cammax360.views.activities.BaseAppCompactActivity
import com.max360group.cammax360.views.activities.HomeActivity
import com.max360group.cammax360.views.activities.doFragmentTransaction
import com.max360group.cammax360.views.adapters.ImagesAdapter
import com.max360group.cammax360.views.dialgofragments.UploadTypeDialogFragment
import com.max360group.cammax360.views.dialgofragments.UploadTypeDialogFragment.Companion.INTENT_BEFORE_AFTER_PHOTO
import com.max360group.cammax360.views.dialgofragments.UploadTypeDialogFragment.Companion.INTENT_SIMPLE_PHOTO
import com.max360group.cammax360.views.fragments.*
import com.max360group.cammax360.views.fragments.BeforeAfterImageUpdateFragment.Companion.INTENT_AFTER_IMAGE
import com.max360group.cammax360.views.fragments.BeforeAfterImageUpdateFragment.Companion.INTENT_IMAGE
import com.max360group.cammax360.views.interfaces.PictureListener
import com.max360group.cammax360.views.utils.PixelGridView
import kotlinx.android.synthetic.main.dialog_fragment_camera.*
import kotlinx.android.synthetic.main.dialog_fragment_camera.ivDone
import kotlinx.android.synthetic.main.dialog_fragment_camera.rvGallary
import kotlinx.android.synthetic.main.fragment_before_after_update.*
import kotlinx.coroutines.*
import java.io.File
import java.lang.Runnable
import java.nio.ByteBuffer
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.collections.ArrayList
import kotlin.coroutines.CoroutineContext

typealias LumaListener = (luma: Double) -> Unit

class CameraDialogFragment : BasePictureOptionsFragment(), View.OnClickListener, PictureListener,
    CoroutineScope {

    private val mImagesAdapter by lazy {
        ImagesAdapter(this)
    }
    private val mJobMediaViewModel by lazy {
        ViewModelProvider(this).get(JobMediaViewModel::class.java)
    }


    companion object {
        const val REQUEST_CODE_CAMERA_CODES_DIALOG_FRAGMENT = 432
        private const val TAG = "CameraFragment"
        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0
        private const val KEY_EVENT_ACTION = "key_event_action"
        private const val KEY_EVENT_EXTRA = "key_event_extra"
        private const val BUNDLE_IMAGE_LIST = "imageList"
        private const val BUNDLE_FROM = "from"
        const val BUNDLE_SIMPLE = "simple"
        const val BUNDLE_AFTER = "after"
        const val BUNDLE_BEFORE_IMAGE = "beforeImage"
        const val BUNDLE_JOB_ID = "jobId"
        const val BUNDLE_LOCAL_ID = "localId"

        fun newInstance(
            from: String,
            image: String,
            id: String,
            localId: String
        ): CameraDialogFragment {
            val takePhotosFragment = CameraDialogFragment()
            val bundle = Bundle()
            bundle.putString(BUNDLE_FROM, from)
            bundle.putString(BUNDLE_BEFORE_IMAGE, image)
            bundle.putString(BUNDLE_JOB_ID, id)
            bundle.putString(BUNDLE_LOCAL_ID, localId)
            takePhotosFragment.arguments = bundle

            return takePhotosFragment
        }
    }

    private var job: Job = Job()
    var photoFile: File? = null
    var mFRom = ""
    var mJobId = ""
    var mLocalId = ""
    var isSimpleImage = false
    var beforeImage = ""
    var mJobMediaRequestModel: JobMediaRequestModel? = null
    private var mMembersList = mutableListOf<JobMembers>()
    private var mUser: ArrayList<User>? =
        ArrayList()

    var mImageList = ArrayList<BeforeAfterImageModel>()

    private val mDisplayManager by lazy {
        requireContext().getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
    }


    private val mCameraExecutor: ExecutorService by lazy {
        Executors.newSingleThreadExecutor()
    }

    private val mLocalBroadcastManager: LocalBroadcastManager by lazy {
        LocalBroadcastManager.getInstance(requireContext())
    }

    private var mDisplayId: Int = -1
    private var mLensFacing: Int = CameraSelector.LENS_FACING_BACK
    private var mPreview: Preview? = null
    private var mImageCapture: ImageCapture? = null
    private var mImageAnalyzer: ImageAnalysis? = null
    private var mCamera: Camera? = null
    private var mCameraProvider: ProcessCameraProvider? = null
    private var isSettingOpen = false
    private var isFlashOn = false
    private var isGridOn = false
    private var isBalanceView = false

    override val layoutId: Int
        get() = R.layout.dialog_fragment_camera

    override fun setData(savedInstanceState: Bundle?) {
        // Get arguments
        mFRom = arguments?.getString(BUNDLE_FROM).toString()
        beforeImage = arguments?.getString(BUNDLE_BEFORE_IMAGE).toString()
        mJobId = arguments?.getString(BUNDLE_JOB_ID).toString()
        mLocalId = arguments?.getString(BUNDLE_LOCAL_ID).toString()

        //Initialize local broadcast to refreshFragment
        LocalBroadcastManager.getInstance(activityContext)
            .registerReceiver(
                mUpdateProfileBroadcastReceiver,
                IntentFilter(INTENT_IMAGE)
            )

        //Initialize local broadcast to refreshFragment
        LocalBroadcastManager.getInstance(activityContext)
            .registerReceiver(
                mUpdateDialogReceiver,
                IntentFilter(UploadTypeDialogFragment.INTENT_PHOTO_DIALOG)
            )

        if (mFRom == BUNDLE_SIMPLE) {
            previewBeforeImage.visibility = View.GONE

        } else {
            previewBeforeImage.visibility = View.VISIBLE
            ivGallary.visibility = View.GONE
            ivCaptureBefore.visibility = View.GONE
            previewBeforeImage.setImageURI(GeneralFunctions.getLocalImageFile(File(beforeImage)))
        }

        // Check for permissions
        if (mMarshMallowPermissions.isCameraPermission) {
            startCamera()

        } else {
            mMarshMallowPermissions.reqPermissionsForCameraCapture(true)
        }

        //Set click listener
        ivCapture.setOnClickListener(this)
        ivCaptureBefore.setOnClickListener(this)
        ivDone.setOnClickListener(this)
        ivGallary.setOnClickListener(this)
        ivEffects.setOnClickListener(this)
        ivLocation.setOnClickListener(this)
        ivTime.setOnClickListener(this)
        ivFlash.setOnClickListener(this)
        ivBalance.setOnClickListener(this)
        ivGrid.setOnClickListener(this)

        val myView = PixelGridView(requireContext())
        myView.setNumColumns(3)
        myView.setNumRows(3)

        flGridView.addView(myView)

        //Set adapter
        rvGallary.adapter = mImagesAdapter

        //Call api
        mJobMediaViewModel.getJobMembers(PhotoDetailsPermissions.PARAM_SUBKIND, mJobId)

    }

    override val viewModel: BaseViewModel?
        get() = mJobMediaViewModel

    override fun observeProperties() {
        mJobMediaViewModel.onGetMembers().observe(this, androidx.lifecycle.Observer {
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


    private fun startCamera() {
        // Set up the intent filter that will receive events from our main activity
        val filter = IntentFilter().apply { addAction(KEY_EVENT_ACTION) }
        mLocalBroadcastManager.registerReceiver(volumeDownReceiver, filter)

        // Every time the orientation of device changes, update rotation for use cases
        mDisplayManager.registerDisplayListener(displayListener, null)

        // Wait for the views to be properly laid out
        viewFinder.post {

            // Keep track of the display in which this view is attached
            mDisplayId = viewFinder.display.displayId

            // Set up the camera and its use cases
            setUpCamera()
        }
    }

    private fun switchCamera() {
        // Disable the button until the camera is set up
        // Listener for button used to switch cameras. Only called if the button is enabled
        mLensFacing = if (CameraSelector.LENS_FACING_FRONT == mLensFacing) {
            CameraSelector.LENS_FACING_BACK
        } else {
            CameraSelector.LENS_FACING_FRONT
        }
        // Re-bind use cases to update selected camera
        bindCameraUseCases()
    }

    private fun takePhoto() {

        // Create output file to hold the image
        photoFile = GeneralFunctions.createFile(
            requireContext(), GeneralFunctions.MEDIA_TYPE_IMAGE
        )
        // Setup image capture metadata
        val metadata = ImageCapture.Metadata().apply {

            // Mirror image when using the front camera
            isReversedHorizontal = mLensFacing == CameraSelector.LENS_FACING_FRONT
        }

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile!!)
            .setMetadata(metadata)
            .build()

        // Setup image capture listener which is triggered after photo has been taken
        mImageCapture?.takePicture(
            outputOptions, mCameraExecutor, object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }


                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = output.savedUri ?: Uri.fromFile(photoFile)
                    Log.d(TAG, "Photo capture succeeded: $savedUri")

                    // Update the gallery thumbnail with latest picture taken

                    // Implicit broadcasts will be ignored for devices running API level >= 24
                    // so if you only target API level 24+ you can remove this statement
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                        requireActivity().sendBroadcast(
                            Intent(android.hardware.Camera.ACTION_NEW_PICTURE, savedUri)
                        )
                    }
                    GlobalScope.launch {
                        withContext(Dispatchers.Main) {
                            ivDone.visibility = View.VISIBLE
                            setUpCamera()

                            //Save Image
                            if (mFRom == BUNDLE_SIMPLE) {
                                if (isSimpleImage) {
                                    mImageList.add(
                                        BeforeAfterImageModel(
                                            simpleImage = photoFile!!.absolutePath,
                                            simpleImageName = "Simple${mImageList.size + 1}"
                                        )
                                    )
                                    mImagesAdapter.updateData(mImageList, -1)
                                } else {
                                    mImageList.add(
                                        BeforeAfterImageModel(
                                            beforeImage = photoFile!!.absolutePath,
                                            beforeImageName = "Before${mImageList.size + 1}",
                                            afterImageName = "After${mImageList.size + 1}"
                                        )
                                    )
                                    mImagesAdapter.updateData(mImageList, -1)
                                }
                            } else {
                                //Send broadcast
                                LocalBroadcastManager.getInstance(requireContext())
                                    .sendBroadcast(
                                        Intent(INTENT_IMAGE).putExtra(
                                            INTENT_AFTER_IMAGE, photoFile!!.absolutePath
                                        )
                                    )
                                (requireContext() as BaseAppCompactActivity).onBackPressed()
                            }
                        }

                    }

                    // If the folder selected is an external media directory, this is
                    // unnecessary but otherwise other apps will not be able to access our
                    // images unless we scan them using [MediaScannerConnection]
                    val mimeType = MimeTypeMap.getSingleton()
                        .getMimeTypeFromExtension(savedUri.toFile().extension)
                    MediaScannerConnection.scanFile(
                        context,
                        arrayOf(savedUri.toFile().absolutePath),
                        arrayOf(mimeType)
                    ) { _, uri ->
                        Log.d(
                            TAG,
                            "Image capture scanned into media store: $uri"
                        )
                    }

                }

            })

    }

    private val volumeDownReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.getIntExtra(
                KEY_EVENT_EXTRA,
                KeyEvent.KEYCODE_UNKNOWN
            )) {

                KeyEvent.KEYCODE_VOLUME_DOWN -> {
                    takePhoto()
                }
            }
        }
    }


    private val displayListener = object : DisplayManager.DisplayListener {
        override fun onDisplayAdded(displayId: Int) = Unit
        override fun onDisplayRemoved(displayId: Int) = Unit
        override fun onDisplayChanged(displayId: Int) = view?.let { view ->
            if (displayId == this@CameraDialogFragment.mDisplayId) {
                Log.d(TAG, "Rotation changed: ${view.display.rotation}")
                mImageCapture?.targetRotation = view.display.rotation
                mImageAnalyzer?.targetRotation = view.display.rotation
            }
        } ?: Unit
    }

    private fun setUpCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener(Runnable {

            // CameraProvider
            mCameraProvider = cameraProviderFuture.get()

            // Select lensFacing depending on the available cameras
            if (mFRom == BasePictureOptionsFragment.BUNDLE_TAKE_ID) {
                CameraSelector.LENS_FACING_BACK
            } else {
                CameraSelector.LENS_FACING_FRONT
            }
            /*mLensFacing = when {
                hasBackCamera() -> CameraSelector.LENS_FACING_BACK
                hasFrontCamera() -> CameraSelector.LENS_FACING_FRONT
                else -> throw IllegalStateException("Back and front camera are unavailable")
            }*/

            // Enable or disable switching between cameras

            // Build and bind the camera use cases
            bindCameraUseCases()
        }, ContextCompat.getMainExecutor(requireContext()))

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun pinchToZoom() {
        // Listen to pinch gestures
        val listener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                // Get the camera's current zoom ratio
                val currentZoomRatio = mCamera!!.cameraInfo.zoomState.value?.zoomRatio ?: 0F

                // Get the pinch gesture's scaling factor
                val delta = detector.scaleFactor

                // Update the camera's zoom ratio. This is an asynchronous operation that returns
                // a ListenableFuture, allowing you to listen to when the operation completes.
                mCamera!!.cameraControl.setZoomRatio(currentZoomRatio * delta)

                // Return true, as the event was handled
                return true
            }
        }
        val scaleGestureDetector = ScaleGestureDetector(context, listener)

// Attach the pinch gesture listener to the viewfinder
        viewFinder.setOnTouchListener { _, event ->
            scaleGestureDetector.onTouchEvent(event)
            return@setOnTouchListener true
        }
    }


    private fun bindCameraUseCases() {
        // Get screen metrics used to setup camera for full screen resolution
        val metrics = DisplayMetrics().also { viewFinder.display.getRealMetrics(it) }
        Log.d(
            TAG,
            "Screen metrics: ${metrics.widthPixels} x ${metrics.heightPixels}"
        )

        val screenAspectRatio = aspectRatio(metrics.widthPixels, metrics.heightPixels)
        Log.d(TAG, "Preview aspect ratio: $screenAspectRatio")

        val rotation = viewFinder.display.rotation

        // CameraProvider
        val cameraProvider = mCameraProvider
            ?: throw IllegalStateException("Camera initialization failed.")

        // CameraSelector
        val cameraSelector = CameraSelector.Builder().requireLensFacing(mLensFacing).build()

        // Preview
        mPreview = Preview.Builder()
            // We request aspect ratio but no resolution
            .setTargetAspectRatio(screenAspectRatio)
            // Set initial target rotation
            .setTargetRotation(rotation)
            .build()

        // ImageCapture
        mImageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            // We request aspect ratio but no resolution to match preview config, but letting
            // CameraX optimize for whatever specific resolution best fits our use cases
            .setTargetAspectRatio(screenAspectRatio)
            // Set initial target rotation, we will have to call this again if rotation changes
            // during the lifecycle of this use case
            .setTargetRotation(rotation)
            .build()

        // ImageAnalysis
        mImageAnalyzer = ImageAnalysis.Builder()
            // We request aspect ratio but no resolution
            .setTargetAspectRatio(screenAspectRatio)
            // Set initial target rotation, we will have to call this again if rotation changes
            // during the lifecycle of this use case
            .setTargetRotation(rotation)
            .build()
            // The analyzer can then be assigned to the instance
            .also {
                it.setAnalyzer(mCameraExecutor, LuminosityAnalyzer { luma ->
                    // Values returned from our analyzer are passed to the attached listener
                    // We log image analysis results here - you should do something useful
                    // instead!
//                    Log.d(TAG, "Average luminosity: $luma")
                })
            }

        // Must unbind the use-cases before rebinding them
        cameraProvider.unbindAll()

        try {
            // A variable number of use-cases can be passed here -
            // camera provides access to CameraControl & CameraInfo
            mCamera = cameraProvider.bindToLifecycle(
                this, cameraSelector, mPreview, mImageCapture, mImageAnalyzer
            )

            // Attach the viewfinder's surface provider to preview use case
            mPreview?.setSurfaceProvider(viewFinder.createSurfaceProvider(mCamera?.cameraInfo))

            //Zoom state
            pinchToZoom()
        } catch (exc: Exception) {
            Log.e(TAG, "Use case binding failed", exc)
        }
    }

    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = Math.max(width, height).toDouble() / Math.min(width, height)
        if (Math.abs(previewRatio - RATIO_4_3_VALUE) <= Math.abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }

    private fun hasBackCamera(): Boolean {
        return mCameraProvider?.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA) ?: false
    }

    private fun hasFrontCamera(): Boolean {
        return mCameraProvider?.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA) ?: false
    }


    private class LuminosityAnalyzer(listener: LumaListener? = null) : ImageAnalysis.Analyzer {
        private val frameRateWindow = 8
        private val frameTimestamps = ArrayDeque<Long>(5)
        private val listeners = ArrayList<LumaListener>().apply { listener?.let { add(it) } }
        private var lastAnalyzedTimestamp = 0L
        var framesPerSecond: Double = -1.0
            private set


        fun onFrameAnalyzed(listener: LumaListener) = listeners.add(listener)


        private fun ByteBuffer.toByteArray(): ByteArray {
            rewind()    // Rewind the buffer to zero
            val data = ByteArray(remaining())
            get(data)   // Copy the buffer into a byte array
            return data // Return the byte array
        }

        override fun analyze(image: ImageProxy) {
            // If there are no listeners attached, we don't need to perform analysis
            if (listeners.isEmpty()) {
                image.close()
                return
            }

            // Keep track of frames analyzed
            val currentTime = System.currentTimeMillis()
            frameTimestamps.push(currentTime)

            // Compute the FPS using a moving average
            while (frameTimestamps.size >= frameRateWindow) frameTimestamps.removeLast()
            val timestampFirst = frameTimestamps.peekFirst() ?: currentTime
            val timestampLast = frameTimestamps.peekLast() ?: currentTime
            framesPerSecond = 1.0 / ((timestampFirst - timestampLast) /
                    frameTimestamps.size.coerceAtLeast(1).toDouble()) * 1000.0

            // Analysis could take an arbitrarily long amount of time
            // Since we are running in a different thread, it won't stall other use cases

            lastAnalyzedTimestamp = frameTimestamps.first

            // Since format in ImageAnalysis is YUV, image.planes[0] contains the luminance plane
            val buffer = image.planes[0].buffer

            // Extract image data from callback object
            val data = buffer.toByteArray()

            // Convert the data into an array of pixel values ranging 0-255
            val pixels = data.map { it.toInt() and 0xFF }

            // Compute average luminance for the image
            val luma = pixels.average()

            // Call all listeners with new value
            listeners.forEach { it(luma) }

            image.close()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            MarshMallowPermissions.RQ_CAMERA_CAPTURE_PERMISSION ->
                if (PackageManager.PERMISSION_GRANTED == grantResults[0]) {
                    startCamera()
                } else {
                    Toast.makeText(
                        requireContext(), R.string.androidx_camera_default_config_provider,
                        Toast.LENGTH_LONG
                    ).show()
                    false

                }
        }
    }

    override fun onGettingImageFile(file: File) {
        if (isSimpleImage) {
            mImageList.add(
                BeforeAfterImageModel(
                    simpleImage = file.absolutePath
                )
            )
            mImagesAdapter.updateData(mImageList, -1)
        } else {
            mImageList.add(
                BeforeAfterImageModel(
                    beforeImage = file.absolutePath
                )
            )
            mImagesAdapter.updateData(mImageList, -1)
        }
    }

    override fun onGettingMultipleImages(list: List<String>) {
        if (list.size > 5) {
            showMessage(
                null,
                getString(R.string.st_image_selection_limit),
                true
            )
        } else {
            ivDone.visibility = View.VISIBLE
            if (isSimpleImage) {
                for (i in list.indices) {
                    mImageList.add(
                        BeforeAfterImageModel(
                            simpleImage = list[i],
                            simpleImageName = "Simple${mImageList.size + 1}"
                        )
                    )

                }
                mImagesAdapter.updateData(mImageList, -1)
            } else {
                for (i in list.indices) {
                    mImageList.add(
                        BeforeAfterImageModel(
                            beforeImage = list[i],
                            beforeImageName = "Before${mImageList.size + 1}",
                            afterImageName = "After${mImageList.size + 1}"
                        )
                    )
                }
                mImagesAdapter.updateData(mImageList, -1)
            }
        }
    }

    override fun onStartCameraDialogFragment(isOpen: Boolean) {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Shut down our background executor
        mCameraExecutor.shutdown()

        // Unregister the broadcast receivers and listeners
        mLocalBroadcastManager.unregisterReceiver(volumeDownReceiver)
        mDisplayManager.unregisterDisplayListener(displayListener)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ivCapture -> {
                isSimpleImage = true
                takePhoto()
            }
            R.id.ivCaptureBefore -> {
                isSimpleImage = false
                takePhoto()
            }

            R.id.ivLocation -> {

            }
            R.id.ivTime -> {

            }
            R.id.ivFlash -> {
                if (isFlashOn) {
                    isFlashOn = false
                    mCamera!!.cameraControl.enableTorch(false)
                } else {
                    isFlashOn = true
                    mCamera!!.cameraControl.enableTorch(true)
                }


            }
            R.id.ivBalance -> {
                if (isBalanceView) {
                    isBalanceView = false
                    clBalanceView.visibility = View.GONE
                } else {
                    isBalanceView = true
                    clBalanceView.visibility = View.VISIBLE
                }

            }
            R.id.ivEffects -> {
                if (isSettingOpen) {
                    isSettingOpen = false
                    clEffectsView.visibility = View.GONE
                } else {
                    isSettingOpen = true
                    clEffectsView.visibility = View.VISIBLE
                }

            }
            R.id.ivGrid -> {
                if (isGridOn) {
                    isGridOn = false
                    flGridView.visibility = View.GONE
                } else {
                    isGridOn = true
                    flGridView.visibility = View.VISIBLE
                }

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

                if (GeneralFunctions.isInternetConnected(requireContext())) {
                    mJobMediaViewModel.addJobMedia(mJobId, mImageList, mUser!!)
                } else {
                    mJobMediaViewModel.saveJobMediaLocal(mJobId, mLocalId, mImageList, mUser)
                }
            }

            R.id.ivGallary -> {
                UploadTypeDialogFragment().show(childFragmentManager, "")


            }
        }
    }

    override fun onImageClick(position: Int) {
        (activityContext as BaseAppCompactActivity).doFragmentTransaction(
            fragment = BeforeAfterImageUpdateFragment.newInstance(
                position,
                mImageList,
                mJobId,
                mLocalId,
                mUser
            ),
            containerViewId = R.id.flFragContainerMain,
            enterAnimation = R.animator.slide_right_in,
            popExitAnimation = R.animator.slide_right_out
        )
    }

    override fun onDeleteImage(position: Int) {
        mImageList.removeAt(position)
        mImagesAdapter.updateData(mImageList, -1)
    }

    private val mUpdateProfileBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, p1: Intent?) {
            // Get data
            try {
                mImageList = p1!!.getParcelableArrayListExtra<BeforeAfterImageModel>(
                    BeforeAfterImageUpdateFragment.BUNDLE_IMAGE_LIST
                ) as ArrayList<BeforeAfterImageModel>
                mImagesAdapter.updateData(mImageList, -1)

            } catch (e: Exception) {

            }
        }
    }

    private val mUpdateDialogReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, p1: Intent?) {
            // Get data
            try {
                when (p1!!.getStringExtra(UploadTypeDialogFragment.INTENT_PHOTO_TYPE)) {
                    INTENT_SIMPLE_PHOTO -> {
                        isSimpleImage = true
                        showGallaryImage(
                            Constants.LOCAL_STORAGE_BASE_PATH_FOR_USER_PHOTOS,
                            true
                        )
                    }

                    INTENT_BEFORE_AFTER_PHOTO -> {
                        isSimpleImage = false
                        showGallaryImage(Constants.LOCAL_STORAGE_BASE_PATH_FOR_USER_PHOTOS, true)
                    }
                }

            } catch (e: Exception) {

            }

        }
    }
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

}