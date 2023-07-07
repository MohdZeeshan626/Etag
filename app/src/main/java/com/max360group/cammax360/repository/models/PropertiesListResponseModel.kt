package com.max360group.cammax360.repository.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.devstory.generalhome.repository.room.DBConstants
import kotlinx.android.parcel.Parcelize
@Parcelize
data class PropertiesListResponseModel(
    var `data`: PropertiesData,
    var message: String,
    var statusCode: Int
) : Parcelable

@Parcelize
data class PropertiesData(
        var records: List<PropertyDetail>? = mutableListOf()
) : Parcelable


@Entity(
    tableName = DBConstants.TABLE_PROPERTIES,
    indices = [Index(value = ["id", "propertyLocalId"], unique = true)]
)
@Parcelize
data class PropertyDetail(
    @PrimaryKey(autoGenerate = true)
    var propertyLocalId: Int = 0,
    @ColumnInfo(name = "id") var id: String = "",
    var access: Access=Access(),
    var addresses: List<OwnerAddressModel>?= mutableListOf(),
    var billingAddress: OwnerAddressModel=OwnerAddressModel(),
    var chargeTypes: List<String>?= mutableListOf(),
    var comments: String="",
    var createdAt: String="",
    var creatorId: String="",
    var isActive: Boolean=false,
    var isDeleted: Boolean=false,
    var name: String="",
    var pic: String="",
    var picURL: String="",
    var primaryAddress: OwnerAddressModel= OwnerAddressModel(),
    var primaryUserId: String="",
    var propertyTypes: List<String>?= mutableListOf(),
    var propertyUnits: ArrayList<UnitRecord>?= ArrayList(),
    var rm: Rm= Rm(),
    var shortName: String="",
    var squareFootage: Int=0,
    var taxId: String="",
    var updatedAt: String="",
    var userOwners: List<UserOwner>?= mutableListOf(),
    var isSyncServer:Boolean=true,
    var isUpdateStateInLocal:Boolean=false,
    var isUpdateInLocal:Boolean=false
) : Parcelable
