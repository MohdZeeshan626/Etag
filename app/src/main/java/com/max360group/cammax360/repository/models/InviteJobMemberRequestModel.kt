package com.max360group.cammax360.repository.models

data class InviteJobMemberRequestModel(
    val email: String,
    val name: String,
    val permissions: Jobs
)

