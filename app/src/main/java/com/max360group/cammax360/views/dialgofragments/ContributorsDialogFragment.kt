package com.max360group.cammax360.views.dialgofragments

import android.os.Bundle
import android.view.View
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.UserX
import com.max360group.cammax360.viewmodels.BaseViewModel
import com.max360group.cammax360.views.adapters.ContributorsListAdapter
import kotlinx.android.synthetic.main.contributor_dialog.*
import kotlinx.android.synthetic.main.save_role_dialog.ivCancel

class ContributorsDialogFragment : BaseDialogFragment(), View.OnClickListener {

    companion object {
        const val BUNDLE_CONTRIBUTOR_LIST = "contributor"

        fun newInstance(mList: ArrayList<UserX>): ContributorsDialogFragment {
            val mContributorsDialogFragment = ContributorsDialogFragment()
            val bundle = Bundle()
            bundle.putParcelableArrayList(BUNDLE_CONTRIBUTOR_LIST, mList)
            mContributorsDialogFragment.arguments = bundle

            return mContributorsDialogFragment
        }
    }

    private val mContributorsListAdapter by lazy {
        ContributorsListAdapter(this)
    }

    private var mContributionList = mutableListOf<UserX>()

    override val isFullScreenDialog: Boolean
        get() = false

    override val layoutId: Int
        get() = R.layout.contributor_dialog

    override fun init() {
        //Set click listener
        ivCancel.setOnClickListener(this)

        //Get arguments
        mContributionList= arguments?.getParcelableArrayList<UserX>(BUNDLE_CONTRIBUTOR_LIST)!!

        //Set adapter
        rvContributors.adapter=mContributorsListAdapter
        //Update adapter
        mContributorsListAdapter.updateFunction(mContributionList)

    }

    override val viewModel: BaseViewModel?
        get() = null

    override fun observeProperties() {

    }

    override fun onClick(p0: View?) {
        when (p0?.id) {

            R.id.ivCancel -> {
                dismiss()
            }
        }
    }
}