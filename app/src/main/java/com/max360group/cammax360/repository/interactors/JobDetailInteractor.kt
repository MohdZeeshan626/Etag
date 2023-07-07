package com.max360group.cammax360.repository.interactors

import android.util.Log
import com.max360group.cammax360.repository.models.TimeLineRequestModel
import com.max360group.cammax360.repository.networkrequests.NetworkRequestCallbacks
import com.max360group.cammax360.repository.networkrequests.RestClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.Response

class JobDetailInteractor {
    fun getJobDetail(
        mediaPopulate: Boolean,
        userPopulated: Boolean,
        mJobId: String,
        networkRequestCallbacks: NetworkRequestCallbacks
    ): Disposable {
        return RestClient.get().jobDetail(mJobId,mediaPopulate,userPopulated)
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

    fun getJobTimeLine(
        mJobId: String,
        mTimeLineRequestModel: TimeLineRequestModel,
        networkRequestCallbacks: NetworkRequestCallbacks
    ): Disposable {
        return RestClient.get().jobTimeline(mJobId,mTimeLineRequestModel)
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

    fun deleteJob(
        mJobId: String,
        networkRequestCallbacks: NetworkRequestCallbacks
    ): Disposable {
        return RestClient.get().deleteJob(mJobId)
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

    fun getJobMedia(
        mJobId: String,
        media:String,
        networkRequestCallbacks: NetworkRequestCallbacks
    ): Disposable {
        return RestClient.get().getJobMedia(mJobId,media)
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

    fun getConversation(
        mJobId: String,
        kind:String,
        search:String,
        networkRequestCallbacks: NetworkRequestCallbacks
    ): Disposable {
        return RestClient.get().getConversation(kind,mJobId,search)
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

    fun getMediaComment(
        mJobId: String,
        mMediaId:String,
        mSubMediaId:String,
        kind:String,
        search:String,
        networkRequestCallbacks: NetworkRequestCallbacks
    ): Disposable {
        return RestClient.get().getMediaComment(kind,mJobId,mMediaId,mSubMediaId,search)
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

    fun getAllComments(
        mJobId: String,
        search:String,
        networkRequestCallbacks: NetworkRequestCallbacks
    ): Disposable {
        return RestClient.get().getAllComments(mJobId,search)
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

    fun createConversation(
        mJobId: String,
        kind:String,
        message:String,
        networkRequestCallbacks: NetworkRequestCallbacks
    ): Disposable {
        return RestClient.get().createConversation(kind,mJobId,message)
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

    fun createComment(
        mJobId: String,
        mMediaId: String,
        mSubMediaId: String,
        kind:String,
        message:String,
        networkRequestCallbacks: NetworkRequestCallbacks
    ): Disposable {
        return RestClient.get().createComment(kind,mJobId,message,mMediaId,mSubMediaId)
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

