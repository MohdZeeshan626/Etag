package com.max360group.cammax360.repository.room.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.devstory.generalhome.repository.room.DBConstants
import com.max360group.cammax360.repository.models.JobMedia
import com.max360group.cammax360.repository.models.JobMediaList
import com.max360group.cammax360.views.utils.JobsConstants.JOB_KIND_VIDEO

@Dao
interface MediaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedia(mMedia: List<JobMediaList>)

    @Query("SELECT * FROM ${DBConstants.TABLE_MEDIA} WHERE jobLocalId=:jobLocalId AND kind=:kind  ")
    fun getMediaByKindByLocalId(jobLocalId: String, kind: String = ""): List<JobMediaList>

    @Query("SELECT * FROM ${DBConstants.TABLE_MEDIA} WHERE jobId=:jobId AND kind=:kind  ")
    fun getMediaByKindByJobId(jobId: String, kind: String = ""): List<JobMediaList>

    @Query("SELECT * FROM ${DBConstants.TABLE_MEDIA} WHERE kind=:kind")
    fun getMediaByKind(kind: String = ""): List<JobMediaList>

    @Query("SELECT * FROM ${DBConstants.TABLE_MEDIA} WHERE mediaLocalId=:id")
    fun getMediaById(id: String): JobMediaList

    @Query("DELETE FROM ${DBConstants.TABLE_MEDIA} WHERE mediaLocalId = :mMediaLocalId")
    fun deleteMedia(mMediaLocalId: String)

    @Query("DELETE FROM ${DBConstants.TABLE_MEDIA} WHERE jobId = :mMediaJobId")
    fun deleteMediaByJobId(mMediaJobId: String)

    @Query("UPDATE ${DBConstants.TABLE_MEDIA} SET name=:name,tags=:tags,medias=:media WHERE mediaLocalId=:id")
    suspend fun updateMediaInfo(
        id: Int,
        name: String,
        tags: List<String>,
        media: ArrayList<JobMedia>
    )

    @Query("UPDATE ${DBConstants.TABLE_MEDIA} SET jobId=:mJobId WHERE jobLocalId=:mJobLocalId")
    fun updateJobIdInMedia(
        mJobId: String,
        mJobLocalId: String
    )

    @Query("SELECT * FROM ${DBConstants.TABLE_MEDIA}")
    fun getAllMedia(): List<JobMediaList>
}