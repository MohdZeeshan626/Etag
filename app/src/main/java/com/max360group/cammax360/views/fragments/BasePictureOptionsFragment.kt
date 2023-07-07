package com.max360group.cammax360.views.fragments

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import com.anilokcun.uwmediapicker.UwMediaPicker
import com.anilokcun.uwmediapicker.model.UwMediaPickerMediaModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.max360group.cammax360.BuildConfig
import com.max360group.cammax360.R
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.utils.GetSampledImage
import com.max360group.cammax360.utils.MarshMallowPermissions
import com.max360group.cammax360.utils.MultipleMediaSelection
import com.max360group.cammax360.views.activities.inflate
import kotlinx.android.synthetic.main.fragment_video_recorder.*
import kotlinx.android.synthetic.main.show_picture_options_bottom_sheet.view.*
import java.io.File


/**
 * Created by Mukesh on 17/5/18.
 */
abstract class BasePictureOptionsFragment : BaseFragment(), GetSampledImage.SampledImageAsyncResp {
    companion object {
        private const val REQUEST_CODE_GALLERY_PHOTOS = 192
        private const val REQUEST_CODE_TAKE_PHOTO = 281
        private const val REQUEST_CODE_TAKE_MULTIPLE_PHOTO = 193
        const val INTENT_EXTRAS_IMAGE = "Camera_Image"
        const val BUNDLE_TAKE_ID = "FROM"
        const val BUNDLE_EXTRA_OPEN_CAMERA = 13456
    }

    private var picturePath: String? = null
    private var imagesDirectory: String? = null
    private var isCameraOptionSelected: Boolean = false
    private var isMultipleImage = false
    private var selectedImages = ArrayList<String>()


    override fun init(savedInstanceState: Bundle?) {
        setData(savedInstanceState)
    }

    fun showPictureOptionsBottomSheet(
        imagesDirectory: String,
        showRecentUploadsOption: Boolean = false
    ) {
        val bottomSheetDialog = BottomSheetDialog(requireActivity())
        val view = (view as ViewGroup).inflate(R.layout.show_picture_options_bottom_sheet)

        view.tvCamera.setOnClickListener {
            checkForPermissions(true, imagesDirectory)
            bottomSheetDialog.dismiss()
        }
        view.tvGallery.setOnClickListener {
            checkForPermissions(false, imagesDirectory)
            bottomSheetDialog.dismiss()
        }

        view.tvCancel.setOnClickListener { bottomSheetDialog.dismiss() }

        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
    }


    fun showGallaryImage(
        imagesDirectory: String,
        isMultipleSelection: Boolean = false
    ) {
        isMultipleImage = isMultipleSelection
        checkForPermissions(false, imagesDirectory, isMultipleSelection)
    }

    private fun checkForPermissions(
        isCameraOptionSelected: Boolean,
        imagesDirectory: String,
        isMultipleSelection: Boolean = false
    ) {
        this.isCameraOptionSelected = isCameraOptionSelected
        this.imagesDirectory = imagesDirectory

        if (mMarshMallowPermissions.isPermissionGrantedForWriteExtStorage) {
            if (isCameraOptionSelected) {
                startCameraIntent()
            } else {
                if (isMultipleSelection) {
                    multiplePhotoSelection()
                } else {
                    openGallery()
                }
            }
        } else {
            mMarshMallowPermissions.requestPermissionForWriteExtStorage()
        }
    }

