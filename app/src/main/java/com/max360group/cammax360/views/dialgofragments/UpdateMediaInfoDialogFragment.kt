package com.max360group.cammax360.views.dialgofragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.Selection
import android.text.TextWatcher
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.JobMedia
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.viewmodels.BaseViewModel
import com.max360group.cammax360.viewmodels.MediaDetailViewModel
import com.max360group.cammax360.views.fragments.JobDetailsFragment
import com.max360group.cammax360.views.fragments.PhotoDetailFragment.Companion.INTENT_PHOTO_DETAIL
import com.max360group.cammax360.views.utils.JobsConstants
import kotlinx.android.synthetic.main.forgot_dialog.btnSubmit
import kotlinx.android.synthetic.main.media_tags_dialog.*
import kotlinx.android.synthetic.main.save_role_dialog.ivCancel


class UpdateMediaInfoDialogFragment : BaseDialogFragment(), View.OnClickListener {

    companion object {
        const val BUNDLE_MEDIA_INFO = "mediaInfo"
        const val BUNDLE_MEDIA_KIND = "kind"
        const val BUNDLE_MEDIA_LOCAL_ID = "mediaLocalId"
        const val BUNDLE_MEDIA_POSITION = "position"

        fun newInstance(
            mediaLocalId: Int, mInfo: JobMedia,mediaPosition:Int, kind: String
        ): UpdateMediaInfoDialogFragment {
            var mFragment = UpdateMediaInfoDialogFragment()
            var mBundle = Bundle()
            mBundle.putParcelable(BUNDLE_MEDIA_INFO, mInfo)
            mBundle.putString(BUNDLE_MEDIA_KIND, kind)
            mBundle.putInt(BUNDLE_MEDIA_LOCAL_ID, mediaLocalId)
            mBundle.putInt(BUNDLE_MEDIA_POSITION, mediaPosition)

            mFragment.arguments = mBundle
            return mFragment
        }
    }

    private val mMediaDetailViewModel by lazy {
        ViewModelProvider(this).get(MediaDetailViewModel::class.java)
    }


    private var mTagsList = ArrayList<String>()
    private var mJobMediaList: JobMedia? = null
    private var mKind = ""
    private var mMediaLocalId = 0
    private var mMediaPosition = 0

    override val isFullScreenDialog: Boolean
        get() = false
    override val layoutId: Int
        get() = R.layout.media_tags_dialog

    override fun init() {
        //Set click listener
        ivCancel.setOnClickListener(this)
        etTags.setOnClickListener(this)
        btnSubmit.setOnClickListener(this)

        //Ge arguments
        mJobMediaList = arguments?.getParcelable(BUNDLE_MEDIA_INFO)
        mKind = arguments?.getString(BUNDLE_MEDIA_KIND).toString()
        mMediaLocalId = arguments?.getInt(BUNDLE_MEDIA_LOCAL_ID)!!
        mMediaPosition = arguments?.getInt(BUNDLE_MEDIA_POSITION)!!

        //Set info
        etMediaName.setText(mJobMediaList!!.name)
        mJobMediaList!!.tags?.let { mTagsList.addAll(it!!) }
        initAddChips(mTagsList)

        //Text listener
        etTags.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int,
                after: Int
            ) {

            }

