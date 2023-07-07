package com.max360group.cammax360.repository.preferences

import android.content.Context
import android.content.SharedPreferences
import com.max360group.cammax360.utils.ApplicationGlobal
import com.google.gson.Gson
import com.max360group.cammax360.repository.models.*

/**
 * Created by Mukesh on 20/7/18.
 */
class UserPrefsManager(context: Context) {

    private val mSharedPreferences: SharedPreferences
    private val mEditor: SharedPreferences.Editor

    companion object {
        // SharedPreference Keys
        private const val PREFS_FILENAME = "Kaimzz"
        private const val PREFS_MODE = 0
        private const val PREFS_USER = "user"
        private const val PREFS_IS_LOGINED = "isLogined"
        private const val PREFS_ACCESS_TOKEN = "accessToken"
        private const val PREFS_ACCOUNT_ID = "accountId"
        private const val PREFS_USER_ID = "userId"
        private const val PREFS_DELETED_JOB_ID = "jobIds"
        private const val PREFS_DELETED_MEDIA_ID = "mediaIds"
        private const val PREFS_DELETED_MEMBERS_ID = "membersId"
        private const val PREFS_DELETED_OWNERS_ID = "ownersId"
        private const val PREFS_DELETED_PROPERTIES_ID = "propertiesId"
    }

    init {
        mSharedPreferences = context.getSharedPreferences(PREFS_FILENAME, PREFS_MODE)
        mEditor = mSharedPreferences.edit()
    }

    fun clearUserPrefs() {
        ApplicationGlobal.accessToken = ""
        mEditor.clear()
        mEditor.apply()
    }

    val isLogined: Boolean
        get() = mSharedPreferences.getBoolean(PREFS_IS_LOGINED, false)

    fun saveUserSession(isRememberMe: Boolean = true, userData: UserData) {
        if (isRememberMe) {
            mEditor.putBoolean(PREFS_IS_LOGINED, isRememberMe)
        }
        mEditor.putString(PREFS_ACCESS_TOKEN, userData.auth!!.token)
        ApplicationGlobal.accessToken ="Bearer "+ userData.auth!!.token!!
        setAccount(userData.user!!.accounts!![0].primaryUserId?.id.toString())

        userData.user?.let { user ->
            mEditor.putString(PREFS_USER, Gson().toJson(user))
        }
        mEditor.apply()
    }

    val loginedUser: User?
        get() = Gson().fromJson(
            mSharedPreferences.getString(PREFS_USER, ""),
            User::class.java
        )

    fun updateUserData(user: User?) {
        if (null != user) {
            mEditor.putString(PREFS_USER, Gson().toJson(user))
            mEditor.apply()
        }
    }

    fun saveUserId(userId: String?) {
        mEditor.putString(PREFS_USER_ID, userId)
        mEditor.commit()
    }

    fun saveDeletedJobIds(mDeletedJobs: DeletedJobs) {
        if (null != mDeletedJobs) {
            mEditor.putString(PREFS_DELETED_JOB_ID, Gson().toJson(mDeletedJobs))
            mEditor.apply()
        }
    }

    fun saveDeletedPropertiesIds(mDeletedProperties: DeletedProperties) {
        if (null != mDeletedProperties) {
            mEditor.putString(PREFS_DELETED_PROPERTIES_ID, Gson().toJson(mDeletedProperties))
            mEditor.apply()
        }
    }

    fun saveDeletedMembersIds(mLocalDeletedMembers: LocalDeletedMembers) {
        if (null != mLocalDeletedMembers) {
            mEditor.putString(PREFS_DELETED_MEMBERS_ID, Gson().toJson(mLocalDeletedMembers))
            mEditor.apply()
        }
    }

    fun saveDeletedOwnersIds(mDeletedOwners: DeletedOwners) {
        if (null != mDeletedOwners) {
            mEditor.putString(PREFS_DELETED_OWNERS_ID, Gson().toJson(mDeletedOwners))
            mEditor.apply()
        }
    }

    fun saveDeletedMediaIds(mDeletedMedia: DeletedMedia) {
        if (null != mDeletedMedia) {
            mEditor.putString(PREFS_DELETED_MEDIA_ID, Gson().toJson(mDeletedMedia))
            mEditor.apply()
        }
    }

    val getJobDeletedIds: DeletedJobs?
        get() = Gson().fromJson(
            mSharedPreferences.getString(PREFS_DELETED_JOB_ID, ""),
            DeletedJobs::class.java
        )

    val getPropertyDeletedIds: DeletedProperties?
        get() = Gson().fromJson(
            mSharedPreferences.getString(PREFS_DELETED_PROPERTIES_ID, ""),
            DeletedProperties::class.java
        )

    val getOwnersDeletedIds: DeletedOwners?
        get() = Gson().fromJson(
            mSharedPreferences.getString(PREFS_DELETED_OWNERS_ID, ""),
            DeletedOwners::class.java
        )

    val getMediaDeletedIds: DeletedMedia?
        get() = Gson().fromJson(
            mSharedPreferences.getString(PREFS_DELETED_MEDIA_ID, ""),
            DeletedMedia::class.java
        )

    val getMembersDeletedIds: LocalDeletedMembers?
        get() = Gson().fromJson(
            mSharedPreferences.getString(PREFS_DELETED_MEMBERS_ID, ""),
            LocalDeletedMembers::class.java
        )

    fun setAccount(accountId:String) {
        if (null != accountId) {
            mEditor.putString(PREFS_ACCOUNT_ID, accountId)
            mEditor.apply()
            ApplicationGlobal.accountId =accountId
        }
    }

    val getUserId: String
        get() = mSharedPreferences.getString(PREFS_USER_ID, "") ?: ""

    val getAccount: String
        get() = mSharedPreferences.getString(PREFS_ACCOUNT_ID, "") ?: ""

    val accessToken: String
        get() = mSharedPreferences.getString(PREFS_ACCESS_TOKEN, "") ?: ""

}