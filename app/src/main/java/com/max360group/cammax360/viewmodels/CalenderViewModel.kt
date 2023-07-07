package com.max360group.cammax360.viewmodels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.interactors.AccountInteractor
import com.max360group.cammax360.repository.interactors.CalenderInteractor
import com.max360group.cammax360.repository.interactors.OnBoardingInteractor
import com.max360group.cammax360.repository.models.*
import com.max360group.cammax360.repository.networkrequests.NetworkRequestCallbacks
import com.max360group.cammax360.repository.networkrequests.RetrofitRequest
import com.max360group.cammax360.utils.AmazonS3
import com.max360group.cammax360.utils.AmazonS3.Companion.S3_BUCKET_FOR_USER_PHOTOS
import com.max360group.cammax360.utils.ColorTheme
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.views.adapters.NotificationsAdapter
import com.max360group.cammax360.views.utils.JobsConstants
import retrofit2.Response
import java.io.File

class CalenderViewModel(application: Application) : BaseViewModel(application) {

    private val mCalenderInteractor by lazy {
        CalenderInteractor()
    }

    private var mEventsData = MutableLiveData<List<Events>>()
    private var isLoadEvent = MutableLiveData<Boolean>()
    private var mNotification = MutableLiveData<List<Record>>()
    private var isInvitationResponse = MutableLiveData<Boolean>()

    fun getEvents(showLoader: Boolean = true) {
        if (showLoader) {
            isLoadEvent.value = true
        }
        mCompositeDisposable.add(
            mCalenderInteractor.getEvents(
                object :
                    NetworkRequestCallbacks {
                    override fun onSuccess(response: Response<*>) {
                        isLoadEvent.value = false
                        try {
                            val pojoNetworkResponse =
                                RetrofitRequest.checkForResponseCode(response.code())
                            when {
                                pojoNetworkResponse.isSuccess && null != response.body() -> {
                                    val mResponse =
                                        response.body() as CalenderEventsResponseModel
                                    mEventsData.value = mResponse.data.records
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
                        isLoadEvent.value = false
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

    fun getNotifications(skip: Int, showLoader: Boolean = true) {
        if (showLoader) {
            isLoadEvent.value = true
        }
        isShowSwipeRefreshLayout.value = true
        mCompositeDisposable.add(
            mCalenderInteractor.getNotifications(skip, NotificationsAdapter.LIMIT,
                object :
                    NetworkRequestCallbacks {
                    override fun onSuccess(response: Response<*>) {
                        isShowSwipeRefreshLayout.value = false
                        try {
                            val pojoNetworkResponse =
                                RetrofitRequest.checkForResponseCode(response.code())
                            when {
                                pojoNetworkResponse.isSuccess && null != response.body() -> {
                                    val mResponse =
                                        response.body() as NotificationResponseModel

                                    val mList = ArrayList<Record>()
                                    mList.clear()
                                    mList.addAll(mResponse.data.records)
                                    mList.removeAll {it.kind != "JobCreate" &&
                                        it.kind != "JobInvite" && it.kind != "PhotoCreate" && it.kind != "VideoCreate" && it.kind != "DocCreate"
                                                && it.kind != "CommentCreate" && it.kind != "NoteCreate" && it.kind != "ConversationCreate" && it.kind != "JobDetailEdit"
                                    }

                                    mNotification.value = mList
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

    fun inviteAcceptReject(status: String, token: String) {
        isShowLoader.value = true
        mCompositeDisposable.add(
            mCalenderInteractor.inviteAcceptReject(token, status,
                object :
                    NetworkRequestCallbacks {
                    override fun onSuccess(response: Response<*>) {
                        isShowLoader.value = false
                        try {
                            val pojoNetworkResponse =
                                RetrofitRequest.checkForResponseCode(response.code())
                            when {
                                pojoNetworkResponse.isSuccess && null != response.body() -> {
                                    val mResponse =
                                        response.body() as SimpleSuccessResponse
                                    isInvitationResponse.value = true
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

    fun onGetEventsData() = mEventsData
    fun onLoadEvent() = isLoadEvent
    fun onGetNotifications() = mNotification
    fun onAcceptRejectInvite() = isInvitationResponse
}