package com.max360group.cammax360.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.interactors.PropertyInteractor
import com.max360group.cammax360.repository.models.*
import com.max360group.cammax360.repository.networkrequests.NetworkRequestCallbacks
import com.max360group.cammax360.repository.networkrequests.RetrofitRequest
import com.max360group.cammax360.repository.room.CamMaxRoomDatabase
import com.max360group.cammax360.utils.AmazonS3
import com.max360group.cammax360.views.adapters.IntegrationTypeAdapter
import com.max360group.cammax360.views.adapters.PropertiesAdapter
import com.max360group.cammax360.views.fragments.CreatePropertyFragment.Companion.INTEGRATION_CHARGE_TYPE
import com.max360group.cammax360.views.fragments.CreatePropertyFragment.Companion.INTEGRATION_PROPERTY_TYPE
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.File


class PropertyViewModel(application: Application) : BaseViewModel(application) {

    private val mPropertyInteractor by lazy {
        PropertyInteractor()
    }

    private val bucketName = AmazonS3.S3_BUCKET_FOR_USER_PHOTOS + "FieldMax360/development/" +
            "${mUserPrefsManager.loginedUser!!.accounts?.get(0)?.primaryUserId!!.id}" + "/properties"


    private val serverName = "FieldMax360/development/" +
            "${mUserPrefsManager.loginedUser!!.accounts?.get(0)?.primaryUserId!!.id}" + "/properties/"


    private val mIntegrationData = MutableLiveData<List<IntegrationData>>()
    private val mAllOwners = MutableLiveData<List<UserOwner>>()
    private var mTabsList = MutableLiveData<List<Int>>()
    private var mPropertiesList = MutableLiveData<List<PropertyDetail>>()
    private var isSuccess = MutableLiveData<Boolean>()
    private var isPropertyUpdated = MutableLiveData<Boolean>()
    private var mCreatePropertySuccess = MutableLiveData<PropertyDetail>()
    private var mPropertyDetail = MutableLiveData<PropertyDetail>()
    private var mIntegrationList = MutableLiveData<IntegrationList>()


    fun getTabs() {
        val mList = mutableListOf<Int>(
            R.string.st_general_info,
            R.string.st_owners,
            R.string.st_units,
            R.string.st_note_history,
            R.string.st_access,
            R.string.st_integrations
        )
        mTabsList.value = mList
    }

    fun getDetailTabs() {
        val mList = mutableListOf<Int>(
            R.string.st_general_info,
            R.string.st_owners,
            R.string.st_units,
            R.string.st_note_history,
            R.string.st_access,
            /* R.string.st_jobs,*/
            R.string.st_integrations
        )
        mTabsList.value = mList
    }

