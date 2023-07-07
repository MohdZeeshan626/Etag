package com.max360group.cammax360.repository.models.model

import com.max360group.cammax360.repository.models.Job
import com.max360group.cammax360.repository.models.Jobs

data class MembersPermissionsModel(
    var permissionName:String?="",
    var permission:List<JobsCategory>?= ArrayList(),
    var isChecked:Boolean=false

)

data class JobsCategory(
    var name: String?,
    var jobPermissionsValue: List<JobsPermissionsValue>? = ArrayList(),
    var mJobs:Jobs
)

data class JobsPermissionsValue(
    var name:String?="",
    var isChecked:Boolean=false
)