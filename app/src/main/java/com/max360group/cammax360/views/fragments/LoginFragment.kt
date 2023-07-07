package com.max360group.cammax360.views.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.max360group.cammax360.R
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.viewmodels.BaseViewModel
import com.max360group.cammax360.viewmodels.OnBoardingViewModel
import com.max360group.cammax360.views.activities.BaseAppCompactActivity
import com.max360group.cammax360.views.activities.HomeActivity
import com.max360group.cammax360.views.activities.MainActivity
import com.max360group.cammax360.views.activities.doFragmentTransaction
import com.max360group.cammax360.views.dialgofragments.ForgotPasswordDialogFragment
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_login.etEmail
import kotlinx.android.synthetic.main.fragment_login.etPassword
import kotlinx.android.synthetic.main.fragment_sign_up.*

class LoginFragment : BaseFragment(), View.OnClickListener {

    private val mOnBoardingViewModel by lazy {
        ViewModelProvider(this).get(OnBoardingViewModel::class.java)
    }


    override val layoutId: Int
        get() = R.layout.fragment_login

    override fun init(savedInstanceState: Bundle?) {
        // Set click listener
        tvDontHaveAccount.setOnClickListener(this)
        btnLogin.setOnClickListener(this)
        tvForgotPassword.setOnClickListener(this)
    }

    override val viewModel: BaseViewModel?
        get() = mOnBoardingViewModel

    override fun observeProperties() {
        mOnBoardingViewModel.onSignup().observe(this, Observer {
            var intent = Intent(requireContext(), HomeActivity::class.java)
            startActivity(intent)
            (requireContext() as BaseAppCompactActivity).finish()
        })
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.tvDontHaveAccount -> {
                (requireContext() as BaseAppCompactActivity).doFragmentTransaction(
                    fragment = SignUpFragment(),
                    containerViewId = R.id.flFragContainerMain,
                    enterAnimation = R.animator.slide_right_in,
                    popExitAnimation = R.animator.slide_right_out
                )
            }
            R.id.btnLogin -> {
                //Api call
                mOnBoardingViewModel.login(
                    etEmail.text.toString().trim(),
                    etPassword.text.toString().trim()
                )
            }
            R.id.tvForgotPassword -> {
                ForgotPasswordDialogFragment().show(childFragmentManager, "")
            }
        }
    }
}