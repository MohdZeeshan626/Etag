package com.max360group.cammax360.repository.models

import com.max360group.cammax360.repository.models.model.SubMedia
import java.security.Permission

data class TimelineResponseModel(
    val `data`: TimeLineData,
    val message: String,
    val statusCode: Int
)

data class TimeLineData(
    val list: List<TimeLine>
)

data class TimeLine(
    val createdAt: String,
    val event: String,
    val id: String,
    val user: TimelineUser,
    val reqData: ReqData,
    val postData: PostReq,
    val media: Media,
    val member: Member
)

data class TimelineUser(
    val email: String,
    val firstName: String,
    val id: String,
    val lastName: String,
    val profilePic: String,
    val picURL: String

)

data class ReqData(
    val jobId: String,
    val kind: String,
    val message: String
)

data class PostReq(
    val createdAt: String,
    val creatorId: String,
    val id: String,
    val isDeleted: Boolean,
    val jobId: String,
    val kind: String,
    val message: String,
    val primaryUserId: String,
    val updatedAt: String
)

data class Member(
    val email: String,
    val firstName: String,
    val id: String,
    val lastName: String,
    val profilePic: String,
    val picURL: String
)