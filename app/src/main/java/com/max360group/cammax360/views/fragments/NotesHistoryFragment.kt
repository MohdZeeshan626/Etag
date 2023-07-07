package com.max360group.cammax360.views.fragments

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.anilokcun.uwmediapicker.UwMediaPicker
import com.anilokcun.uwmediapicker.model.UwMediaPickerMediaModel
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.Note
import com.max360group.cammax360.repository.models.NoteMedia
import com.max360group.cammax360.repository.models.UserOwner
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.utils.MarshMallowPermissions
import com.max360group.cammax360.viewmodels.BaseViewModel
import com.max360group.cammax360.viewmodels.OwnerViewModel
import com.max360group.cammax360.views.adapters.*
import com.max360group.cammax360.views.dialgofragments.MediaTypeDialogFragment
import com.max360group.cammax360.views.dialgofragments.MediaTypeDialogFragment.Companion.INTENT_DOC
import com.max360group.cammax360.views.dialgofragments.MediaTypeDialogFragment.Companion.INTENT_IMAGE
import com.max360group.cammax360.views.dialgofragments.MediaTypeDialogFragment.Companion.INTENT_MEDIA_TYPE
import com.max360group.cammax360.views.dialgofragments.MediaTypeDialogFragment.Companion.INTENT_VIDEO
import com.max360group.cammax360.views.interfaces.NotesHistoryInterface
import droidninja.filepicker.FilePickerBuilder
import droidninja.filepicker.FilePickerConst
import droidninja.filepicker.utils.ContentUriUtils
import kotlinx.android.synthetic.main.layout_owner_notes_history.*
import java.io.File

class NotesHistoryFragment : BaseFragment(), View.OnClickListener, NotesHistoryInterface {

    companion object {
        const val INTENT_NOTES_HISTORY = "notesHistory"
        const val TYPE_OWNER = "userOwnerId"
        const val TYPE_PROPERTY = "propertyId"
        const val TYPE_UNIT = "propertyUnitId"
        var TYPE = ""

        var mNotesList = ArrayList<Note>()
    }

    private val mOwnerNotesAdapter by lazy {
        OwnerNotesAdapter(this)
    }

    private val mOwnerNoteMediaSelectionAdapter by lazy {
        OwnerNoteMediaSelectionAdapter(this)
    }

    private var mNotesMedia = ArrayList<NoteMedia>()
    private var mId: String = ""

    private val mCreateOwnerViewModel by lazy {
        ViewModelProvider(this).get(OwnerViewModel::class.java)
    }

    override val layoutId: Int
        get() = R.layout.layout_owner_notes_history

