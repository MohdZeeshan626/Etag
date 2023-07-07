package com.max360group.cammax360.views.dialgofragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.Jobs
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.viewmodels.BaseViewModel
import com.max360group.cammax360.viewmodels.JobsViewModel
import com.max360group.cammax360.views.adapters.NewPropertyListAdapter
import com.max360group.cammax360.views.adapters.NewUnitListAdapter
import kotlinx.android.synthetic.main.dialog_add_new_property.*
import kotlinx.android.synthetic.main.dialog_add_new_property.ivCancel
import kotlinx.android.synthetic.main.dialog_add_new_property.btnSubmit
import kotlinx.android.synthetic.main.dialog_add_new_unit.*
import kotlinx.android.synthetic.main.forgot_dialog.etEmail
import kotlinx.android.synthetic.main.save_role_dialog.*

class AddNewUnitDialogFragment : BaseDialogFragment(), View.OnClickListener {

  /*  companion object{
        const val  BUNDLE_JOBS_PERMISSIONS="permissions"

        fun newInstance(jobs: Jobs): AddNewPropertyDialogFragment {
            val mFragment= AddNewPropertyDialogFragment()
            val mBundle= Bundle()
            mBundle.putParcelable(BUNDLE_JOBS_PERMISSIONS,jobs)
            mFragment.arguments = mBundle
            return mFragment
        }
    }
*/
    private val mNewUnitListAdapter by lazy {
        NewUnitListAdapter(this)
    }

    override val isFullScreenDialog: Boolean
        get() = false

    override val layoutId: Int
        get() = R.layout.dialog_add_new_unit

    override fun init() {
        //Set click listener
        btnSubmit.setOnClickListener(this)
        tvUnit.setOnClickListener(this)
        ivCancel.setOnClickListener(this)

        //Set adapter
        rvUnitList.adapter=mNewUnitListAdapter
    }

    override val viewModel: BaseViewModel?
        get() = null

    override fun observeProperties() {
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btnSubmit -> {

            }
            R.id.ivCancel -> {
                dismiss()
            }
            R.id.tvUnit -> {
                if (rvUnitList.isVisible){
                    rvUnitList.visibility=View.GONE
                }else{
                    rvUnitList.visibility=View.VISIBLE
                }
            }
        }
    }
}