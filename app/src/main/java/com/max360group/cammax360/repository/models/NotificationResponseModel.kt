package com.max360group.cammax360.repository.models

import android.provider.Telephony

data class NotificationResponseModel(
    val `data`: NotificationData,
    val message: String,
    val statusCode: Int
)

data class NotificationData(
    val count: Int,
    val records: List<Record>,
    val unreadCount: Int
)

data class Record(
    val createdAt: String,
    val creatorId: CreatorId,
    val id: String,
    val isDeleted: Boolean,
    val isGlobal: Boolean,
    val isRead: Boolean,
    val jobId: JobId,
    val kind: String,
    val mediaId: Media,
    val conversationId: ConversationId,
    val primaryUserId: String,
    val propertyId: PropertyId,
    val propertyUnitId: PropertyUnitId,
    val readUsers: List<String>,
    val receivers: List<String>,
    val reqId: String,
    val updatedAt: String
)

data class JobId(
    val endDt: String,
    val id: String,
    val startDt: String,
    val title: String,
    val users: List<NotificationUser>
)

data class PropertyId(
    val id: String,
    val name: String,
    val shortName: String
)

data class PropertyUnitId(
    val id: String,
    val name: String,
    val pic: String,
    val picURL: String
)

data class NotificationUser(
    val invite: Invite,
    val userId: String
)

data class ConversationId(
    val id: String,
    val kind: String,
    val message: String
)
