package com.max360group.cammax360.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.interactors.JobsInteractor
import com.max360group.cammax360.repository.models.*
import com.max360group.cammax360.repository.networkrequests.NetworkRequestCallbacks
import com.max360group.cammax360.repository.networkrequests.RetrofitRequest
import com.max360group.cammax360.repository.room.CamMaxRoomDatabase
import com.max360group.cammax360.views.adapters.JobsAdapter.Companion.LIMIT
import com.max360group.cammax360.views.fragments.SelectLocationFragment
import kotlinx.coroutines.launch
import retrofit2.Response
import java.util.concurrent.CopyOnWriteArrayList

class JobsViewModel(application: Application) : BaseViewModel(application) {

    private val mJobsInteractor by lazy {
        JobsInteractor()
    }

    private var jobsListing = MutableLiveData<List<Job>>()
    private var moduleUserList = MutableLiveData<List<AccountList>>()
    private var mSelectedUserList = mutableListOf<UserDetail>()
    private var isJobCreated = MutableLiveData<Boolean>()
    private var isRoleSaved = MutableLiveData<Boolean>()
    private var mRolesList = MutableLiveData<List<RolesList>>()
    private var mJob = Job()
    private var mJobSyncCount = 1
    private var isStartMediaSyncing = MutableLiveData<Boolean>()
    private var jobSyncValue = MutableLiveData<JobsSyncValueModel>()
    private var jobMediaCunt = MutableLiveData<JobsData>()

    private val mJobMediaViewModel by lazy {
        ViewModelProvider(getApplication()).get(JobMediaViewModel::class.java)
    }

