package com.max360group.cammax360.views.dialgofragments

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.UserX
import com.max360group.cammax360.viewmodels.BaseViewModel
import com.max360group.cammax360.views.activities.MainActivity
import com.max360group.cammax360.views.interfaces.CreateOwnerInterface
import kotlinx.android.synthetic.main.alert_dialog.*
import kotlinx.android.synthetic.main.save_role_dialog.ivCancel


class SaveSendInviteDialogFragment(var mContext: Fragment) : BaseDialogFragment(), View.OnClickListener {

    override val isFullScreenDialog: Boolean
        get() = false

    override val layoutId: Int
        get() = R.layout.dialog_send_invite

    override fun init() {
        //Set click listener
        btnYes.setOnClickListener(this)
        ivCancel.setOnClickListener(this)
    }

    override val viewModel: BaseViewModel?
        get() = null

    override fun observeProperties() {
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btnYes -> {
                (mContext as CreateOwnerInterface).onSendInviteClick()
                 dismiss()
            }
            R.id.ivCancel -> {
                dismiss()
            }
        }
    }
}