package com.max360group.cammax360.viewmodels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.Jobs
import com.max360group.cammax360.repository.models.PermissionsBitValues
import com.max360group.cammax360.repository.models.model.JobsCategory
import com.max360group.cammax360.repository.models.model.JobsPermissionsValue
import com.max360group.cammax360.repository.models.model.MembersPermissionsModel

class JobsPermissionsViewModel(application: Application) : BaseViewModel(application) {

    private var mJobsList = ArrayList<MembersPermissionsModel>()
    private var permission = ArrayList<JobsCategory>()
    private var permissionValue = ArrayList<JobsPermissionsValue>()
    private var isJobsListing = MutableLiveData<List<MembersPermissionsModel>>()

    private var isMenuList = MutableLiveData<List<MembersPermissionsModel>>()
    private var context = application
    private var mPermissionsBitValues = PermissionsBitValues()

    fun getMenu(mJobs: Jobs?) {
        permissionValue.clear()
        permission.clear()
        mJobsList.clear()

        permissionValue.add(JobsPermissionsValue(
            name = context.getString(R.string.st_all)
        ))
        permissionValue.add(JobsPermissionsValue(
            name = context.getString(R.string.st_view)
        ))

        permissionValue.add(JobsPermissionsValue(
            name = context.getString(R.string.st_add)
        ))

        permissionValue.add(JobsPermissionsValue(
            name = context.getString(R.string.st_edit)
        ))

        permissionValue.add(JobsPermissionsValue(
            name = context.getString(R.string.st_delete)
        ))
        permissionValue.add(JobsPermissionsValue(
            name = context.getString(R.string.st_time_line)
        ))

        permission.add(JobsCategory(
            context.getString(R.string.st_job_level_permissions),
            ArrayList<JobsPermissionsValue>(permissionValue),
            mJobs!!
        ))

        permission.add(JobsCategory(
            context.getString(R.string.st_media_photo),
            ArrayList<JobsPermissionsValue>(permissionValue),
            mJobs
        ))

        permission.add(JobsCategory(
            context.getString(R.string.st_documents),
            ArrayList<JobsPermissionsValue>(permissionValue),
            mJobs
        ))

        permission.add(JobsCategory(
            context.getString(R.string.st_conversation),
            ArrayList<JobsPermissionsValue>(permissionValue),
            mJobs
        ))

        permission.add(JobsCategory(
            context.getString(R.string.st_notes),
            ArrayList<JobsPermissionsValue>(permissionValue),
            mJobs
        ))

        permission.add(JobsCategory(
            context.getString(R.string.st_Comments),
            ArrayList<JobsPermissionsValue>(permissionValue),
            mJobs
        ))

        permission.add(JobsCategory(
            context.getString(R.string.st_members),
            ArrayList<JobsPermissionsValue>(permissionValue),
            mJobs
        ))


        mJobsList.add(MembersPermissionsModel(
            context.getString(R.string.st_job_permissions),
            permission
        ))


        isJobsListing.value = mJobsList
    }

    fun onGetMenu() = isJobsListing

}