    fun getJobs(
        skip: Int,
        sort: String,
        search: String = ""
    ) {
        if (skip == 0) {
            isShowSwipeRefreshLayout.value = true
        }
        mCompositeDisposable.add(
            mJobsInteractor.getJobs(skip, LIMIT, sort, search,
                object :
                    NetworkRequestCallbacks {
                    override fun onSuccess(response: Response<*>) {
                        try {
                            isShowSwipeRefreshLayout.value = false
                            val pojoNetworkResponse =
                                RetrofitRequest.checkForResponseCode(response.code())
                            when {
                                pojoNetworkResponse.isSuccess && null != response.body() -> {
                                    val mResponse =
                                        response.body() as JobsListingResponseModel
                                    jobsListing.value = mResponse.data.list!!
                                    jobMediaCunt.value = mResponse.data

                                    //Save data in database
                                    saveJobsInDataBase(mResponse.data)
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
                        isShowSwipeRefreshLayout.value = false
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

    fun getModuleUsers(
        moduleKind: String,
        showLoader: Boolean = true
    ) {
        isShowLoader.value = showLoader
        mCompositeDisposable.add(
            mJobsInteractor.getModuleUsers(moduleKind,
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
                                        response.body() as GetUserModuleWiseResponseMode

                                    moduleUserList.value = mResponse.data.data!!

                                    //Save members in database
                                    saveMembersInDataBase(mResponse.data.data)
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

    fun createRole(
        mRoleName: String,
        mJob: Jobs
    ) {
        when {
            mRoleName.isBlank() -> errorHandler.value = ErrorHandler.EMPTY_START_DATE

            else -> {
                isShowLoader.value = true
                mCompositeDisposable.add(
                    mJobsInteractor.saveAsRole(CreateRoleRequestModel(
                        permissions = mJob,
                        name = mRoleName
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
                                            isRoleSaved.value = true
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

    fun getJobRole(
        mRoleKind: String
    ) {
        isShowLoader.value = true
        mCompositeDisposable.add(
            mJobsInteractor.getJobRole(mRoleKind,
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
                                        response.body() as RolesListResponseModel

                                    mRolesList.value = mResponse.data.list

                                    //Save roles in room database
                                    saveRolesInLocal(mResponse.data.list)
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

    fun createJob(
        title: String,
        address: String,
        state: String,
        city: String,
        mLat: Double,
        mLong: Double,
        startDate: String,
        endDate: String,
        usersList: List<AccountList>,
        country: String,
        propertyId: String,
        propertyUnitId: String,
        showLoader: Boolean = true,
        mJobLocalId: Int = 0,
        mLocalDataSize: Int = 0
    ) {
        mSelectedUserList.clear()
        for (i in usersList.indices) {
            val mUserDetail = UserDetail()
            mUserDetail.email = usersList[i].email
            mUserDetail.name = usersList[i].firstName

            mUserDetail.permissions = usersList[i].accounts!![0].permissions.jobs!!
            mSelectedUserList.add(mUserDetail)
        }
        val coordinates = ArrayList<Double>()
        coordinates.add(mLat)
        coordinates.add(mLong)

        when {
            startDate.isBlank() -> errorHandler.value = ErrorHandler.EMPTY_START_DATE
            endDate.isBlank() -> errorHandler.value = ErrorHandler.EMPTY_END_DATE

            else -> {
                if (showLoader) {
                    isShowLoader.value = true
                }
                mCompositeDisposable.add(
                    mJobsInteractor.createJob(CreateJobRequestModel(
                        propertyId,
                        propertyUnitId,
                        AddressRequest(
                            city = city,
                            country = country,
                            latitude = mLat,
                            longitude = mLong,
                            state = state,
                            formatted = address,
                            line1 = city,
                            location = Coordinates(coordinates = coordinates)
                        ), endDate, startDate, title, mSelectedUserList
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
                                                response.body() as CreateJobResponseModel

                                            successMessage.value = mResponse.message
                                            isJobCreated.value = true

                                            //Save jobId in local media created by this job
                                            val id = mJobLocalId
                                            if (id != 0) {
                                                CamMaxRoomDatabase.getDatabase(getApplication())
                                                    .media().updateJobIdInMedia(
                                                        mResponse.data.job.id, id.toString()
                                                    )
                                            }
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

    fun createJobInLocal(
        title: String,
        address: String,
        state: String,
        city: String,
        mLat: Double,
        mLong: Double,
        startDate: String,
        endDate: String,
        usersList: List<AccountList>,
        country: String,
        propertyId:String,
        propertyUnitId:String
    ) {
        mSelectedUserList.clear()
        for (i in usersList.indices) {
            val mUserDetail = UserDetail()
            mUserDetail.email = usersList[i].email
            mUserDetail.name = usersList[i].firstName


            mUserDetail.permissions = usersList[i].accounts!![0].permissions.jobs!!
            mSelectedUserList.add(mUserDetail)
        }

        //Get jobs data from database
        val jobsData =
            CamMaxRoomDatabase.getDatabase(getApplication()).jobsDao().getAllJobs()
        //Save new jobs in database
        val mJobsList = mutableListOf<Job>()
        mJobsList.addAll(jobsData)

        viewModelScope.launch {
            when {
                startDate.isBlank() -> errorHandler.value = ErrorHandler.EMPTY_START_DATE
                endDate.isBlank() -> errorHandler.value = ErrorHandler.EMPTY_END_DATE

                else -> {
                    val coordinates = mutableListOf<Double>()
                    val mUsersList = ArrayList<UserX>()
                    coordinates.add(mLat)
                    coordinates.add(mLong)

                    mJob.propertyId = propertyId
                    mJob.propertyUnitId = propertyUnitId
                    mJob.title = title
                    mJob.startDt = startDate
                    mJob.endDt = endDate
                    mJob.isSavedInServer = false
                    mJob.address = Address(
                        city = city,
                        country = country,
                        formatted = address,
                        line1 = city,
                        state = state,
                        location = Location(
                            coordinates = coordinates
                        ),
                        propertyId = SelectLocationFragment.propertyId,
                        propertyUnitId = SelectLocationFragment.propertyUnitId
                    )

                    for (i in mSelectedUserList.indices) {
                        val mUserX = UserX()
                        mUserX.permissions = mSelectedUserList[i].permissions
                        mUserX.userId = UserId(
                            email = mSelectedUserList[i].email,
                            firstName = mSelectedUserList[i].name,
                            lastName = mSelectedUserList[i].name,
                            id = usersList[i].id
                        )
                        mUsersList.add(mUserX)
                    }
                    mJob.users = mUsersList
                    mJobsList.add(mJob)


                    //Get jobs data from database
                    var jobsData =
                        CamMaxRoomDatabase.getDatabase(getApplication()).jobsDao()
                            .insertJob(
                                mJobsList
                            )
                    isJobCreated.value = true
                }
            }
        }
    }

    fun createRoleInLocal(name: String, mPermission: Jobs) {
        viewModelScope.launch {
            var mRoleList = ArrayList<RolesList>()
            var mRole = RolesList()
            mRole.name = name
            mRole.permissions = mPermission
            mRole.isSavedInServer = false
            mRoleList.add(mRole)
            CamMaxRoomDatabase.getDatabase(getApplication()).rolesDao().insertRoles(mRoleList)
            isRoleSaved.value = true
        }
    }

    fun saveMembersInDataBase(data: List<AccountList>?) {
        viewModelScope.launch {
            CamMaxRoomDatabase.getDatabase(getApplication()).membersDao().insertMembers(
                data!!
            )
        }
    }

    fun saveJobsInDataBase(mJobsData: JobsData) {
        with(mJobsData) {
            viewModelScope.launch {
                CamMaxRoomDatabase.getDatabase(getApplication()).jobsDao()
                    .deleteAllJobs()
                CamMaxRoomDatabase.getDatabase(getApplication()).jobsDao()
                    .insertJob(mJobsData.list!!)
            }
        }

    }

    fun saveRolesInLocal(list: List<RolesList>) {
        viewModelScope.launch {
            CamMaxRoomDatabase.getDatabase(getApplication()).rolesDao().insertRoles(
                list
            )
        }
    }

    fun getJobsFromDataBase() {
        val mJobs = CamMaxRoomDatabase.getDatabase(getApplication()).jobsDao().getAllJobs()
        if (mJobs != null) {
            jobsListing.value = mJobs
            isShowSwipeRefreshLayout.value = false

        } else {
            errorHandler.value = ErrorHandler.NO_INTERNET
        }
    }

    fun getAllRolesFromLocal() {
        val mRolesData = CamMaxRoomDatabase.getDatabase(getApplication()).rolesDao().getAllRoles()
        if (mRolesData != null) {
            if (mRolesData.isNotEmpty()) {
                mRolesList.value = mRolesData
            }
        }
    }

    fun searchJobs(query: String) {
        val mJobs = CamMaxRoomDatabase.getDatabase(getApplication()).jobsDao().searchJobs(query)
        if (mJobs != null) {
            jobsListing.value = mJobs
            isShowSwipeRefreshLayout.value = false

        } else {
            errorHandler.value = ErrorHandler.NO_INTERNET
        }
    }


    fun getAllMembersFromDatabase() {
        val mJobs = CamMaxRoomDatabase.getDatabase(getApplication()).membersDao()
            .getAllMembers()
        if (mJobs != null) {
            moduleUserList.value = mJobs
            isShowSwipeRefreshLayout.value = false

        } else {
            errorHandler.value = ErrorHandler.NO_INTERNET
        }
    }

    fun uploadJobsLocalToServer() {
        val mJobsData = CamMaxRoomDatabase.getDatabase(getApplication()).jobsDao().getNotSyncJobs()
        val mUserList = ArrayList<AccountList>()

        if (mJobsData.isNotEmpty()) {
            for (i in mJobsData.indices) {
                mUserList.clear()
                if (!mJobsData[i].isSavedInServer) {
                    for (j in mJobsData[i].users!!.indices) {
                        val mUserDetail = AccountList()
                        mUserDetail.email = mJobsData[i].users!![j].userId.email
                        mUserDetail.firstName = mJobsData[i].users!![j].userId.firstName
                        mUserDetail.lastName = mJobsData[i].users!![j].userId.lastName
                        mUserDetail.accounts!!.add(AccountDetail(permissions = Permissions(jobs = mJobsData[i].users!![j].permissions)))
                        mUserList.add(mUserDetail)
                    }

                    //Call Api
                    createJob(
                        mJobsData[i].title,
                        mJobsData[i].address.formatted,
                        mJobsData[i].address.state,
                        mJobsData[i].address.city,
                        mJobsData[i].address.location.coordinates!![0],
                        mJobsData[i].address.location.coordinates!![1],
                        mJobsData[i].startDt,
                        mJobsData[i].endDt,
                        mUserList,
                        mJobsData[i].address.country,
                        mJobsData[i].propertyId!!,
                        mJobsData[i].propertyUnitId!!,
                        false,
                        mJobsData[i].jobLocalId,
                        mJobsData.size
                    )

                    //Delete job from local after syn on server
                    CamMaxRoomDatabase.getDatabase(getApplication()).jobsDao()
                        .deleteJob(mJobsData[i].jobLocalId.toString())
                }
            }
        }
        isStartMediaSyncing.value = true
    }

    fun uploadRolesLocalToServer() {
        val mJobRoles = CamMaxRoomDatabase.getDatabase(getApplication()).rolesDao().getAllRoles()
        if (mJobRoles != null) {
            if (mJobRoles.isNotEmpty()) {
                for (i in mJobRoles.indices) {
                    if (!mJobRoles[i].isSavedInServer) {
                        //Call api
                        createRole(
                            mJobRoles[i].name,
                            mJobRoles[i].permissions
                        )
                    }
                }
                //Delete all roles after sync in server
                CamMaxRoomDatabase.getDatabase(getApplication()).rolesDao().getAllRoles()
            }
        }
    }


    fun filterJob(isOrder: Int, type: String) {
        if (type == "title") {
            val mJobs = CamMaxRoomDatabase.getDatabase(getApplication()).jobsDao()
                .filterJobsByTitle(isOrder)
            if (mJobs != null) {
                jobsListing.value = mJobs
                isShowSwipeRefreshLayout.value = false

            } else {
                errorHandler.value = ErrorHandler.NO_INTERNET
            }
        } else {
            val mJobs = CamMaxRoomDatabase.getDatabase(getApplication()).jobsDao()
                .filterJobsByDueDate(isOrder)
            if (mJobs != null) {
                jobsListing.value = mJobs
                isShowSwipeRefreshLayout.value = false

            } else {
                errorHandler.value = ErrorHandler.NO_INTERNET
            }
        }
    }


    fun onGetJobs() = jobsListing
    fun onGetModuleUsers() = moduleUserList
    fun onJobCreated() = isJobCreated
    fun onRoleCreated() = isRoleSaved
    fun onGetRollsList() = mRolesList
    fun onMediaSyncingStart() = isStartMediaSyncing
    fun onMediaCount() = jobMediaCunt

}