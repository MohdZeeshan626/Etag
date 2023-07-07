package com.max360group.cammax360.viewmodels

import android.app.Application
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.interactors.AccountInteractor
import com.max360group.cammax360.repository.interactors.OnBoardingInteractor
import com.max360group.cammax360.repository.models.*
import com.max360group.cammax360.repository.networkrequests.NetworkRequestCallbacks
import com.max360group.cammax360.repository.networkrequests.RetrofitRequest
import com.max360group.cammax360.utils.AmazonS3
import com.max360group.cammax360.utils.AmazonS3.Companion.S3_BUCKET_FOR_USER_PHOTOS
import com.max360group.cammax360.utils.ColorTheme
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.views.utils.JobsConstants
import retrofit2.Response
import java.io.File

class TimelineViewModel(application: Application) : BaseViewModel(application) {
    var mItemsList = ArrayList<TimeLineItems>()
    var mUserUpdates = ArrayList<TimeLineItems>()
    var mAdapterItemsList = MutableLiveData<List<TimeLineItems>>()
    var mAdapterUserUpdateList = MutableLiveData<List<TimeLineItems>>()
    var mContext=application

    fun getItems(){
        mItemsList.add(TimeLineItems(mContext.resources.getString(R.string.st_all)))
        mItemsList.add(TimeLineItems(mContext.resources.getString(R.string.media)))
        mItemsList.add(TimeLineItems(mContext.resources.getString(R.string.st_documents)))
        mItemsList.add(TimeLineItems(mContext.resources.getString(R.string.st_conversation)))
        mItemsList.add(TimeLineItems(mContext.resources.getString(R.string.st_notes)))
        mItemsList.add(TimeLineItems(mContext.resources.getString(R.string.st_Comments)))
        mAdapterItemsList.value=mItemsList
    }

    fun getUserUpdates(){
        mUserUpdates.add(TimeLineItems(mContext.resources.getString(R.string.st_all)))
        mUserUpdates.add(TimeLineItems(mContext.resources.getString(R.string.st_permissions)))
        mUserUpdates.add(TimeLineItems(mContext.resources.getString(R.string.st_invitations)))
        mUserUpdates.add(TimeLineItems(mContext.resources.getString(R.string.st_terminations)))
        mAdapterUserUpdateList.value=mUserUpdates
    }

    fun onGetItems()=mAdapterItemsList
    fun onGetUserUpdates()=mAdapterUserUpdateList
}