            override fun afterTextChanged(s: Editable) {
                if (s.isBlank()) {
                    etTags.backgroundTintList = ContextCompat.getColorStateList(
                        requireContext(),
                        R.color.colorSearchBar
                    )
                } else {
                    etTags.backgroundTintList =
                        ContextCompat.getColorStateList(
                            requireContext(),
                            R.color.colorBlueTransparent
                        )
                    if (!s.toString().startsWith(getString(R.string.st_click_to_add))) {
                        etTags.setText(getString(R.string.st_click_to_add))
                        Selection.setSelection(etTags.text, etTags.text.length)

                    }

                }
            }
        })
    }

    private fun initAddChips(list: List<String>) {
        for (i in list.indices) {
            val entryChip1 = getChip(
                chipGroup,
                list[i],
                true,
                object : ChipClickCallback {
                    override fun onChipRemoved(string: String?) {
                        mTagsList.remove(string)
                    }

                })

            chipGroup.addView(entryChip1)
        }
    }

    override val viewModel: BaseViewModel?
        get() = mMediaDetailViewModel

    override fun observeProperties() {
        mMediaDetailViewModel.onSuccess().observe(this, Observer {
            //Send broadcast to update photo detail
            LocalBroadcastManager.getInstance(requireContext())
                .sendBroadcast(
                    Intent(INTENT_PHOTO_DETAIL)
                )

            //Send broadcast to update photo detail
            LocalBroadcastManager.getInstance(requireContext())
                .sendBroadcast(
                    Intent(JobDetailsFragment.BUNDLE_JOB_DETAIL_INTENT)
                )

            dismiss()
        })
    }

    // Set chipView
    private fun getChip(
        entryChipGroup: ChipGroup,
        text: String?,
        shouldShowClose: Boolean,
        chipClickCallback: ChipClickCallback
    ): Chip {
        val chip = Chip(requireContext())
        val paddingDp = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 10f,
            resources.displayMetrics
        ).toInt()
        if (shouldShowClose) {
            chip.setPadding(paddingDp, paddingDp, paddingDp * 3, paddingDp)
        } else {
            chip.setPadding(paddingDp, paddingDp, paddingDp, paddingDp)
        }
        chip.text = text
        chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        chip.isCloseIconEnabled = shouldShowClose
        chip.setChipBackgroundColorResource(R.color.colorAccent)
        chip.setOnCloseIconClickListener {
            chipClickCallback.onChipRemoved(text)
            entryChipGroup.removeView(chip)
        }
        //
        return chip
    }

    // Remove chipView
    interface ChipClickCallback {
        fun onChipRemoved(string: String?)
    }


    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btnSubmit -> {
                if (mKind == JobsConstants.JOB_KIND_VIDEO) {
                    if (GeneralFunctions.isInternetConnected(requireContext())) {
                        mMediaDetailViewModel.updateMediaInfo(
                            id = mJobMediaList!!.id,
                            kind = mKind,
                            name = etMediaName.text.toString().trim(),
                            mTagsList = mTagsList,
                            mMedia = mJobMediaList!!.media,
                            thumbnailUrl = mJobMediaList!!.thumbnail
                        )
                    }else{
                        mMediaDetailViewModel.updateMediaInfoInLocal(
                            id = mMediaLocalId.toString(),
                            kind = mKind,
                            name = etMediaName.text.toString().trim(),
                            mTagsList = mTagsList,
                            media = mJobMediaList!!.media,
                            mediaUrl = mJobMediaList!!.mediaURL,
                            mMediaPosition,
                            thumbnail = mJobMediaList!!.thumbnail,
                            isEditUrl = mJobMediaList!!.isEditUrl
                        )
                    }

                } else {
                    if (GeneralFunctions.isInternetConnected(requireContext())) {
                        mMediaDetailViewModel.updateMediaInfo(
                            id = mJobMediaList!!.id,
                            kind = mKind,
                            name = etMediaName.text.toString().trim(),
                            mTagsList = mTagsList,
                            mMedia = mJobMediaList!!.media
                        )
                    }else{
                        mMediaDetailViewModel.updateMediaInfoInLocal(
                            id = mMediaLocalId.toString(),
                            kind = mKind,
                            name = etMediaName.text.toString().trim(),
                            mTagsList = mTagsList,
                            media = mJobMediaList!!.media,
                            mediaUrl="",
                            mediaPosition= mMediaPosition,
                            isEditUrl = mJobMediaList!!.isEditUrl,
                            thumbnail = mJobMediaList!!.thumbnail
                        )
                    }
                }
            }

            R.id.ivCancel -> {
                dismiss()
            }

            R.id.etTags -> {
                if (etTags.text.toString().trim().substringAfter(":").isBlank()) {
                    showMessage(null, getString(R.string.st_add_tags))

                } else {
                    val entryChip1 = getChip(
                        chipGroup,
                        etTags.text.toString().trim().substringAfter(":"),
                        true,
                        object : ChipClickCallback {
                            override fun onChipRemoved(string: String?) {
                                mTagsList.remove(string)
                                if (mTagsList.contains(string)) {
                                    mTagsList.remove(string)
                                }
                            }

                        })

                    chipGroup.addView(entryChip1)
                    mTagsList.add(etTags.text.toString().trim().substringAfter(":"))
                    etTags.setText("")
                }
            }
        }
    }
}