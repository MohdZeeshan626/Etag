package com.max360group.cammax360.repository.interactors

import android.util.Log
import com.max360group.cammax360.repository.models.JobDocsRequestModel
import com.max360group.cammax360.repository.models.JobVideoRequestModel
import com.max360group.cammax360.repository.models.model.JobMediaRequestModel
import com.max360group.cammax360.repository.networkrequests.NetworkRequestCallbacks
import com.max360group.cammax360.repository.networkrequests.RestClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.Response

class JobsMediaInteractor {

    fun getJobsMembers(
        subKind: String,
        id: String,
        networkRequestCallbacks: NetworkRequestCallbacks
    ): Disposable {
        return RestClient.get().getJobMembers(id,subKind)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableObserver<Response<*>>() {
                override fun onNext(response: Response<*>) {
                    Log.e("Intrector", "data" + "yes")

                    networkRequestCallbacks.onSuccess(response)
                }

                override fun onError(t: Throwable) {
                    networkRequestCallbacks.onError(t)
                    Log.e("EmployeModel", "data" + "No")

                }

                override fun onComplete() {

                }
            })
    }

    fun addMedia(
        mJobId: String,
        mMediaRequestModel: JobMediaRequestModel,
        networkRequestCallbacks: NetworkRequestCallbacks
    ): Disposable {
        return RestClient.get().addMedia(mJobId,mMediaRequestModel)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableObserver<Response<*>>() {
                override fun onNext(response: Response<*>) {
                    Log.e("Intrector", "data" + "yes")

                    networkRequestCallbacks.onSuccess(response)
                }

                override fun onError(t: Throwable) {
                    networkRequestCallbacks.onError(t)
                    Log.e("EmployeModel", "data" + "No")

                }

                override fun onComplete() {

                }
            })
    }

    fun addVideo(
        mJobId: String,
        mJobVideoRequestModel: JobVideoRequestModel,
        networkRequestCallbacks: NetworkRequestCallbacks
    ): Disposable {
        return RestClient.get().addVideoJob(mJobId,mJobVideoRequestModel)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableObserver<Response<*>>() {
                override fun onNext(response: Response<*>) {
                    Log.e("Intrector", "data" + "yes")

                    networkRequestCallbacks.onSuccess(response)
                }

                override fun onError(t: Throwable) {
                    networkRequestCallbacks.onError(t)
                    Log.e("EmployeModel", "data" + "No")

                }

                override fun onComplete() {

                }
            })
    }

    fun addDocs(
        mJobId: String,
        mJobDocsRequestModel: JobDocsRequestModel,
        networkRequestCallbacks: NetworkRequestCallbacks
    ): Disposable {
        return RestClient.get().addDocsJob(mJobId,mJobDocsRequestModel)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableObserver<Response<*>>() {
                override fun onNext(response: Response<*>) {
                    Log.e("Intrector", "data" + "yes")

                    networkRequestCallbacks.onSuccess(response)
                }

                override fun onError(t: Throwable) {
                    networkRequestCallbacks.onError(t)
                    Log.e("EmployeModel", "data" + "No")

                }

                override fun onComplete() {

                }
            })
    }

}

