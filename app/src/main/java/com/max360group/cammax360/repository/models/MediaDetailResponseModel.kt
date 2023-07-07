package com.max360group.cammax360.repository.models

import com.amazonaws.services.s3.model.Permission
import com.max360group.cammax360.repository.models.model.Permissions

data class MediaDetailResponseModel(
    val `data`: MediaDetail,
    val message: String,
    val statusCode: Int
)

data class MediaDetail(
    val media: MediaData1
)

data class MediaData1(
    val createdAt: String="",
    val creatorId: CreatorId= CreatorId(),
    val id: String="",
    val isActive: Boolean=false,
    val isDeleted: Boolean=false,
    val jobId: String="",
    val kind: String="",
    val medias: List<Medias>?= mutableListOf(),
    val name: String="",
    val primaryUserId: String="",
    val subKind: String="",
    val tags: List<String>?= mutableListOf(),
    val updatedAt: String=""
)


data class Medias(
    val createdAt: String,
    val creatorId: String,
    val id: String,
    val media: String,
    val mediaURL: String,
    val thumbnail: String,
    val thumbnailURL: String,
    val name: String,
    val subKind: String,
    val tags: List<String>,
    val updatedAt: String,
    val users: List<MediaUsers>
)

data class MediaUsers(
    val createdAt: String,
    val details: MediaUserDetails,
    val invite: Invite,
    val permissions: Permissions,
    val primaryUserId: String,
    val updatedAt: String,
    val userId: UserId
)

data class MediaUserDetails(
    val commentsCount: Int,
    val contributionCount: Int,
    val contributionUpdatedAt: String,
    val conversationCount: Int,
    val mediaDocsCount: Int,
    val mediaPhotosCount: Int,
    val mediaVideosCount: Int,
    val notesCount: Int
)
