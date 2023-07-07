package com.max360group.cammax360.viewmodels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.interactors.MediaDetailInteractor
import com.max360group.cammax360.repository.models.*
import com.max360group.cammax360.repository.models.model.User
import com.max360group.cammax360.repository.models.model.Users
import com.max360group.cammax360.repository.networkrequests.NetworkRequestCallbacks
import com.max360group.cammax360.repository.networkrequests.RetrofitRequest
import com.max360group.cammax360.repository.room.CamMaxRoomDatabase
import com.max360group.cammax360.utils.AmazonS3
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.views.utils.JobsConstants
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.File

class MediaDetailViewModel(application: Application) : BaseViewModel(application) {

    private val mJobDetailInteractor by lazy {
        MediaDetailInteractor()
    }

    private var mCommentList = MutableLiveData<List<Comments>>()
    private var mMediaDetail = MutableLiveData<MediaData1>()
    private var isShowShimmer = MutableLiveData<Boolean>()
    private var isSuccess = MutableLiveData<Boolean>()
    private var isMediaDeleted = MutableLiveData<Boolean>()
    private var mMediaDetailFromLocal = MutableLiveData<JobMediaList>()

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

    fun updateMediaInfo(
        id: String, kind: String, name: String, mTagsList: List<String>, mMedia: String,
        thumbnailUrl: String = "", mEditMediaFil: String = "", mJobId: String = ""
    ) {
        //when call from photo edit
        var mPhotoMedia = mMedia
        if (mEditMediaFil.isNotEmpty()) {
            var bucketName = JobsConstants.savePathForAws(
                AmazonS3.S3_BUCKET_FOR_USER_PHOTOS,
                mUserPrefsManager.loginedUser!!.accounts?.get(0)?.primaryUserId!!.id.toString(),
                mJobId
            )

            if (!GeneralFunctions.isRemoteImage(mEditMediaFil)) {
                val file = File(mEditMediaFil)

                // Upload photos to s3
                AmazonS3(getApplication()).uploadFileToS3(
                    file,
                    bucketName
                )
                mPhotoMedia = JobsConstants.savePathForServer(
                    mUserPrefsManager.loginedUser!!.accounts?.get(0)?.primaryUserId!!.id.toString(),
                    mJobId,
                    file.name
                )

            }
        }
        isShowLoader.value = true
        mCompositeDisposable.add(
            mJobDetailInteractor.updateMediaInfo(id, kind, UpdateMediaRequetsModel(
                tags = mTagsList,
                name = name,
                media = mPhotoMedia,
                thumbnail = thumbnailUrl//for video info
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

                                    successMessage.value = "Photo updated successfully"
                                    isSuccess.value = true
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
                            isShowLoader.value = false
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

    fun updateMediaPermissions(
        id: String, kind: String, mList: List<Users>


    ) {
        //Add members in request list
        var mediaUsers = ArrayList<User>()
        for (i in mList.indices) {
            var users = User()
            users.permissions = mList[i].permissions
            users.primaryUserId = mList[i].primaryUserId
            users.userId = mList[i].userId
            mediaUsers.add(users)

        }
        isShowLoader.value = true
        mCompositeDisposable.add(
            mJobDetailInteractor.updateMediaPermissions(id, kind, UpdateMediaPermissionsRequests(
                mediaUsers
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
                                    isSuccess.value = true
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
                            isShowLoader.value = false
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

    fun getPhotoDetail(mJobId: String, mMediaId: String, kind: String, mSubMediaId: String) {
        isShowShimmer.value = true
        mCompositeDisposable.add(
            mJobDetailInteractor.getMediaDetail(mJobId, mMediaId, kind, mSubMediaId,
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
                                        response.body() as MediaDetailResponseModel
                                    mMediaDetail.value = mResponse.data.media
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


    fun deleteMedia(mJobId: String, mKind: String, mMedia: List<String>) {
        isShowLoader.value = true
        mCompositeDisposable.add(
            mJobDetailInteractor.deleteMedia(mJobId, mKind, mMedia,
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
                                    isMediaDeleted.value = true
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
                            isShowLoader.value = false
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


    fun updateMediaInfoInLocal(
        id: String,
        kind: String,
        name: String,
        mTagsList: List<String>,
        media: String,
        mediaUrl: String,
        mediaPosition: Int,
        thumbnail: String = "",
        isEditUrl:Boolean=false
    ) {
        viewModelScope.launch {
            //Get media which has to be update
            val mediaList = ArrayList<JobMedia>()
            val mMedia = CamMaxRoomDatabase.getDatabase(getApplication()).media().getMediaById(id)

            if (mMedia != null) {
                mediaList.addAll(mMedia.medias!!)
                if (mediaList.isNotEmpty()) {
                    mediaList[mediaPosition].tags = mTagsList
                    mediaList[mediaPosition].media = media
                    mediaList[mediaPosition].mediaURL = mediaUrl
                    mediaList[mediaPosition].name = name
                    mediaList[mediaPosition].thumbnailURL = thumbnail
                    mediaList[mediaPosition].isUpdateInLocal = true
                    mediaList[mediaPosition].isEditUrl = isEditUrl

                    //update media
                    CamMaxRoomDatabase.getDatabase(getApplication()).media().updateMediaInfo(
                        id.toInt(), name, mTagsList, mediaList
                    )
                    isSuccess.value = true
                    successMessage.value="Media updated successfully"
                }
            }
        }
    }

    fun getMediaFromLocal(mMediaId: String) {
        val mMediaData =
            CamMaxRoomDatabase.getDatabase(getApplication()).media().getMediaById(mMediaId)
        if (mMediaData != null) {
            mMediaDetailFromLocal.value = mMediaData
        }
    }

    fun deleteMediaFromLocal(
        mMediaLocalId: String,
        mMediaKind: String,
        jobId: String,
        mMediaIds: ArrayList<String>
    ) {

        CamMaxRoomDatabase.getDatabase(getApplication()).media().deleteMedia(mMediaLocalId)

        //Save deleted media for delete from server when network is back
        if (mMediaIds.isNotEmpty()) {
            var mSavedMedia=ArrayList<MediaIds>()
            if (mUserPrefsManager.getMediaDeletedIds!=null){
                mSavedMedia = mUserPrefsManager.getMediaDeletedIds!!.deletedMedia!!
                mSavedMedia.add(MediaIds(mJobId = jobId, mMediaKind, mMediaIds))
            }
            val mDeletedMedia = DeletedMedia()
            mDeletedMedia.deletedMedia = mSavedMedia
            mUserPrefsManager.saveDeletedMediaIds(mDeletedMedia)
        }

        isMediaDeleted.value = true

    }

    fun deleteMediaLocalToServer() {
        val mMedia = mUserPrefsManager.getMediaDeletedIds
        if (mMedia != null) {
            for (i in mMedia.deletedMedia!!.indices) {
                //Call api
                deleteMedia(
                    mMedia.deletedMedia!![i].mJobId,
                    mMedia.deletedMedia!![i].kind, mMedia.deletedMedia!![i].idsList!!
                )
            }
            //Save the empty data after sync in server
            mUserPrefsManager.saveDeletedMediaIds(DeletedMedia())
        }
    }

    fun updateMediaLocalToServer(){
        val mMedia=CamMaxRoomDatabase.getDatabase(getApplication()).media().getAllMedia()
        if (mMedia.isNotEmpty()){
            for (i in mMedia.indices){
                mMedia[i].medias!!.indices.forEach { j ->
                    if (mMedia[i].medias!![j].isUpdateInLocal){
                        var mMediaUrl=""

                        mMediaUrl = if (mMedia[i].medias!![j].isEditUrl){
                            mMedia[i].medias!![j].media
                        }else{
                            ""
                        }
                        updateMediaInfo(
                            id = mMedia[i].medias!![j].id,
                            kind = mMedia[i].kind,
                            name = mMedia[i].medias!![j].name,
                            mTagsList = mMedia[i].medias!![j].tags!!,
                            mMedia =  mMedia[i].medias!![j].media,
                            mEditMediaFil =  mMediaUrl,
                            mJobId = mMedia[i].jobId
                        )
                    }
                }
            }
        }
    }

    fun onShowShimmer() = isShowShimmer
    fun onGetComments() = mCommentList
    fun onGetMediaDetail() = mMediaDetail
    fun onSuccess() = isSuccess
    fun onDeleteMedia() = isMediaDeleted
    fun onGetPhotoDetailFromLocal() = mMediaDetailFromLocal

}