package com.max360group.cammax360.repository.models

data class CreateRoleRequestModel(
    val description: String="",
    val kind: String="jobRole",
    val name: String,
    val permissions: Jobs

)
