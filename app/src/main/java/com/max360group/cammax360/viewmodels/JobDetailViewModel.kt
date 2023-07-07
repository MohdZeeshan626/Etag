package com.max360group.cammax360.viewmodels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.interactors.JobDetailInteractor
import com.max360group.cammax360.repository.models.*
import com.max360group.cammax360.repository.networkrequests.NetworkRequestCallbacks
import com.max360group.cammax360.repository.networkrequests.RetrofitRequest
import com.max360group.cammax360.repository.room.CamMaxRoomDatabase
import com.max360group.cammax360.utils.Constants.DATE_FORMAT_DISPLAY
import com.max360group.cammax360.utils.Constants.DATE_FORMAT_SERVER_ISO
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.views.utils.JobsConstants.JOB_KIND_PHOTO
import com.max360group.cammax360.views.utils.JobsConstants.JOB_KIND_VIDEO
import kotlinx.coroutines.launch
import retrofit2.Response

class JobDetailViewModel(application: Application) : BaseViewModel(application) {

    companion object {
        const val SORT_BY_CONTRIBUTOR = 0
        const val SORT_BY_DATE = 1
    }

    private val mJobDetailInteractor by lazy {
        JobDetailInteractor()
    }

    private var mJobDetail = MutableLiveData<JobDetail>()
    private var isDeleteJob = MutableLiveData<Boolean>()
    private var mJobMediaList = MutableLiveData<List<JobMediaList>>()
    private var mFilteredJobMediaList = MutableLiveData<List<FilteredJobsMediaModel>>()
    private var mConversationList = MutableLiveData<List<ConversationList>>()
    private var mTimelineData = MutableLiveData<TimeLineData>()
    private var mCommentList = MutableLiveData<List<Comments>>()
    private var isShowShimmer = MutableLiveData<Boolean>()

