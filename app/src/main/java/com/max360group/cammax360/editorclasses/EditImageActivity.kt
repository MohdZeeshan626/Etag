package com.max360group.cammax360.editorclasses

import android.Manifest

import com.max360group.cammax360.editorclasses.base.BaseActivity
import ja.burhanrashid52.photoeditor.OnPhotoEditorListener
import com.max360group.cammax360.editorclasses.EmojiBSFragment.EmojiListener
import com.max360group.cammax360.editorclasses.StickerBSFragment.StickerListener
import com.max360group.cammax360.editorclasses.tools.EditingToolsAdapter.OnItemSelected
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.PhotoEditorView
import android.widget.TextView
import android.graphics.Typeface
import androidx.recyclerview.widget.RecyclerView
import com.max360group.cammax360.editorclasses.tools.EditingToolsAdapter
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import ja.burhanrashid52.photoeditor.TextStyleBuilder
import ja.burhanrashid52.photoeditor.ViewType
import android.view.MotionEvent
import android.annotation.SuppressLint
import androidx.core.content.FileProvider
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import ja.burhanrashid52.photoeditor.SaveSettings
import ja.burhanrashid52.photoeditor.PhotoEditor.OnSaveListener
import ja.burhanrashid52.photoeditor.shape.ShapeType
import android.content.DialogInterface
import android.net.Uri
import android.util.Log
import android.view.View
import ja.burhanrashid52.photoeditor.PhotoFilter
import com.max360group.cammax360.editorclasses.tools.ToolType
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.view.animation.AnticipateOvershootInterpolator
import android.widget.ImageView
import androidx.annotation.NonNull
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AlertDialog
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.google.zxing.client.result.URIParsedResult
import com.max360group.cammax360.R
import com.max360group.cammax360.editorclasses.FileSaveHelper.OnFileCreateResult
import com.max360group.cammax360.editorclasses.filters.FilterListener
import com.max360group.cammax360.editorclasses.filters.FilterViewAdapter
import ja.burhanrashid52.photoeditor.shape.ShapeBuilder
import java.io.File
import java.io.IOException
import java.lang.Exception
import com.max360group.cammax360.editorclasses.TextEditorDialogFragment.TextEditor
import com.max360group.cammax360.utils.ApplicationGlobal
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.views.activities.BaseAppCompactActivity
import com.max360group.cammax360.views.fragments.BeforeAfterImageUpdateFragment
import com.max360group.cammax360.views.fragments.BeforeAfterImageUpdateFragment.Companion.INTENT_AFTER_IMAGE
import com.max360group.cammax360.views.fragments.BeforeAfterImageUpdateFragment.Companion.INTENT_FILTER_INTENT


