package com.max360group.cammax360.repository.room.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.devstory.generalhome.repository.room.DBConstants
import com.max360group.cammax360.repository.models.JobMediaList
import com.max360group.cammax360.repository.models.RolesList
import com.max360group.cammax360.views.utils.JobsConstants.JOB_KIND_VIDEO

@Dao
interface RolesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoles(mMedia:List<RolesList>)

    @Query("SELECT * FROM ${DBConstants.TABLE_ROLES}")
    fun getAllRoles():List<RolesList>

    @Query("DELETE FROM  ${DBConstants.TABLE_ROLES}")
    fun deleteAllRoles()

}