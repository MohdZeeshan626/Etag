package com.max360group.cammax360.viewmodels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.interactors.AccountInteractor
import com.max360group.cammax360.repository.models.*
import com.max360group.cammax360.repository.networkrequests.NetworkRequestCallbacks
import com.max360group.cammax360.repository.networkrequests.RetrofitRequest
import com.max360group.cammax360.repository.room.CamMaxRoomDatabase
import com.max360group.cammax360.utils.AmazonS3
import com.max360group.cammax360.utils.AmazonS3.Companion.S3_BUCKET_FOR_USER_PHOTOS
import com.max360group.cammax360.utils.GeneralFunctions
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.File

class AccountViewModel(application: Application) : BaseViewModel(application) {

    private val mAccountInteractor by lazy {
        AccountInteractor()
    }
    private var mUser = MutableLiveData<User>()
    private var isProfileUpdate = MutableLiveData<Boolean>()
    private var isPasswordUpdate = MutableLiveData<Boolean>()
    private var onAccountUpdated = MutableLiveData<Boolean>()

    fun getProfile(showLoader: Boolean = true) {
        if (showLoader) {
            isShowLoader.value = true
        }
        mCompositeDisposable.add(
            mAccountInteractor.getProfile(
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
                                        response.body() as PojoUserLogin
                                    mUser.value = mResponse.data!!.user!!

                                    //Add token in new user detail
                                    val mAuth = Auth()
                                    mAuth.token = mUserPrefsManager.accessToken

                                    val mUserData = UserData()
                                    mUserData.auth = mAuth
                                    mUserData.user = mResponse.data!!.user

                                    //Save user
                                    mUserPrefsManager.saveUserSession(
                                        true,
                                        mUserData
                                    )

                                    //Save user in room database
                                    mResponse.data!!.user!!.accessToken =
                                        mResponse.data!!.auth!!.token
                                    mResponse.data!!.user!!.isLoggedIn = true
                                    saveUserToRoom(mResponse.data!!.user!!)
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

    fun updateProfile(firstName: String, lastName: String, profile: String) {

        var mProfileRequest = profile

        val bucketName = S3_BUCKET_FOR_USER_PHOTOS + "FieldMax360/development/public/" +
                "${mUserPrefsManager.loginedUser!!.accounts?.get(0)?.primaryUserId!!.id}" + "account"

        val serverName = "FieldMax360/development/public/" +
                "${mUserPrefsManager.loginedUser!!.accounts?.get(0)?.primaryUserId!!.id}" + "account/"

        if (profile.isNotEmpty()) {
            if (!profile.startsWith("FieldMax360")) {
                val file = File(profile)

                // Upload photos to s3
                AmazonS3(getApplication()).uploadFileToS3(
                    file,
                    bucketName
                )
                mProfileRequest = serverName + file.name

            }
        }

        isShowLoader.value = true
        mCompositeDisposable.add(
            mAccountInteractor.updateProfile(firstName, lastName, mProfileRequest,
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
                                    isProfileUpdate.value = true
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

    fun updatePassword(oldPassword: String, newPassword: String, confirmPassword: String) {
        when {
            oldPassword.isBlank() -> errorHandler.value = ErrorHandler.EMPTY_OLD_PASSWORD
            newPassword.isBlank() -> errorHandler.value = ErrorHandler.EMPTY_NEW_PASSWORD
            confirmPassword.isBlank() -> errorHandler.value = ErrorHandler.EMPTY_CONFIRM_PASSWORD
            confirmPassword != newPassword -> errorHandler.value =
                ErrorHandler.INVALID_CONFIRM_PASSWORD
            else -> {
                isShowLoader.value = true
                mCompositeDisposable.add(
                    mAccountInteractor.updatePassword(oldPassword, newPassword,
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
                                            isPasswordUpdate.value = true
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

    fun updateAccount(
        accountId: String,
        name: String, email: String, number: String, address: OwnerAddressModel, image: String,
        primaryColor: String, primaryLightColor: String
    ) {

        when {
            name.isBlank() -> errorHandler.value = ErrorHandler.EMPTY_FIRST_NAME
            email.isBlank() -> errorHandler.value = ErrorHandler.EMPTY_EMAIL
            !GeneralFunctions.isValidEmail(email) -> errorHandler.value = ErrorHandler.INVALID_EMAIL
            else -> {

                var mProfileRequest = image

                val bucketName = S3_BUCKET_FOR_USER_PHOTOS + "FieldMax360/development/public/" +
                        "${mUserPrefsManager.loginedUser!!.accounts?.get(0)?.primaryUserId!!.id}" + "account"

                val serverName = "FieldMax360/development/public/" +
                        "${mUserPrefsManager.loginedUser!!.accounts?.get(0)?.primaryUserId!!.id}" + "account/"

                if (image.isNotEmpty()) {
                    if (!image.startsWith("FieldMax360")) {
                        val file = File(image)

                        // Upload photos to s3
                        AmazonS3(getApplication()).uploadFileToS3(
                            file,
                            bucketName
                        )
                        mProfileRequest = serverName + file.name

                    }
                }

                isShowLoader.value = true
                mCompositeDisposable.add(
                    mAccountInteractor.updateAccount(accountId, UpdateAccountRequestModel(
                        address = address,
                        logo = mProfileRequest,
                        name = name,
                        phone = number,
                        primaryEmail = email,
                        theme = Theme(
                            primaryColor,
                            primaryLightColor
                        )
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

                                            getProfile(false)
                                            successMessage.value = mResponse.message
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
                                        ) }
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

    fun saveUserToRoom(user: User) {
        with(user) {
            viewModelScope.launch {
                //Save user to database
                CamMaxRoomDatabase.getDatabase(getApplication()).userDao().insertUser(user)
            }
        }
    }

    fun updateUserInLocal(firstName: String, lastName: String, mImageFile: String) {
        viewModelScope.launch {
            CamMaxRoomDatabase.getDatabase(getApplication()).userDao().updateUser(
                firstName,lastName,mImageFile,true)
            isProfileUpdate.value = true
            successMessage.value="Profile updated successfully"
        }
    }

    fun updateAccountInLocal(
        accountId: String,
        name: String, email: String, number: String, address: OwnerAddressModel, image: String,
        primaryColor: String, primaryLightColor: String
    ){
        viewModelScope.launch {
            val mUserdata=CamMaxRoomDatabase.getDatabase(getApplication()).userDao().getUser()
            if (mUserdata!=null){
                val mAccountList=mUserdata.accounts
                if (mAccountList!!.isNotEmpty()){
                    mAccountList[0].primaryUserId!!.id=accountId
                    mAccountList[0].isUpdateInLocal=true
                    mAccountList[0].primaryUserId!!.account!!.name=name
                    mAccountList[0].primaryUserId!!.account!!.primaryEmail=email
                    mAccountList[0].primaryUserId!!.account!!.phone=number
                    mAccountList[0].primaryUserId!!.account!!.address=address
                    mAccountList[0].primaryUserId!!.account!!.logoURL=image
                    mUserdata.theme!!.primary=primaryColor
                    mUserdata.theme!!.primaryLight=primaryLightColor
                }

                //Insert updated data
                CamMaxRoomDatabase.getDatabase(getApplication()).userDao().insertUser(mUserdata)

                //Update user preference
                val mAuth = Auth()
                mAuth.token = mUserPrefsManager.accessToken

                val mUser = UserData()
                mUser.auth = mAuth
                mUser.user = mUserdata

                //Save user
                mUserPrefsManager.saveUserSession(
                    true,
                    mUser
                )
                onAccountUpdated.value=true
            }
        }
    }

    fun syncUserLocalToServer(){
        val mUserData=CamMaxRoomDatabase.getDatabase(getApplication()).userDao().getUser()
        if (mUserData!=null){
            //Call api
                if (mUserData.isLocalUpdate!!){
                    updateProfile(mUserData.firstName!!,
                        mUserData.lastName!!,mUserData.picURL!!)
                } }
    }

    fun syncUserAccountLocalToServer(){
        val mUserData=CamMaxRoomDatabase.getDatabase(getApplication()).userDao().getUser()
        if (mUserData!=null){
            //Call api
            if (mUserData.accounts!![0].isUpdateInLocal!!){
                updateAccount(mUserData.accounts!![0].primaryUserId!!.id!!,
                    mUserData.accounts!![0].primaryUserId!!.account!!.name!!,
                    mUserData.accounts!![0].primaryUserId!!.account!!.primaryEmail!!,
                    mUserData.accounts!![0].primaryUserId!!.account!!.phone!!,
                    mUserData.accounts!![0].primaryUserId!!.account!!.address!!,
                    mUserData.accounts!![0].primaryUserId!!.account!!.logoURL!!,
                    mUserData.theme!!.primary!!,mUserData.theme!!.primaryLight!!)
            }
        }
    }

    fun getUserProfileFromLocal() {
        val mUserData=CamMaxRoomDatabase.getDatabase(getApplication()).userDao().getUser()
        if (mUserData!=null){
            mUser.value = mUserData
        } }

    fun onGetProfile() = mUser
    fun onAccountUpdated() = onAccountUpdated
    fun onProfileUpdate() = isProfileUpdate
    fun onPasswordUpdate() = isPasswordUpdate

}