package com.max360group.cammax360.repository.room.daos

import androidx.room.*
import com.devstory.generalhome.repository.room.DBConstants
import com.max360group.cammax360.repository.models.Address
import com.max360group.cammax360.repository.models.Job
import com.max360group.cammax360.repository.models.UserOwner
import com.max360group.cammax360.repository.models.UserX

@Dao
interface OwnersDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOwner(mOwner: List<UserOwner>)

    @Query("SELECT * FROM ${DBConstants.TABLE_OWNERS}")
    fun getAllOwner(): List<UserOwner>

    @Query("SELECT * FROM ${DBConstants.TABLE_OWNERS} WHERE firstName LIKE '%' || :query || '%'")
    fun searchOwner(query: String): List<UserOwner>

    @Query("DELETE FROM ${DBConstants.TABLE_OWNERS} WHERE ownerLocalId = :ownerId")
    fun deleteOwner(ownerId: Int)

    @Query("DELETE FROM  ${DBConstants.TABLE_OWNERS}")
    fun deleteAllOwner()

    @Query("SELECT * FROM ${DBConstants.TABLE_OWNERS} WHERE ownerLocalId =:ownerId")
    fun getSingleOwnerId(ownerId: Int): UserOwner

    @Query("UPDATE ${DBConstants.TABLE_OWNERS} SET isActive=:isActive , isUpdateActiveInLocal=:isUpdateInLocal   WHERE ownerLocalId=:mOwnerId")
    suspend fun updateState(
        mOwnerId: Int,
        isActive: Boolean,
        isUpdateInLocal: Boolean = true
    )
}