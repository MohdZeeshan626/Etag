package com.max360group.cammax360.views.dialgofragments

import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.max360group.cammax360.R
import com.max360group.cammax360.viewmodels.BaseViewModel
import com.max360group.cammax360.viewmodels.OnBoardingViewModel
import kotlinx.android.synthetic.main.forgot_dialog.*
import kotlinx.android.synthetic.main.toolbar_dialog_fragments.*

class ForgotPasswordDialogFragment : BaseDialogFragment(), View.OnClickListener {

    private val mOnBoardingViewModel by lazy {
        ViewModelProvider(this).get(OnBoardingViewModel::class.java)
    }

    override val isFullScreenDialog: Boolean
        get() = false
    override val layoutId: Int
        get() = R.layout.forgot_dialog

    override fun init() {
        //Set toolbar
        tvToolbarTitle.text = getString(R.string.forgot_password)

        //Set click listener
        btnSubmit.setOnClickListener(this)
    }

    override val viewModel: BaseViewModel?
        get() = mOnBoardingViewModel

    override fun observeProperties() {
        mOnBoardingViewModel.onForgotPassword().observe(this, Observer {
            dismiss()
        })
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btnSubmit -> {
                //Call Api
                mOnBoardingViewModel.forgotPassword(etEmail.text.toString().trim())
            }
        }
    }
}