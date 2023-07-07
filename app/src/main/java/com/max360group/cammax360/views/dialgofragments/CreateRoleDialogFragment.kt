package com.max360group.cammax360.views.dialgofragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.Jobs
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.viewmodels.BaseViewModel
import com.max360group.cammax360.viewmodels.JobsViewModel
import kotlinx.android.synthetic.main.forgot_dialog.btnSubmit
import kotlinx.android.synthetic.main.forgot_dialog.etEmail
import kotlinx.android.synthetic.main.save_role_dialog.*

class CreateRoleDialogFragment : BaseDialogFragment(), View.OnClickListener {

    companion object{
        const val  BUNDLE_JOBS_PERMISSIONS="permissions"

        fun newInstance(jobs: Jobs): CreateRoleDialogFragment {
            var mFragment= CreateRoleDialogFragment()
            var mBundle= Bundle()
            mBundle.putParcelable(BUNDLE_JOBS_PERMISSIONS,jobs)
            mFragment.arguments = mBundle
            return mFragment
        }
    }

    private val mJobsViewModel by lazy {
        ViewModelProvider(this).get(JobsViewModel::class.java)
    }

    private var mPermission=Jobs()

    override val isFullScreenDialog: Boolean
        get() = false
    override val layoutId: Int
        get() = R.layout.save_role_dialog

    override fun init() {
        //Get arguments
        mPermission= arguments?.getParcelable<Jobs>(BUNDLE_JOBS_PERMISSIONS)!!

        //Set click listener
        btnSubmit.setOnClickListener(this)
        ivCancel.setOnClickListener(this)
    }

    override val viewModel: BaseViewModel?
        get() = mJobsViewModel

    override fun observeProperties() {
        mJobsViewModel.onRoleCreated().observe(this, Observer {
            dismiss()
        })
    }



    // Remove chipView
    interface ChipClickCallback {
        fun onChipRemoved(string: String?)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btnSubmit -> {
                //Call Api
                if (GeneralFunctions.isInternetConnected(requireContext())){
                    mJobsViewModel.createRole(etEmail.text.toString().trim(),mPermission)
                }else{
                    mJobsViewModel.createRoleInLocal(etEmail.text.toString().trim(),mPermission)
                }

            }
            R.id.ivCancel -> {
                dismiss()
            }
        }
    }
}