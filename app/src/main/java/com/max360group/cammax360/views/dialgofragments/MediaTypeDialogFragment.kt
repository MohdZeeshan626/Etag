package com.max360group.cammax360.views.dialgofragments

import android.content.Intent
import android.view.View
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.max360group.cammax360.R
import com.max360group.cammax360.viewmodels.BaseViewModel
import com.max360group.cammax360.views.fragments.NotesHistoryFragment.Companion.INTENT_NOTES_HISTORY
import kotlinx.android.synthetic.main.dialog_upload_media_type.*

class MediaTypeDialogFragment : BaseDialogFragment(), View.OnClickListener {
    companion object {
        const val INTENT_IMAGE = "0"
        const val INTENT_VIDEO = "1"
        const val INTENT_DOC = "3"
        const val INTENT_MEDIA_TYPE = "mediaType"
    }

    override val isFullScreenDialog: Boolean
        get() = false

    override val layoutId: Int
        get() = R.layout.dialog_upload_media_type

    override fun init() {
        //Set click listener
        tvImage.setOnClickListener(this)
        tvVideo.setOnClickListener(this)
        tvDocs.setOnClickListener(this)
        ivDismiss.setOnClickListener(this)

    }

    override val viewModel: BaseViewModel?
        get() = null

    override fun observeProperties() {

    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.tvImage -> {
                //Send broadcast
                LocalBroadcastManager.getInstance(requireContext())
                    .sendBroadcast(
                        Intent(INTENT_NOTES_HISTORY).putExtra(
                            INTENT_MEDIA_TYPE, INTENT_IMAGE
                        )
                    )
                dismiss()
            }

            R.id.tvVideo -> {
                //Send broadcast
                LocalBroadcastManager.getInstance(requireContext())
                    .sendBroadcast(
                        Intent(INTENT_NOTES_HISTORY).putExtra(
                            INTENT_MEDIA_TYPE, INTENT_VIDEO
                        )
                    )
                dismiss()
            }
            R.id.tvDocs -> {
                //Send broadcast
                LocalBroadcastManager.getInstance(requireContext())
                    .sendBroadcast(
                        Intent(INTENT_NOTES_HISTORY).putExtra(
                            INTENT_MEDIA_TYPE, INTENT_DOC
                        )
                    )
                dismiss()
            }

            R.id.ivDismiss -> {
                dismiss()
            }
        }
    }


}