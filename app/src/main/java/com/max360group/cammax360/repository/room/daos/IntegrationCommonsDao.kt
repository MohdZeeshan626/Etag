package com.max360group.cammax360.repository.room.daos

import androidx.room.*
import com.devstory.generalhome.repository.room.DBConstants
import com.max360group.cammax360.repository.models.*

@Dao
interface IntegrationCommonsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIntegrationCommons(integrations: List<IntegrationData>)

    @Query("SELECT * FROM ${DBConstants.TABLE_INTEGRATION_COMMONS}")
    fun getAllIntegration(): List<IntegrationData>

    @Query("SELECT * FROM ${DBConstants.TABLE_INTEGRATION_COMMONS} WHERE kind=:kind")
    fun getIntegrationByKind(kind: String = ""): List<IntegrationData>

}