    fun getJobDetail(mJobId: String, showLoader: Boolean = true) {
        if (showLoader) {
            isShowLoader.value = true
        }
        mCompositeDisposable.add(
            mJobDetailInteractor.getJobDetail(true, true, mJobId,
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
                                        response.body() as JobDetailResponseModel
                                    mJobDetail.value = mResponse.data
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

    fun getJobTimeLine(
        mJobId: String,
        mTimeLineRequestModel: TimeLineRequestModel = TimeLineRequestModel()
    ) {
        isShowShimmer.value = true
        mCompositeDisposable.add(
            mJobDetailInteractor.getJobTimeLine(mJobId, mTimeLineRequestModel,
                object :
                    NetworkRequestCallbacks {
                    override fun onSuccess(response: Response<*>) {
                        try {
                            isShowShimmer.value = false
                            val pojoNetworkResponse =
                                RetrofitRequest.checkForResponseCode(response.code())
                            when {
                                pojoNetworkResponse.isSuccess && null != response.body() -> {
                                    val mResponse =
                                        response.body() as TimelineResponseModel
                                    mTimelineData.value = mResponse.data
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
                        isShowShimmer.value = false
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

    fun deleteJob(mJobId: String) {
        isShowLoader.value = true
        mCompositeDisposable.add(
            mJobDetailInteractor.deleteJob(mJobId,
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
                                    isDeleteJob.value = true
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

    fun getJobMedia(mJobId: String, mJobLocalId: String, mediaKind: String, sortBy: Int) {
        isShowShimmer.value = true
        mCompositeDisposable.add(
            mJobDetailInteractor.getJobMedia(mJobId, mediaKind,
                object :
                    NetworkRequestCallbacks {
                    override fun onSuccess(response: Response<*>) {
                        try {
                            isShowShimmer.value = false
                            val pojoNetworkResponse =
                                RetrofitRequest.checkForResponseCode(response.code())
                            when {
                                pojoNetworkResponse.isSuccess && null != response.body() -> {
                                    val mResponse =
                                        response.body() as JobMediaResponseModel

                                    //save local id of job in list
                                    for (i in mResponse.data.list.indices) {
                                        mResponse.data.list[i].jobLocalId = mJobLocalId.toInt()
                                    }

                                   /* if (sortBy == SORT_BY_CONTRIBUTOR) {
                                        filteredMediaByContributor(mResponse.data.list)
                                    } else {
                                        filteredMediaByDate(mResponse.data.list)
                                    }*/

                                    //Save data in room database
                                    saveMediaInLocalDatabase(mResponse.data.list,mJobId,mJobLocalId,sortBy,mediaKind,mJobId)
                                   
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
                            isShowShimmer.value = false
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
                        isShowShimmer.value = false
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

    fun getConversation(
        mJobId: String,
        mediaKind: String,
        search: String = "",
        showLoader: Boolean = true
    ) {
        if (showLoader) {
            isShowShimmer.value = true
        }
        mCompositeDisposable.add(
            mJobDetailInteractor.getConversation(mJobId, mediaKind, search,
                object :
                    NetworkRequestCallbacks {
                    override fun onSuccess(response: Response<*>) {
                        try {
                            isShowShimmer.value = false
                            val pojoNetworkResponse =
                                RetrofitRequest.checkForResponseCode(response.code())
                            when {
                                pojoNetworkResponse.isSuccess && null != response.body() -> {
                                    val mResponse =
                                        response.body() as ConversationResponseModel
                                    mConversationList.value = mResponse.data.list

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
                            isShowShimmer.value = false
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
                        isShowShimmer.value = false
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

    fun getMediaComment(
        mJobId: String,
        mMediaId: String,
        mSubMediaId: String,
        mediaKind: String,
        search: String = "",
        showLoader: Boolean = true
    ) {
        if (showLoader) {
            isShowShimmer.value = true
        }
        mCompositeDisposable.add(
            mJobDetailInteractor.getMediaComment(mJobId, mMediaId, mSubMediaId, mediaKind, search,
                object :
                    NetworkRequestCallbacks {
                    override fun onSuccess(response: Response<*>) {
                        try {
                            isShowShimmer.value = false
                            val pojoNetworkResponse =
                                RetrofitRequest.checkForResponseCode(response.code())
                            when {
                                pojoNetworkResponse.isSuccess && null != response.body() -> {
                                    val mResponse =
                                        response.body() as MediaCommentResponseModel
                                    mCommentList.value = mResponse.data.list

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
                            isShowShimmer.value = false
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
                        isShowShimmer.value = false
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

    fun getAllComments(mJobId: String, search: String = "") {
        isShowShimmer.value = true
        mCompositeDisposable.add(
            mJobDetailInteractor.getAllComments(mJobId, search,
                object :
                    NetworkRequestCallbacks {
                    override fun onSuccess(response: Response<*>) {
                        try {
                            isShowShimmer.value = false
                            val pojoNetworkResponse =
                                RetrofitRequest.checkForResponseCode(response.code())
                            when {
                                pojoNetworkResponse.isSuccess && null != response.body() -> {
                                    val mResponse =
                                        response.body() as ConversationResponseModel
                                    mConversationList.value = mResponse.data.list

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
                            isShowShimmer.value = false
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
                        isShowShimmer.value = false
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

    fun createConversation(mJobId: String, mediaKind: String, message: String) {
        when {
            message.isBlank() -> errorHandler.value = ErrorHandler.EMPTY_MESSAGE
            else -> {
                mCompositeDisposable.add(
                    mJobDetailInteractor.createConversation(mJobId, mediaKind, message,
                        object :
                            NetworkRequestCallbacks {
                            override fun onSuccess(response: Response<*>) {
                                try {
                                    isShowShimmer.value = false
                                    val pojoNetworkResponse =
                                        RetrofitRequest.checkForResponseCode(response.code())
                                    when {
                                        pojoNetworkResponse.isSuccess && null != response.body() -> {
                                            val mResponse =
                                                response.body() as SimpleSuccessResponse
                                            //Call api
                                            getConversation(mJobId, mediaKind, "", false)

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
                                    isShowShimmer.value = false
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
                                isShowShimmer.value = false
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

    fun createComment(
        mJobId: String,
        mMediaId: String,
        mSubMediaId: String,
        mediaKind: String,
        message: String
    ) {
        when {
            message.isBlank() -> errorHandler.value = ErrorHandler.EMPTY_MESSAGE
            else -> {
                mCompositeDisposable.add(
                    mJobDetailInteractor.createComment(mJobId,
                        mMediaId,
                        mSubMediaId,
                        mediaKind,
                        message,
                        object :
                            NetworkRequestCallbacks {
                            override fun onSuccess(response: Response<*>) {
                                try {
                                    isShowShimmer.value = false
                                    val pojoNetworkResponse =
                                        RetrofitRequest.checkForResponseCode(response.code())
                                    when {
                                        pojoNetworkResponse.isSuccess && null != response.body() -> {
                                            val mResponse =
                                                response.body() as SimpleSuccessResponse
                                            //Call api
                                            getMediaComment(
                                                mJobId,
                                                mMediaId,
                                                mSubMediaId,
                                                mediaKind,
                                                "",
                                                false
                                            )

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
                                    isShowShimmer.value = false
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
                                isShowShimmer.value = false
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

    fun filteredMediaByDate(mJobMediaList: List<JobMediaList>) {
        var date = ArrayList<String>()
        var mFilterJobMediaList = ArrayList<FilteredJobsMediaModel>()

        //Grouping the all date in array
        for (i in mJobMediaList.indices) {
            if (date.isEmpty()) {
                date.add(
                    GeneralFunctions.changeDateFormat(
                        mJobMediaList[i].createdAt,
                        DATE_FORMAT_SERVER_ISO,
                        DATE_FORMAT_DISPLAY
                    )
                )
                var mFilteredJobsMediaModel = FilteredJobsMediaModel()
                mFilteredJobsMediaModel.date =
                    GeneralFunctions.changeDateFormat(
                        mJobMediaList[i].createdAt,
                        DATE_FORMAT_SERVER_ISO,
                        DATE_FORMAT_DISPLAY
                    )
                mFilterJobMediaList.add(mFilteredJobsMediaModel)

            } else {
                //Check the duplicates dates from string list
                if (!date.contains(
                        GeneralFunctions.changeDateFormat(
                            mJobMediaList[i].createdAt,
                            DATE_FORMAT_SERVER_ISO,
                            DATE_FORMAT_DISPLAY
                        )
                    )
                ) {
                    var mFilteredJobsMediaModel = FilteredJobsMediaModel()
                    date.add(
                        GeneralFunctions.changeDateFormat(
                            mJobMediaList[i].createdAt,
                            DATE_FORMAT_SERVER_ISO,
                            DATE_FORMAT_DISPLAY
                        )
                    )
                    mFilteredJobsMediaModel.date =
                        GeneralFunctions.changeDateFormat(
                            mJobMediaList[i].createdAt,
                            DATE_FORMAT_SERVER_ISO,
                            DATE_FORMAT_DISPLAY
                        )
                    mFilterJobMediaList.add(mFilteredJobsMediaModel)
                }
            }
        }

        //Add the data in main list of on the basis of date
        for (i in mJobMediaList.indices) {
            for (j in mFilterJobMediaList.indices) {
                if (GeneralFunctions.changeDateFormat(
                        mJobMediaList[i].createdAt,
                        DATE_FORMAT_SERVER_ISO,
                        DATE_FORMAT_DISPLAY
                    ) ==
                    mFilterJobMediaList[j].date
                ) {
                    mFilterJobMediaList[j].mJobMediaList!!.add(mJobMediaList[i])
                }
            }

        }

        mFilteredJobMediaList.value = mFilterJobMediaList

    }

    fun getJobDetailFromLocal(mJobLocalId: String, mJobId: String) {
        var mData = Job()
        mData = if (mJobId.isNotBlank()) {
            CamMaxRoomDatabase.getDatabase(getApplication()).jobsDao().getSingleJobByServerId(
                mJobId
            )
        } else {
            CamMaxRoomDatabase.getDatabase(getApplication()).jobsDao().getSingleJobByLocalId(
                mJobLocalId
            )
        }

        if (mData != null) {
            mJobDetail.value = JobDetail(mData)
        } else {
            errorHandler.value = ErrorHandler.NO_INTERNET
        }
    }

    fun deleteJobFromLocal(mJobLocalId: String, mJobId: String) {
        CamMaxRoomDatabase.getDatabase(getApplication()).jobsDao().deleteJob(mJobLocalId)

        //Save deleted job from local
        if (mJobId.isNotBlank()) {
            var mSavedIds=ArrayList<String>()
            if (mUserPrefsManager.getJobDeletedIds!=null){
                 mSavedIds = mUserPrefsManager.getJobDeletedIds!!.mJobsIds!!
                 mSavedIds.add(mJobId)
            }

            val mDeletedJobs = DeletedJobs()
            mDeletedJobs.mJobsIds = mSavedIds
            mUserPrefsManager.saveDeletedJobIds(mDeletedJobs)
            isDeleteJob.value = true
        }
    }

    fun saveMediaInLocalDatabase(
        serverList: List<JobMediaList>,
        mJobId: String,
        mJobLocalId: String,
        sortBy: Int,
        mediaKind: String,
        mJobId1: String
    ) {
        viewModelScope.launch {
            //Delete local data
             CamMaxRoomDatabase.getDatabase(getApplication()).media().deleteMediaByJobId(mJobId)

            //Save data with new list
            CamMaxRoomDatabase.getDatabase(getApplication()).media().insertMedia(
                serverList)

            //Get media
            getMediaFromLocal(mJobLocalId,sortBy,mediaKind,mJobId)
        }
    }

    fun getMediaFromLocal(mJobLocalId: String, sortBy: Int, kind: String, mJobId: String) {
        isShowShimmer.value = true
        var mMediaList = ArrayList<JobMediaList>()

        if (kind == JOB_KIND_PHOTO) {
            if (mJobId.isNotBlank()) {
                mMediaList.addAll(
                    CamMaxRoomDatabase.getDatabase(getApplication()).media()
                        .getMediaByKindByJobId(mJobId, JOB_KIND_PHOTO)
                )
                mMediaList.addAll(
                    CamMaxRoomDatabase.getDatabase(getApplication()).media()
                        .getMediaByKindByJobId(mJobId, JOB_KIND_VIDEO)
                )
            } else {
                mMediaList.addAll(
                    CamMaxRoomDatabase.getDatabase(getApplication()).media()
                        .getMediaByKindByLocalId(mJobLocalId, JOB_KIND_PHOTO)
                )
                mMediaList.addAll(
                    CamMaxRoomDatabase.getDatabase(getApplication()).media()
                        .getMediaByKindByLocalId(mJobLocalId, JOB_KIND_VIDEO)
                )
            }
        } else {
            if (mJobId.isNotBlank()) {
                mMediaList.addAll(
                    CamMaxRoomDatabase.getDatabase(getApplication()).media()
                        .getMediaByKindByJobId(mJobId, kind)
                )
            } else {
                mMediaList.addAll(
                    CamMaxRoomDatabase.getDatabase(getApplication()).media()
                        .getMediaByKindByLocalId(mJobLocalId, kind)
                )
            }
        }

        //Set filter
        if (mMediaList.isNotEmpty()) {
            if (sortBy == SORT_BY_CONTRIBUTOR) {
                filteredMediaByContributor(mMediaList)
            } else {
                filteredMediaByDate(mMediaList)
            }
            isShowShimmer.value = false
        } else {
            isShowShimmer.value = false
        }
    }

    fun deleteJobsFromLocalToServer() {
        if (mUserPrefsManager.getJobDeletedIds != null) {
            for (i in mUserPrefsManager.getJobDeletedIds!!.mJobsIds!!.indices) {
                deleteJob(mUserPrefsManager.getJobDeletedIds!!.mJobsIds!![i])
            }
            //Save the empty data after sync in server
            mUserPrefsManager.saveDeletedJobIds(DeletedJobs())
        }
    }


    private fun filteredMediaByContributor(mJobMediaList: List<JobMediaList>) {
        var date = ArrayList<String>()
        var mFilterJobMediaList = ArrayList<FilteredJobsMediaModel>()

        //Grouping the all date in array
        for (i in mJobMediaList.indices) {
            if (date.isEmpty()) {
                date.add(mJobMediaList[i].creatorId.firstName + mJobMediaList[i].creatorId.lastName)
                var mFilteredJobsMediaModel = FilteredJobsMediaModel()
                mFilteredJobsMediaModel.date =
                    mJobMediaList[i].creatorId.firstName + mJobMediaList[i].creatorId.lastName
                mFilterJobMediaList.add(mFilteredJobsMediaModel)

            } else {
                //Check the duplicates dates from string list
                if (!date.contains(mJobMediaList[i].creatorId.firstName + mJobMediaList[i].creatorId.lastName)
                ) {
                    var mFilteredJobsMediaModel = FilteredJobsMediaModel()
                    date.add(mJobMediaList[i].creatorId.firstName + mJobMediaList[i].creatorId.lastName)
                    mFilteredJobsMediaModel.date =
                        mJobMediaList[i].creatorId.firstName + mJobMediaList[i].creatorId.lastName
                    mFilterJobMediaList.add(mFilteredJobsMediaModel)
                }
            }
        }

        //Add the data in main list of on the basis of date
        for (i in mJobMediaList.indices) {
            for (j in mFilterJobMediaList.indices) {
                if (mJobMediaList[i].creatorId.firstName + mJobMediaList[i].creatorId.lastName ==
                    mFilterJobMediaList[j].date
                ) {
                    mFilterJobMediaList[j].mJobMediaList!!.add(mJobMediaList[i])
                }
            }

        }

        mFilteredJobMediaList.value = mFilterJobMediaList

    }

    fun onGetJobDetail() = mJobDetail
    fun onDeleteJob() = isDeleteJob
    fun onGetJobMedia() = mJobMediaList
    fun onFilteredGetJobMedia() = mFilteredJobMediaList
    fun onShowShimmer() = isShowShimmer
    fun onGetConversation() = mConversationList
    fun onGetComments() = mCommentList
    fun onGetTimeline() = mTimelineData

}