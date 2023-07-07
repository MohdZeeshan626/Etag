package com.max360group.cammax360.repository.room.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.devstory.generalhome.repository.room.DBConstants
import com.max360group.cammax360.repository.models.AccountList
@Dao
interface MembersDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMembers(members:List<AccountList>)

    @Query("SELECT * FROM ${DBConstants.TABLE_MEMBERS}")
     fun getAllMembers():List<AccountList>
}