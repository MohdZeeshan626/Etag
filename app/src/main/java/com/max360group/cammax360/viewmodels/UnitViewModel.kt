package com.max360group.cammax360.viewmodels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.interactors.UnitInteractor
import com.max360group.cammax360.repository.models.*
import com.max360group.cammax360.repository.models.model.CreateUnitWithoutPropertyRequestModel
import com.max360group.cammax360.repository.networkrequests.NetworkRequestCallbacks
import com.max360group.cammax360.repository.networkrequests.RetrofitRequest
import com.max360group.cammax360.utils.AmazonS3
import retrofit2.Response
import java.io.File


class UnitViewModel(application: Application) : BaseViewModel(application) {

    private val mOwnerMenuTabs = MutableLiveData<List<Int>>()
    private val mUnitInteractor by lazy {
        UnitInteractor()
    }

    private val bucketName = AmazonS3.S3_BUCKET_FOR_USER_PHOTOS + "FieldMax360/development/" +
            "${mUserPrefsManager.loginedUser!!.accounts?.get(0)?.primaryUserId!!.id}" + "/user"

    private val serverName = "FieldMax360/development/" +
            "${mUserPrefsManager.loginedUser!!.accounts?.get(0)?.primaryUserId!!.id}" + "/user/"

    private var mCreateUnitSuccess = MutableLiveData<UnitRecord>()
    private var mUnitDetail = MutableLiveData<UnitRecord>()

    fun getMenuTabs() {
        val mList = mutableListOf<Int>(
            R.string.st_general_info,
            R.string.st_note_history,
            R.string.st_access,
            R.string.st_integrations
        )
        mOwnerMenuTabs.value = mList
    }