class EditImageActivity : BaseActivity(), OnPhotoEditorListener, View.OnClickListener,
    PropertiesBSFragment.Properties, ShapeBSFragment.Properties, EmojiListener, StickerListener,
    OnItemSelected, FilterListener {

    var mPhotoEditor: PhotoEditor? = null
    private var mPhotoEditorView: PhotoEditorView? = null
    private var mPropertiesBSFragment: PropertiesBSFragment? = null
    private var mShapeBSFragment: ShapeBSFragment? = null
    private var mShapeBuilder: ShapeBuilder? = null
    private var mEmojiBSFragment: EmojiBSFragment? = null
    private var mStickerBSFragment: StickerBSFragment? = null
    private var mTxtCurrentTool: TextView? = null
    private var mWonderFont: Typeface? = null
    private var mRvTools: RecyclerView? = null
    private var mRvFilters: RecyclerView? = null
    private val mEditingToolsAdapter = EditingToolsAdapter(this)
    private val mFilterViewAdapter = FilterViewAdapter(this)
    private var mRootView: ConstraintLayout? = null
    private val mConstraintSet = ConstraintSet()
    private var mIsFilterVisible = false


    @VisibleForTesting
    var mSaveImageUri: Uri? = null
    private var mSaveFileHelper: FileSaveHelper? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        makeFullScreen()
        setContentView(R.layout.activity_edit_image)
        initViews()
        handleIntentImage(mPhotoEditorView!!.source)
        mWonderFont = Typeface.createFromAsset(assets, "beyond_wonderland.ttf")
        mPropertiesBSFragment = PropertiesBSFragment()
        mEmojiBSFragment = EmojiBSFragment()
        mStickerBSFragment = StickerBSFragment()
        mShapeBSFragment = ShapeBSFragment()
        mStickerBSFragment!!.setStickerListener(this)
        mEmojiBSFragment!!.setEmojiListener(this)
        mPropertiesBSFragment!!.setPropertiesChangeListener(this)
        mShapeBSFragment!!.setPropertiesChangeListener(this)
        val llmTools = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mRvTools!!.layoutManager = llmTools
        mRvTools!!.adapter = mEditingToolsAdapter
        val llmFilters = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mRvFilters!!.layoutManager = llmFilters
        mRvFilters!!.adapter = mFilterViewAdapter

        // NOTE(lucianocheng): Used to set integration testing parameters to PhotoEditor
        val pinchTextScalable = intent.getBooleanExtra(PINCH_TEXT_SCALABLE_INTENT_KEY, true)

        //Typeface mTextRobotoTf = ResourcesCompat.getFont(this, R.font.roboto_medium);
        //Typeface mEmojiTypeFace = Typeface.createFromAsset(getAssets(), "emojione-android.ttf");
        mPhotoEditor = PhotoEditor.Builder(this, mPhotoEditorView)
            .setPinchTextScalable(pinchTextScalable) // set flag to make text scalable when pinch
            //.setDefaultTextTypeface(mTextRobotoTf)
            //.setDefaultEmojiTypeface(mEmojiTypeFace)
            .build() // build photo editor sdk
        mPhotoEditor!!.setOnPhotoEditorListener(this)

        //Set Image Dynamically
        val mImage=intent?.getStringExtra("IMAGE")
        mPhotoEditor!!.clearAllViews()
        mPhotoEditorView!!.source.setImageURI(Uri.parse(mImage))
        mPhotoEditorView!!.source.setImageURI(Uri.parse(mImage))
        mSaveFileHelper = FileSaveHelper(this)
        mPhotoEditor!!.setFilterEffect(PhotoFilter.NONE)
    }

    private fun handleIntentImage(source: ImageView) {
        val intent = intent
        if (intent != null) {
            // NOTE(lucianocheng): Using "yoda conditions" here to guard against
            //                     a null Action in the Intent.
            if (Intent.ACTION_EDIT == intent.action || ACTION_NEXTGEN_EDIT == intent.action) {
                try {
                    val uri = intent.data
                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                    source.setImageBitmap(bitmap)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } else {
                val intentType = intent.type
                if (intentType != null && intentType.startsWith("image/")) {
                    val imageUri = intent.data
                    if (imageUri != null) {
                        source.setImageURI(imageUri)
                    }
                }
            }
        }
    }

    private fun initViews() {
        val imgUndo: ImageView
        val imgRedo: ImageView
        val imgCamera: ImageView
        val imgGallery: ImageView
        val imgSave: ImageView
        val imgClose: ImageView
        val imgShare: ImageView
        mPhotoEditorView = findViewById(R.id.photoEditorView)
        mTxtCurrentTool = findViewById(R.id.txtCurrentTool)
        mRvTools = findViewById(R.id.rvConstraintTools)
        mRvFilters = findViewById(R.id.rvFilterView)
        mRootView = findViewById(R.id.rootView)
        imgUndo = findViewById(R.id.imgUndo)
        imgUndo.setOnClickListener(this)
        imgRedo = findViewById(R.id.imgRedo)
        imgRedo.setOnClickListener(this)
        imgCamera = findViewById(R.id.imgCamera)
        imgCamera.setOnClickListener(this)
        imgGallery = findViewById(R.id.imgGallery)
        imgGallery.setOnClickListener(this)
        imgSave = findViewById(R.id.imgSave)
        imgSave.setOnClickListener(this)
        imgClose = findViewById(R.id.imgClose)
        imgClose.setOnClickListener(this)
        imgShare = findViewById(R.id.imgShare)
        imgShare.setOnClickListener(this)
    }

    override fun onEditTextChangeListener(rootView: View, text: String, colorCode: Int) {
        val textEditorDialogFragment = TextEditorDialogFragment.show(this, text, colorCode)
        textEditorDialogFragment.setOnTextEditorListener(object : TextEditor {
            override fun onDone(inputText: String?, colorCode: Int) {
                val styleBuilder = TextStyleBuilder()
                styleBuilder.withTextColor(colorCode)
                mPhotoEditor!!.editText(rootView, inputText, styleBuilder)
                mTxtCurrentTool!!.setText(R.string.label_text)
            }
        })
    }

    override fun onAddViewListener(viewType: ViewType, numberOfAddedViews: Int) {
        Log.d(
            TAG,
            "onAddViewListener() called with: viewType = [$viewType], numberOfAddedViews = [$numberOfAddedViews]"
        )
    }

    override fun onRemoveViewListener(viewType: ViewType, numberOfAddedViews: Int) {
        Log.d(
            TAG,
            "onRemoveViewListener() called with: viewType = [$viewType], numberOfAddedViews = [$numberOfAddedViews]"
        )
    }

    override fun onStartViewChangeListener(viewType: ViewType) {
        Log.d(TAG, "onStartViewChangeListener() called with: viewType = [$viewType]")
    }

    override fun onStopViewChangeListener(viewType: ViewType) {
        Log.d(TAG, "onStopViewChangeListener() called with: viewType = [$viewType]")
    }

    override fun onTouchSourceImage(event: MotionEvent) {
        Log.d(TAG, "onTouchView() called with: event = [$event]")
    }

    @SuppressLint("NonConstantResourceId")
    override fun onClick(view: View) {
        when (view.id) {
            R.id.imgUndo -> mPhotoEditor!!.undo()
            R.id.imgRedo -> mPhotoEditor!!.redo()
            R.id.imgSave -> saveImage()
            R.id.imgClose -> onBackPressed()
            R.id.imgShare -> shareImage()
            R.id.imgCamera -> {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, CAMERA_REQUEST)
            }
            R.id.imgGallery -> {
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_REQUEST)
            }
        }
    }

    private fun shareImage() {
        if (mSaveImageUri == null) {
            showSnackbar(getString(R.string.msg_save_image_to_share))
            return
        }
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_STREAM, buildFileProviderUri(mSaveImageUri!!))
        startActivity(Intent.createChooser(intent, getString(R.string.msg_share_image)))
    }

    private fun buildFileProviderUri(uri: Uri): Uri {
        return FileProvider.getUriForFile(
            this,
            FILE_PROVIDER_AUTHORITY,
            File(uri.path)
        )
    }

    @SuppressLint("MissingPermission")
    private fun saveImage() {
        val fileName = System.currentTimeMillis().toString() + ".png"
        val hasStoragePermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        if (hasStoragePermission && FileSaveHelper.isSdkHigherThan28) {
            showLoading("Saving...")
            mSaveFileHelper!!.createFile(fileName, object : OnFileCreateResult {
                override fun onFileCreateResult(
                    fileCreated: Boolean,
                    filePath: String?,
                    error: String?,
                    uri: Uri?
                ) {
                    if (fileCreated) {
                        val saveSettings = SaveSettings.Builder()
                            .setClearViewsEnabled(true)
                            .setTransparencyEnabled(true)
                            .build()
                        mPhotoEditor!!.saveAsFile(
                            GeneralFunctions.createFile(
                                ApplicationGlobal.context!!, GeneralFunctions.MEDIA_TYPE_IMAGE
                            ).absolutePath,
                            saveSettings,
                            object : OnSaveListener {
                                override fun onSuccess(@NonNull imagePath: String) {
                                    mSaveFileHelper!!.notifyThatFileIsNowPubliclyAvailable(
                                        contentResolver
                                    )
                                    hideLoading()
                                    showSnackbar("Image Saved Successfully")
                                    mSaveImageUri = uri
                                    mPhotoEditorView!!.source.setImageURI(mSaveImageUri)

                                    //Send broadcast
                                    LocalBroadcastManager.getInstance(this@EditImageActivity)
                                        .sendBroadcast(
                                            Intent(INTENT_FILTER_INTENT).putExtra(
                                                INTENT_AFTER_IMAGE, imagePath
                                            )
                                        )
                                   finish()
                                }

                                override fun onFailure(@NonNull exception: Exception) {
                                    hideLoading()
                                    showSnackbar("Failed to save Image")
                                }
                            })
                    } else {
                        hideLoading()
                        showSnackbar(error!!)
                    }
                }
            })
        } else {
            requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                CAMERA_REQUEST -> {
                    mPhotoEditor!!.clearAllViews()
                    val photo = data!!.extras!!["data"] as Bitmap?
                    mPhotoEditorView!!.source.setImageBitmap(photo)
                }
                PICK_REQUEST -> try {
                    mPhotoEditor!!.clearAllViews()
                    val uri = data!!.data
                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                    mPhotoEditorView!!.source.setImageBitmap(bitmap)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onColorChanged(colorCode: Int) {
        mPhotoEditor!!.setShape(mShapeBuilder!!.withShapeColor(colorCode))
        mTxtCurrentTool!!.setText(R.string.label_brush)
    }

    override fun onOpacityChanged(opacity: Int) {
        mPhotoEditor!!.setShape(mShapeBuilder!!.withShapeOpacity(opacity))
        mTxtCurrentTool!!.setText(R.string.label_brush)
    }

    override fun onShapeSizeChanged(shapeSize: Int) {
        mPhotoEditor!!.setShape(mShapeBuilder!!.withShapeSize(shapeSize.toFloat()))
        mTxtCurrentTool!!.setText(R.string.label_brush)
    }

    override fun onShapePicked(shapeType: ShapeType?) {
        mPhotoEditor!!.setShape(mShapeBuilder!!.withShapeType(shapeType))
    }


    override fun isPermissionGranted(isGranted: Boolean, permission: String?) {
        if (isGranted) {
            saveImage()
        }
    }

    private fun showSaveDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(getString(R.string.msg_save_image))
        builder.setPositiveButton("Save") { dialog: DialogInterface?, which: Int -> saveImage() }
        builder.setNegativeButton("Cancel") { dialog: DialogInterface, which: Int -> dialog.dismiss() }
        builder.setNeutralButton("Discard") { dialog: DialogInterface?, which: Int -> finish() }
        builder.create().show()
    }

    override fun onFilterSelected(photoFilter: PhotoFilter?) {
        mPhotoEditor!!.setFilterEffect(photoFilter)
    }

    private fun showBottomSheetDialogFragment(fragment: BottomSheetDialogFragment?) {
        if (fragment == null || fragment.isAdded) {
            return
        }
        fragment.show(supportFragmentManager, fragment.tag)
    }

    fun showFilter(isVisible: Boolean) {
        mIsFilterVisible = isVisible
        mConstraintSet.clone(mRootView)
        if (isVisible) {
            mConstraintSet.clear(mRvFilters!!.id, ConstraintSet.START)
            mConstraintSet.connect(
                mRvFilters!!.id, ConstraintSet.START,
                ConstraintSet.PARENT_ID, ConstraintSet.START
            )
            mConstraintSet.connect(
                mRvFilters!!.id, ConstraintSet.END,
                ConstraintSet.PARENT_ID, ConstraintSet.END
            )
        } else {
            mConstraintSet.connect(
                mRvFilters!!.id, ConstraintSet.START,
                ConstraintSet.PARENT_ID, ConstraintSet.END
            )
            mConstraintSet.clear(mRvFilters!!.id, ConstraintSet.END)
        }
        val changeBounds = ChangeBounds()
        changeBounds.duration = 350
        changeBounds.interpolator = AnticipateOvershootInterpolator(1.0f)
        TransitionManager.beginDelayedTransition(mRootView!!, changeBounds)
        mConstraintSet.applyTo(mRootView)
    }

    override fun onBackPressed() {
        if (mIsFilterVisible) {
            showFilter(false)
            mTxtCurrentTool!!.setText(R.string.app_name)
        } else if (!mPhotoEditor!!.isCacheEmpty) {
            showSaveDialog()
        } else {
            super.onBackPressed()
        }
    }

    override fun onPointerCaptureChanged(hasCapture: Boolean) {}

    companion object {
        private val TAG = EditImageActivity::class.java.simpleName
        const val FILE_PROVIDER_AUTHORITY = "com.burhanrashid52.photoeditor.fileprovider"
        private const val CAMERA_REQUEST = 52
        private const val PICK_REQUEST = 53
        const val ACTION_NEXTGEN_EDIT = "action_nextgen_edit"
        const val PINCH_TEXT_SCALABLE_INTENT_KEY = "PINCH_TEXT_SCALABLE"
        const val REQUEST_WRITE_PERMISSION = 233
    }

    override fun onToolSelected(toolType: ToolType?) {
        when (toolType) {
            ToolType.SHAPE -> {
                mPhotoEditor!!.setBrushDrawingMode(true)
                mShapeBuilder = ShapeBuilder()
                mPhotoEditor!!.setShape(mShapeBuilder)
                mTxtCurrentTool!!.setText(R.string.label_shape)
                showBottomSheetDialogFragment(mShapeBSFragment)
            }
            ToolType.TEXT -> {
                val textEditorDialogFragment = TextEditorDialogFragment.show(this)
                textEditorDialogFragment.setOnTextEditorListener(object : TextEditor {
                    override fun onDone(inputText: String?, colorCode: Int) {
                        val styleBuilder = TextStyleBuilder()
                        styleBuilder.withTextColor(colorCode)
                        mPhotoEditor!!.addText(inputText, styleBuilder)
                        mTxtCurrentTool!!.setText(R.string.label_text)
                    }
                })
            }

            ToolType.ERASER -> {
                mPhotoEditor!!.brushEraser()
                mTxtCurrentTool!!.setText(R.string.label_eraser_mode)
            }

            ToolType.FILTER -> {
                mTxtCurrentTool!!.setText(R.string.label_filter)
                showFilter(true)
            }

            ToolType.EMOJI -> showBottomSheetDialogFragment(mEmojiBSFragment)
            ToolType.STICKER -> showBottomSheetDialogFragment(mStickerBSFragment)
        }
    }

    override fun onEmojiClick(emojiUnicode: String?) {
        mPhotoEditor!!.addEmoji(emojiUnicode)
        mTxtCurrentTool!!.setText(R.string.label_emoji)
    }

    override fun onStickerClick(bitmap: Bitmap?) {
        mPhotoEditor!!.addImage(bitmap)
        mTxtCurrentTool!!.setText(R.string.label_sticker)
    }


}