    override fun init(savedInstanceState: Bundle?) {
        //Initialize local broadcast to refreshFragment
        LocalBroadcastManager.getInstance(activityContext)
            .registerReceiver(
                mUpdateDialogReceiver,
                IntentFilter(INTENT_NOTES_HISTORY)
            )

        //Set adapter
        mNotesList.clear()
        rvNotes.adapter = mOwnerNotesAdapter
        rvGallary.adapter = mOwnerNoteMediaSelectionAdapter

        //Set on click listener
        btnFilePicker.setOnClickListener(this)
        btnPost.setOnClickListener(this)

        //Search
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                mCreateOwnerViewModel.getNotes(mId, s.toString(), false)
            }
        })
    }

    override val viewModel: BaseViewModel?
        get() = mCreateOwnerViewModel

    override fun observeProperties() {
        mCreateOwnerViewModel.onGetNote().observe(this, Observer {
            mNotesMedia.clear()
            mOwnerNoteMediaSelectionAdapter.updateData(mNotesMedia)
            etMessage.setText("")
            mNotesList.add(it)
            mOwnerNotesAdapter.updateFunction(mNotesList)
        })
        mCreateOwnerViewModel.onGetNotesList().observe(this, Observer {
            mNotesList.clear()
            mNotesList.addAll(it)
            mOwnerNotesAdapter.updateFunction(mNotesList)
        })
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btnFilePicker -> {
                //Check for permissions
                if (mMarshMallowPermissions.isPermissionGrantedForWriteExtStorage) {
                    MediaTypeDialogFragment().show(childFragmentManager, "")
                } else {
                    mMarshMallowPermissions.requestPermissionForWriteExtStorage()
                }


            }
            R.id.btnPost -> {
                mCreateOwnerViewModel.createNote(etMessage.text.toString().trim(), mId, mNotesMedia)
            }
        }
    }

    fun getEditableDetail(id: String, type: String) {
        mId = id
        TYPE = type
        if (id.isNotBlank()) {
            etSearch.visibility = View.VISIBLE
        }
        if (mId.isNotBlank()) {
            mCreateOwnerViewModel.getNotes(mId, showLoader = false)
        }

    }

    private fun initImageIntent() {
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

    private fun initVideoIntent() {
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
            .setCompressionQuality(85)
            .setCompressedFileDestinationPath(
                GeneralFunctions.getOutputDirectory(activityContext).absolutePath
            )
            .setCancelCallback { }
            .launch(::onVideoSelected)
    }

    private fun initDocIntent() {
        val zipTypes = arrayOf("pdf")
        FilePickerBuilder.instance
            .setMaxCount(5) //optional
            .addFileSupport("Files", zipTypes, R.drawable.ic_document)
            .setActivityTheme(R.style.BaseAppTheme) //optional
            .pickFile(this)
    }

    private fun onMediaSelected(selectedMediaList: List<UwMediaPickerMediaModel>?) {
        if (selectedMediaList != null) {
            val imagesList = selectedMediaList.map { it.mediaPath }
            //Save the file in camMax folder
            for (file in imagesList) {
                val bitmap = BitmapFactory.decodeFile(file)
                val file = GeneralFunctions.bitmapToFile(bitmap, requireContext())
                mNotesMedia.add(
                    NoteMedia(
                        kind = "photo",
                        media = file!!.absolutePath,
                        name = file.name,
                        thumbnail = file.absolutePath
                    )
                )
            }
            mOwnerNoteMediaSelectionAdapter.updateData(mNotesMedia)
            rvGallary.visibility = View.VISIBLE
        } else {
            Toast.makeText(requireContext(), "Unexpected Error", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onVideoSelected(selectedMediaList: List<UwMediaPickerMediaModel>?) {
        if (selectedMediaList != null) {
            val selectedVideos = selectedMediaList.map { it.mediaPath } as ArrayList<String>
            if (selectedVideos.size > 5) {
                showMessage(null, getString(R.string.st_video_limit), true)
            } else {
                for (i in selectedVideos.indices) {
                    //Create video thumbnail
                    val thumb = ThumbnailUtils.createVideoThumbnail(
                        selectedVideos[i],
                        MediaStore.Images.Thumbnails.MINI_KIND
                    )

                    mNotesMedia.add(
                        NoteMedia(
                            media = selectedVideos[i],
                            thumbnail = GeneralFunctions.bitmapToFile(
                                thumb!!,
                                requireContext()
                            )!!.absolutePath,
                            kind = "video",
                            name = File(selectedVideos[i]).name
                        )
                    )
                }
                mOwnerNoteMediaSelectionAdapter.updateData(mNotesMedia)
                rvGallary.visibility = View.VISIBLE
            }
        } else {
            Toast.makeText(requireContext(), "Unexpected Error", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                FilePickerConst.REQUEST_CODE_DOC -> {
                    val docPaths = java.util.ArrayList<Uri>()
                    data?.getParcelableArrayListExtra<Uri>(FilePickerConst.KEY_SELECTED_DOCS)
                        ?.let { docPaths.addAll(it) }
                    if (docPaths.isNotEmpty()) {
                        for (uri in docPaths) {
                            mNotesMedia.add(
                                NoteMedia(
                                    media = ContentUriUtils.getFilePath(requireContext(), uri)!!,
                                    thumbnail = ContentUriUtils.getFilePath(
                                        requireContext(),
                                        uri
                                    )!!,
                                    kind = "doc",
                                    name = File(
                                        ContentUriUtils.getFilePath(
                                            requireContext(),
                                            uri
                                        )!!
                                    ).name

                                )
                            )
                        }
                        mOwnerNoteMediaSelectionAdapter.updateData(mNotesMedia)
                        rvGallary.visibility = View.VISIBLE
                    }

                }
            }
        }
    }

    private val mUpdateDialogReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, p1: Intent?) {
            // Get data
            try {
                when (p1!!.getStringExtra(INTENT_MEDIA_TYPE)) {
                    INTENT_IMAGE -> {
                        initImageIntent()
                    }

                    INTENT_VIDEO -> {
                        initVideoIntent()
                    }

                    INTENT_DOC -> {
                        initDocIntent()
                    }
                }

            } catch (e: Exception) {

            }
        }
    }

    override fun onDeleteMedia(position: Int) {
        mNotesMedia.removeAt(position)
        mOwnerNoteMediaSelectionAdapter.updateData(mNotesMedia)
        if (mNotesMedia.isEmpty()) {
            rvGallary.visibility = View.GONE
        }
    }

}