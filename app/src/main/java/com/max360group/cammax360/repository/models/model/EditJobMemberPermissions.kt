package com.max360group.cammax360.repository.models.model

import com.max360group.cammax360.repository.models.Jobs

data class EditJobMemberPermissions(
    val permissions: Jobs,
    val userId: String
)
