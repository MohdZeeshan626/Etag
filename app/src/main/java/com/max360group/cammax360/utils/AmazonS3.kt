package com.max360group.cammax360.utils

import android.content.Context
import android.util.Log
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.CannedAccessControlList
import java.io.File


/**
 * Created by Mukesh on 01/03/20.
 */
class AmazonS3(private val context: Context) {

    companion object {
        private const val COGNITO_POOL_ID = "us-east-2:ba1c4d6c-1a94-430f-9781-22c84d005903"
        private const val COGNITO_POOL_REGION = "us-east-2"
        private const val S3_BUCKET_REGION = "us-east-2"
        internal const val S3_BUCKET_FOR_USER_PHOTOS = "general-home-updated/"
        internal const val S3_BUCKET_FOR_USER_QUESTIONS = "base-updated/user_profile_questions"

        internal const val S3_BUCKET_BASE_URL_FOR_USER_PHOTOS =
            "https://base-updated.s3-us-west-2.amazonaws.com/user_profile_photos/"

        internal const val S3_BUCKET_BASE_URL_FOR_USER_QUESTIONS =
            "https://base-updated.s3-us-west-2.amazonaws.com/user_profile_questions/"
    }

    private val mAmazonS3Client by lazy {
        AmazonS3Client(
            CognitoCachingCredentialsProvider(
                context,
                COGNITO_POOL_ID,
                Regions.fromName(COGNITO_POOL_REGION)
            ), Region.getRegion(Regions.fromName(S3_BUCKET_REGION))
        )
    }

    private val mTransferUtility by lazy {
        TransferUtility.builder()
            .context(context)
//            .awsConfiguration(AWSConfiguration(context))
            .s3Client(mAmazonS3Client)
            .build()
    }


    /**
     * This method is used to upload file to S3 by using TransferUtility class
     */
    fun uploadFileToS3(file: File, s3Bucket: String) {
        setTransferObserverListener(
            mTransferUtility
                .upload(
                    s3Bucket,
                    file.name,
                    file,
                    CannedAccessControlList.PublicRead
                )
        )
    }

    /**
     * This is listener method of the TransferObserver
     * Within this listener method, we got status of uploading and downloading file,
     * to diaplay percentage of the part of file to be uploaded or downloaded to S3
     * It display error, when there is problem to upload and download file to S3.
     *
     *
     * transferObserver
     */
    private fun setTransferObserverListener(transferObserver: TransferObserver) {

        transferObserver.setTransferListener(object : TransferListener {

            override fun onStateChanged(id: Int, state: TransferState) {
                Log.e("onStateChanged", state.toString() + "")
            }

            override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                val percentage = (bytesCurrent / bytesTotal * 100).toInt()
                Log.e("onProgressChanged", percentage.toString() + "")
            }

            override fun onError(id: Int, ex: Exception) {
                Log.e("error", "error    " + id + "  " + ex.toString())
            }

        })
    }

}
