package com.max360group.cammax360.views.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.max360group.cammax360.R
import com.max360group.cammax360.viewmodels.BaseViewModel
import com.max360group.cammax360.viewmodels.OnBoardingViewModel
import com.max360group.cammax360.views.activities.BaseAppCompactActivity
import com.max360group.cammax360.views.activities.HomeActivity
import kotlinx.android.synthetic.main.fragment_sign_up.*

class SignUpFragment : BaseFragment(), View.OnClickListener {

    private val mOnBoardingViewModel by lazy {
        ViewModelProvider(this).get(OnBoardingViewModel::class.java)
    }

    override val layoutId: Int
        get() = R.layout.fragment_sign_up

    override fun init(savedInstanceState: Bundle?) {
        // Set click listener
        tvAlreadyHaveAccount.setOnClickListener(this)
        btnSignUp.setOnClickListener(this)
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
            R.id.tvAlreadyHaveAccount -> {
                (requireContext() as BaseAppCompactActivity).onBackPressed()
            }
            R.id.btnSignUp -> {
                //Api call
                mOnBoardingViewModel.register(etFirstName.text.toString().trim(),
                    etLastName.text.toString().trim(),
                    etEmail.text.toString().trim(),
                    etPassword.text.toString().trim(),
                    etConfirmPassword.text.toString().trim())


            }
        }
    }
}