    private fun openGallery() {
        startActivityForResult(
            Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            ), REQUEST_CODE_GALLERY_PHOTOS
        )
    }

    private fun multiplePhotoSelection() {
        UwMediaPicker
            .with(this)
            .setGalleryMode(UwMediaPicker.GalleryMode.ImageGallery)
            .setGridColumnCount(2)
            .setMaxSelectableMediaCount(5)
            .setLightStatusBar(true)
            .enableImageCompression(true)
            .setCompressionMaxWidth(1280F)
            .setCompressionMaxHeight(720F)
            .setCompressFormat(Bitmap.CompressFormat.JPEG)
            .setCompressionQuality(85)
            .setCompressedFileDestinationPath(
                GeneralFunctions.getOutputDirectory(activityContext).absolutePath
            )
            .setCancelCallback { }
            .launch(::onMediaSelected)
    }

    private fun startCameraIntent() {
        if (mMarshMallowPermissions.isCameraPermission) {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            try {
                val file = GeneralFunctions.setUpImageFile(imagesDirectory!!)
                picturePath = file!!.absolutePath

                val outputUri = FileProvider
                    .getUriForFile(
                        requireActivity(),
                        BuildConfig.APPLICATION_ID + ".provider",
                        file
                    )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri)
                takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

            } catch (e: Exception) {
                e.printStackTrace()
                picturePath = null
            }
            startActivityForResult(takePictureIntent, REQUEST_CODE_TAKE_PHOTO)

        } else {
            mMarshMallowPermissions.giveCameraPermission()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            MarshMallowPermissions.RQ_WRITE_EXTERNAL_STORAGE ->
                if (PackageManager.PERMISSION_GRANTED == grantResults[0]) {
                    if (isCameraOptionSelected) {
                        startCameraIntent()
                    } else {
                        openGallery()
                    }
                } else {
                    showMessage(
                        R.string.enable_storage_permission, null,
                        false
                    )
                }
            MarshMallowPermissions.RQ_CAMERA_CAPTURE_PERMISSION ->
                if (PackageManager.PERMISSION_GRANTED == grantResults[0]) {
                    if (isCameraOptionSelected) {
                        startCameraIntent()
                    } else {
                        openGallery()
                    }
                } else {
                    showMessage(
                        R.string.enable_storage_permission, null,
                        false
                    )
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (Activity.RESULT_OK == resultCode) {
            when (requestCode) {
                REQUEST_CODE_GALLERY_PHOTOS, REQUEST_CODE_TAKE_PHOTO -> {
                    var isGalleryImage = false
                    if (requestCode == REQUEST_CODE_GALLERY_PHOTOS) {


                        val selectedImage = data!!.data
                        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                        val cursor = requireActivity().contentResolver.query(
                            selectedImage!!,
                            filePathColumn, null, null, null
                        )

                        cursor!!.moveToFirst()
                        val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                        picturePath = cursor.getString(columnIndex)
                        cursor.close()
                        isGalleryImage = true
                    }

                    // DownSample image
                    GetSampledImage(this).execute(
                        picturePath, imagesDirectory,
                        isGalleryImage.toString(),
                        resources.getDimension(R.dimen.image_downsample_size).toInt().toString()
                    )
                }
            }
        }
    }

    private fun onMediaSelected(selectedMediaList: List<UwMediaPickerMediaModel>?) {
        if (selectedMediaList != null) {
            selectedImages.clear()
            val imagesList = selectedMediaList.map { it.mediaPath }
            //Save the file in camMax folder
            for (file in imagesList) {
                val bitmap = BitmapFactory.decodeFile(file)
                selectedImages.add(
                    GeneralFunctions.bitmapToFile(
                        bitmap,
                        requireContext()
                    )!!.absolutePath
                )
            }
            onGettingMultipleImages(selectedImages)
        } else {
            Toast.makeText(requireContext(), "Unexpected Error", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSampledImageAsyncPostExecute(file: File) {
        onGettingImageFile(file)
    }

    abstract fun setData(savedInstanceState: Bundle?)

    abstract fun onGettingImageFile(file: File)

    abstract fun onGettingMultipleImages(list: List<String>)

    abstract fun onStartCameraDialogFragment(isOpen: Boolean)

    /// to open camera


/*    val cameraDialogFragment = CameraDialogFragment()
    cameraDialogFragment.setTargetFragment(this, BUNDLE_EXTRA_OPEN_CAMERA)
    cameraDialogFragment.show(parentFragmentManager, getString(R.string.dialog))
}


// get response
override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
    super.onActivityResult(requestCode, resultCode, intent)
    if (Activity.RESULT_OK == resultCode && BasePictureOptionsFragment.BUNDLE_EXTRA_OPEN_CAMERA == requestCode) {
        // Get data from decline dialog
        Log.e("TAG", "onActivityResult: "+  intent?.getStringExtra(BasePictureOptionsFragment.INTENT_EXTRAS_IMAGE))

    }*/
}