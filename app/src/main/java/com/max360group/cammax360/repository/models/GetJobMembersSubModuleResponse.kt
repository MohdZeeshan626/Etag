package com.max360group.cammax360.repository.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class GetJobMembersSubModuleResponse(
    val `data`: JobSubModule,
    val message: String,
    val statusCode: Int
):Parcelable


@Parcelize
data class JobSubModule(
    val `data`: List<JobMembers>
):Parcelable

@Parcelize
data class JobMembers(
    val accounts: List<AccountJobModule>,
    val email: String,
    val firstName: String,
    val id: String,
    val lastName: String,
    val userType: Int,
    var isChecked:Boolean=false
):Parcelable

@Parcelize
data class AccountJobModule(
    val createdAt: String,
    val invite: Invite,
    val isBlocked: Boolean,
    val isDefault: Boolean,
    val permissions: Permissions,
    val primaryUserId: String,
    val updatedAt: String,
    val userType: Int
):Parcelable

