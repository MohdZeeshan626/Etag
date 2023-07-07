package com.max360group.cammax360.views.dialgofragments

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.Jobs
import com.max360group.cammax360.viewmodels.AccountViewModel
import com.max360group.cammax360.viewmodels.BaseViewModel
import com.max360group.cammax360.viewmodels.JobsViewModel
import com.max360group.cammax360.viewmodels.OnBoardingViewModel
import com.max360group.cammax360.views.fragments.EditMembersPermissionsFragment
import kotlinx.android.synthetic.main.forgot_dialog.*
import kotlinx.android.synthetic.main.forgot_dialog.btnSubmit
import kotlinx.android.synthetic.main.forgot_dialog.etEmail
import kotlinx.android.synthetic.main.save_change_password_dialog.*
import kotlinx.android.synthetic.main.save_role_dialog.*
import kotlinx.android.synthetic.main.save_role_dialog.ivCancel
import kotlinx.android.synthetic.main.toolbar_dialog_fragments.*

class ChangePasswordDialogFragment : BaseDialogFragment(), View.OnClickListener {

    private val mAccountViewModel by lazy {
        ViewModelProvider(this).get(AccountViewModel::class.java)
    }

    override val isFullScreenDialog: Boolean
        get() = false
    override val layoutId: Int
        get() = R.layout.save_change_password_dialog

    override fun init() {
        //Set click listener
        btnSubmit.setOnClickListener(this)
        ivCancel.setOnClickListener(this)
    }

    override val viewModel: BaseViewModel?
        get() = mAccountViewModel

    override fun observeProperties() {
        mAccountViewModel.onPasswordUpdate().observe(this, Observer {
            dismiss()
        })
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btnSubmit -> {
                //Call Api
                mAccountViewModel.updatePassword(etOldPassword.text.toString().trim(),etNewPassword.text.toString().trim(),
                    etConfirmPassword.text.toString().trim())
            }
            R.id.ivCancel -> {
                dismiss()
            }
        }
    }
}