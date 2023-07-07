package com.max360group.cammax360.views.dialgofragments

import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.max360group.cammax360.R
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.viewmodels.BaseViewModel
import com.max360group.cammax360.viewmodels.OwnerViewModel
import kotlinx.android.synthetic.main.alert_dialog.*
import kotlinx.android.synthetic.main.save_role_dialog.ivCancel


class SendInviteDialogFragment(var id: String,var localId:Int,var sendInvite: Boolean) : BaseDialogFragment(), View.OnClickListener {

    private val mOwnerViewModel by lazy {
        ViewModelProvider(this).get(OwnerViewModel::class.java)
    }

    override val isFullScreenDialog: Boolean
        get() = false

    override val layoutId: Int
        get() = R.layout.dialog_send_invite

    override fun init() {
        //Set click listener
        btnYes.setOnClickListener(this)
        ivCancel.setOnClickListener(this)

        tvTitle.text=getString(R.string.st_send_invite)
        btnYes.text=getString(R.string.st_send_invite)
    }

    override val viewModel: BaseViewModel?
        get() = mOwnerViewModel

    override fun observeProperties() {
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btnYes -> {
                mOwnerViewModel.sendInvite(id)
            }
            R.id.ivCancel -> {
                dismiss()
            }
        }
    }
}