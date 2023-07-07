package com.max360group.cammax360.viewmodels

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.RetrofitErrorMessage
import com.max360group.cammax360.repository.preferences.UserPrefsManager
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by Mukesh on 13/02/2018.
 */
abstract class BaseViewModel(application: Application) : AndroidViewModel(application) {

    protected val mUserPrefsManager: UserPrefsManager by lazy {
        UserPrefsManager(getApplication()) }
    protected val isShowLoader = MutableLiveData<Boolean>()
    protected val isShowNoDataText = MutableLiveData<Boolean>()
    protected val isShowSwipeRefreshLayout = MutableLiveData<Boolean>()
    protected val isSessionExpired = MutableLiveData<Boolean>()
    protected val retrofitErrorDataMessage = MutableLiveData<RetrofitErrorMessage>()
    protected val retrofitErrorMessage = MutableLiveData<RetrofitErrorMessage>()
    protected val successMessage = MutableLiveData<String>()
    protected val errorHandler = MutableLiveData<ErrorHandler>()
    protected val mCompositeDisposable: CompositeDisposable by lazy {
        CompositeDisposable() }

    fun isShowLoader(): LiveData<Boolean> = isShowLoader

    fun isShowNoDataText(): LiveData<Boolean> = isShowNoDataText

    fun isSessionExpired(): LiveData<Boolean> = isSessionExpired

    fun isShowSwipeRefreshLayout(): LiveData<Boolean> = isShowSwipeRefreshLayout

    fun getRetrofitErrorDataMessage(): LiveData<RetrofitErrorMessage> = retrofitErrorDataMessage

    fun getRetrofitErrorMessage(): LiveData<RetrofitErrorMessage> = retrofitErrorMessage

    fun getErrorHandler(): LiveData<ErrorHandler> = errorHandler

    fun getSuccessMessage(): LiveData<String> = successMessage

    enum class ErrorHandler(@StringRes private val resourceId: Int) : ErrorEvent {
        EMPTY_COUNTRY_CODE(R.string.empty_country_code),
        EMPTY_PHONE_NUMBER(R.string.empty_phone_number),
        EMPTY_OTP(R.string.empty_otp),
        EMPTY_FIRST_NAME(R.string.empty_first_name),
        EMPTY_UNIT_NAME(R.string.empty_unit_name),
        EMPTY_UNIT_IMAGE(R.string.empty_unit_image),
        EMPTY_PROPERTY_NAME(R.string.empty_property_name),
        EMPTY_PROPERTY_IMAGE(R.string.empty_property_image),
        EMPTY_SHORT_NAME(R.string.empty_short_name),
        EMPTY_PROPERTY_TYPE(R.string.empty_property_type),
        EMPTY_CHARGE_TYPE(R.string.empty_charge_type),
        EMPTY_UNIT_TYPE(R.string.empty_unit_type),
        EMPTY_START_DATE(R.string.empty_start_date),
        EMPTY_ROLE_NAME(R.string.empty_role_name),
        EMPTY_END_DATE(R.string.empty_end_date),
        EMPTY_PROPERTY_ID(R.string.st_empty_property),
        EMPTY_LAST_NAME(R.string.empty_last_name),
        EMPTY_PROFILE(R.string.empty_profile),
        EMPTY_EMAIL(R.string.empty_email),
        EMPTY_TAX_ID(R.string.empty_tax_id),
        EMPTY_COMMENT(R.string.empty_comment),
        EMPTY_RMS(R.string.empty_rms),
        EMPTY_PRIMARY_EMAIL(R.string.empty_primary_email),
        INVALID_EMAIL(R.string.invalid_email),
        EMPTY_PASSWORD(R.string.empty_password),
        INCORRECT_PASSWORD(R.string.password_incorrect),
        NO_INTERNET(R.string.no_internet),
        INVALID_PASSWORD(R.string.invalid_password),
        INVALID_CONFIRM_PASSWORD(R.string.invalid_confirm_password),
        EMPTY_DOB(R.string.empty_dob),
        INVALID_DOB(R.string.invalid_dob),
        EMPTY_LOCATION(R.string.empty_location),
        INVALID_NUMBER_OF_PHOTOS(R.string.invalid_number_of_photos),
        EMPTY_NAME_MEANING(R.string.empty_name_meaning),
        EMPTY_NAME_STORY(R.string.empty_name_story),
        INVALID_HEIGHT(R.string.invalid_height),
        INVALID_ETHNICITY(R.string.invalid_ethnicity),
        INVALID_ANSWER(R.string.invalid_answer),
        EMPTY_MESSAGE(R.string.empty_message),
        EMPTY_OLD_PASSWORD(R.string.empty_old_password),
        EMPTY_NEW_PASSWORD(R.string.empty_new_password),
        EMPTY_CONFIRM_PASSWORD(R.string.empty_new_password),
        EMPTY_PRIMARY_ADDRESS(R.string.empty_primary_address),
        EMPTY_BILLING_ADDRESS(R.string.empty_billing_address);



        override fun getErrorResource() = resourceId
    }

    interface ErrorEvent {
        @StringRes
        fun getErrorResource(): Int
    }
}