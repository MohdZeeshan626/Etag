package com.max360group.cammax360.views.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.DocsModel
import com.max360group.cammax360.repository.models.JobMembers
import com.max360group.cammax360.repository.models.model.Permissions
import com.max360group.cammax360.repository.models.model.User
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.viewmodels.BaseViewModel
import com.max360group.cammax360.viewmodels.JobMediaViewModel
import com.max360group.cammax360.views.activities.BaseAppCompactActivity
import com.max360group.cammax360.views.activities.doFragmentTransaction
import com.max360group.cammax360.views.adapters.DocsAdapter
import com.max360group.cammax360.views.calender.MonthlyActivity
import com.max360group.cammax360.views.dialgofragments.DocsTagsDialogFragment
import com.max360group.cammax360.views.fragments.JobDetailsFragment.Companion.BUNDLE_JOB_DETAIL_INTENT
import droidninja.filepicker.FilePickerBuilder
import droidninja.filepicker.FilePickerConst
import droidninja.filepicker.FilePickerConst.KEY_SELECTED_DOCS
import droidninja.filepicker.utils.ContentUriUtils
import kotlinx.android.synthetic.main.fragment_docs_view.*
import kotlinx.android.synthetic.main.fragment_video_view.ivDelete
import kotlinx.android.synthetic.main.fragment_video_view.ivDetail
import kotlinx.android.synthetic.main.fragment_video_view.ivDone
import kotlinx.android.synthetic.main.fragment_video_view.ivPreview
import kotlinx.android.synthetic.main.toolbar.*
import java.io.File
import java.io.InputStream
import java.util.*


class DocumentPreviewFragment : BaseFragment(), View.OnClickListener, DocsAdapter.DocsListener {

    companion object {
        const val BUNDLE_JOB_ID = "jobId"
        const val BUNDLE_LOCAL_ID = "localId"
        const val BUNDLE_SUB_KIND = "documents"

        fun newInstance(mJobId: String, localId: String): DocumentPreviewFragment {
            val mFragment = DocumentPreviewFragment()
            val bundle = Bundle()
            bundle.putString(BUNDLE_JOB_ID, mJobId)
            bundle.putString(BUNDLE_LOCAL_ID, localId)
            mFragment.arguments = bundle

            return mFragment
        }
    }

    private val mJobMediaViewModel by lazy {
        ViewModelProvider(this).get(JobMediaViewModel::class.java)
    }

    private val mDocsAdapter by lazy {
        DocsAdapter(this)
    }

    private var mPosition = 0
    private var mJobId = ""
    private var mLocalId = ""
    private var mMembersList = ArrayList<JobMembers>()
    private var mUser: ArrayList<User>? =
        ArrayList()
    private var mList = ArrayList<DocsModel>()
    private val SELECT_PDF = 122

    override val layoutId: Int
        get() = R.layout.fragment_docs_view

    override fun init(savedInstanceState: Bundle?) {
        //Get arguments
        mJobId = arguments?.getString(BUNDLE_JOB_ID).toString()
        mLocalId = arguments?.getString(BUNDLE_LOCAL_ID).toString()

        // Set toolbar
        toolbar.navigationIcon =
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_back_primary)
        ivToolbarUserImage.setImageURI(mUserPrefsManager.loginedUser!!.picURL)

        //Set click listener
        ivDone.setOnClickListener(this)
        ivPreview.setOnClickListener(this)
        ivDetail.setOnClickListener(this)
        ivDelete.setOnClickListener(this)
        ivCancelDocs.setOnClickListener(this)
        ivUploadFile.setOnClickListener(this)
        ivToolbarUserImage.setOnClickListener(this)
        ivToolbarRightIcon.setOnClickListener(this)
        ivToolbarRightIconBell.setOnClickListener(this)

        //Set adapter
        rvDocsView.adapter = mDocsAdapter

        //Get pdf in android
        getPdfFile()

