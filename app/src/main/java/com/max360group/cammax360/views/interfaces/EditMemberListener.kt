package com.max360group.cammax360.views.interfaces

import com.max360group.cammax360.repository.models.Jobs

interface EditMemberListener {
    fun onDelete(userId:String)
    fun onEdit(jobs: Jobs, name: String, id: String)
}