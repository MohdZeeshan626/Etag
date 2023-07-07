package com.max360group.cammax360.repository.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.devstory.generalhome.repository.room.DBConstants
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.Job


@Parcelize
data class RolesListResponseModel(
    val `data`: RolesData,
    val message: String,
    val statusCode: Int
):Parcelable


@Parcelize
data class RolesData(
    val count: Int,
    val list: List<RolesList>
):Parcelable


@Entity(tableName = DBConstants.TABLE_ROLES,indices = [Index(value = ["id","roleLocalId"], unique = true)])
@Parcelize
data class RolesList(
    @PrimaryKey(autoGenerate = true)
    var roleLocalId:Int=0,
    @ColumnInfo(name = "id")  var id: String = "",
    val createdAt: String="",
    val creatorId: String="",
    val description: String="",
    val isActive: Boolean=false,
    val isDeleted: Boolean=false,
    var isSavedInServer: Boolean=true,
    val kind: String="",
    var name: String="",
    var permissions: Jobs=Jobs(),
    val primaryUserId: String="",
    val updatedAt: String=""
):Parcelable