        //Call api
        mJobMediaViewModel.getJobMembers(BUNDLE_SUB_KIND, mJobId)

    }

    override val viewModel: BaseViewModel?
        get() = mJobMediaViewModel

    override fun observeProperties() {
        mJobMediaViewModel.onMediaUpdated().observe(this, Observer {
            //Send broadcast
            LocalBroadcastManager.getInstance(requireContext())
                .sendBroadcast(
                    Intent(BUNDLE_JOB_DETAIL_INTENT)
                )
            (requireContext() as BaseAppCompactActivity).onBackPressed()
        })

        mJobMediaViewModel.onGetMembers().observe(this, Observer {
            mMembersList.clear()
            mMembersList.addAll(it)

            //Remove my self from the list
            for (i in mMembersList.indices) {
                if (mMembersList[i].id == mUserPrefsManager.loginedUser!!.id) {
                    mMembersList.removeAt(i)
                    break
                }
            }

            for (i in mMembersList.indices) {
                mUser!!.add(
                    User(
                        userId = mMembersList[i].id,
                        primaryUserId = mMembersList[i].accounts[0].primaryUserId,
                        permissions = Permissions(
                            base = mMembersList[i].accounts[0].permissions.jobs!!.documents,
                            comments = mMembersList[i].accounts[0].permissions.jobs!!.comments,
                            members = mMembersList[i].accounts[0].permissions.jobs!!.members
                        )
                    )
                )
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                FilePickerConst.REQUEST_CODE_DOC -> {
                    val docPaths = ArrayList<Uri>()
                    data?.getParcelableArrayListExtra<Uri>(KEY_SELECTED_DOCS)
                        ?.let { docPaths.addAll(it) }
                    if (docPaths.isNotEmpty()) {
                        pdfViewer(ContentUriUtils.getFilePath(requireContext(), docPaths[0])!!)
                        for (uri in docPaths) {
                            mList.add(
                                DocsModel(
                                    docs = ContentUriUtils.getFilePath(requireContext(), uri)!!,
                                    docsName = "Document${mList.size + 1}"
                                )
                            )

                            mDocsAdapter.updateData(mList, 0)
                        }
                    }

                }
            }
        }
    }

    private fun InputStream.toFile(path: String) {
        File(path).outputStream().use { this.copyTo(it) }
    }

    private fun pdfViewer(file: String) {
        webView.fromUri(Uri.parse(GeneralFunctions.getLocalImageFile(File(file))))
            .defaultPage(0)
            .spacing(10)
            .load()
    }

    private fun getPdfFile() {
        val zipTypes = arrayOf("pdf")
        FilePickerBuilder.instance
            .setMaxCount(5) //optional
            .addFileSupport("Files", zipTypes,R.drawable.ic_document)
            .setActivityTheme(R.style.BaseAppTheme) //optional
            .pickFile(this)

    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ivCamera -> {
                (requireContext() as BaseAppCompactActivity).onBackPressed()
            }

            R.id.ivDetail -> {
                DocsTagsDialogFragment.newInstance(mList, mPosition).show(childFragmentManager, "")

            }

            R.id.ivDelete -> {
                mList.removeAt(mPosition)
                if (mList.isEmpty()) {
                    (requireContext() as BaseAppCompactActivity).onBackPressed()
                } else {
                    if (mPosition == mList.size) {
                        mPosition = 0
                    }
                    mDocsAdapter.updateData(mList, mPosition)
                }
            }
            R.id.ivPreview -> {
                (activityContext as BaseAppCompactActivity).doFragmentTransaction(
                    fragment = DocsDetailsPermissions.newInstance(mList, mMembersList, mPosition),
                    containerViewId = R.id.flFragContainerMain,
                    enterAnimation = R.animator.slide_right_in,
                    popExitAnimation = R.animator.slide_right_out
                )
            }
            R.id.ivDone -> {
                if (GeneralFunctions.isInternetConnected(requireContext())) {
                    mJobMediaViewModel.addJobDocs(mJobId, mList, mUser!!)
                } else {
                    mJobMediaViewModel.saveDocsInLocal(mJobId, mLocalId, mList, mUser)
                }
            }

            R.id.ivCancelDocs -> {
                (requireContext() as BaseAppCompactActivity).onBackPressed()
            }

            R.id.ivUploadFile -> {
                getPdfFile()
            }
            R.id.ivToolbarUserImage -> {
                (activityContext as BaseAppCompactActivity).doFragmentTransaction(
                    fragment = AccountsFragment.newInstance(AccountsFragment.BUNDLE_NORMAl_VIEWS),
                    containerViewId = R.id.flFragContainerMain,
                    enterAnimation = R.animator.slide_right_in,
                    popExitAnimation = R.animator.slide_right_out
                )
            }
            R.id.ivToolbarRightIcon -> {
                startActivity(Intent(requireContext(), MonthlyActivity::class.java))
            }
            R.id.ivToolbarRightIconBell -> {
                (activityContext as BaseAppCompactActivity).doFragmentTransaction(
                    fragment = NotificationFragment(),
                    containerViewId = R.id.flFragContainerMain,
                    enterAnimation = R.animator.slide_right_in,
                    popExitAnimation = R.animator.slide_right_out
                )
            }
        }
    }

    override fun onDocsClick(position: Int) {
        mPosition = position
        pdfViewer(mList[position].docs)
    }

    override fun onRemove(position: Int) {
        mList.removeAt(position)
        if (mList.isEmpty()) {
            (requireContext() as BaseAppCompactActivity).onBackPressed()
        } else {
            if (mPosition == mList.size) {
                mPosition = 0
            }
            mDocsAdapter.updateData(mList, mPosition)
        }
    }

}