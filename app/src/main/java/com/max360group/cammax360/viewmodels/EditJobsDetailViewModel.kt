package com.max360group.cammax360.viewmodels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.interactors.EditJobDetailInteractor
import com.max360group.cammax360.repository.models.*
import com.max360group.cammax360.repository.models.model.EditJobMemberPermissions
import com.max360group.cammax360.repository.networkrequests.NetworkRequestCallbacks
import com.max360group.cammax360.repository.networkrequests.RetrofitRequest
import com.max360group.cammax360.repository.room.CamMaxRoomDatabase
import kotlinx.coroutines.launch
import retrofit2.Response

class EditJobsDetailViewModel(application: Application) : BaseViewModel(application) {

    private val mJobsInteractor by lazy {
        EditJobDetailInteractor()
    }

    private var mJobUpdateSuccess = MutableLiveData<Boolean>()
    private var mSelectedUserList = mutableListOf<UserX>()

    fun updateJob(
        mJobId: String,
        title: String,
        address: String,
        state: String,
        city: String,
        mLat: Double,
        mLong: Double,
        startDate: String,
        endDate: String,
        country: String,
        propertyId: String,
        propertyUnitId: String
    ) {
        when {
            startDate.isBlank() -> errorHandler.value = ErrorHandler.EMPTY_START_DATE
            endDate.isBlank() -> errorHandler.value = ErrorHandler.EMPTY_END_DATE
            else -> {
                val coordinates = ArrayList<Double>()
                coordinates.add(mLat)
                coordinates.add(mLong)
                var mPropertyId: String? = null
                if (propertyId.isNotBlank()) {
                    mPropertyId = propertyId
                }

                isShowLoader.value = true
                mCompositeDisposable.add(
                    mJobsInteractor.editJobDescription(mJobId, UpdateJobDescriptionRequestModel(
                        AddressRequest(
                            city = city,
                            country = country,
                            latitude = mLat,
                            longitude = mLong,
                            state = state,
                            formatted = address,
                            line1 = city,
                            location = Coordinates(coordinates = coordinates)
                        ), endDate, startDate, title,
                        propertyId = mPropertyId, propertyUnitId
                    ),
                        object :
                            NetworkRequestCallbacks {
                            override fun onSuccess(response: Response<*>) {
                                try {
                                    isShowLoader.value = false
                                    val pojoNetworkResponse =
                                        RetrofitRequest.checkForResponseCode(response.code())
                                    when {
                                        pojoNetworkResponse.isSuccess && null != response.body() -> {
                                            val mResponse =
                                                response.body() as SimpleSuccessResponse
                                            successMessage.value = mResponse.message
                                            mJobUpdateSuccess.value = true
                                        }

                                        pojoNetworkResponse.isSessionExpired -> {
                                            isSessionExpired.value = true
                                        }


                                        else -> {
                                            retrofitErrorMessage
                                                .postValue(
                                                    RetrofitErrorMessage(
                                                        errorMessage =
                                                        RetrofitRequest.getErrorMessage(
                                                            response.errorBody()!!
                                                        )
                                                    )
                                                )
                                        }
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    retrofitErrorMessage
                                        .postValue(
                                            RetrofitErrorMessage(
                                                errorResId =
                                                R.string.retrofit_failure
                                            )
                                        )
                                }
                            }

                            override fun onError(t: Throwable) {
                                t.printStackTrace()
                                isShowLoader.value = false
                                retrofitErrorMessage
                                    .postValue(
                                        RetrofitErrorMessage(
                                            errorResId =
                                            RetrofitRequest.getRetrofitError(t)
                                        )
                                    )
                            }

                        })
                )
            }
        }
    }

    fun editPermissions(
        mJobId: String,
        mUserId: String,
        mJobs: Jobs
    ) {
        isShowLoader.value = true
        mCompositeDisposable.add(
            mJobsInteractor.editPermissions(mJobId, EditJobMemberPermissions(
                userId = mUserId,
                permissions = mJobs
            ),
                object :
                    NetworkRequestCallbacks {
                    override fun onSuccess(response: Response<*>) {
                        try {
                            isShowLoader.value = false
                            val pojoNetworkResponse =
                                RetrofitRequest.checkForResponseCode(response.code())
                            when {
                                pojoNetworkResponse.isSuccess && null != response.body() -> {
                                    val mResponse =
                                        response.body() as SimpleSuccessResponse
                                    successMessage.value = mResponse.message
                                    mJobUpdateSuccess.value = true
                                }

                                pojoNetworkResponse.isSessionExpired -> {
                                    isSessionExpired.value = true
                                }

                                else -> {
                                    retrofitErrorMessage
                                        .postValue(
                                            RetrofitErrorMessage(
                                                errorMessage =
                                                RetrofitRequest.getErrorMessage(
                                                    response.errorBody()!!
                                                )
                                            )
                                        )
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            retrofitErrorMessage
                                .postValue(
                                    RetrofitErrorMessage(
                                        errorResId =
                                        R.string.retrofit_failure
                                    )
                                )
                        }
                    }

                    override fun onError(t: Throwable) {
                        t.printStackTrace()
                        isShowLoader.value = false
                        retrofitErrorMessage
                            .postValue(
                                RetrofitErrorMessage(
                                    errorResId =
                                    RetrofitRequest.getRetrofitError(t)
                                )
                            )
                    }

                })
        )
    }

    fun deleteJobMember(
        mJobId: String,
        mUserId: String
    ) {
        isShowLoader.value = true
        mCompositeDisposable.add(
            mJobsInteractor.deleteJobMember(mJobId, mUserId,
                object :
                    NetworkRequestCallbacks {
                    override fun onSuccess(response: Response<*>) {
                        try {
                            isShowLoader.value = false
                            val pojoNetworkResponse =
                                RetrofitRequest.checkForResponseCode(response.code())
                            when {
                                pojoNetworkResponse.isSuccess && null != response.body() -> {
                                    val mResponse =
                                        response.body() as SimpleSuccessResponse
                                    successMessage.value = mResponse.message
                                    mJobUpdateSuccess.value = true
                                }

                                pojoNetworkResponse.isSessionExpired -> {
                                    isSessionExpired.value = true
                                }

                                else -> {
                                    retrofitErrorMessage
                                        .postValue(
                                            RetrofitErrorMessage(
                                                errorMessage =
                                                RetrofitRequest.getErrorMessage(
                                                    response.errorBody()!!
                                                )
                                            )
                                        )
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            retrofitErrorMessage
                                .postValue(
                                    RetrofitErrorMessage(
                                        errorResId =
                                        R.string.retrofit_failure
                                    )
                                )
                        }
                    }

                    override fun onError(t: Throwable) {
                        t.printStackTrace()
                        isShowLoader.value = false
                        retrofitErrorMessage
                            .postValue(
                                RetrofitErrorMessage(
                                    errorResId =
                                    RetrofitRequest.getRetrofitError(t)
                                )
                            )
                    }

                })
        )
    }

    fun addJobMember(
        mJobId: String,
        mUsers: ArrayList<AccountList>
    ) {

        var mMembersList = ArrayList<Members>()
        for (i in mUsers.indices) {
            var mMembers = Members()
            mMembers.userId = mUsers[i].id
            mMembers.permissions = mUsers[i].accounts?.get(0)?.permissions!!.jobs!!
            mMembersList.add(mMembers)
        }

        isShowLoader.value = true
        mCompositeDisposable.add(
            mJobsInteractor.addJobMember(mJobId, AddJobMembersRequestModel(
                mMembersList
            ),
                object :
                    NetworkRequestCallbacks {
                    override fun onSuccess(response: Response<*>) {
                        try {
                            isShowLoader.value = false
                            val pojoNetworkResponse =
                                RetrofitRequest.checkForResponseCode(response.code())
                            when {
                                pojoNetworkResponse.isSuccess && null != response.body() -> {
                                    val mResponse =
                                        response.body() as SimpleSuccessResponse
                                    successMessage.value = mResponse.message
                                    mJobUpdateSuccess.value = true
                                }

                                pojoNetworkResponse.isSessionExpired -> {
                                    isSessionExpired.value = true
                                }

                                else -> {
                                    retrofitErrorMessage
                                        .postValue(
                                            RetrofitErrorMessage(
                                                errorMessage =
                                                RetrofitRequest.getErrorMessage(
                                                    response.errorBody()!!
                                                )
                                            )
                                        )
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            retrofitErrorMessage
                                .postValue(
                                    RetrofitErrorMessage(
                                        errorResId =
                                        R.string.retrofit_failure
                                    )
                                )
                        }
                    }

                    override fun onError(t: Throwable) {
                        t.printStackTrace()
                        isShowLoader.value = false
                        retrofitErrorMessage
                            .postValue(
                                RetrofitErrorMessage(
                                    errorResId =
                                    RetrofitRequest.getRetrofitError(t)
                                )
                            )
                    }
                })
        )
    }

    fun inviteJobMember(
        mJobId: String,
        name: String,
        email: String,
        permissions: Jobs
    ) {
        isShowLoader.value = true
        mCompositeDisposable.add(
            mJobsInteractor.inviteJobMember(mJobId, InviteJobMemberRequestModel(
                email,
                name,
                permissions
            ),
                object :
                    NetworkRequestCallbacks {
                    override fun onSuccess(response: Response<*>) {
                        try {
                            isShowLoader.value = false
                            val pojoNetworkResponse =
                                RetrofitRequest.checkForResponseCode(response.code())
                            when {
                                pojoNetworkResponse.isSuccess && null != response.body() -> {
                                    val mResponse =
                                        response.body() as SimpleSuccessResponse
                                    successMessage.value = mResponse.message
                                    mJobUpdateSuccess.value = true
                                }

                                pojoNetworkResponse.isSessionExpired -> {
                                    isSessionExpired.value = true
                                }

                                else -> {
                                    retrofitErrorMessage
                                        .postValue(
                                            RetrofitErrorMessage(
                                                errorMessage =
                                                RetrofitRequest.getErrorMessage(
                                                    response.errorBody()!!
                                                )
                                            )
                                        )
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            retrofitErrorMessage
                                .postValue(
                                    RetrofitErrorMessage(
                                        errorResId =
                                        R.string.retrofit_failure
                                    )
                                )
                        }
                    }

                    override fun onError(t: Throwable) {
                        t.printStackTrace()
                        isShowLoader.value = false
                        retrofitErrorMessage
                            .postValue(
                                RetrofitErrorMessage(
                                    errorResId =
                                    RetrofitRequest.getRetrofitError(t)
                                )
                            )
                    }

                })
        )
    }

    fun updateJobDetailInLocal(
        mJobId: String,
        title: String,
        address: String,
        state: String,
        city: String,
        mLat: Double,
        mLong: Double,
        startDate: String,
        endDate: String,
        country: String,
        propertyId: String,
        propertyUnitId: String
    ) {
        when {
            startDate.isBlank() -> errorHandler.value = ErrorHandler.EMPTY_START_DATE
            endDate.isBlank() -> errorHandler.value = ErrorHandler.EMPTY_END_DATE
            else -> {
                viewModelScope.launch {
                    val mAddress = Address()
                    mAddress.formatted = address
                    mAddress.city = city
                    mAddress.state = state
                    mAddress.line1 = city
                    mAddress.country = country
                    mAddress.propertyId = propertyId
                    mAddress.propertyUnitId = propertyUnitId
                    mAddress.location.coordinates = mutableListOf(mLat, mLong)

                    CamMaxRoomDatabase.getDatabase(getApplication()).jobsDao().updateDetail(
                        mJobId.toInt(), title, mAddress, startDate, endDate, true
                    )
                    mJobUpdateSuccess.value = true
                }
            }
        }
    }

    fun addMemberInDataBase(id: String, usersList: ArrayList<AccountList>) {
        val jobsData = CamMaxRoomDatabase.getDatabase(getApplication()).jobsDao()
            .getSingleJobByLocalId(id)
        if (jobsData != null) {
            viewModelScope.launch {
                mSelectedUserList.clear()
                mSelectedUserList.addAll(ArrayList<UserX>(jobsData.users!!))

                for (i in usersList.indices) {
                    var mUserX = UserX()
                    mUserX.permissions = usersList[i].accounts!![0].permissions.jobs!!
                    mUserX.userId = UserId(
                        email = usersList[i].email,
                        firstName = usersList[i].firstName,
                        lastName = usersList[i].lastName,
                        id = usersList[i].id,
                    )
                    mUserX.isAddInLocal = true
                    mSelectedUserList.add(mUserX)
                }

                CamMaxRoomDatabase.getDatabase(getApplication()).jobsDao().updateMembers(
                    id, mSelectedUserList
                )
                mJobUpdateSuccess.value = true
            }
        }
    }

    fun deleteMembersInDatabase(id: String, mUserId: String) {
        val mJobData =
            CamMaxRoomDatabase.getDatabase(getApplication()).jobsDao().getSingleJobByLocalId(id)

        viewModelScope.launch {
            if (mJobData != null) {
                var mSavedIds = ArrayList<MembersData>()

                for (users in mJobData.users!!) {
                    if (users.userId.id == mUserId) {
                        mJobData.users!!.remove(users)

                        //Update job after delete member
                        CamMaxRoomDatabase.getDatabase(getApplication()).jobsDao()
                            .updateMembers(id, mJobData.users!!)

                        //Save deleted ids for deleted from server after network back
                        if (mUserPrefsManager.getMembersDeletedIds != null) {
                            mSavedIds = mUserPrefsManager.getMembersDeletedIds!!.deletedUsers!!
                            mSavedIds.add(MembersData(mJobData.id, users.userId.id))
                        }
                        val mLocalDeletedMembers = LocalDeletedMembers()
                        mLocalDeletedMembers.deletedUsers = mSavedIds
                        mUserPrefsManager.saveDeletedMembersIds(mLocalDeletedMembers)
                        mJobUpdateSuccess.value = true
                    }
                }
            }
        }
    }

    fun editMemberPermissionInDataBase(id: String, userId: String, jobsPermission: Jobs?) {
        var jobsData =
            CamMaxRoomDatabase.getDatabase(getApplication()).jobsDao().getSingleJobByLocalId(id)
        viewModelScope.launch {
            mSelectedUserList.clear()
            mSelectedUserList.addAll(ArrayList<UserX>(jobsData.users!!))

            for (i in mSelectedUserList.indices) {
                if (mSelectedUserList[i].userId.id == userId) {
                    mSelectedUserList[i].permissions = jobsPermission!!
                    mSelectedUserList[i].isPermissionUpdatedInLocal = true
                    break
                }
            }

            CamMaxRoomDatabase.getDatabase(getApplication()).jobsDao().updateMembers(
                id,
                mSelectedUserList
            )
            mJobUpdateSuccess.value = true
        }
    }

    fun syncUpdateJobInfoLocalToServer() {
        val jobsData = CamMaxRoomDatabase.getDatabase(getApplication()).jobsDao().getAllJobs()
        if (jobsData.isNotEmpty()) {
            for (i in jobsData.indices) {
                if (jobsData[i].isUpdateInLocal) {
                    updateJob(
                        jobsData[i].id, jobsData[i].title,
                        jobsData[i].address.formatted,
                        jobsData[i].address.state,
                        jobsData[i].address.city,
                        jobsData[i].address.location.coordinates!![0],
                        jobsData[i].address.location.coordinates!![1],
                        jobsData[i].startDt,
                        jobsData[i].endDt,
                        jobsData[i].address.country,
                        jobsData[i].address.propertyId,
                        jobsData[i].address.propertyUnitId,
                    )
                }
            }
        }
    }

    fun syncJobMemberPermissionLocalToServer() {
        val jobsData = CamMaxRoomDatabase.getDatabase(getApplication()).jobsDao().getAllJobs()
        if (jobsData.isNotEmpty()) {
            for (i in jobsData.indices) {
                for (j in jobsData[i].users!!.indices) {
                    if (jobsData[i].users!![j].isPermissionUpdatedInLocal) {
                        editPermissions(
                            jobsData[i].id,
                            jobsData[i].users!![j].userId.id,
                            jobsData[i].users!![j].permissions
                        )
                    }
                }
            }
        }
    }

    fun syncJobMemberMembersLocalToServer() {
        val jobsData = CamMaxRoomDatabase.getDatabase(getApplication()).jobsDao().getAllJobs()
        val mSelectedUsers = ArrayList<AccountList>()
        if (jobsData.isNotEmpty()) {
            for (i in jobsData.indices) {
                for (j in jobsData[i].users!!.indices) {
                    mSelectedUsers.clear()
                    if (jobsData[i].users!![j].isAddInLocal) {
                        val mAccountList = AccountList()
                        mAccountList.id = jobsData[i].users!![j].userId.id
                        mAccountList.accounts!!.add(AccountDetail(permissions = Permissions(jobs = jobsData[i].users!![j].permissions)))
                        mSelectedUsers.add(mAccountList)

                        //Call Api
                        addJobMember(jobsData[i].id, mSelectedUsers)

                        //Update flag after sync in local
                        viewModelScope.launch {
                            jobsData[i].users!![j].isAddInLocal = false
                            CamMaxRoomDatabase.getDatabase(getApplication()).jobsDao()
                                .updateMembers(
                                    jobsData[i].jobLocalId.toString(), jobsData[i].users!!
                                )
                        }

                    }
                }
            }
        }
    }

    fun deleteJobMembersLocalToServer() {
        if (mUserPrefsManager.getMembersDeletedIds != null) {
            for (i in mUserPrefsManager.getMembersDeletedIds!!.deletedUsers!!.indices) {
                deleteJobMember(
                    mUserPrefsManager.getMembersDeletedIds!!.deletedUsers!![i].mJobId,
                    mUserPrefsManager.getMembersDeletedIds!!.deletedUsers!![i].membersId
                )
            }
            //Clear the pref after sync the data
            mUserPrefsManager.saveDeletedMembersIds(LocalDeletedMembers())
        }
    }

    fun onUpdateJob() = mJobUpdateSuccess
}