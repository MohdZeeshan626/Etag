package com.max360group.cammax360.viewmodels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.interactors.OwnerInteractor
import com.max360group.cammax360.repository.models.*
import com.max360group.cammax360.repository.networkrequests.NetworkRequestCallbacks
import com.max360group.cammax360.repository.networkrequests.RetrofitRequest
import com.max360group.cammax360.repository.room.CamMaxRoomDatabase
import com.max360group.cammax360.utils.AmazonS3
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.views.adapters.AccessListAdapter
import com.max360group.cammax360.views.adapters.OwnersAdapter
import com.max360group.cammax360.views.fragments.NotesHistoryFragment
import com.max360group.cammax360.views.fragments.NotesHistoryFragment.Companion.TYPE_OWNER
import com.max360group.cammax360.views.fragments.NotesHistoryFragment.Companion.TYPE_PROPERTY
import com.max360group.cammax360.views.fragments.NotesHistoryFragment.Companion.TYPE_UNIT
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.File


class OwnerViewModel(application: Application) : BaseViewModel(application) {

    private val mCreateInfoInteractor by lazy {
        OwnerInteractor()
    }

    companion object {
        const val NOTES_KIND_OWNER = "userOwners"
        const val NOTES_KIND_PROPERTY = "properties"
        const val NOTES_KIND_UNITS = "units"
    }

    private val mPropertiesData = MutableLiveData<PropertiesData>()
    private val mNotesData = MutableLiveData<Note>()
    private val mOwnersList = MutableLiveData<List<UserOwner>>()
    private val mOwnerCreatedSuccess = MutableLiveData<UserOwner>()
    private val mOwnerMenuTabs = MutableLiveData<List<Int>>()
    private val mOwnerDetail = MutableLiveData<OwnerData>()
    private val mNotesList = MutableLiveData<List<Note>>()

    private val bucketName = AmazonS3.S3_BUCKET_FOR_USER_PHOTOS + "FieldMax360/development/" +
            "${mUserPrefsManager.loginedUser!!.accounts?.get(0)?.primaryUserId!!.id}" + "/user"

    private val bucketNameNotes = AmazonS3.S3_BUCKET_FOR_USER_PHOTOS + "FieldMax360/development/" +
            "${mUserPrefsManager.loginedUser!!.accounts?.get(0)?.primaryUserId!!.id}" + "/notes/photo"

    private val serverName = "FieldMax360/development/" +
            "${mUserPrefsManager.loginedUser!!.accounts?.get(0)?.primaryUserId!!.id}" + "/user/"

    private val serverNameNotes = "FieldMax360/development/" +
            "${mUserPrefsManager.loginedUser!!.accounts?.get(0)?.primaryUserId!!.id}" + "/notes/photo/"


    fun getMenuTabs() {
        val mList = mutableListOf<Int>(
            R.string.st_general_info,
            R.string.st_properties,
            R.string.st_note_history,
            R.string.st_access,
            R.string.st_integrations
        )
        mOwnerMenuTabs.value = mList
    }

