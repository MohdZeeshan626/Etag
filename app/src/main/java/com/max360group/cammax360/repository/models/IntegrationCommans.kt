package com.max360group.cammax360.repository.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.devstory.generalhome.repository.room.DBConstants

data class IntegrationCommans(
    val `data`: Integration,
    val message: String,
    val statusCode: Int
)

data class Integration(
    val count: Count,
    val list: List<IntegrationData>
)

data class Count(
    val addressTypes: Int,
    val base: Int,
    val chargeTypes: Int,
    val propertyTypes: Int,
    val serviceManagerCategories: Int,
    val serviceManagerPriorities: Int,
    val serviceManagerStatuses: Int,
    val unitTypes: Int
)

@Entity(tableName = DBConstants.TABLE_INTEGRATION_COMMONS,indices = [Index(value = ["id","integrationLocalId"], unique = true)])
data class IntegrationData(
    @PrimaryKey(autoGenerate = true)
    var integrationLocalId: Int = 0,
    @ColumnInfo(name = "id")  var id: String = "",
    val bold: Boolean,
    val color: String,
    val createdAt: String,
    val description: String,
    val isActive: Boolean,
    val isGlobal: Boolean,
    val italic: Boolean,
    val kind: String,
    val name: String,
    val underline: Boolean,
    val updatedAt: String
)