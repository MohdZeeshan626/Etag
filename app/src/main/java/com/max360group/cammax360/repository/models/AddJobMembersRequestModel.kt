package com.max360group.cammax360.repository.models

data class AddJobMembersRequestModel(
    val users: List<Members>
)

data class Members(
    var permissions: Jobs=Jobs(),
    var userId: String=""
)