    fun createNote(
        noteMedia: String,
        id: String,
        mMediaList: List<NoteMedia>
    ) {
        when {
            noteMedia.isBlank() -> errorHandler.value = ErrorHandler.EMPTY_MESSAGE
            else -> {
                for (i in mMediaList.indices) {
                    if (mMediaList[i].media.isNotEmpty()) {
                        if (!GeneralFunctions.isRemoteImage((mMediaList[i].media))) {
                            val file = File(mMediaList[i].media)

                            // Upload photos to s3
                            AmazonS3(getApplication()).uploadFileToS3(
                                file,
                                bucketNameNotes
                            )
                            mMediaList[i].media = serverNameNotes + file.name
                        }
                    }

                    if (mMediaList[i].thumbnail.isNotEmpty()) {
                        if (!GeneralFunctions.isRemoteImage((mMediaList[i].thumbnail))) {
                            val file = File(mMediaList[i].thumbnail)

                            // Upload photos to s3
                            AmazonS3(getApplication()).uploadFileToS3(
                                file,
                                bucketNameNotes
                            )
                            mMediaList[i].thumbnail = serverNameNotes + file.name

                        }
                    }
                }

                var mOwnerId: String? = null
                var mPropertyId: String? = null
                var mUnitId: String? = null
                var mKind = ""
                if (id.isNotBlank()) {
                    when (NotesHistoryFragment.TYPE) {
                        TYPE_OWNER -> {
                            mOwnerId = id
                            mKind = NOTES_KIND_OWNER
                        }
                        TYPE_PROPERTY -> {
                            mPropertyId = id
                            mKind = NOTES_KIND_PROPERTY
                        }
                        else -> {
                            mUnitId = id
                            mKind = NOTES_KIND_PROPERTY
                        }
                    }
                } else {
                    mKind = when (NotesHistoryFragment.TYPE) {
                        TYPE_OWNER -> {
                            NOTES_KIND_OWNER
                        }
                        TYPE_PROPERTY -> {
                            NOTES_KIND_PROPERTY
                        }
                        else -> {
                            NOTES_KIND_PROPERTY
                        }
                    }
                }

                isShowLoader.value = true
                mCompositeDisposable.add(
                    mCreateInfoInteractor.createNote(OwnerCreateNoteRequestModel(
                        medias = mMediaList,
                        note = noteMedia,
                        userOwnerId = mOwnerId,
                        propertyId = mPropertyId,
                        propertyUnitId = mUnitId,
                        kind = mKind
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
                                                response.body() as CreateNotesResponseModel
                                            mNotesData.value = mResponse.data.record

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

    fun getProperties(
    ) {
        mCompositeDisposable.add(
            mCreateInfoInteractor.propertiesList(
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

    fun getOwners(
        search: String = "",
        skip: Int,
        showLoader:Boolean=true
    ) {
        isShowSwipeRefreshLayout.value = showLoader
        mCompositeDisposable.add(
            mCreateInfoInteractor.getOwners(skip, OwnersAdapter.LIMIT, search,
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
                                        response.body() as OwnerListResponseModel
                                    saveOwnerInDataBase(mResponse.data.records!!)
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

    fun deleteOwner(
        id: String
    ) {
        isShowLoader.value = true
        mCompositeDisposable.add(
            mCreateInfoInteractor.deleteOwner(id,
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
                                    getOwners(skip = 0)
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

    fun blockOwner(
        id: String,
        status: Boolean,
    ) {
        isShowLoader.value = true
        mCompositeDisposable.add(
            mCreateInfoInteractor.blockOwner(id, status,
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

    fun sendInvite(
        id: String
    ) {
        isShowLoader.value = true
        mCompositeDisposable.add(
            mCreateInfoInteractor.sendInvite(id,
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

    fun getOwnerDetail(
        id: String
    ) {
        isShowLoader.value = true
        mCompositeDisposable.add(
            mCreateInfoInteractor.getOwnerDetails(id,
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
                                        response.body() as OwnerDetailResponseModel
                                    mOwnerDetail.value = mResponse.data

                                    saveOwnerDetailInLocal(mResponse.data.record, id)

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

    fun getNotes(
        id: String,
        search: String = "",
        showLoader: Boolean = true
    ) {
        val map: HashMap<String, String> = HashMap()
        when (NotesHistoryFragment.TYPE) {
            TYPE_OWNER -> {
                map[TYPE_OWNER] = id
            }
            TYPE_PROPERTY -> {
                map[TYPE_PROPERTY] = id
            }
            else -> {
                map[TYPE_UNIT] = id
            }
        }
        isShowLoader.value = showLoader
        mCompositeDisposable.add(
            mCreateInfoInteractor.getNotesList(map, search,
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
                                        response.body() as NotesHistoryResponseModel
                                    mNotesList.value = mResponse.data.records

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

    fun editOwner(
        id: String,
        userImage: String,
        firstName: String,
        lastName: String,
        email: String,
        taxId: String,
        comment: String,
        rmsDisplay: String,
        mEmailList: ArrayList<EmailsModel>,
        mPhoneList: ArrayList<PhoneNumberModel>,
        mAddressList: ArrayList<OwnerAddressModel>,
        mPropertyList: ArrayList<PropertyDetail>,
        mNotesList: ArrayList<Note>,
        mList: ArrayList<AccountList>,
        isSendInvite: Boolean,
        showLoade: Boolean = true
    ) {
        when {
            firstName.isBlank() -> errorHandler.value = ErrorHandler.EMPTY_FIRST_NAME
            lastName.isBlank() -> errorHandler.value = ErrorHandler.EMPTY_LAST_NAME
            userImage.isBlank() -> errorHandler.value = ErrorHandler.EMPTY_PROFILE
            email.isBlank() -> errorHandler.value = ErrorHandler.EMPTY_EMAIL
            mEmailList[0].email.isEmpty() -> errorHandler.value = ErrorHandler.EMPTY_PRIMARY_EMAIL
            mPhoneList[0].phoneNumber.isEmpty() -> errorHandler.value =
                ErrorHandler.EMPTY_PHONE_NUMBER
            mAddressList[0].formatted.isEmpty() -> errorHandler.value =
                ErrorHandler.EMPTY_PRIMARY_ADDRESS
            mAddressList[1].formatted.isEmpty() -> errorHandler.value =
                ErrorHandler.EMPTY_BILLING_ADDRESS
            rmsDisplay.isBlank() -> errorHandler.value = ErrorHandler.EMPTY_RMS

            else -> {
                isShowLoader.value = true
                val mNotesIds = ArrayList<String>()
                val mPropertiesIds = ArrayList<PropertyUnit>()
                val mUsersIds = ArrayList<String>()
                var mPrimaryAddress = OwnerAddressModel()
                var mBillingAddress = OwnerAddressModel()
                var mImageServerPath = ""
                var isAllAccess = false
                mNotesIds.clear()
                mPropertiesIds.clear()
                mUsersIds.clear()

                //Filter notes ids
                for (i in mNotesList) {
                    mNotesIds.add(i.id)
                }

                //Filter properties ids
                for (data in mPropertyList) {
                    val mPropertyUnit = PropertyUnit()
                    mPropertyUnit.propertyId = data.id
                    mPropertiesIds.add(mPropertyUnit)

                    //Add selected units of property
                    for (j in data.propertyUnits!!.indices) {
                        if (data.propertyUnits!![j].isChecked) {
                            mPropertyUnit.propertyUnits!!.add(data.propertyUnits!![j].id)
                        }
                    }
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
                if (userImage.isNotEmpty()) {
                    mImageServerPath = if (!userImage.startsWith("FieldMax360")) {
                        val file = File(userImage)

                        AmazonS3(getApplication()).uploadFileToS3(
                            file,
                            bucketName
                        )
                        serverName + file.name
                    } else {
                        userImage
                    }
                }

                //Get primary Address
                mPrimaryAddress = mAddressList[0]
                mBillingAddress = mAddressList[1]
                //Remove primary and billing from list
                mAddressList.removeAt(0)
                mAddressList.removeAt(0)

                mCompositeDisposable.add(
                    mCreateInfoInteractor.editOwner(id,
                        CreateOwnerRequestModel(
                            addresses = mAddressList,
                            billingAddress = mBillingAddress,
                            email = email,
                            emails = mEmailList,
                            firstName = firstName,
                            lastName = lastName,
                            phoneNumbers = mPhoneList,
                            primaryAddress = mPrimaryAddress,
                            notes = mNotesIds,
                            properties = mPropertiesIds,
                            pic = mImageServerPath,
                            rm = RmFields(displayName = rmsDisplay),
                            access = Access(isAllAccess, mUsersIds),
                            comments = comment,
                            taxId = taxId,
                            sendInvite = isSendInvite
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
                                                response.body() as CreateOwnerResponseModel
                                            mOwnerCreatedSuccess.value = mResponse.data.record
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


    fun createOwner(
        userImage: String,
        firstName: String,
        lastName: String,
        email: String,
        taxId: String,
        comment: String,
        rmsDisplay: String,
        mEmailList: ArrayList<EmailsModel>,
        mPhoneList: ArrayList<PhoneNumberModel>,
        mAddressList: ArrayList<OwnerAddressModel>,
        mPropertyList: ArrayList<PropertyDetail>,
        mNotesList: ArrayList<Note>,
        mList: ArrayList<AccountList>,
        isSendInvite: Boolean,
        showLoader: Boolean = true
    ) {
        when {
            firstName.isBlank() -> errorHandler.value = ErrorHandler.EMPTY_FIRST_NAME
            lastName.isBlank() -> errorHandler.value = ErrorHandler.EMPTY_LAST_NAME
            userImage.isBlank() -> errorHandler.value = ErrorHandler.EMPTY_PROFILE
            email.isBlank() -> errorHandler.value = ErrorHandler.EMPTY_EMAIL
            taxId.isBlank() -> errorHandler.value = ErrorHandler.EMPTY_TAX_ID
            comment.isBlank() -> errorHandler.value = ErrorHandler.EMPTY_COMMENT
            mEmailList[0].email.isEmpty() -> errorHandler.value = ErrorHandler.EMPTY_PRIMARY_EMAIL
            mPhoneList[0].phoneNumber.isEmpty() -> errorHandler.value =
                ErrorHandler.EMPTY_PHONE_NUMBER
            mAddressList[0].formatted.isEmpty() -> errorHandler.value =
                ErrorHandler.EMPTY_PRIMARY_ADDRESS
            mAddressList[1].formatted.isEmpty() -> errorHandler.value =
                ErrorHandler.EMPTY_BILLING_ADDRESS
            rmsDisplay.isBlank() -> errorHandler.value = ErrorHandler.EMPTY_RMS

            else -> {
                isShowLoader.value = showLoader
                val mNotesIds = ArrayList<String>()
                val mPropertiesIds = ArrayList<PropertyUnit>()
                val mUsersIds = ArrayList<String>()
                var mPrimaryAddress = OwnerAddressModel()
                var mBillingAddress = OwnerAddressModel()
                var mImageServerPath = ""
                var isAllAccess = false
                mNotesIds.clear()
                mPropertiesIds.clear()
                mUsersIds.clear()

                //Filter notes ids
                for (i in mNotesList) {
                    mNotesIds.add(i.id)
                }

                //Filter properties ids
                for (data in mPropertyList) {
                    val mPropertyUnit = PropertyUnit()
                    mPropertyUnit.propertyId = data.id
                    mPropertiesIds.add(mPropertyUnit)

                    //Add selected units of property
                    for (j in data.propertyUnits!!.indices) {
                        if (data.propertyUnits!![j].isChecked) {
                            mPropertyUnit.propertyUnits!!.add(data.propertyUnits!![j].id)
                        }
                    }
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
                if (userImage.isNotEmpty()) {
                    if (!GeneralFunctions.isRemoteImage((userImage))) {
                        val file = File(userImage)

                        AmazonS3(getApplication()).uploadFileToS3(
                            file,
                            bucketName
                        )
                        mImageServerPath = serverName + file.name
                    }
                }

                //Get primary Address
                mPrimaryAddress = mAddressList[0]
                mBillingAddress = mAddressList[1]
                //Remove primary and billing from list
                mAddressList.removeAt(0)
                mAddressList.removeAt(0)

                mCompositeDisposable.add(
                    mCreateInfoInteractor.createOwner(
                        CreateOwnerRequestModel(
                            addresses = mAddressList,
                            billingAddress = mBillingAddress,
                            email = email,
                            emails = mEmailList,
                            firstName = firstName,
                            lastName = lastName,
                            phoneNumbers = mPhoneList,
                            primaryAddress = mPrimaryAddress,
                            notes = mNotesIds,
                            properties = mPropertiesIds,
                            pic = mImageServerPath,
                            rm = RmFields(displayName = rmsDisplay),
                            access = Access(isAllAccess, mUsersIds),
                            comments = comment,
                            taxId = taxId,
                            sendInvite = isSendInvite
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
                                                response.body() as CreateOwnerResponseModel
                                            mOwnerCreatedSuccess.value = mResponse.data.record
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

    fun saveOwnerInDataBase(mOwnersData: List<UserOwner>?) {
        with(mOwnersData) {
            viewModelScope.launch {
                //Save owners
                CamMaxRoomDatabase.getDatabase(getApplication()).ownersDao()
                    .deleteAllOwner()

                CamMaxRoomDatabase.getDatabase(getApplication()).ownersDao()
                    .insertOwner(mOwnersData!!)

                //Get owners
                val mOwners =
                    CamMaxRoomDatabase.getDatabase(getApplication()).ownersDao().getAllOwner()
                if (mOwners.isNotEmpty()) {
                    mOwnersList.value = mOwners
                }
            }
        }
    }

    fun getSearchOwners(value: String) {
        val mOwners =
            CamMaxRoomDatabase.getDatabase(getApplication()).ownersDao().searchOwner(value)
        if (mOwners != null) {
            mOwnersList.value = mOwners
            isShowSwipeRefreshLayout.value = false

        } else {
            errorHandler.value = ErrorHandler.NO_INTERNET
        }
    }

    fun getOwnersFromDataBase() {
        val mOwners = CamMaxRoomDatabase.getDatabase(getApplication()).ownersDao().getAllOwner()
        if (mOwners != null) {
            mOwnersList.value = mOwners
            isShowSwipeRefreshLayout.value = false

        } else {
            errorHandler.value = ErrorHandler.NO_INTERNET
        }
    }

    fun deleteOwnerFromLocal(ownerId: String, ownerLocalId: Int) {
        CamMaxRoomDatabase.getDatabase(getApplication()).ownersDao().deleteOwner(ownerLocalId)
        getOwnersFromDataBase()

        //Save deleted job from local
        if (ownerId.isNotBlank()) {
            var mSavedIds = ArrayList<String>()
            if (mUserPrefsManager.getOwnersDeletedIds != null) {
                mSavedIds = mUserPrefsManager.getOwnersDeletedIds!!.mOwnersIds!!
                mSavedIds.add(ownerId)
            }

            val mDeletedOwners = DeletedOwners()
            mDeletedOwners.mOwnersIds = mSavedIds
            mUserPrefsManager.saveDeletedOwnersIds(mDeletedOwners)

        } else {
            errorHandler.value = ErrorHandler.NO_INTERNET
        }
    }

    fun saveOwnerDetailInLocal(data: UserOwner, id: String) {
        val mOwners = CamMaxRoomDatabase.getDatabase(getApplication()).ownersDao().getAllOwner()
        val mOwnersList = ArrayList<UserOwner>()
        mOwnersList.clear()
        mOwnersList.addAll(mOwners)

        viewModelScope.launch {
            if (mOwnersList.isNotEmpty()) {
                for (i in mOwnersList.indices) {
                    if (mOwnersList[i].id == id) {
                        data.ownerLocalId = mOwnersList[i].ownerLocalId
                        mOwnersList[i] = data
                        break
                    }
                }
            }
            CamMaxRoomDatabase.getDatabase(getApplication()).ownersDao().insertOwner(mOwnersList)
        }
    }

    fun getOwnerDetailFromLocal(id: Int) {
        val ownerDetail =
            CamMaxRoomDatabase.getDatabase(getApplication()).ownersDao().getSingleOwnerId(id)
        if (ownerDetail != null) {
            mOwnerDetail.value = OwnerData(record = ownerDetail)
        }
    }

    fun createOwnerInLocal(
        userImage: String,
        firstName: String,
        lastName: String,
        email: String,
        taxId: String,
        comment: String,
        rmsDisplay: String,
        mEmailList: ArrayList<EmailsModel>,
        mPhoneList: ArrayList<PhoneNumberModel>,
        mAddressList: ArrayList<OwnerAddressModel>,
        mPropertyList: ArrayList<PropertyDetail>,
        mNotesList: ArrayList<Note>,
        mList: ArrayList<AccountList>,
        isSendInvite: Boolean
    ) {
        viewModelScope.launch {
            when {
                firstName.isBlank() -> errorHandler.value = ErrorHandler.EMPTY_FIRST_NAME
                lastName.isBlank() -> errorHandler.value = ErrorHandler.EMPTY_LAST_NAME
                userImage.isBlank() -> errorHandler.value = ErrorHandler.EMPTY_PROFILE
                email.isBlank() -> errorHandler.value = ErrorHandler.EMPTY_EMAIL
                taxId.isBlank() -> errorHandler.value = ErrorHandler.EMPTY_TAX_ID
                comment.isBlank() -> errorHandler.value = ErrorHandler.EMPTY_COMMENT
                mEmailList[0].email.isEmpty() -> errorHandler.value =
                    ErrorHandler.EMPTY_PRIMARY_EMAIL
                mPhoneList[0].phoneNumber.isEmpty() -> errorHandler.value =
                    ErrorHandler.EMPTY_PHONE_NUMBER
                mAddressList[0].formatted.isEmpty() -> errorHandler.value =
                    ErrorHandler.EMPTY_PRIMARY_ADDRESS
                mAddressList[1].formatted.isEmpty() -> errorHandler.value =
                    ErrorHandler.EMPTY_BILLING_ADDRESS
                rmsDisplay.isBlank() -> errorHandler.value = ErrorHandler.EMPTY_RMS

                else -> {
                    //Save property
                    val propertiesList = ArrayList<OwnerProperty>()
                    val propertyUnitList = ArrayList<String>()
                    propertiesList.clear()

                    for (i in mPropertyList.indices) {
                        val mOwnerProperty = OwnerProperty()
                        propertyUnitList.clear()
                        mOwnerProperty.propertyId = mPropertyList[i].id
                        //Add property unit
                        for (j in mPropertyList[i].propertyUnits!!.indices) {
                            propertyUnitList.add(mPropertyList[i].propertyUnits!![j].id)
                        }
                        propertiesList.add(mOwnerProperty)
                    }

                    //Save units
                    val notesList = ArrayList<String>()
                    notesList.clear()
                    for (i in mNotesList.indices) {
                        notesList.add(mNotesList[i].id)
                    }

                    //Save Access
                    val mUsersIds = ArrayList<String>()
                    var isAllAccess = false
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

                    //Filter address
                    val mPrimaryAddress = mAddressList[0]
                    val mBillingAddress = mAddressList[1]
                    //Remove primary and billing from list
                    mAddressList.removeAt(0)
                    mAddressList.removeAt(0)

                    val mUserOwner = UserOwner()
                    mUserOwner.firstName = firstName
                    mUserOwner.lastName = lastName
                    mUserOwner.email = email
                    mUserOwner.taxId = taxId
                    mUserOwner.comments = comment
                    mUserOwner.rm.displayName = rmsDisplay
                    mUserOwner.rm.enabled = true
                    mUserOwner.emails = mEmailList
                    mUserOwner.addresses = mAddressList
                    mUserOwner.billingAddress = mBillingAddress
                    mUserOwner.primaryAddress = mPrimaryAddress
                    mUserOwner.phoneNumbers = mPhoneList
                    mUserOwner.properties = propertiesList
                    mUserOwner.notes = notesList
                    mUserOwner.access = Access(isAllAccess, mUsersIds)
                    mUserOwner.isSendInvite = isSendInvite
                    mUserOwner.picURL = userImage
                    mUserOwner.pic = userImage
                    mUserOwner.isSyncServer = false

                    //Get saved list from local
                    val mUserOwnerList = ArrayList<UserOwner>()
                    val localOwnerList =
                        CamMaxRoomDatabase.getDatabase(getApplication()).ownersDao().getAllOwner()
                    mUserOwnerList.addAll(localOwnerList)
                    mUserOwnerList.add(mUserOwner)

                    //Insert list in local
                    CamMaxRoomDatabase.getDatabase(getApplication()).ownersDao()
                        .insertOwner(mUserOwnerList)
                    mOwnerCreatedSuccess.value = mUserOwner
                }
            }
        }
    }

    fun savePropertyInLocal(records: List<PropertyDetail>) {
        viewModelScope.launch {
            CamMaxRoomDatabase.getDatabase(getApplication()).propertyDoa().deleteAllProperties()
            CamMaxRoomDatabase.getDatabase(getApplication()).propertyDoa().insertProperties(records)

            //Get property from local
            val mPropertiesList =
                CamMaxRoomDatabase.getDatabase(getApplication()).propertyDoa().getAllProperties()
            mPropertiesData.value = PropertiesData(mPropertiesList)

        }
    }

    fun getPropertiesFromLocal() {
        val properties =
            CamMaxRoomDatabase.getDatabase(getApplication()).propertyDoa().getAllProperties()
        if (properties.isNotEmpty()) {
            mPropertiesData.value = PropertiesData(properties)
        }
    }

    fun updateStateFromLocal(localId: Int, sendInvite: Boolean) {
        viewModelScope.launch {
            CamMaxRoomDatabase.getDatabase(getApplication()).ownersDao()
                .updateState(localId, sendInvite)
        }
    }

    fun updateStateLocalToServer() {
        val mList = CamMaxRoomDatabase.getDatabase(getApplication()).ownersDao().getAllOwner()
        if (mList != null) {
            for (i in mList.indices) {
                if (mList[i].isUpdateActiveInLocal) {
                    blockOwner(mList[i].id, mList[i].isActive)
                }
            }
        }
    }

    fun updateOwnerInLocal(
        serverId: String,
        ownerLocalId: Int,
        userImage: String,
        firstName: String,
        lastName: String,
        email: String,
        taxId: String,
        comment: String,
        rmsDisplay: String,
        mEmailList: ArrayList<EmailsModel>,
        mPhoneList: ArrayList<PhoneNumberModel>,
        mAddressList: ArrayList<OwnerAddressModel>,
        mPropertyList: ArrayList<PropertyDetail>,
        mNotesList: ArrayList<Note>,
        mList: ArrayList<AccountList>,
        isSendInvite: Boolean
    ) {
        viewModelScope.launch {
            when {
                firstName.isBlank() -> errorHandler.value = ErrorHandler.EMPTY_FIRST_NAME
                lastName.isBlank() -> errorHandler.value = ErrorHandler.EMPTY_LAST_NAME
                userImage.isBlank() -> errorHandler.value = ErrorHandler.EMPTY_PROFILE
                email.isBlank() -> errorHandler.value = ErrorHandler.EMPTY_EMAIL
                taxId.isBlank() -> errorHandler.value = ErrorHandler.EMPTY_TAX_ID
                comment.isBlank() -> errorHandler.value = ErrorHandler.EMPTY_COMMENT
                mEmailList[0].email.isEmpty() -> errorHandler.value =
                    ErrorHandler.EMPTY_PRIMARY_EMAIL
                mPhoneList[0].phoneNumber.isEmpty() -> errorHandler.value =
                    ErrorHandler.EMPTY_PHONE_NUMBER
                mAddressList[0].formatted.isEmpty() -> errorHandler.value =
                    ErrorHandler.EMPTY_PRIMARY_ADDRESS
                mAddressList[1].formatted.isEmpty() -> errorHandler.value =
                    ErrorHandler.EMPTY_BILLING_ADDRESS
                rmsDisplay.isBlank() -> errorHandler.value = ErrorHandler.EMPTY_RMS

                else -> {
                    //Save property
                    val propertiesList = ArrayList<OwnerProperty>()
                    val propertyUnitList = ArrayList<String>()
                    propertiesList.clear()

                    for (i in mPropertyList.indices) {
                        val mOwnerProperty = OwnerProperty()
                        propertyUnitList.clear()
                        mOwnerProperty.propertyId = mPropertyList[i].id
                        //Add property unit
                        for (j in mPropertyList[i].propertyUnits!!.indices) {
                            propertyUnitList.add(mPropertyList[i].propertyUnits!![j].id)
                        }
                        propertiesList.add(mOwnerProperty)
                    }

                    //Save units
                    val notesList = ArrayList<String>()
                    notesList.clear()
                    for (i in mNotesList.indices) {
                        notesList.add(mNotesList[i].id)
                    }

                    //Save Access
                    val mUsersIds = ArrayList<String>()
                    var isAllAccess = false
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

                    //Filter address
                    val mPrimaryAddress = mAddressList[0]
                    val mBillingAddress = mAddressList[1]
                    //Remove primary and billing from list
                    mAddressList.removeAt(0)
                    mAddressList.removeAt(0)

                    val mUserOwner = UserOwner()
                    mUserOwner.firstName = firstName
                    mUserOwner.lastName = lastName
                    mUserOwner.email = email
                    mUserOwner.taxId = taxId
                    mUserOwner.comments = comment
                    mUserOwner.rm.displayName = rmsDisplay
                    mUserOwner.rm.enabled = true
                    mUserOwner.emails = mEmailList
                    mUserOwner.addresses = mAddressList
                    mUserOwner.billingAddress = mBillingAddress
                    mUserOwner.primaryAddress = mPrimaryAddress
                    mUserOwner.phoneNumbers = mPhoneList
                    mUserOwner.properties = propertiesList
                    mUserOwner.notes = notesList
                    mUserOwner.access = Access(isAllAccess, mUsersIds)
                    mUserOwner.isSendInvite = isSendInvite
                    mUserOwner.picURL = userImage
                    mUserOwner.pic = userImage
                    mUserOwner.ownerLocalId = ownerLocalId
                    mUserOwner.id = serverId
                    mUserOwner.isUpdateInLocal = true

                    //Get saved list from local
                    val mUserOwnerList = ArrayList<UserOwner>()
                    val localOwnerList =
                        CamMaxRoomDatabase.getDatabase(getApplication()).ownersDao().getAllOwner()
                    mUserOwnerList.clear()
                    mUserOwnerList.addAll(localOwnerList)

                    for (i in mUserOwnerList.indices) {
                        if (mUserOwnerList[i].ownerLocalId == ownerLocalId) {
                            mUserOwnerList[i] = mUserOwner
                            break
                        }
                    }

                    //Insert list in local
                    CamMaxRoomDatabase.getDatabase(getApplication()).ownersDao()
                        .insertOwner(mUserOwnerList)
                    mOwnerCreatedSuccess.value = mUserOwner
                }
            }
        }
    }

    fun uploadOwnerFromLocalToServer() {
        val ownersList = CamMaxRoomDatabase.getDatabase(getApplication()).ownersDao().getAllOwner()
        if (ownersList != null) {
            val mAddressList = ArrayList<OwnerAddressModel>()
            val mPropertyList = ArrayList<PropertyDetail>()
            val mNotesList = ArrayList<Note>()
            val mAccountList = ArrayList<AccountList>()

            for (i in ownersList.indices) {
                if (!ownersList[i].isSyncServer) {
                    mAddressList.clear()
                    mPropertyList.clear()
                    mAccountList.clear()
                    mAddressList.add(ownersList[i].primaryAddress)
                    mAddressList.add(ownersList[i].billingAddress)
                    mAddressList.addAll(ownersList[i].addresses!!)

                    //Filter properties
                    for (property in ownersList[i].properties!!) {
                        val mPropertyDetail = PropertyDetail()
                        mPropertyDetail.id = property.propertyId

                        //Filter properties units
                        for (unit in property.propertyUnits!!) {
                            mPropertyDetail.propertyUnits!!.add(UnitRecord(id = unit))
                        }
                        mPropertyList.add(mPropertyDetail)
                    }

                    //Filter users
                    if (ownersList[i].access.all) {
                        mAccountList.add(AccountList(firstName = "All"))
                    } else {
                        for (users in ownersList[i].access.users!!) {
                            val account = AccountList()
                            account.id = users
                            mAccountList.add(account)
                        }
                    }

                    //Call api
                    createOwner(
                        ownersList[i].picURL,
                        ownersList[i].firstName,
                        ownersList[i].lastName,
                        ownersList[i].email!!,
                        ownersList[i].taxId,
                        ownersList[i].comments,
                        ownersList[i].rm.displayName,
                        ownersList[i].emails as ArrayList<EmailsModel>,
                        ownersList[i].phoneNumbers as ArrayList<PhoneNumberModel>,
                        mAddressList,
                        mPropertyList,
                        mNotesList,
                        mAccountList,
                        ownersList[i].isSendInvite,
                        false
                    )
                }
            }
        }
    }

    fun updateOwnerFromLocalToServer() {
        val ownersList = CamMaxRoomDatabase.getDatabase(getApplication()).ownersDao().getAllOwner()
        if (ownersList != null) {
            val mAddressList = ArrayList<OwnerAddressModel>()
            val mPropertyList = ArrayList<PropertyDetail>()
            val mNotesList = ArrayList<Note>()
            val mAccountList = ArrayList<AccountList>()

            for (i in ownersList.indices) {
                if (ownersList[i].isUpdateInLocal) {
                    mAddressList.clear()
                    mPropertyList.clear()
                    mAccountList.clear()
                    mAddressList.add(ownersList[i].primaryAddress)
                    mAddressList.add(ownersList[i].billingAddress)
                    mAddressList.addAll(ownersList[i].addresses!!)

                    //Filter properties
                    for (property in ownersList[i].properties!!) {
                        val mPropertyDetail = PropertyDetail()
                        mPropertyDetail.id = property.propertyId

                        //Filter properties units
                        for (unit in property.propertyUnits!!) {
                            mPropertyDetail.propertyUnits!!.add(UnitRecord(id = unit))
                        }
                        mPropertyList.add(mPropertyDetail)
                    }

                    //Filter users
                    if (ownersList[i].access.all) {
                        mAccountList.add(AccountList(firstName = "All"))
                    } else {
                        for (users in ownersList[i].access.users!!) {
                            val account = AccountList()
                            account.id = users
                            mAccountList.add(account)
                        }
                    }

                    //Call api
                    editOwner(
                        ownersList[i].id,
                        ownersList[i].picURL,
                        ownersList[i].firstName,
                        ownersList[i].lastName,
                        ownersList[i].email!!,
                        ownersList[i].taxId,
                        ownersList[i].comments,
                        ownersList[i].rm.displayName,
                        ownersList[i].emails as ArrayList<EmailsModel>,
                        ownersList[i].phoneNumbers as ArrayList<PhoneNumberModel>,
                        mAddressList,
                        mPropertyList,
                        mNotesList,
                        mAccountList,
                        ownersList[i].isSendInvite,
                        false
                    )
                }
            }
        }
    }

    fun deleteOwnersFromLocalToServer() {
        if (mUserPrefsManager.getOwnersDeletedIds != null) {
            for (i in mUserPrefsManager.getOwnersDeletedIds!!.mOwnersIds!!.indices) {
                deleteOwner(mUserPrefsManager.getOwnersDeletedIds!!.mOwnersIds!![i])
            }
            //Save the empty data after sync in server
            mUserPrefsManager.saveDeletedOwnersIds(DeletedOwners())
        }
    }

    fun onGetPropertiesData() = mPropertiesData
    fun onGetNote() = mNotesData
    fun onGetNotesList() = mNotesList
    fun onGetOwnersList() = mOwnersList
    fun onOwnerCreateSuccess() = mOwnerCreatedSuccess
    fun onGetTabMenu() = mOwnerMenuTabs
    fun onGetOwnerData() = mOwnerDetail

}