    fun getIntegration(
        type: String,
        skip: Int
    ) {
        mCompositeDisposable.add(
            mPropertyInteractor.getIntegrationType(type,
                skip.toString(),
                IntegrationTypeAdapter.LIMIT.toString(),
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
                                        response.body() as IntegrationCommans
                                    mIntegrationData.value = mResponse.data.list

                                    saveIntegrationCommonsInLocal(mResponse.data.list)
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

    fun getIntegrationAll(
    ) {
        mCompositeDisposable.add(
            mPropertyInteractor.getIntegrationAll(
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
                                        response.body() as IntegrationAllResponseModel
                                    mIntegrationList.value = mResponse.data.list

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

    fun createProperty(
        mImageFile: String,
        propertyName: String,
        sortName: String,
        taxId: String,
        mPropertyType: List<String>,
        mChargeType: ArrayList<String>,
        totalSqFit: String,
        comments: String,
        mAddressList: ArrayList<OwnerAddressModel>,
        mOwnersList: ArrayList<UserOwner>,
        mUnitList: List<UnitRecord>,
        mNotesList: ArrayList<Note>,
        mAccessList: ArrayList<AccountList>
    ) {
        when {
            mImageFile.isBlank() -> errorHandler.value = ErrorHandler.EMPTY_PROPERTY_IMAGE
            propertyName.isBlank() -> errorHandler.value = ErrorHandler.EMPTY_PROPERTY_NAME
            sortName.isBlank() -> errorHandler.value = ErrorHandler.EMPTY_SHORT_NAME
            mPropertyType.isEmpty() -> errorHandler.value = ErrorHandler.EMPTY_PROPERTY_TYPE
            mChargeType.isEmpty() -> errorHandler.value = ErrorHandler.EMPTY_CHARGE_TYPE
            mAddressList[0].formatted.isEmpty() -> errorHandler.value =
                ErrorHandler.EMPTY_PRIMARY_ADDRESS
            mAddressList[1].formatted.isEmpty() -> errorHandler.value =
                ErrorHandler.EMPTY_BILLING_ADDRESS
            else -> {
                isShowLoader.value = true
                val mUsersIds = ArrayList<String>()
                val mNotesIds = ArrayList<String>()
                val mUnitsIds = ArrayList<String>()
                val mUserOwners = ArrayList<String>()
                var mPrimaryAddress = OwnerAddressModel()
                var mBillingAddress = OwnerAddressModel()
                var mImageServerPath = ""
                var isAllAccess = false
                mUsersIds.clear()
                mNotesIds.clear()
                mUnitsIds.clear()
                mUserOwners.clear()

                //Filter notes ids
                for (i in mNotesList) {
                    mNotesIds.add(i.id)
                }

                //Filter unit ids
                for (i in mUnitList) {
                    mUnitsIds.add(i.id)
                }

                //Filter owners ids
                for (i in mOwnersList) {
                    mUserOwners.add(i.id)
                }

                //Filter users ids
                if (mAccessList.isNotEmpty()) {
                    if (mAccessList[0].isChecked) {
                        isAllAccess = true
                    } else {
                        mAccessList.removeAt(0)
                        for (i in mAccessList) {
                            if (i.isChecked) {
                                mUsersIds.add(i.id)
                            }
                        }
                    }
                }

                // Upload photos to s3
                if (mImageFile.isNotEmpty()) {
                    mImageServerPath = if (!mImageFile.startsWith("FieldMax360")) {
                        val file = File(mImageFile)

                        AmazonS3(getApplication()).uploadFileToS3(
                            file,
                            bucketName
                        )
                        serverName + file.name
                    } else {
                        mImageFile
                    }
                }


                //Get primary Address
                mPrimaryAddress = mAddressList[0]
                mBillingAddress = mAddressList[1]
                //Remove primary and billing from list
                mAddressList.removeAt(0)
                mAddressList.removeAt(0)

                mCompositeDisposable.add(
                    mPropertyInteractor.createProperty(CreatePropertyRequestModel(
                        access = Access(isAllAccess, mUsersIds),
                        addresses = mAddressList,
                        billingAddress = mBillingAddress,
                        chargeTypes = mChargeType,
                        comments = comments,
                        name = propertyName,
                        notes = mNotesIds,
                        pic = mImageServerPath,
                        primaryAddress = mPrimaryAddress,
                        propertyTypes = mPropertyType,
                        propertyUnits = mUnitsIds,
                        shortName = sortName,
                        squareFootage = totalSqFit.ifBlank {
                            "0"
                        },
                        userOwners = mUserOwners,
                        taxId = taxId
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
                                                response.body() as CreatePropertyResponseModel
                                            mCreatePropertySuccess.value = mResponse.data.record
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

    fun updateProperty(
        propertyId: String,
        mImageFile: String,
        propertyName: String,
        sortName: String,
        taxId: String,
        mPropertyType: ArrayList<String>,
        mChargeType: ArrayList<String>,
        totalSqFit: String,
        comments: String,
        mAddressList: ArrayList<OwnerAddressModel>,
        mOwnersList: ArrayList<UserOwner>,
        mUnitList: List<UnitRecord>,
        mNotesList: ArrayList<Note>,
        mAccessList: ArrayList<AccountList>
    ) {
        when {
            mImageFile.isBlank() -> errorHandler.value = ErrorHandler.EMPTY_PROPERTY_IMAGE
            propertyName.isBlank() -> errorHandler.value = ErrorHandler.EMPTY_PROPERTY_NAME
            sortName.isBlank() -> errorHandler.value = ErrorHandler.EMPTY_SHORT_NAME
            mPropertyType.isEmpty() -> errorHandler.value = ErrorHandler.EMPTY_PROPERTY_TYPE
            mChargeType.isEmpty() -> errorHandler.value = ErrorHandler.EMPTY_CHARGE_TYPE
            mAddressList[0].formatted.isEmpty() -> errorHandler.value =
                ErrorHandler.EMPTY_PRIMARY_ADDRESS
            mAddressList[1].formatted.isEmpty() -> errorHandler.value =
                ErrorHandler.EMPTY_BILLING_ADDRESS
            else -> {
                isShowLoader.value = true
                val mUsersIds = ArrayList<String>()
                val mNotesIds = ArrayList<String>()
                val mUnitsIds = ArrayList<String>()
                val mUserOwners = ArrayList<String>()
                var mPrimaryAddress = OwnerAddressModel()
                var mBillingAddress = OwnerAddressModel()
                var mImageServerPath = ""
                var isAllAccess = false
                mUsersIds.clear()
                mNotesIds.clear()
                mUnitsIds.clear()
                mUserOwners.clear()

                //Filter notes ids
                for (i in mNotesList) {
                    mNotesIds.add(i.id)
                }

                //Filter unit ids
                for (i in mUnitList) {
                    mUnitsIds.add(i.id)
                }

                //Filter owners ids
                for (i in mOwnersList) {
                    mUserOwners.add(i.id)
                }

                //Filter users ids
                if (mAccessList.isNotEmpty()) {
                    if (mAccessList[0].isChecked) {
                        isAllAccess = true
                    } else {
                        mAccessList.removeAt(0)
                        for (i in mAccessList) {
                            if (i.isChecked) {
                                mUsersIds.add(i.id)
                            }
                        }
                    }
                }

                // Upload photos to s3
                if (mImageFile.isNotEmpty()) {
                    mImageServerPath = if (!mImageFile.startsWith("FieldMax360")) {
                        val file = File(mImageFile)

                        AmazonS3(getApplication()).uploadFileToS3(
                            file,
                            bucketName
                        )
                        serverName + file.name
                    } else {
                        mImageFile
                    }
                }

                //Get primary Address
                mPrimaryAddress = mAddressList[0]
                mBillingAddress = mAddressList[1]
                //Remove primary and billing from list
                mAddressList.removeAt(0)
                mAddressList.removeAt(0)

                mCompositeDisposable.add(
                    mPropertyInteractor.updateProperty(propertyId, CreatePropertyRequestModel(
                        access = Access(isAllAccess, mUsersIds),
                        addresses = mAddressList,
                        billingAddress = mBillingAddress,
                        chargeTypes = mChargeType,
                        comments = comments,
                        name = propertyName,
                        notes = mNotesIds,
                        pic = mImageServerPath,
                        primaryAddress = mPrimaryAddress,
                        propertyTypes = mPropertyType,
                        propertyUnits = mUnitsIds,
                        shortName = sortName,
                        squareFootage = totalSqFit.ifBlank {
                            "0"
                        },
                        userOwners = mUserOwners,
                        taxId = taxId

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
                                            isPropertyUpdated.value = true
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

    fun getAllOwners(
    ) {
        mCompositeDisposable.add(
            mPropertyInteractor.getAllOwners(
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
                                        response.body() as OwnerListResponseModel
                                    mAllOwners.value = mResponse.data.records!!
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

    fun getProperties(
        skip: Int,
        search: String = "",
        showLoader:Boolean=true
    ) {
        isShowSwipeRefreshLayout.value = showLoader
        mCompositeDisposable.add(
            mPropertyInteractor.getProperties(skip, PropertiesAdapter.LIMIT, search,
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
                                        response.body() as PropertiesListResponseModel
                                    savePropertyInLocal(mResponse.data.records!!)

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

    fun deleteProperties(
        id: String
    ) {
        isShowSwipeRefreshLayout.value = true
        mCompositeDisposable.add(
            mPropertyInteractor.deleteProperty(id,
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
                                        response.body() as SimpleSuccessResponse
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

    fun getPropertyDetail(
        id: String
    ) {
        isShowLoader.value = true
        mCompositeDisposable.add(
            mPropertyInteractor.getPropertyDetail(id,
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
                                        response.body() as PropertyDetailResponseModel
                                    mPropertyDetail.value = mResponse.data.record
                                    savePropertyDetailInLocal(mResponse.data.record, id)
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

    fun deletePropertyUnit(
        id: String,
        showLoader: Boolean = true
    ) {
        isShowLoader.value = showLoader
        mCompositeDisposable.add(
            mPropertyInteractor.deletePropertyUnit(id,
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

    fun blockUnblock(
        id: String,
        block: Boolean,
    ) {
        isShowSwipeRefreshLayout.value = true
        mCompositeDisposable.add(
            mPropertyInteractor.blockUnblockProperty(id, block,
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
                                        response.body() as SimpleSuccessResponse
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

    fun savePropertyInLocal(records: List<PropertyDetail>) {
        viewModelScope.launch {
            //Delete all local properties to insert all new server properties
            CamMaxRoomDatabase.getDatabase(getApplication()).propertyDoa().deleteAllProperties()
            //Save the  values in local
            CamMaxRoomDatabase.getDatabase(getApplication()).propertyDoa()
                .insertProperties(records)

            //Get property from local
            val propertiesList =
                CamMaxRoomDatabase.getDatabase(getApplication()).propertyDoa().getAllProperties()
            mPropertiesList.value = propertiesList

        }
    }

    fun getPropertiesFromLocal() {
        val properties =
            CamMaxRoomDatabase.getDatabase(getApplication()).propertyDoa().getAllProperties()
        if (properties != null) {
            mPropertiesList.value = properties
            isShowSwipeRefreshLayout.value = false
        }
    }

    fun getPropertiesDetailFromLocal(propertyId: Int) {
        val detail =
            CamMaxRoomDatabase.getDatabase(getApplication()).propertyDoa()
                .getPropertyDetail(propertyId)
        if (detail != null) {
            mPropertyDetail.value = detail
        }
    }

    fun deletePropertyFromLocal(propertyId: String, propertyLocalId: Int) {
        //Delete property
        CamMaxRoomDatabase.getDatabase(getApplication()).propertyDoa()
            .deleteProperty(propertyLocalId)
        getPropertiesFromLocal()

        //Save deleted job from local
        if (propertyId.isNotBlank()) {
            var mSavedIds = ArrayList<String>()
            if (mUserPrefsManager.getPropertyDeletedIds != null) {
                mSavedIds = mUserPrefsManager.getPropertyDeletedIds!!.mPropertiesIds!!
                mSavedIds.add(propertyId)
            }

            val mDeletedProperties = DeletedProperties()
            mDeletedProperties.mPropertiesIds = mSavedIds
            mUserPrefsManager.saveDeletedPropertiesIds(mDeletedProperties)

        } else {
            errorHandler.value = ErrorHandler.NO_INTERNET
        }
    }

    fun searchProperties(value: String) {
        val properties =
            CamMaxRoomDatabase.getDatabase(getApplication()).propertyDoa().searchProperties(value)
        if (properties != null) {
            mPropertiesList.value = properties
        }
    }

    fun createPropertyInLocal(
        mImageFile: String,
        propertyName: String,
        sortName: String,
        taxId: String,
        mPropertyType: ArrayList<String>,
        mChargeType: ArrayList<String>,
        totalSqFit: String,
        comments: String,
        mAddressList: ArrayList<OwnerAddressModel>,
        mOwnersList: ArrayList<UserOwner>,
        mUnitList: List<UnitRecord>,
        mNotesList: ArrayList<Note>,
        mAccessList: ArrayList<AccountList>
    ) {
        when {
            mImageFile.isBlank() -> errorHandler.value = ErrorHandler.EMPTY_PROPERTY_IMAGE
            propertyName.isBlank() -> errorHandler.value = ErrorHandler.EMPTY_PROPERTY_NAME
            sortName.isBlank() -> errorHandler.value = ErrorHandler.EMPTY_SHORT_NAME
            mPropertyType.isEmpty() -> errorHandler.value = ErrorHandler.EMPTY_PROPERTY_TYPE
            mChargeType.isEmpty() -> errorHandler.value = ErrorHandler.EMPTY_CHARGE_TYPE
            mAddressList[0].formatted.isEmpty() -> errorHandler.value =
                ErrorHandler.EMPTY_PRIMARY_ADDRESS
            mAddressList[1].formatted.isEmpty() -> errorHandler.value =
                ErrorHandler.EMPTY_BILLING_ADDRESS
            else -> {
                viewModelScope.launch {
                    val mPropertyDetail = PropertyDetail()
                    val mUsersIds = ArrayList<String>()
                    val mNotesIds = ArrayList<String>()
                    var mImageServerPath = ""
                    var isAllAccess = false
                    mUsersIds.clear()
                    mNotesIds.clear()

                    //Filter notes ids
                    for (i in mNotesList) {
                        mNotesIds.add(i.id)
                    }

                    //Filter users ids
                    if (mAccessList.isNotEmpty()) {
                        if (mAccessList[0].isChecked) {
                            isAllAccess = true
                        } else {
                            mAccessList.removeAt(0)
                            for (i in mAccessList) {
                                if (i.isChecked) {
                                    mUsersIds.add(i.id)
                                }
                            }
                        }
                    }
                    mPropertyDetail.access = Access(all = isAllAccess, mUsersIds)
                    mPropertyDetail.billingAddress = mAddressList[1]
                    mPropertyDetail.primaryAddress = mAddressList[0]

                    //Remove primary and billing address
                    mAddressList.removeAt(0)
                    mAddressList.removeAt(0)

                    mPropertyDetail.addresses = mAddressList
                    mPropertyDetail.picURL = mImageFile
                    mPropertyDetail.pic = mImageFile
                    mPropertyDetail.chargeTypes = mChargeType
                    mPropertyDetail.comments = comments
                    mPropertyDetail.name = propertyName
                    mPropertyDetail.shortName = sortName
                    mPropertyDetail.propertyTypes = mPropertyType
                    mPropertyDetail.squareFootage = (totalSqFit.ifBlank {
                        "0"
                    }).toInt()
                    mPropertyDetail.taxId = taxId
                    mPropertyDetail.userOwners = mOwnersList
                    mPropertyDetail.propertyUnits!!.addAll(mUnitList)
                    mPropertyDetail.isSyncServer = false

                    //Get saved list from local
                    val mPropertyList = ArrayList<PropertyDetail>()
                    val localPropertiesList =
                        CamMaxRoomDatabase.getDatabase(getApplication()).propertyDoa()
                            .getAllProperties()
                    mPropertyList.addAll(localPropertiesList)
                    mPropertyList.add(mPropertyDetail)

                    //Insert list in local
                    CamMaxRoomDatabase.getDatabase(getApplication()).propertyDoa()
                        .insertProperties(mPropertyList)
                    mCreatePropertySuccess.value = mPropertyDetail
                }
            }
        }
    }

    fun savePropertyDetailInLocal(data: PropertyDetail, id: String) {
        val mProperty =
            CamMaxRoomDatabase.getDatabase(getApplication()).propertyDoa().getAllProperties()
        val mPropertyList = ArrayList<PropertyDetail>()
        mPropertyList.clear()
        mPropertyList.addAll(mProperty)

        viewModelScope.launch {
            if (mPropertyList.isNotEmpty()) {
                for (i in mPropertyList.indices) {
                    if (mPropertyList[i].id == id) {
                        data.propertyLocalId = mPropertyList[i].propertyLocalId
                        mPropertyList[i] = data
                        break
                    }
                }
            }

            CamMaxRoomDatabase.getDatabase(getApplication()).propertyDoa()
                .insertProperties(mPropertyList)
        }
    }

    fun saveIntegrationCommonsInLocal(list: List<IntegrationData>) {
        viewModelScope.launch {
            val mData = CamMaxRoomDatabase.getDatabase(getApplication()).integrationCommonsDoa()
                .getAllIntegration()
            val mIntegrationList = ArrayList<IntegrationData>()
            mIntegrationList.clear()
            mIntegrationList.addAll(mData)
            mIntegrationList.addAll(list)

            CamMaxRoomDatabase.getDatabase(getApplication()).integrationCommonsDoa()
                .insertIntegrationCommons(mIntegrationList)
        }
    }

    fun getIntegrationCommonsFromLocal(kind: String) {
        val mData = CamMaxRoomDatabase.getDatabase(getApplication()).integrationCommonsDoa()
            .getIntegrationByKind(kind)
        if (mData != null) {
            mIntegrationData.value = mData
        }
    }

    fun getAllIntegrationCommonsFromLocal() {
        val mPropertyData = CamMaxRoomDatabase.getDatabase(getApplication()).integrationCommonsDoa()
            .getIntegrationByKind(INTEGRATION_PROPERTY_TYPE)
        val mChargeData = CamMaxRoomDatabase.getDatabase(getApplication()).integrationCommonsDoa()
            .getIntegrationByKind(INTEGRATION_CHARGE_TYPE)
        val mIntegration = IntegrationList()

        mIntegration.propertyTypes = mPropertyData
        mIntegration.chargeTypes = mChargeData
        mIntegrationList.value = mIntegration

    }

    fun getOwnersFromDataBase() {
        val mOwners = CamMaxRoomDatabase.getDatabase(getApplication()).ownersDao().getAllOwner()
        if (mOwners != null) {
            mAllOwners.value = mOwners
            isShowSwipeRefreshLayout.value = false
        } else {
            errorHandler.value = ErrorHandler.NO_INTERNET
        }
    }

    fun updatePropertyInLocal(
        serverId: String,
        propertyLocalId: Int,
        mImageFile: String,
        propertyName: String,
        sortName: String,
        taxId: String,
        mPropertyType: ArrayList<String>,
        mChargeType: ArrayList<String>,
        totalSqFit: String,
        comments: String,
        mAddressList: ArrayList<OwnerAddressModel>,
        mOwnersList: ArrayList<UserOwner>,
        mUnitList: List<UnitRecord>,
        mNotesList: ArrayList<Note>,
        mAccessList: ArrayList<AccountList>
    ) {
        when {
            mImageFile.isBlank() -> errorHandler.value = ErrorHandler.EMPTY_PROPERTY_IMAGE
            propertyName.isBlank() -> errorHandler.value = ErrorHandler.EMPTY_PROPERTY_NAME
            sortName.isBlank() -> errorHandler.value = ErrorHandler.EMPTY_SHORT_NAME
            mPropertyType.isEmpty() -> errorHandler.value = ErrorHandler.EMPTY_PROPERTY_TYPE
            mChargeType.isEmpty() -> errorHandler.value = ErrorHandler.EMPTY_CHARGE_TYPE
            mAddressList[0].formatted.isEmpty() -> errorHandler.value =
                ErrorHandler.EMPTY_PRIMARY_ADDRESS
            mAddressList[1].formatted.isEmpty() -> errorHandler.value =
                ErrorHandler.EMPTY_BILLING_ADDRESS
            else -> {
                viewModelScope.launch {
                    val mPropertyDetail = PropertyDetail()
                    val mUsersIds = ArrayList<String>()
                    val mNotesIds = ArrayList<String>()
                    var isAllAccess = false
                    mUsersIds.clear()
                    mNotesIds.clear()

                    //Filter notes ids
                    for (i in mNotesList) {
                        mNotesIds.add(i.id)
                    }

                    //Filter users ids
                    if (mAccessList.isNotEmpty()) {
                        if (mAccessList[0].isChecked) {
                            isAllAccess = true
                        } else {
                            mAccessList.removeAt(0)
                            for (i in mAccessList) {
                                if (i.isChecked) {
                                    mUsersIds.add(i.id)
                                }
                            }
                        }
                    }

                    mPropertyDetail.access = Access(all = isAllAccess, mUsersIds)
                    mPropertyDetail.billingAddress = mAddressList[1]
                    mPropertyDetail.primaryAddress = mAddressList[0]

                    //Remove primary and billing address
                    mAddressList.removeAt(0)
                    mAddressList.removeAt(0)

                    mPropertyDetail.addresses = mAddressList
                    mPropertyDetail.picURL = mImageFile
                    mPropertyDetail.pic = mImageFile
                    mPropertyDetail.chargeTypes = mChargeType
                    mPropertyDetail.comments = comments
                    mPropertyDetail.name = propertyName
                    mPropertyDetail.shortName = sortName
                    mPropertyDetail.propertyTypes = mPropertyType
                    mPropertyDetail.squareFootage = (totalSqFit.ifBlank {
                        "0"
                    }).toInt()
                    mPropertyDetail.taxId = taxId
                    mPropertyDetail.userOwners = mOwnersList
                    mPropertyDetail.propertyUnits!!.addAll(mUnitList)
                    mPropertyDetail.propertyLocalId = propertyLocalId
                    mPropertyDetail.id = serverId
                    mPropertyDetail.isUpdateInLocal = true

                    //Get saved list from local
                    val mPropertyList = ArrayList<PropertyDetail>()
                    val localPropertiesList =
                        CamMaxRoomDatabase.getDatabase(getApplication()).propertyDoa()
                            .getAllProperties()
                    mPropertyList.clear()
                    mPropertyList.addAll(localPropertiesList)
                    mPropertyList.add(mPropertyDetail)

                    for (i in mPropertyList.indices) {
                        if (mPropertyList[i].propertyLocalId == propertyLocalId) {
                            mPropertyList[i] = mPropertyDetail
                            break
                        }
                    }

                    //Insert list in local
                    CamMaxRoomDatabase.getDatabase(getApplication()).propertyDoa()
                        .insertProperties(mPropertyList)
                    mCreatePropertySuccess.value = mPropertyDetail
                }
            }
        }
    }

    fun uploadPropertyFromLocalToServer() {
        val mPropertyList =
            CamMaxRoomDatabase.getDatabase(getApplication()).propertyDoa().getAllProperties()
        if (mPropertyList != null) {
            val mAddressList = ArrayList<OwnerAddressModel>()
            val mNotesList = ArrayList<Note>()
            val mAccountList = ArrayList<AccountList>()
            for (i in mPropertyList.indices) {
                if (!mPropertyList[i].isSyncServer) {
                    mAddressList.clear()
                    mAddressList.add(mPropertyList[i].primaryAddress)
                    mAddressList.add(mPropertyList[i].billingAddress)
                    mAddressList.addAll(mPropertyList[i].addresses!!)

                    //Filter users
                    if (mPropertyList[i].access.all) {
                        mAccountList.add(AccountList(firstName = "All"))
                    } else {
                        for (users in mPropertyList[i].access.users!!) {
                            val account = AccountList()
                            account.id = users
                            mAccountList.add(account)
                        }
                    }

                    createProperty(
                        mPropertyList[i].picURL,
                        mPropertyList[i].name,
                        mPropertyList[i].shortName,
                        mPropertyList[i].taxId,
                        mPropertyList[i].propertyTypes!!,
                        mPropertyList[i].chargeTypes!! as ArrayList<String>,
                        mPropertyList[i].squareFootage.toString(),
                        mPropertyList[i].comments,
                        mAddressList,
                        mPropertyList[i].userOwners as ArrayList<UserOwner>,
                        mPropertyList[i].propertyUnits as ArrayList<UnitRecord>,
                        mNotesList,
                        mAccountList
                    )
                }
            }
        }
    }

    fun updatePropertyStateInLocal(mPropertyLocalId: Int, isState: Boolean) {
        viewModelScope.launch {
            CamMaxRoomDatabase.getDatabase(getApplication()).propertyDoa()
                .updatePropertyState(mPropertyLocalId, isState)
        }
    }

    fun updatePropertyStateLocalToServer() {
        val mList =
            CamMaxRoomDatabase.getDatabase(getApplication()).propertyDoa().getAllProperties()
        if (mList != null) {

            for (i in mList.indices) {
                if (mList[i].isUpdateStateInLocal) {
                    blockUnblock(mList[i].id, mList[i].isActive)
                }
            }
        }
    }

    fun deletePropertyFromLocalToServer() {
        if (mUserPrefsManager.getPropertyDeletedIds != null) {
            for (i in mUserPrefsManager.getPropertyDeletedIds!!.mPropertiesIds!!.indices) {
                deleteProperties(mUserPrefsManager.getPropertyDeletedIds!!.mPropertiesIds!![i])
            }
            //Save the empty data after sync in server
            mUserPrefsManager.saveDeletedPropertiesIds(DeletedProperties())
        }
    }

    fun updatePropertyLocalToServer() {
        val mList =
            CamMaxRoomDatabase.getDatabase(getApplication()).propertyDoa().getAllProperties()
        val mNotesList = ArrayList<Note>()
        val mAccountList = ArrayList<AccountList>()
        val mAddressList = ArrayList<OwnerAddressModel>()
        if (mList != null) {
            for (i in mList.indices) {
                if (mList[i].isUpdateInLocal) {
                    mAccountList.clear()
                    mAddressList.clear()
                    //Filter users
                    if (mList[i].access.all) {
                        mAccountList.add(AccountList(firstName = "All"))
                    } else {
                        for (users in mList[i].access.users!!) {
                            val account = AccountList()
                            account.id = users
                            mAccountList.add(account)
                        }
                    }

                    //Filter address
                    mAddressList.add(mList[i].primaryAddress)
                    mAddressList.add(mList[i].billingAddress)
                    mAddressList.addAll(mList[i].addresses!!)

                    //Call api
                    updateProperty(
                        mList[i].id,
                        mList[i].picURL,
                        mList[i].name,
                        mList[i].shortName,
                        mList[i].taxId,
                        mList[i].propertyTypes as ArrayList<String>,
                        mList[i].chargeTypes as ArrayList<String>,
                        mList[i].squareFootage.toString(),
                        mList[i].comments,
                        mAddressList,
                        mList[i].userOwners as ArrayList<UserOwner>,
                        mList[i].propertyUnits!!,
                        mNotesList,
                        mAccountList
                    )
                }
            }
        }
    }

    fun onGetIntegration() = mIntegrationData
    fun onGetTabs() = mTabsList
    fun onGetAllOwners() = mAllOwners
    fun onGetProperties() = mPropertiesList
    fun onGetSuccess() = isSuccess
    fun onCreateProperty() = mCreatePropertySuccess
    fun onGetPropertyDetail() = mPropertyDetail
    fun onGetIntegrationAll() = mIntegrationList
    fun onPropertyUpdated() = isPropertyUpdated

}