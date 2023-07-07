package com.max360group.cammax360.views.utils

object JobsConstants {
    const val JOB_KIND_VIDEO="jobVideo"
    const val JOB_KIND_PHOTO="jobPhoto"
    const val JOB_KIND_DOCUMENT="jobDoc"
    const val JOB_KIND_CONVERSATION="Conversation"
    const val JOB_KIND_NOTE="Note"
    const val JOB_KIND_COMMENT="Comment"

    fun savePathForAws(
        bucketName: String = "",
        primaryUserId: String = "",
        jobId: String = ""
    ): String {
        return "${bucketName}FieldMax360/development/${primaryUserId}" +
                "/job_$jobId" + "/jobPhoto"
    }

    fun saveVideoPathForAws(
        bucketName: String = "",
        primaryUserId: String = "",
        jobId: String = ""
    ): String {
        return "${bucketName}FieldMax360/development/${primaryUserId}" +
                "/job_$jobId" + "/jobVideo"
    }

    fun saveDocsPathForAws(
        bucketName: String = "",
        primaryUserId: String = "",
        jobId: String = ""
    ): String {
        return "${bucketName}FieldMax360/development/${primaryUserId}" +
                "/job_$jobId" + "/jobDoc"
    }

    fun savePathForServer(
        primaryUserId: String = "",
        jobId: String = "",
        fileName: String
    ): String {
        return "FieldMax360/development/${primaryUserId}" + "/job_$jobId" + "/jobPhoto/" + fileName
    }

    fun saveVideoPathForServer(
        primaryUserId: String = "",
        jobId: String = "",
        fileName: String
    ): String {
        return "FieldMax360/development/${primaryUserId}" + "/job_$jobId" + "/jobVideo/" + fileName
    }

    fun saveDocsPathForServer(
        primaryUserId: String = "",
        jobId: String = "",
        fileName: String
    ): String {
        return "FieldMax360/development/${primaryUserId}" + "/job_$jobId" + "/jobDoc/" + fileName
    }


}