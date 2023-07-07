package com.max360group.cammax360.repository.room.daos

import androidx.room.*
import com.devstory.generalhome.repository.room.DBConstants
import com.max360group.cammax360.repository.models.*

@Dao
interface PropertiesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProperties(mOwner: List<PropertyDetail>)

    @Query("SELECT * FROM ${DBConstants.TABLE_PROPERTIES}")
    fun getAllProperties(): List<PropertyDetail>

    @Query("DELETE FROM  ${DBConstants.TABLE_PROPERTIES}")
    fun deleteAllProperties()

    @Query("SELECT * FROM ${DBConstants.TABLE_PROPERTIES} WHERE propertyLocalId =:propertyId")
    fun getPropertyDetail(propertyId: Int): PropertyDetail

    @Query("DELETE FROM ${DBConstants.TABLE_PROPERTIES} WHERE propertyLocalId = :propertyId")
    fun deleteProperty(propertyId:Int)

    @Query("SELECT * FROM ${DBConstants.TABLE_PROPERTIES} WHERE name LIKE '%' || :value || '%'")
    fun searchProperties(value:String):List<PropertyDetail>

    @Query("UPDATE ${DBConstants.TABLE_PROPERTIES} SET isActive=:isActive , isUpdateStateInLocal=:isStateInLocal   WHERE propertyLocalId=:mPropertyId")
    suspend fun updatePropertyState(
        mPropertyId: Int,
        isActive: Boolean,
        isStateInLocal: Boolean = true
    )

}