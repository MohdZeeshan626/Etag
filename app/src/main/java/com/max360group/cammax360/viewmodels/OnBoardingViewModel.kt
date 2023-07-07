package com.max360group.cammax360.viewmodels

import android.app.Application
import android.provider.Settings
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.max360group.cammax360.R
import com.max360group.cammax360.editorclasses.TextEditorDialogFragment.Companion.TAG
import com.max360group.cammax360.repository.interactors.OnBoardingInteractor
import com.max360group.cammax360.repository.models.PojoUserLogin
import com.max360group.cammax360.repository.models.RetrofitErrorMessage
import com.max360group.cammax360.repository.models.SimpleSuccessResponse
import com.max360group.cammax360.repository.models.User
import com.max360group.cammax360.repository.networkrequests.NetworkRequestCallbacks
import com.max360group.cammax360.repository.networkrequests.RetrofitRequest
import com.max360group.cammax360.repository.room.CamMaxRoomDatabase
import com.max360group.cammax360.utils.ApplicationGlobal
import com.max360group.cammax360.utils.GeneralFunctions
import kotlinx.coroutines.launch
import retrofit2.Response

class OnBoardingViewModel(application: Application) : BaseViewModel(application) {

    private val mOnBoardingInteractor by lazy {
        OnBoardingInteractor()
    }

    private val isLogin = MutableLiveData<Boolean>()
    private val isForgotPasswordSucess = MutableLiveData<Boolean>()

    fun register(
        firstName: String,
        lastname: String,
        email: String,
        password: String,
        confirmPassword: String

    ) {
        when {
            firstName.isBlank() -> errorHandler.value = ErrorHandler.EMPTY_FIRST_NAME
            lastname.isBlank() -> errorHandler.value = ErrorHandler.EMPTY_LAST_NAME
            email.isBlank() -> errorHandler.value = ErrorHandler.EMPTY_EMAIL
            !GeneralFunctions.isValidEmail(email) -> errorHandler.value =
                ErrorHandler.INVALID_EMAIL
            password.isBlank() -> errorHandler.value = ErrorHandler.EMPTY_PASSWORD
            password.length < 6 -> errorHandler.value = ErrorHandler.INVALID_PASSWORD
            confirmPassword != password -> errorHandler.value =
                ErrorHandler.INVALID_CONFIRM_PASSWORD

            else -> {
                isShowLoader.value = true
                mCompositeDisposable.add(
                    mOnBoardingInteractor.register(firstName, lastname, email, password,ApplicationGlobal.mAndroidId,ApplicationGlobal.mFcmToken,
                        object :
                            NetworkRequestCallbacks {
                            override fun onSuccess(response: Response<*>) {
                                try {
                                    isShowLoader.value = false
                                    val pojoNetworkResponse =
                                        RetrofitRequest.checkForResponseCode(response.code())
                                    when {
                                        pojoNetworkResponse.isSuccess && null != response.body() -> {
                                            val pojoUserLogin =
                                                response.body() as PojoUserLogin

                                            mUserPrefsManager.saveUserSession(
                                                true,
                                                pojoUserLogin.data!!
                                            )
                                            isLogin.value = true

                                            //Save data in database
                                            pojoUserLogin.data!!.user!!.accessToken =
                                                pojoUserLogin.data!!.auth!!.token
                                            pojoUserLogin.data!!.user!!.password = password
                                            pojoUserLogin.data!!.user!!.isLoggedIn = true
                                            saveUserToRoom(pojoUserLogin.data!!.user!!)
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

    fun login(
        email: String,
        password: String

    ) {
        when {
            email.isBlank() -> errorHandler.value = ErrorHandler.EMPTY_EMAIL
            !GeneralFunctions.isValidEmail(email) -> errorHandler.value =
                ErrorHandler.INVALID_EMAIL
            password.isBlank() -> errorHandler.value = ErrorHandler.EMPTY_PASSWORD
            password.length < 6 -> errorHandler.value = ErrorHandler.INVALID_PASSWORD
            else -> {
                isShowLoader.value = true
                mCompositeDisposable.add(
                    mOnBoardingInteractor.login(email, password,ApplicationGlobal.mAndroidId,ApplicationGlobal.mFcmToken,
                        object :
                            NetworkRequestCallbacks {
                            override fun onSuccess(response: Response<*>) {
                                try {
                                    isShowLoader.value = false
                                    val pojoNetworkResponse =
                                        RetrofitRequest.checkForResponseCode(response.code())
                                    when {
                                        pojoNetworkResponse.isSuccess && null != response.body() -> {
                                            val pojoUserLogin = response.body() as PojoUserLogin

                                            mUserPrefsManager.saveUserSession(
                                                true,
                                                pojoUserLogin.data!!
                                            )
                                            isLogin.value = true

                                            //Save data in database
                                            pojoUserLogin.data!!.user!!.accessToken =
                                                pojoUserLogin.data!!.auth!!.token
                                            pojoUserLogin.data!!.user!!.password = password
                                            pojoUserLogin.data!!.user!!.isLoggedIn = true
                                            saveUserToRoom(pojoUserLogin.data!!.user!!)
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

    fun forgotPassword(
        email: String
    ) {
        when {
            email.isBlank() -> errorHandler.value = ErrorHandler.EMPTY_EMAIL
            !GeneralFunctions.isValidEmail(email) -> errorHandler.value =
                ErrorHandler.INVALID_EMAIL
            else -> {
                isShowLoader.value = true
                mCompositeDisposable.add(
                    mOnBoardingInteractor.forgotPassword(email,
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

                                            isForgotPasswordSucess.value = true
                                            successMessage.value = mResponse.message
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

    fun getUser(email: String, password: String) {

        var user =
            CamMaxRoomDatabase.getDatabase(getApplication()).userDao().getUserByEmailPassword(
                email
            )
        if (null != user) {
            if (user.password == password) {
                if (user.accessToken!!.isNotBlank()) {
                    mUserPrefsManager.saveUserId(user.id)
                    user.isLoggedIn=true

                    isLogin.value=true

                } else {
                    errorHandler.value = ErrorHandler.NO_INTERNET
                }

            } else {
                errorHandler.value = ErrorHandler.INCORRECT_PASSWORD
            }
        } else {
            errorHandler.value = ErrorHandler.NO_INTERNET
        }
    }

    fun saveUserToRoom(user: User) {
        with(user) {
            viewModelScope.launch {
                //Save user to database
                CamMaxRoomDatabase.getDatabase(getApplication()).userDao().insertUser(user)
            }
        }
    }


    fun onSignup() = isLogin
    fun onForgotPassword() = isForgotPasswordSucess
}