    fun createUnit(
        propertyId: String,
        image: String,
        firstname: String,
        type: List<String>,
        bathroom: String,
        bedroom: String,
        sqft: String,
        rmDisplay: Boolean,
        addresses: ArrayList<OwnerAddressModel>,
        mNotesList: ArrayList<Note>,
        mList: ArrayList<AccountList>
    ) {
        when {
            image.isBlank() -> errorHandler.value = ErrorHandler.EMPTY_UNIT_IMAGE
            firstname.isBlank() -> errorHandler.value = ErrorHandler.EMPTY_UNIT_NAME
            type.isEmpty() -> errorHandler.value = ErrorHandler.EMPTY_UNIT_TYPE
            addresses[0].formatted.isEmpty() -> errorHandler.value =
                ErrorHandler.EMPTY_PRIMARY_ADDRESS

            else -> {
                val mNotesIds = ArrayList<String>()
                val mUsersIds = ArrayList<String>()
                var mPrimaryAddress = OwnerAddressModel()
                var mImageServerPath = ""
                var isAllAccess = false
                var mPropertyId:String?=null
                var bathRoomValue = bathroom
                var bedroomValue = bedroom
                var sqftValue = sqft
                mNotesIds.clear()
                mUsersIds.clear()

                //Filter notes ids
                for (i in mNotesList) {
                    mNotesIds.add(i.id)
                }

                //Filter users ids
                if (mList.isNotEmpty()) {
                    if (mList[0].isChecked) {
                        isAllAccess = true
                    } else {
                        mList.removeAt(0)
                        for (i in mList) {
                            if (i.isChecked) {
                                mUsersIds.add(i.id)
                            }
                        }
                    }
                }

                // Upload photos to s3
                if (image.isNotEmpty()) {
                    mImageServerPath = if (!image.startsWith("FieldMax360")) {
                        val file = File(image)

                        AmazonS3(getApplication()).uploadFileToS3(
                            file,
                            bucketName
                        )
                        serverName + file.name
                    } else {
                        image
                    }
                }

                //Get primary Address
                mPrimaryAddress = addresses[0]
                //Remove primary address from list
                addresses.removeAt(0)

                if (bathroom.isBlank()) {
                    bathRoomValue = "0"
                }
                if (bedroom.isBlank()) {
                    bedroomValue = "0"
                }
                if (sqft.isBlank()) {
                    sqftValue = "0"
                }

                if (propertyId.isNotBlank()){
                    mPropertyId=propertyId
                }

                isShowLoader.value = true
                mCompositeDisposable.add(
                    mUnitInteractor.createUnit(CreateUnitWithRequestModel(
                        access = Access(isAllAccess, mUsersIds),
                        addresses = addresses,
                        bathrooms = bathRoomValue,
                        bedrooms = bedroomValue,
                        name = firstname,
                        notes = mNotesIds,
                        primaryAddress = mPrimaryAddress,
                        rm = RmFields(enabled = rmDisplay),
                        squareFootage = sqftValue,
                        unitTypes = type,
                        propertyId = mPropertyId,
                        pic = mImageServerPath
                    ),
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
                                                response.body() as CreateUnitResponseModel
                                            mCreateUnitSuccess.value = mResponse.data.record
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

    fun createUnitWithoutProperty(
        image: String,
        firstname: String,
        type: List<String>,
        bathroom: String,
        bedroom: String,
        sqft: String,
        rmDisplay: Boolean,
        addresses: ArrayList<OwnerAddressModel>,
        mNotesList: ArrayList<Note>,
        mList: ArrayList<AccountList>
    ) {
        when {
            firstname.isBlank() -> errorHandler.value = ErrorHandler.EMPTY_UNIT_NAME
            type.isEmpty() -> errorHandler.value = ErrorHandler.EMPTY_UNIT_TYPE
            addresses[0].formatted.isEmpty() -> errorHandler.value =
                ErrorHandler.EMPTY_PRIMARY_ADDRESS

            else -> {
                val mNotesIds = ArrayList<String>()
                val mUsersIds = ArrayList<String>()
                var mPrimaryAddress = OwnerAddressModel()
                var mImageServerPath = ""
                var isAllAccess = false
                mNotesIds.clear()
                mUsersIds.clear()

                //Filter notes ids
                for (i in mNotesList) {
                    mNotesIds.add(i.id)
                }

                //Filter users ids
                if (mList.isNotEmpty()) {
                    if (mList[0].isChecked) {
                        isAllAccess = true
                    } else {
                        mList.removeAt(0)
                        for (i in mList) {
                            if (i.isChecked) {
                                mUsersIds.add(i.id)
                            }
                        }
                    }
                }

                // Upload photos to s3
                if (image.isNotEmpty()) {
                    mImageServerPath = if (!image.startsWith("FieldMax360")) {
                        val file = File(image)

                        AmazonS3(getApplication()).uploadFileToS3(
                            file,
                            bucketName
                        )
                        serverName + file.name
                    } else {
                        image
                    }
                }

                //Get primary Address
                mPrimaryAddress = addresses[0]
                //Remove primary address from list
                addresses.removeAt(0)

                isShowLoader.value = true
                mCompositeDisposable.add(
                    mUnitInteractor.createUnitWithoutProperty(
                        CreateUnitWithoutPropertyRequestModel(
                            access = Access(isAllAccess, mUsersIds),
                            addresses = addresses,
                            bathrooms = bathroom.ifBlank {
                                "0"
                            },
                            bedrooms = bedroom.ifBlank {
                                "0"
                            },
                            name = firstname,
                            notes = mNotesIds,
                            primaryAddress = mPrimaryAddress,
                            rm = RmFields(enabled = rmDisplay),
                            squareFootage = sqft.ifBlank {
                                "0"
                            },
                            unitTypes = type,
                            pic = mImageServerPath
                        ),
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
                                                response.body() as CreateUnitResponseModel
                                            mCreateUnitSuccess.value = mResponse.data.record
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

    fun getUnitDetail(
       id:String,
    ) {
        isShowLoader.value = true
                mCompositeDisposable.add(
                    mUnitInteractor.getUnitDetail(id,
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
                                                response.body() as CreateUnitResponseModel
                                            mUnitDetail.value = mResponse.data.record
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

    fun editUnit(
        unitId:String,
        propertyId: String,
        image: String,
        firstname: String,
        type: List<String>,
        bathroom: String,
        bedroom: String,
        sqft: String,
        rmDisplay: Boolean,
        addresses: ArrayList<OwnerAddressModel>,
        mNotesList: ArrayList<Note>,
        mList: ArrayList<AccountList>
    ) {
        when {
            image.isBlank() -> errorHandler.value = ErrorHandler.EMPTY_UNIT_IMAGE
            firstname.isBlank() -> errorHandler.value = ErrorHandler.EMPTY_UNIT_NAME
            type.isEmpty() -> errorHandler.value = ErrorHandler.EMPTY_UNIT_TYPE
            addresses[0].formatted.isEmpty() -> errorHandler.value =
                ErrorHandler.EMPTY_PRIMARY_ADDRESS

            else -> {
                val mNotesIds = ArrayList<String>()
                val mUsersIds = ArrayList<String>()
                var mPrimaryAddress = OwnerAddressModel()
                var mImageServerPath = ""
                var isAllAccess = false
                var bathRoomValue = bathroom
                var bedroomValue = bedroom
                var sqftValue = sqft
                mNotesIds.clear()
                mUsersIds.clear()

                //Filter notes ids
                for (i in mNotesList) {
                    mNotesIds.add(i.id)
                }

                //Filter users ids
                if (mList.isNotEmpty()) {
                    if (mList[0].isChecked) {
                        isAllAccess = true
                    } else {
                        mList.removeAt(0)
                        for (i in mList) {
                            if (i.isChecked) {
                                mUsersIds.add(i.id)
                            }
                        }
                    }
                }

                // Upload photos to s3
                if (image.isNotEmpty()) {
                    mImageServerPath = if (!image.startsWith("FieldMax360")) {
                        val file = File(image)

                        AmazonS3(getApplication()).uploadFileToS3(
                            file,
                            bucketName
                        )
                        serverName + file.name
                    } else {
                        image
                    }
                }

                //Get primary Address
                mPrimaryAddress = addresses[0]
                //Remove primary address from list
                addresses.removeAt(0)

                if (bathroom.isBlank()) {
                    bathRoomValue = "0"
                }
                if (bedroom.isBlank()) {
                    bedroomValue = "0"
                }
                if (sqft.isBlank()) {
                    sqftValue = "0"
                }

                isShowLoader.value = true
                mCompositeDisposable.add(
                    mUnitInteractor.editUnit(unitId,CreateUnitWithRequestModel(
                        access = Access(isAllAccess, mUsersIds),
                        addresses = addresses,
                        bathrooms = bathRoomValue,
                        bedrooms = bedroomValue,
                        name = firstname,
                        notes = mNotesIds,
                        primaryAddress = mPrimaryAddress,
                        rm = RmFields(enabled = rmDisplay),
                        squareFootage = sqftValue,
                        unitTypes = type,
                        propertyId = propertyId,
                        pic = mImageServerPath
                    ),
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
                                                response.body() as CreateUnitResponseModel
                                            mCreateUnitSuccess.value = mResponse.data.record
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

    fun onGetTabMenu() = mOwnerMenuTabs
    fun onUnitDetail() = mUnitDetail
    fun onCreateUnit() = mCreateUnitSuccess

}