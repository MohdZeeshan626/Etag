package com.max360group.cammax360.repository.room.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.devstory.generalhome.repository.room.DBConstants
import com.max360group.cammax360.repository.models.User

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM ${DBConstants.TABLE_USERS}")
    fun getUser(): User?

    @Query("UPDATE ${DBConstants.TABLE_USERS} SET isLocalUpdate=:isLocalUpdate,firstName=:firstName,lastName=:lastName,picURL=:image")
   suspend fun updateUser(
        firstName: String,
        lastName: String, image: String,isLocalUpdate:Boolean
    )


    @Query("SELECT * FROM ${DBConstants.TABLE_USERS} WHERE email = :email")
    fun getUserByEmailPassword(email: String): User?

    @Query("UPDATE ${DBConstants.TABLE_USERS} SET isLoggedIn = :isLoggedIn")
    fun isRememberUpdate(isLoggedIn: Boolean)

}