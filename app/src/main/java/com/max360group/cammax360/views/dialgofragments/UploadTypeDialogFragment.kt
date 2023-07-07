package com.max360group.cammax360.views.dialgofragments

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.max360group.cammax360.R
import com.max360group.cammax360.viewmodels.BaseViewModel
import com.max360group.cammax360.views.fragments.BeforeAfterImageUpdateFragment
import kotlinx.android.synthetic.main.dialog_upload.*

class UploadTypeDialogFragment : BaseDialogFragment(),View.OnClickListener {

    companion object{
        const val INTENT_SIMPLE_PHOTO="0"
        const val INTENT_BEFORE_AFTER_PHOTO="1"
        const val INTENT_PHOTO_TYPE="photoType"
        const val INTENT_PHOTO_DIALOG="photoDialog"
    }

    override val isFullScreenDialog: Boolean
        get() = false
    override val layoutId: Int
        get() = R.layout.dialog_upload

    override fun init() {
        //Set click listener
        tvNormalPhoto.setOnClickListener(this)
        tvBeforeAfter.setOnClickListener(this)
        ivCancel.setOnClickListener(this)

    }

    override val viewModel: BaseViewModel?
        get() = null

    override fun observeProperties() {

    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.tvNormalPhoto->{
                //Send broadcast
                LocalBroadcastManager.getInstance(requireContext())
                    .sendBroadcast(
                        Intent(INTENT_PHOTO_DIALOG).putExtra(
                            INTENT_PHOTO_TYPE, INTENT_SIMPLE_PHOTO
                        )
                    )
                dismiss()
            }

            R.id.tvBeforeAfter->{
                //Send broadcast
                LocalBroadcastManager.getInstance(requireContext())
                    .sendBroadcast(
                        Intent(INTENT_PHOTO_DIALOG).putExtra(
                            INTENT_PHOTO_TYPE, INTENT_BEFORE_AFTER_PHOTO
                        )
                    )
                dismiss()
            }  R.id.ivCancel->{
            dismiss()
            }
        }
    }


}