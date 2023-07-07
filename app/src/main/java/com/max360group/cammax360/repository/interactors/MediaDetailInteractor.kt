package com.max360group.cammax360.repository.interactors

import android.util.Log
import com.max360group.cammax360.repository.models.UpdateMediaPermissionsRequests
import com.max360group.cammax360.repository.models.UpdateMediaRequetsModel
import com.max360group.cammax360.repository.networkrequests.NetworkRequestCallbacks
import com.max360group.cammax360.repository.networkrequests.RestClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.Response

class MediaDetailInteractor {
    fun getJobDetail(
        mediaPopulate: Boolean,
        userPopulated: Boolean,
        mJobId: String,
        networkRequestCallbacks: NetworkRequestCallbacks
    ): Disposable {
        return RestClient.get().jobDetail(mJobId, mediaPopulate, userPopulated)
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
        mMediaId: String,
        mSubMediaId: String,
        kind: String,
        search: String,
        networkRequestCallbacks: NetworkRequestCallbacks
    ): Disposable {
        return RestClient.get().getMediaComment(kind, mJobId, mMediaId, mSubMediaId, search)
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
        kind: String,
        message: String,
        networkRequestCallbacks: NetworkRequestCallbacks
    ): Disposable {
        return RestClient.get().createComment(kind, mJobId, message, mMediaId, mSubMediaId)
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

    fun updateMediaInfo(
        mMediaId: String,
        kind: String,
        mUpdateMediaRequetsModel: UpdateMediaRequetsModel,
        networkRequestCallbacks: NetworkRequestCallbacks
    ): Disposable {
        return RestClient.get().updateMediaInfo(mMediaId, kind, mUpdateMediaRequetsModel)
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

    fun updateMediaPermissions(
        mMediaId: String,
        kind: String,
        mUpdateMediaPermissionsRequests: UpdateMediaPermissionsRequests,
        networkRequestCallbacks: NetworkRequestCallbacks
    ): Disposable {
        return RestClient.get()
            .updateMediaPermissions(mMediaId, kind, mUpdateMediaPermissionsRequests)
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


    fun getMediaDetail(
        mJobId: String,
        mMediaId: String,
        kind: String,
        subMediaId: String,
        networkRequestCallbacks: NetworkRequestCallbacks
    ): Disposable {
        return RestClient.get().photoDetail(mJobId, mMediaId, kind, subMediaId)
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

    fun deleteMedia(
        mJobId:String,
        kind: String,
        mMediaId: List<String>,
        networkRequestCallbacks: NetworkRequestCallbacks
    ): Disposable {
        return RestClient.get().deleteMedia(mJobId,kind, mMediaId)
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

