package com.max360group.cammax360.viewmodels

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.interactors.JobsMediaInteractor
import com.max360group.cammax360.repository.models.*
import com.max360group.cammax360.repository.models.model.*
import com.max360group.cammax360.repository.models.model.Media
import com.max360group.cammax360.repository.models.model.SubMedia
import com.max360group.cammax360.repository.models.model.User
import com.max360group.cammax360.repository.models.model.Users
import com.max360group.cammax360.repository.networkrequests.NetworkRequestCallbacks
import com.max360group.cammax360.repository.networkrequests.RetrofitRequest
import com.max360group.cammax360.repository.room.CamMaxRoomDatabase
import com.max360group.cammax360.utils.AmazonS3
import com.max360group.cammax360.utils.AmazonS3.Companion.S3_BUCKET_FOR_USER_PHOTOS
import com.max360group.cammax360.utils.Constants.DATE_FORMAT_SERVER_ISO
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.views.utils.JobsConstants
import com.max360group.cammax360.views.utils.JobsConstants.JOB_KIND_DOCUMENT
import com.max360group.cammax360.views.utils.JobsConstants.JOB_KIND_PHOTO
import com.max360group.cammax360.views.utils.JobsConstants.JOB_KIND_VIDEO
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class JobMediaViewModel(application: Application) : BaseViewModel(application) {

    private val mJobsMediaInteractor by lazy {
        JobsMediaInteractor()
    }

    private var mMembersList = MutableLiveData<List<JobMembers>>()
    private var isMediaUpdated = MutableLiveData<Boolean>()
    private var medias: ArrayList<Media>? = ArrayList()
    private var mediasVideo: ArrayList<MediaVideo>? = ArrayList()
    private var mediasDocs: ArrayList<MediaDocs>? = ArrayList()
    private var subMedias: ArrayList<SubMedia>? = ArrayList()
    private var subMediasVideo: ArrayList<SubMediaVideo>? = ArrayList()
    private var subMediasDocs: ArrayList<SubMediaDocs>? = ArrayList()
    private var mUser: ArrayList<User>? =
        ArrayList()
    private val sdfTime: SimpleDateFormat by lazy {
        SimpleDateFormat(
            DATE_FORMAT_SERVER_ISO,
            Locale.US
        )
    }

    fun getJobMembers(
        subKind: String,
        id: String
    ) {
        mCompositeDisposable.add(
            mJobsMediaInteractor.getJobsMembers(subKind, id,
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
                                        response.body() as GetJobMembersSubModuleResponse

                                    mMembersList.value = mResponse.data.data
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

    fun addJobMedia(
        mJobId: String,
        mImageList: ArrayList<BeforeAfterImageModel>,
        mJobMembers: ArrayList<User> = ArrayList<User>()
    ) {
        isShowLoader.value = true
        medias!!.clear()
        var mJobMediaRequestModel = JobMediaRequestModel()
        mJobMediaRequestModel.kind = JOB_KIND_PHOTO

        var bucketName = JobsConstants.savePathForAws(
            S3_BUCKET_FOR_USER_PHOTOS,
            mUserPrefsManager.loginedUser!!.accounts?.get(0)?.primaryUserId!!.id.toString(), mJobId
        )

        //Upload images to s3
        for (i in mImageList.indices) {
            if (mImageList[i]!!.simpleImage.isNotEmpty()) {
                if (!GeneralFunctions.isRemoteImage(mImageList[i].simpleImage)) {
                    val file = File(mImageList[i].simpleImage)

                    // Upload photos to s3
                    AmazonS3(getApplication()).uploadFileToS3(
                        file,
                        bucketName
                    )
                    mImageList[i].simpleImage = JobsConstants.savePathForServer(
                        mUserPrefsManager.loginedUser!!.accounts?.get(0)?.primaryUserId!!.id.toString(),
                        mJobId,
                        file.name
                    )

                }
            }

            if (mImageList[i]!!.beforeImage.isNotEmpty()) {
                if (!GeneralFunctions.isRemoteImage(mImageList[i].beforeImage)) {
                    val file = File(mImageList[i].beforeImage)

                    // Upload photos to s3
                    AmazonS3(getApplication()).uploadFileToS3(
                        file,
                        bucketName
                    )
                    mImageList[i].beforeImage = JobsConstants.savePathForServer(
                        mUserPrefsManager.loginedUser!!.accounts?.get(0)?.primaryUserId!!.id.toString(),
                        mJobId,
                        file.name
                    )

                }
            }
            if (mImageList[i]!!.afterImageName.isNotEmpty()) {
                if (!GeneralFunctions.isRemoteImage(mImageList[i].afterImage)) {
                    val file = File(mImageList[i].afterImage)

                    // Upload photos to s3
                    AmazonS3(getApplication()).uploadFileToS3(
                        file,
                        bucketName
                    )
                    mImageList[i].afterImage = JobsConstants.savePathForServer(
                        mUserPrefsManager.loginedUser!!.accounts?.get(0)?.primaryUserId!!.id.toString(),
                        mJobId,
                        file.name
                    )

                }
            }
        }

        //Add the data in request model
        for (i in mImageList.indices) {
            subMedias!!.clear()
            mUser!!.clear()
            when {
                mImageList[i].simpleImageName.isNotEmpty() -> {
                    if (mImageList[i].users!!.isNotEmpty()) {
                        for (j in mImageList[i].users!!.indices) {
                            mUser!!.add(
                                User(
                                    userId = mImageList!![i].users!![j].userId,
                                    primaryUserId = mImageList!![i].users!![j].primaryUserId,
                                    permissions = mImageList!![i].users!![j].permissions
                                )
                            )
                        }
                    } else {
                        mUser!!.addAll(mJobMembers!!)

                    }


                    var mSubMedia = SubMedia()
                    mSubMedia.name = mImageList[i].simpleImageName
                    mSubMedia.subKind = "simple"
                    mSubMedia.media = mImageList[i].simpleImage
                    mSubMedia.tags = mImageList[i].tags as ArrayList<String>?
                    mSubMedia.users!!.addAll(mUser!!)

                    subMedias!!.add(mSubMedia)

                    var mMedia = Media()
                    mMedia.name = mImageList[i].simpleImageName
                    mMedia.tags = mImageList[i].tags as ArrayList<String>?
                    mMedia.subKind = "simple"
                    mMedia.subMedias!!.addAll(subMedias!!)
                    medias!!.add(i, mMedia)

                    mJobMediaRequestModel.kind = JobsConstants.JOB_KIND_PHOTO
                    mJobMediaRequestModel.medias = medias

                }

                mImageList[i].beforeImageName.isNotEmpty() -> {
                    if (mImageList[i].usersbefore!!.isNotEmpty()) {
                        mUser!!.clear()
                        for (j in mImageList[i].usersbefore!!.indices) {
                            mUser!!.add(
                                User(
                                    userId = mImageList!![i].usersbefore!![j].userId,
                                    primaryUserId = mImageList!![i].usersbefore!![j].primaryUserId,
                                    permissions = mImageList!![i].usersbefore!![j].permissions
                                )
                            )
                        }
                    } else {
                        mUser!!.clear()
                        mUser!!.addAll(mJobMembers!!)
                    }

                    var mSubMedia = SubMedia()
                    mSubMedia.name = mImageList[i].beforeImageName
                    mSubMedia.subKind = "before"
                    mSubMedia.media = mImageList[i].beforeImage
                    mSubMedia.tags = mImageList[i].tagsBefore as ArrayList<String>?
                    mSubMedia.users!!.addAll(mUser!!)

                    subMedias!!.add(mSubMedia)

                    var mMediaBefore = Media()
                    mMediaBefore.name = "Dual Both"
                    mMediaBefore.tags = mImageList[i].tagsBefore as ArrayList<String>?
                    mMediaBefore.subKind = "dual"
                    mMediaBefore.subMedias!!.addAll(subMedias!!)
                    medias!!.add(i, mMediaBefore)

                    mJobMediaRequestModel.kind = JobsConstants.JOB_KIND_PHOTO
                    mJobMediaRequestModel.medias = medias


                    //Add after image
                    if (mImageList[i].usersAfter!!.isNotEmpty()) {
                        mUser!!.clear()
                        for (j in mImageList[i].usersAfter!!.indices) {
                            mUser!!.add(
                                User(
                                    userId = mImageList!![i].usersAfter!![j].userId,
                                    primaryUserId = mImageList!![i].usersAfter!![j].primaryUserId,
                                    permissions = mImageList!![i].usersAfter!![j].permissions
                                )
                            )
                        }
                    } else {
                        mUser!!.clear()
                        mUser!!.addAll(mJobMembers!!)

                    }

                    var mSubMediaAfter = SubMedia()
                    mSubMediaAfter.name = mImageList[i].afterImageName
                    mSubMediaAfter.subKind = "after"
                    mSubMediaAfter.media = mImageList[i].afterImage
                    mSubMediaAfter.tags = mImageList[i].tagsAfter as ArrayList<String>?
                    mSubMediaAfter.users!!.addAll(mUser!!)

                    subMedias!!.add(mSubMediaAfter)
                    mJobMediaRequestModel.medias!![i].subMedias!!.add(mSubMediaAfter)

                }

            }
        }
        mCompositeDisposable.add(
            mJobsMediaInteractor.addMedia(mJobId, mJobMediaRequestModel,
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
                                    isMediaUpdated.value = true
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

    fun addJobVideo(
        mJobId: String,
        mVideoList: ArrayList<VideosModel>,
        mJobMembers: ArrayList<User> = ArrayList<User>()
    ) {
        isShowLoader.value = true
        mediasVideo!!.clear()
        var mJobMediaRequestModel = JobVideoRequestModel()
        mJobMediaRequestModel.kind = JOB_KIND_VIDEO

        val bucketName = JobsConstants.saveVideoPathForAws(
            S3_BUCKET_FOR_USER_PHOTOS,
            mUserPrefsManager.loginedUser!!.accounts?.get(0)?.primaryUserId!!.id.toString(), mJobId
        )

        // Upload photos to s3
        for (i in mVideoList.indices) {
            if (mVideoList[i].video.isNotEmpty()) {
                if (!GeneralFunctions.isRemoteImage(mVideoList[i].video)) {
                    val file = File(mVideoList[i].video)

                    // Upload video to s3
                    AmazonS3(getApplication()).uploadFileToS3(
                        file,
                        bucketName
                    )

                    mVideoList[i].video = JobsConstants.saveVideoPathForServer(
                        mUserPrefsManager.loginedUser!!.accounts?.get(0)?.primaryUserId!!.id.toString(),
                        mJobId,
                        file.name
                    )

                }

                if (!GeneralFunctions.isRemoteImage(mVideoList[i].thumbail)) {
                    val file = File(mVideoList[i].thumbail)

                    // Upload thumbnail to s3
                    AmazonS3(getApplication()).uploadFileToS3(
                        file,
                        bucketName
                    )

                    mVideoList[i].thumbail = JobsConstants.saveVideoPathForServer(
                        mUserPrefsManager.loginedUser!!.accounts?.get(0)?.primaryUserId!!.id.toString(),
                        mJobId,
                        file.name
                    )

                }
            }
        }

        //Add data in request model
        for (i in mVideoList.indices) {
            subMediasVideo!!.clear()
            mUser!!.clear()
            when {
                mVideoList[i].videoName.isNotEmpty() -> {
                    if (mVideoList[i].users!!.isNotEmpty()) {
                        for (j in mVideoList[i].users!!.indices) {
                            mUser!!.add(
                                User(
                                    userId = mVideoList[i].users!![j].userId,
                                    primaryUserId = mVideoList[i].users!![j].primaryUserId,
                                    permissions = mVideoList[i].users!![j].permissions
                                )
                            )
                        }
                    } else {
                        mUser!!.addAll(mJobMembers)

                    }

                    var mSubMedia = SubMediaVideo()
                    mSubMedia.name = mVideoList[i].videoName
                    mSubMedia.thumbnail = mVideoList[i].thumbail
                    mSubMedia.media = mVideoList[i].video
                    mSubMedia.tags = mVideoList[i].tags as ArrayList<String>?
                    mSubMedia.users!!.addAll(mUser!!)

                    subMediasVideo!!.add(mSubMedia)

                    var mMedia = MediaVideo()
                    mMedia.name = mVideoList[i].videoName
                    mMedia.tags = mVideoList[i].tags as ArrayList<String>?
                    mMedia.subMedias!!.addAll(subMediasVideo!!)
                    mediasVideo!!.add(i, mMedia)

                    mJobMediaRequestModel.medias = mediasVideo

                }
            }
        }

        mCompositeDisposable.add(
            mJobsMediaInteractor.addVideo(mJobId, mJobMediaRequestModel,
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
                                    isMediaUpdated.value = true
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

    fun addJobDocs(
        mJobId: String,
        mDocsList: ArrayList<DocsModel>,
        mJobMembers: ArrayList<User> = ArrayList<User>()
    ) {
        mediasDocs!!.clear()
        var mJobDocsRequestModel = JobDocsRequestModel()
        mJobDocsRequestModel.kind = JOB_KIND_DOCUMENT

        var bucketName = JobsConstants.saveDocsPathForAws(
            S3_BUCKET_FOR_USER_PHOTOS,
            mUserPrefsManager.loginedUser!!.accounts?.get(0)?.primaryUserId!!.id.toString(), mJobId
        )

        // Upload photos to s3
        for (i in mDocsList.indices) {
            if (mDocsList[i]!!.docs.isNotEmpty()) {
                if (!GeneralFunctions.isRemoteImage(mDocsList[i].docs)) {
                    val file = File(mDocsList[i].docs)

                    // Upload photos to s3
                    AmazonS3(getApplication()).uploadFileToS3(
                        file,
                        bucketName
                    )

                    mDocsList[i].docs = JobsConstants.saveDocsPathForServer(
                        mUserPrefsManager.loginedUser!!.accounts?.get(0)?.primaryUserId!!.id.toString(),
                        mJobId,
                        file.name
                    )
                }
            }
        }

        //Add data in request model
        for (i in mDocsList.indices) {
            subMediasDocs!!.clear()
            mUser!!.clear()
            when {
                mDocsList[i].docsName.isNotEmpty() -> {
                    if (mDocsList[i].users!!.isNotEmpty()) {
                        for (j in mDocsList[i].users!!.indices) {
                            mUser!!.add(
                                User(
                                    userId = mDocsList!![i].users!![j].userId,
                                    primaryUserId = mDocsList!![i].users!![j].primaryUserId,
                                    permissions = mDocsList!![i].users!![j].permissions
                                )
                            )
                        }
                    } else {
                        mUser!!.addAll(mJobMembers!!)

                    }

                    var mSubMedia = SubMediaDocs()
                    mSubMedia.name = mDocsList[i].docsName
                    mSubMedia.media = mDocsList[i].docs
                    mSubMedia.tags = mDocsList[i].tags as ArrayList<String>?
                    mSubMedia.users!!.addAll(mUser!!)

                    subMediasDocs!!.add(mSubMedia)

                    var mMedia = MediaDocs()
                    mMedia.name = mDocsList[i].docsName
                    mMedia.tags = mDocsList[i].tags as ArrayList<String>?
                    mMedia.subMedias!!.addAll(subMediasDocs!!)
                    mediasDocs!!.add(i, mMedia)

                    mJobDocsRequestModel.medias = mediasDocs

                }
            }
        }

        isShowLoader.value = true
        mCompositeDisposable.add(
            mJobsMediaInteractor.addDocs(mJobId, mJobDocsRequestModel,
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
                                    isMediaUpdated.value = true
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

    fun saveJobMediaLocal(
        mJobId: String, mLocalId: String,
        mImageList: ArrayList<BeforeAfterImageModel>,
        mJobMembers: ArrayList<User>?
    ) {

        viewModelScope.launch {
            var mJobMainList = ArrayList<JobMediaList>()
            var mJobMedia = ArrayList<JobMedia>()
            var mJobUser = ArrayList<JobUser>()
            //Add the data in request model
            val calender = Calendar.getInstance()
            for (i in mImageList.indices) {
                mJobMedia.clear()
                mJobUser.clear()
                when {
                    mImageList[i].simpleImageName.isNotEmpty() -> {
                        if (mImageList[i].users!!.isNotEmpty()) {
                            for (j in mImageList[i].users!!.indices) {
                                mJobUser!!.add(
                                    JobUser(
                                        userId = mImageList!![i].users!![j].userId,
                                        primaryUserId = mImageList!![i].users!![j].primaryUserId,
                                        permissions = JobPermissions(
                                            mImageList!![i].users!![j].permissions.base,
                                            mImageList!![i].users!![j].permissions.comments,
                                            mImageList!![i].users!![j].permissions.members
                                        )
                                    )
                                )
                            }
                        } else {
                            for (j in mJobMembers!!.indices) {
                                var jobUser = JobUser()
                                JobUser(
                                    userId = mImageList!![i].users!![j].userId,
                                    primaryUserId = mImageList!![i].users!![j].primaryUserId,
                                    permissions = JobPermissions(
                                        mImageList!![i].users!![j].permissions.base,
                                        mImageList!![i].users!![j].permissions.comments,
                                        mImageList!![i].users!![j].permissions.members
                                    )
                                )
                                mJobUser!!.add(jobUser)
                            }
                        }


                        var mJobMediaSimple = JobMedia()
                        mJobMediaSimple.name = mImageList[i].simpleImageName
                        mJobMediaSimple.subKind = "simple"
                        mJobMediaSimple.mediaURL = mImageList[i].simpleImage
                        mJobMediaSimple.tags = mImageList[i].tags as ArrayList<String>?
                        mJobMediaSimple.users!!.addAll(mJobUser)

                        mJobMedia.add(mJobMediaSimple)

                        var mJobMediaList = JobMediaList()
                        mJobMediaList.name = mImageList[i].simpleImageName
                        mJobMediaList.tags = mImageList[i].tags as ArrayList<String>?
                        mJobMediaList.subKind = "simple"
                        mJobMediaList.kind = JOB_KIND_PHOTO
                        mJobMediaList.jobId = mJobId
                        mJobMediaList.isSavedInServer = false
                        mJobMediaList.creatorId.firstName =
                            mUserPrefsManager.loginedUser!!.firstName!!
                        mJobMediaList.creatorId.lastName =
                            mUserPrefsManager.loginedUser!!.lastName!!
                        mJobMediaList.jobLocalId = mLocalId.toInt()
                        mJobMediaList.createdAt = sdfTime.format(calender.time)
                        mJobMediaList.medias!!.addAll(mJobMedia)
                        mJobMainList!!.add(i, mJobMediaList)

                    }

                    mImageList[i].beforeImageName.isNotEmpty() -> {
                        if (mImageList[i].usersbefore!!.isNotEmpty()) {
                            mJobUser!!.clear()
                            for (j in mImageList[i].usersbefore!!.indices) {
                                mJobUser!!.add(
                                    JobUser(
                                        userId = mImageList!![i].usersbefore!![j].userId,
                                        primaryUserId = mImageList!![i].usersbefore!![j].primaryUserId,
                                        permissions = JobPermissions(
                                            mImageList!![i].users!![j].permissions.base,
                                            mImageList!![i].users!![j].permissions.comments,
                                            mImageList!![i].users!![j].permissions.members
                                        )
                                    )
                                )
                            }
                        } else {
                            mJobUser.clear()
                            for (j in mJobMembers!!.indices) {
                                var jobUser = JobUser()
                                JobUser(
                                    userId = mImageList!![i].users!![j].userId,
                                    primaryUserId = mImageList!![i].users!![j].primaryUserId,
                                    permissions = JobPermissions(
                                        mImageList!![i].users!![j].permissions.base,
                                        mImageList!![i].users!![j].permissions.comments,
                                        mImageList!![i].users!![j].permissions.members
                                    )
                                )
                                mJobUser!!.add(jobUser)
                            }
                        }


                        var mJobMediaBefore = JobMedia()
                        mJobMediaBefore.name = mImageList[i].beforeImageName
                        mJobMediaBefore.subKind = "before"
                        mJobMediaBefore.mediaURL = mImageList[i].beforeImage
                        mJobMediaBefore.tags = mImageList[i].tags as ArrayList<String>?
                        mJobMediaBefore.users!!.addAll(mJobUser)

                        mJobMedia!!.add(mJobMediaBefore)

                        var mJobMediaList = JobMediaList()
                        mJobMediaList.name = "Dual Both"
                        mJobMediaList.tags = mImageList[i].tagsBefore as ArrayList<String>?
                        mJobMediaList.subKind = "dual"
                        mJobMediaList.kind = JOB_KIND_PHOTO
                        mJobMediaList.isSavedInServer = false
                        mJobMediaList.createdAt = sdfTime.format(calender.time)
                        mJobMediaList.creatorId.firstName =
                            mUserPrefsManager.loginedUser!!.firstName!!
                        mJobMediaList.creatorId.lastName =
                            mUserPrefsManager.loginedUser!!.lastName!!
                        mJobMediaList.jobId = mJobId
                        mJobMediaList.jobLocalId = mLocalId.toInt()
                        mJobMediaList.medias!!.addAll(mJobMedia)
                        mJobMainList.add(i, mJobMediaList)

                        //Add after image
                        if (mImageList[i].usersAfter!!.isNotEmpty()) {
                            mJobUser.clear()
                            for (j in mImageList[i].usersAfter!!.indices) {
                                mJobUser.add(
                                    JobUser(
                                        userId = mImageList[i].usersbefore!![j].userId,
                                        primaryUserId = mImageList!![i].usersbefore!![j].primaryUserId,
                                        permissions = JobPermissions(
                                            mImageList!![i].users!![j].permissions.base,
                                            mImageList!![i].users!![j].permissions.comments,
                                            mImageList!![i].users!![j].permissions.members
                                        )
                                    )
                                )
                            }
                        } else {
                            mJobUser.clear()
                            for (j in mJobMembers!!.indices) {
                                var jobUser = JobUser()
                                JobUser(
                                    userId = mImageList!![i].users!![j].userId,
                                    primaryUserId = mImageList!![i].users!![j].primaryUserId,
                                    permissions = JobPermissions(
                                        mImageList!![i].users!![j].permissions.base,
                                        mImageList!![i].users!![j].permissions.comments,
                                        mImageList!![i].users!![j].permissions.members
                                    )
                                )
                                mJobUser!!.add(jobUser)
                            }
                        }

                        var mJobMediaAfter = JobMedia()
                        mJobMediaAfter.name = mImageList[i].afterImageName
                        mJobMediaAfter.subKind = "after"
                        mJobMediaAfter.mediaURL = mImageList[i].afterImage
                        mJobMediaAfter.tags = mImageList[i].tags as ArrayList<String>?
                        mJobMediaAfter.users!!.addAll(mJobUser)

                        mJobMainList[i].medias!!.add(mJobMediaAfter)
                    }
                }
            }
            //Get saved data
            var localMediaList =
                CamMaxRoomDatabase.getDatabase(getApplication()).media().getAllMedia()
            if (localMediaList != null) {
                if (localMediaList.isNotEmpty()) {
                    mJobMainList.addAll(localMediaList)
                }
            }
            //Save data in local
            CamMaxRoomDatabase.getDatabase(getApplication()).media().insertMedia(mJobMainList)
            isMediaUpdated.value = true
        }
    }

    fun saveVideoInLocal(
        mJobId: String,
        mLocalId: String,
        mVideoList: ArrayList<VideosModel>,
        mJobMembers: ArrayList<User>?
    ) {
        viewModelScope.launch {
            var mJobMainList = ArrayList<JobMediaList>()
            var mJobMedia = ArrayList<JobMedia>()
            var mJobUser = ArrayList<JobUser>()

            //Add the data in request model
            val calender = Calendar.getInstance()
            for (i in mVideoList.indices) {
                mJobMedia.clear()
                mJobUser.clear()
                if (mVideoList[i].users!!.isNotEmpty()) {
                    for (j in mVideoList[i].users!!.indices) {
                        mJobUser!!.add(
                            JobUser(
                                userId = mVideoList!![i].users!![j].userId,
                                primaryUserId = mVideoList!![i].users!![j].primaryUserId,
                                permissions = JobPermissions(
                                    mVideoList!![i].users!![j].permissions.base,
                                    mVideoList!![i].users!![j].permissions.comments,
                                    mVideoList!![i].users!![j].permissions.members
                                )
                            )
                        )
                    }
                } else {
                    for (j in mJobMembers!!.indices) {
                        var jobUser = JobUser()
                        JobUser(
                            userId = mVideoList!![i].users!![j].userId,
                            primaryUserId = mVideoList!![i].users!![j].primaryUserId,
                            permissions = JobPermissions(
                                mVideoList!![i].users!![j].permissions.base,
                                mVideoList!![i].users!![j].permissions.comments,
                                mVideoList!![i].users!![j].permissions.members
                            )
                        )
                        mJobUser!!.add(jobUser)
                    }
                }


                var mJobMediaSimple = JobMedia()
                mJobMediaSimple.name = mVideoList[i].videoName
                mJobMediaSimple.mediaURL = mVideoList[i].video
                mJobMediaSimple.thumbnailURL = mVideoList[i].thumbail
                mJobMediaSimple.tags = mVideoList[i].tags as ArrayList<String>?
                mJobMediaSimple.users!!.addAll(mJobUser)

                mJobMedia.add(mJobMediaSimple)

                var mJobMediaList = JobMediaList()
                mJobMediaList.name = mVideoList[i].videoName
                mJobMediaList.tags = mVideoList[i].tags as ArrayList<String>?
                mJobMediaList.kind = JOB_KIND_VIDEO
                mJobMediaList.jobId = mJobId
                mJobMediaList.isSavedInServer = false
                mJobMediaList.jobLocalId = mLocalId.toInt()
                mJobMediaList.createdAt = sdfTime.format(calender.time)
                mJobMediaList.creatorId.firstName = mUserPrefsManager.loginedUser!!.firstName!!
                mJobMediaList.creatorId.lastName = mUserPrefsManager.loginedUser!!.lastName!!
                mJobMediaList.medias!!.addAll(mJobMedia)
                mJobMainList!!.add(mJobMediaList)
            }

            //Get saved data
            var localMediaList =
                CamMaxRoomDatabase.getDatabase(getApplication()).media().getAllMedia(
                )

            if (localMediaList != null) {
                if (localMediaList.isNotEmpty()) {
                    mJobMainList.addAll(localMediaList)
                }
            }

            //Save data in local
            CamMaxRoomDatabase.getDatabase(getApplication()).media().insertMedia(mJobMainList)
            isMediaUpdated.value = true
        }
    }

    fun saveDocsInLocal(
        mJobId: String,
        mLocalId: String,
        mDocsList: ArrayList<DocsModel>,
        mJobMembers: ArrayList<User>?
    ) {
        viewModelScope.launch {
            var mJobMainList = ArrayList<JobMediaList>()
            var mJobMedia = ArrayList<JobMedia>()
            var mJobUser = ArrayList<JobUser>()
            //Add the data in request model
            val calender = Calendar.getInstance()
            for (i in mDocsList.indices) {
                mJobMedia.clear()
                mJobUser.clear()
                if (mDocsList[i].users!!.isNotEmpty()) {
                    for (j in mDocsList[i].users!!.indices) {
                        mJobUser!!.add(
                            JobUser(
                                userId = mDocsList!![i].users!![j].userId,
                                primaryUserId = mDocsList!![i].users!![j].primaryUserId,
                                permissions = JobPermissions(
                                    mDocsList!![i].users!![j].permissions.base,
                                    mDocsList!![i].users!![j].permissions.comments,
                                    mDocsList!![i].users!![j].permissions.members
                                )
                            )
                        )
                    }
                } else {
                    for (j in mJobMembers!!.indices) {
                        var jobUser = JobUser()
                        JobUser(
                            userId = mDocsList!![i].users!![j].userId,
                            primaryUserId = mDocsList!![i].users!![j].primaryUserId,
                            permissions = JobPermissions(
                                mDocsList!![i].users!![j].permissions.base,
                                mDocsList!![i].users!![j].permissions.comments,
                                mDocsList!![i].users!![j].permissions.members
                            )
                        )
                        mJobUser!!.add(jobUser)
                    }
                }


                var mJobMediaSimple = JobMedia()
                mJobMediaSimple.name = mDocsList[i].docsName
                mJobMediaSimple.mediaURL = mDocsList[i].docs
                mJobMediaSimple.tags = mDocsList[i].tags as ArrayList<String>?
                mJobMediaSimple.users!!.addAll(mJobUser)

                mJobMedia.add(mJobMediaSimple)

                var mJobMediaList = JobMediaList()
                mJobMediaList.name = mDocsList[i].docsName
                mJobMediaList.tags = mDocsList[i].tags as ArrayList<String>?
                mJobMediaList.kind = JOB_KIND_DOCUMENT
                mJobMediaList.jobId = mJobId
                mJobMediaList.isSavedInServer = false
                mJobMediaList.jobLocalId = mLocalId.toInt()
                mJobMediaList.createdAt = sdfTime.format(calender.time)
                mJobMediaList.creatorId.firstName = mUserPrefsManager.loginedUser!!.firstName!!
                mJobMediaList.creatorId.lastName = mUserPrefsManager.loginedUser!!.lastName!!
                mJobMediaList.medias!!.addAll(mJobMedia)
                mJobMainList!!.add(mJobMediaList)
            }

            //Get saved data
            var localMediaList =
                CamMaxRoomDatabase.getDatabase(getApplication()).media().getAllMedia(
                )
            if (localMediaList != null) {
                if (localMediaList.isNotEmpty()) {
                    mJobMainList.addAll(localMediaList)
                }
            }
            //Save data in local
            CamMaxRoomDatabase.getDatabase(getApplication()).media().insertMedia(mJobMainList)
            isMediaUpdated.value = true
        }
    }

    fun syncVideoLocalToServer() {
        var mMediaData =
            CamMaxRoomDatabase.getDatabase(getApplication()).media().getMediaByKind(JOB_KIND_VIDEO)
        var mVideoList = ArrayList<VideosModel>()
        var mJobMembers = ArrayList<Users>()

        if (mMediaData.isNotEmpty()) {
            //Make a api request
            for (i in mMediaData.indices) {
                if (!mMediaData[i].isSavedInServer) {
                    mVideoList.clear()
                    mJobMembers.clear()
                    for (j in mMediaData[i].medias!![0].users!!.indices) {
                        var mUsers = Users()
                        mUsers.userId = mMediaData[i].medias!![0].users!![j].userId
                        mUsers.primaryUserId = mMediaData[i].medias!![0].users!![j].primaryUserId
                        mUsers.permissions.base =
                            mMediaData[i].medias!![0].users!![j].permissions.base
                        mUsers.permissions.comments =
                            mMediaData[i].medias!![0].users!![j].permissions.comments
                        mUsers.permissions.members =
                            mMediaData[i].medias!![0].users!![j].permissions.members
                    }

                    var videoModel = VideosModel()
                    videoModel.video = mMediaData[i].medias!![0].mediaURL
                    videoModel.videoName = mMediaData[i].medias!![0].name
                    videoModel.thumbail = mMediaData[i].medias!![0].thumbnailURL
                    videoModel.tags = mMediaData[i].medias!![0].tags
                    videoModel.users = mJobMembers
                    mVideoList.add(videoModel)

                    //Call api
                    addJobVideo(mMediaData[i].jobId, mVideoList)

                    //Delete from local after sync in server
                    CamMaxRoomDatabase.getDatabase(getApplication()).media()
                        .deleteMedia(mMediaData[i].mediaLocalId.toString())
                }
            }
        }
        //Sync docs to server
        syncDocsLocalToServer()
    }

    fun syncSingleVideoLocalToServer(mJobId:String,mLocalId: String) {
        var mMediaData =
            CamMaxRoomDatabase.getDatabase(getApplication()).media().getMediaByKindByLocalId(mLocalId,JOB_KIND_VIDEO)
        var mVideoList = ArrayList<VideosModel>()
        var mJobMembers = ArrayList<Users>()

        if (mMediaData.isNotEmpty()) {
            //Make a api request
            for (i in mMediaData.indices) {
                if (!mMediaData[i].isSavedInServer) {
                    mVideoList.clear()
                    mJobMembers.clear()
                    for (j in mMediaData[i].medias!![0].users!!.indices) {
                        var mUsers = Users()
                        mUsers.userId = mMediaData[i].medias!![0].users!![j].userId
                        mUsers.primaryUserId = mMediaData[i].medias!![0].users!![j].primaryUserId
                        mUsers.permissions.base =
                            mMediaData[i].medias!![0].users!![j].permissions.base
                        mUsers.permissions.comments =
                            mMediaData[i].medias!![0].users!![j].permissions.comments
                        mUsers.permissions.members =
                            mMediaData[i].medias!![0].users!![j].permissions.members
                    }

                    var videoModel = VideosModel()
                    videoModel.video = mMediaData[i].medias!![0].mediaURL
                    videoModel.videoName = mMediaData[i].medias!![0].name
                    videoModel.thumbail = mMediaData[i].medias!![0].thumbnailURL
                    videoModel.tags = mMediaData[i].medias!![0].tags
                    videoModel.users = mJobMembers
                    mVideoList.add(videoModel)

                    //Call api
                    addJobVideo(mJobId, mVideoList)

                    //Delete from local after sync in server
                    CamMaxRoomDatabase.getDatabase(getApplication()).media()
                        .deleteMedia(mMediaData[i].mediaLocalId.toString())
                }
            }
        }

    }

    private fun syncDocsLocalToServer() {
        var mMediaData = CamMaxRoomDatabase.getDatabase(getApplication()).media()
            .getMediaByKind(JOB_KIND_DOCUMENT)
        var mDocsList = ArrayList<DocsModel>()
        var mJobMembers = ArrayList<Users>()

        if (mMediaData.isNotEmpty()) {
            //Make a api request
            for (i in mMediaData.indices) {
                if (!mMediaData[i].isSavedInServer) {
                    mDocsList.clear()
                    mJobMembers.clear()
                    for (j in mMediaData[i].medias!![0].users!!.indices) {
                        var mUsers = Users()
                        mUsers.userId = mMediaData[i].medias!![0].users!![j].userId
                        mUsers.primaryUserId = mMediaData[i].medias!![0].users!![j].primaryUserId
                        mUsers.permissions.base =
                            mMediaData[i].medias!![0].users!![j].permissions.base
                        mUsers.permissions.comments =
                            mMediaData[i].medias!![0].users!![j].permissions.comments
                        mUsers.permissions.members =
                            mMediaData[i].medias!![0].users!![j].permissions.members
                    }

                    var docsModel = DocsModel()
                    docsModel.docs = mMediaData[i].medias!![0].mediaURL
                    docsModel.docsName = mMediaData[i].medias!![0].name
                    docsModel.tags = mMediaData[i].medias!![0].tags
                    docsModel.users = mJobMembers
                    mDocsList.add(docsModel)

                    //Call api
                    addJobDocs(mMediaData[i].jobId, mDocsList)

                    //Delete from local after sync in server
                    CamMaxRoomDatabase.getDatabase(getApplication()).media()
                        .deleteMedia(mMediaData[i].mediaLocalId.toString())
                }
            }
        }
        //Sync photos to server
        syncPhotosLocalToServer()
    }

     fun syncSingleDocsLocalToServer(mJobId:String,mLocalId: String) {
        var mMediaData = CamMaxRoomDatabase.getDatabase(getApplication()).media()
            .getMediaByKindByLocalId(mLocalId,JOB_KIND_DOCUMENT)
        var mDocsList = ArrayList<DocsModel>()
        var mJobMembers = ArrayList<Users>()

        if (mMediaData.isNotEmpty()) {
            //Make a api request
            for (i in mMediaData.indices) {
                if (!mMediaData[i].isSavedInServer) {
                    mDocsList.clear()
                    mJobMembers.clear()
                    for (j in mMediaData[i].medias!![0].users!!.indices) {
                        var mUsers = Users()
                        mUsers.userId = mMediaData[i].medias!![0].users!![j].userId
                        mUsers.primaryUserId = mMediaData[i].medias!![0].users!![j].primaryUserId
                        mUsers.permissions.base =
                            mMediaData[i].medias!![0].users!![j].permissions.base
                        mUsers.permissions.comments =
                            mMediaData[i].medias!![0].users!![j].permissions.comments
                        mUsers.permissions.members =
                            mMediaData[i].medias!![0].users!![j].permissions.members
                    }

                    var docsModel = DocsModel()
                    docsModel.docs = mMediaData[i].medias!![0].mediaURL
                    docsModel.docsName = mMediaData[i].medias!![0].name
                    docsModel.tags = mMediaData[i].medias!![0].tags
                    docsModel.users = mJobMembers
                    mDocsList.add(docsModel)

                    //Call api
                    addJobDocs(mJobId, mDocsList)

                    //Delete from local after sync in server
                    CamMaxRoomDatabase.getDatabase(getApplication()).media()
                        .deleteMedia(mMediaData[i].mediaLocalId.toString())
                }
            }
        }
    }

    private fun syncPhotosLocalToServer() {
        var mMediaData =
            CamMaxRoomDatabase.getDatabase(getApplication()).media().getMediaByKind(JOB_KIND_PHOTO)
        var mImageList = ArrayList<BeforeAfterImageModel>()
        var mJobMembers = ArrayList<Users>()

        if (mMediaData.isNotEmpty()){
            //Make a api request
            for (i in mMediaData.indices) {
                if (!mMediaData[i].isSavedInServer) {
                    mImageList.clear()
                    mJobMembers.clear()
                    for (j in mMediaData[i].medias!![0].users!!.indices) {
                        var mUsers = Users()
                        mUsers.userId = mMediaData[i].medias!![0].users!![j].userId
                        mUsers.primaryUserId = mMediaData[i].medias!![0].users!![j].primaryUserId
                        mUsers.permissions.base =
                            mMediaData[i].medias!![0].users!![j].permissions.base
                        mUsers.permissions.comments =
                            mMediaData[i].medias!![0].users!![j].permissions.comments
                        mUsers.permissions.members =
                            mMediaData[i].medias!![0].users!![j].permissions.members
                    }

                    var mBeforeAfterImageModel=BeforeAfterImageModel()
                    if (mMediaData[i].medias!!.size==2){
                        //Add beforeImage
                        mBeforeAfterImageModel.beforeImage=mMediaData[i].medias!![0].mediaURL
                        mBeforeAfterImageModel.beforeImageName=mMediaData[i].medias!![0].name
                        mBeforeAfterImageModel.tagsBefore=mMediaData[i].medias!![0].tags
                        mBeforeAfterImageModel.usersbefore=mJobMembers

                        //Add AfterImage
                        mBeforeAfterImageModel.afterImage=mMediaData[i].medias!![1].mediaURL
                        mBeforeAfterImageModel.afterImageName=mMediaData[i].medias!![1].name
                        mBeforeAfterImageModel.tagsAfter=mMediaData[i].medias!![1].tags
                        mBeforeAfterImageModel.usersAfter=mJobMembers

                        mImageList.add(mBeforeAfterImageModel)
                    }else{
                        //Add simpleImage
                        mBeforeAfterImageModel.simpleImage=mMediaData[i].medias!![0].mediaURL
                        mBeforeAfterImageModel.simpleImageName=mMediaData[i].medias!![0].name
                        mBeforeAfterImageModel.tags=mMediaData[i].medias!![0].tags
                        mBeforeAfterImageModel.users=mJobMembers

                        mImageList.add(mBeforeAfterImageModel)
                    }

                    //Call api
                    addJobMedia(mMediaData[i].jobId,mImageList)

                    //Delete from local after sync in server
                    CamMaxRoomDatabase.getDatabase(getApplication()).media()
                        .deleteMedia(mMediaData[i].mediaLocalId.toString())
                }}
        }
    }

     fun syncSinglePhotosLocalToServer(mJobId:String,mLocalId: String) {
        var mMediaData =
            CamMaxRoomDatabase.getDatabase(getApplication()).media().getMediaByKindByLocalId(mLocalId,JOB_KIND_PHOTO)
        var mImageList = ArrayList<BeforeAfterImageModel>()
        var mJobMembers = ArrayList<Users>()

        if (mMediaData.isNotEmpty()){
            //Make a api request
            for (i in mMediaData.indices) {
                if (!mMediaData[i].isSavedInServer) {
                    mImageList.clear()
                    mJobMembers.clear()
                    for (j in mMediaData[i].medias!![0].users!!.indices) {
                        var mUsers = Users()
                        mUsers.userId = mMediaData[i].medias!![0].users!![j].userId
                        mUsers.primaryUserId = mMediaData[i].medias!![0].users!![j].primaryUserId
                        mUsers.permissions.base =
                            mMediaData[i].medias!![0].users!![j].permissions.base
                        mUsers.permissions.comments =
                            mMediaData[i].medias!![0].users!![j].permissions.comments
                        mUsers.permissions.members =
                            mMediaData[i].medias!![0].users!![j].permissions.members
                    }

                    var mBeforeAfterImageModel=BeforeAfterImageModel()
                    if (mMediaData[i].medias!!.size==2){
                        //Add beforeImage
                        mBeforeAfterImageModel.beforeImage=mMediaData[i].medias!![0].mediaURL
                        mBeforeAfterImageModel.beforeImageName=mMediaData[i].medias!![0].name
                        mBeforeAfterImageModel.tagsBefore=mMediaData[i].medias!![0].tags
                        mBeforeAfterImageModel.usersbefore=mJobMembers

                        //Add AfterImage
                        mBeforeAfterImageModel.afterImage=mMediaData[i].medias!![1].mediaURL
                        mBeforeAfterImageModel.afterImageName=mMediaData[i].medias!![1].name
                        mBeforeAfterImageModel.tagsAfter=mMediaData[i].medias!![1].tags
                        mBeforeAfterImageModel.usersAfter=mJobMembers

                        mImageList.add(mBeforeAfterImageModel)
                    }else{
                        //Add simpleImage
                        mBeforeAfterImageModel.simpleImage=mMediaData[i].medias!![0].mediaURL
                        mBeforeAfterImageModel.simpleImageName=mMediaData[i].medias!![0].name
                        mBeforeAfterImageModel.tags=mMediaData[i].medias!![0].tags
                        mBeforeAfterImageModel.users=mJobMembers

                        mImageList.add(mBeforeAfterImageModel)
                    }

                    //Call api
                    addJobMedia(mJobId,mImageList)

                    //Delete from local after sync in server
                    CamMaxRoomDatabase.getDatabase(getApplication()).media()
                        .deleteMedia(mMediaData[i].mediaLocalId.toString())
                }}
        }
    }


    fun onGetMembers() = mMembersList
    fun onMediaUpdated() = isMediaUpdated
}