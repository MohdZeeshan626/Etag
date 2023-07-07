package com.max360group.cammax360.repository.interactors

import com.max360group.cammax360.repository.models.CreateOwnerRequestModel
import com.max360group.cammax360.repository.models.CreatePropertyRequestModel
import com.max360group.cammax360.repository.models.OwnerCreateNoteRequestModel
import com.max360group.cammax360.repository.models.UpdateAccountRequestModel
import com.max360group.cammax360.repository.networkrequests.NetworkRequestCallbacks
import com.max360group.cammax360.repository.networkrequests.RestClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.Response


/**
 * Created by Mukesh on 20/7/18.
 */
class LocationInteractor {
    fun getProperties(
        search:String,
        networkRequestCallbacks: NetworkRequestCallbacks
    ): Disposable {
        return RestClient.get().getPropertiesAll(search)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableObserver<Response<*>>() {
                override fun onNext(response: Response<*>) {
                    networkRequestCallbacks.onSuccess(response)
                }
                override fun onError(t: Throwable) {
                    networkRequestCallbacks.onError(t)
                }
                override fun onComplete() {

                }
            })
    }


}