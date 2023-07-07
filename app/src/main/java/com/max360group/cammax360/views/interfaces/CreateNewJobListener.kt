package com.max360group.cammax360.views.interfaces

import com.max360group.cammax360.repository.models.Jobs

interface CreateNewJobListener {
    fun onEditPermissions(mJobs: Jobs, s: String, adapterPosition: Int)

}