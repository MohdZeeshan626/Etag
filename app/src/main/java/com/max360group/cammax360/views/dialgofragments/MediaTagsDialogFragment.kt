package com.max360group.cammax360.views.dialgofragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.Selection
import android.text.TextWatcher
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.model.BeforeAfterImageModel
import com.max360group.cammax360.viewmodels.BaseViewModel
import com.max360group.cammax360.views.fragments.BeforeAfterImageUpdateFragment
import kotlinx.android.synthetic.main.forgot_dialog.btnSubmit
import kotlinx.android.synthetic.main.media_tags_dialog.*
import kotlinx.android.synthetic.main.save_role_dialog.ivCancel


class MediaTagsDialogFragment : BaseDialogFragment(), View.OnClickListener {

    companion object {
        const val BUNDLE_JOBS_IMAGE_LIST = "jobImageList"
        const val BUNDLE_IMAGE_POSITION = "position"
        const val BUNDLE_IMAGE_TYPE = "type"
        const val BUNDLE_IMAGE_SIMPLE = 1
        const val BUNDLE_IMAGE_BEFORE = 2
        const val BUNDLE_IMAGE_AFTER = 3

        fun newInstance(
            jobs: ArrayList<BeforeAfterImageModel>,
            mPosition: Int,
            mType: Int
        ): MediaTagsDialogFragment {
            val mFragment = MediaTagsDialogFragment()
            val mBundle = Bundle()
            mBundle.putParcelableArrayList(BUNDLE_JOBS_IMAGE_LIST, jobs)
            mBundle.putInt(BUNDLE_IMAGE_POSITION, mPosition)
            mBundle.putInt(BUNDLE_IMAGE_TYPE, mType)
            mFragment.arguments = mBundle
            return mFragment
        }
    }

    private var mTagsList = ArrayList<String>()
    private var mImageList = ArrayList<BeforeAfterImageModel>()
    private var mPosition = 0
    private var mImageType = 1

    override val isFullScreenDialog: Boolean
        get() = false
    override val layoutId: Int
        get() = R.layout.media_tags_dialog

    override fun init() {
        //Get arguments
        arguments?.let {
            mImageList =
                it.getParcelableArrayList<BeforeAfterImageModel>(BUNDLE_JOBS_IMAGE_LIST) as ArrayList<BeforeAfterImageModel>
            mPosition = it.getInt(BUNDLE_IMAGE_POSITION)
            mImageType = it.getInt(BUNDLE_IMAGE_TYPE)
        }

        //Set data
        when (mImageType) {
            BUNDLE_IMAGE_SIMPLE -> {
                etMediaName.setText(mImageList[mPosition].simpleImageName)
                mTagsList.addAll(mImageList[mPosition].tags!!)
                initAddChips(mImageList[mPosition].tags!!)
            }

            BUNDLE_IMAGE_AFTER -> {
                etMediaName.setText(mImageList[mPosition].afterImageName)
                mTagsList.addAll(mImageList[mPosition].tagsAfter!!)
                initAddChips(mImageList[mPosition].tagsAfter!!)
            }

            BUNDLE_IMAGE_BEFORE -> {
                etMediaName.setText(mImageList[mPosition].beforeImageName)
                mTagsList.addAll(mImageList[mPosition].tagsBefore!!)
                initAddChips(mImageList[mPosition].tagsBefore!!)

            }
        }

        //Set click listener
        ivCancel.setOnClickListener(this)
        etTags.setOnClickListener(this)
        btnSubmit.setOnClickListener(this)

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
                    etTags.backgroundTintList = ContextCompat.getColorStateList(requireContext(),
                        R.color.colorSearchBar)
                } else {
                    etTags.backgroundTintList =
                        ContextCompat.getColorStateList(requireContext(),
                            R.color.colorBlueTransparent)
                    if (!s.toString().startsWith(getString(R.string.st_click_to_add))) {
                        etTags.setText(getString(R.string.st_click_to_add))
                        Selection.setSelection(etTags.text, etTags.text.length)

                    }

                }
            }
        })
    }

    private fun initAddChips(list:List<String>){
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
        get() = null

    override fun observeProperties() {
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
                if (mTagsList.isEmpty()) {
                    showMessage(null, getString(R.string.st_empty_tags))
                } else {
                    when (mImageType) {
                        BUNDLE_IMAGE_AFTER -> {
                            mImageList[mPosition].tagsAfter = mTagsList
                            mImageList[mPosition].afterImageName=etMediaName.text.toString().trim()
                        }
                        BUNDLE_IMAGE_BEFORE -> {
                            mImageList[mPosition].tagsBefore = mTagsList
                            mImageList[mPosition].beforeImageName=etMediaName.text.toString().trim()
                        }

                        BUNDLE_IMAGE_SIMPLE -> {
                            mImageList[mPosition].tags = mTagsList
                            mImageList[mPosition].simpleImageName=etMediaName.text.toString().trim()
                        }
                    }

                    //Send broadcast
                    LocalBroadcastManager.getInstance(requireContext())
                        .sendBroadcast(
                            Intent(BeforeAfterImageUpdateFragment.INTENT_IMAGE).putExtra(
                                BeforeAfterImageUpdateFragment.BUNDLE_IMAGE_LIST, mImageList
                            )
                        )
                    dismiss()
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
                                if (mTagsList.contains(string)){
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