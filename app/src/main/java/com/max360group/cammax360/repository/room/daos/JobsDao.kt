package com.max360group.cammax360.repository.room.daos

import androidx.room.*
import com.devstory.generalhome.repository.room.DBConstants
import com.max360group.cammax360.repository.models.Address
import com.max360group.cammax360.repository.models.Job
import com.max360group.cammax360.repository.models.UserX

@Dao
interface JobsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJob(mJob: List<Job>)

    @Query("SELECT * FROM ${DBConstants.TABLE_JOBS}")
    fun getAllJobs(): List<Job>

    @Query("SELECT * FROM ${DBConstants.TABLE_JOBS} WHERE title LIKE '%' || :query || '%'")
    fun searchJobs(query: String): List<Job>

    @Query("DELETE FROM ${DBConstants.TABLE_JOBS} WHERE jobLocalId = :jobId")
    fun deleteJob(jobId: String)

    @Query("SELECT * FROM ${DBConstants.TABLE_JOBS} ORDER BY CASE WHEN :isAsc=1 THEN title END ASC,CASE WHEN :isAsc=-1 THEN title END DESC")
    fun filterJobsByTitle(isAsc: Int): List<Job>

    @Query("SELECT * FROM ${DBConstants.TABLE_JOBS} ORDER BY CASE WHEN :isAsc=1 THEN title END ASC,CASE WHEN :isAsc=-1 THEN endDt END DESC")
    fun filterJobsByDueDate(isAsc: Int): List<Job>

    @Query("DELETE FROM  ${DBConstants.TABLE_JOBS}")
    fun deleteAllJobs()

    @Query("SELECT * FROM ${DBConstants.TABLE_JOBS} WHERE jobLocalId =:mJobLocalId")
    fun getSingleJobByLocalId(mJobLocalId: String): Job

    @Query("SELECT * FROM ${DBConstants.TABLE_JOBS} WHERE id =:mJobId")
    fun getSingleJobByServerId(mJobId: String): Job

    @Query("SELECT * FROM ${DBConstants.TABLE_JOBS} WHERE isSavedInServer =:isSavedInServer")
    fun getNotSyncJobs(isSavedInServer:Boolean=false): List<Job>

    @Query("UPDATE ${DBConstants.TABLE_JOBS} SET isUpdateInLocal=:isUpdateInLocal ,title=:title,address=:mAddress,startDt=:startDate,endDt=:endDate WHERE jobLocalId=:mJobId")
    fun updateDetail(
        mJobId: Int,
        title: String,
        mAddress: Address,
        startDate: String,
        endDate: String,
        isUpdateInLocal: Boolean
    )

    @Query("UPDATE ${DBConstants.TABLE_JOBS} SET users=:mUserX WHERE jobLocalId=:mJobId")
    suspend fun updateMembers(
        mJobId: String,
        mUserX: List<UserX>
    )

}