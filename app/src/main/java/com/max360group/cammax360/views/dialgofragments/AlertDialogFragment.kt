package com.max360group.cammax360.views.dialgofragments

import android.app.Activity
import android.content.Intent
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
import com.max360group.cammax360.viewmodels.BaseViewModel
import com.max360group.cammax360.viewmodels.JobsViewModel
import com.max360group.cammax360.viewmodels.OnBoardingViewModel
import com.max360group.cammax360.views.fragments.EditMembersPermissionsFragment
import kotlinx.android.synthetic.main.alert_dialog.*
import kotlinx.android.synthetic.main.forgot_dialog.*
import kotlinx.android.synthetic.main.forgot_dialog.btnSubmit
import kotlinx.android.synthetic.main.forgot_dialog.etEmail
import kotlinx.android.synthetic.main.fragment_add_members.*
import kotlinx.android.synthetic.main.fragment_add_members.tvTitle
import kotlinx.android.synthetic.main.save_role_dialog.*
import kotlinx.android.synthetic.main.save_role_dialog.ivCancel
import kotlinx.android.synthetic.main.toolbar_dialog_fragments.*

class AlertDialogFragment : BaseDialogFragment(), View.OnClickListener {

    companion object{
        const val  BUNDLE_TITLE="title"
        const val  BUNDLE_MESSAGE="message"
        const val  INTENT_SUBMIT="submit"
        const val  VALUE_TRUE=121

        fun newInstance(message:String,title:String): AlertDialogFragment {
            var mFragment= AlertDialogFragment()
            var mBundle= Bundle()
            mBundle.putString(BUNDLE_TITLE, title)
            mBundle.putString(BUNDLE_MESSAGE,message)
            mFragment.arguments = mBundle
            return mFragment
        }
    }

    private var title=""
    private var message=""

    override val isFullScreenDialog: Boolean
        get() = false
    override val layoutId: Int
        get() = R.layout.alert_dialog

    override fun init() {
        //Get arguments
        arguments?.let {
            title= it.getString(BUNDLE_TITLE).toString()
            message= it.getString(BUNDLE_MESSAGE).toString()
        }

        tvTitle.text=title
        tvMessage.text=message

        //Set click listener
        btnYes.setOnClickListener(this)
        btnCancel.setOnClickListener(this)
        ivCancel.setOnClickListener(this)
    }

    override val viewModel: BaseViewModel?
        get() = null

    override fun observeProperties() {
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btnYes -> {
                if (targetFragment != null) {
                    targetFragment!!.onActivityResult(
                        targetRequestCode,
                        Activity.RESULT_OK,
                        Intent().putExtra(INTENT_SUBMIT,VALUE_TRUE
                        )
                    )
                }
                dismiss()
            }

            R.id.btnCancel -> {
                dismiss()
            }
            R.id.ivCancel -> {
                dismiss()
            }
        }
    }
}