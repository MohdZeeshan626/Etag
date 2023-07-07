package com.max360group.cammax360.utils

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Address
import android.location.Geocoder
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.max360group.cammax360.R
import com.max360group.cammax360.utils.Constants.DATE_FORMAT_SERVER_ISO
import com.max360group.cammax360.views.fragments.SelectLocationFragment
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import id.zelory.compressor.constraint.size
import java.io.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * Created by Mukesh on 20/7/18.
 */
object GeneralFunctions {
    private const val ALPHA_NUMERIC_CHARS =
        "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"
    internal const val JPEG_FILE_PREFIX = "IMG_"
    internal const val JPEG_FILE_SUFFIX = ".jpg"
    internal const val VIDEO_FILE_PREFIX = "VID_"
    internal const val VIDEO_FILE_SUFFIX = ".mp4"
    internal const val VIDEO_THUMB_FILE_PREFIX = "Thumb_"
    internal const val VIDEO_THUMB_FILE_SUFFIX = ".jpg"
    private const val MIN_PASSWORD_LENGTH = 6
    private const val MAX_PASSWORD_LENGTH = 15
    const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    const val MEDIA_TYPE_VEDIO = 1
    const val MEDIA_TYPE_IMAGE = 0

    val isAboveLollipopDevice: Boolean
        get() = Build.VERSION_CODES.LOLLIPOP <= Build.VERSION.SDK_INT

    @Throws(IOException::class)
    fun setUpImageFile(directory: String): File? {
        var imageFile: File? = null
        if (Environment.MEDIA_MOUNTED == Environment
                .getExternalStorageState()
        ) {
            val storageDir = File(directory)
            if (!storageDir.mkdirs()) {
                if (!storageDir.exists()) {
                    Log.d("CameraSample", "failed to create directory")
                    return null
                }
            }

            imageFile = File.createTempFile(
                JPEG_FILE_PREFIX
                        + System.currentTimeMillis() + "_",
                JPEG_FILE_SUFFIX, storageDir
            )
        }
        return imageFile
    }

    @Throws(IOException::class)
    fun setUpVideoFile(directory: String): File? {
        var videoFile: File? = null
        if (Environment.MEDIA_MOUNTED == Environment
                .getExternalStorageState()
        ) {
            val storageDir = File(directory)
            if (!storageDir.mkdirs()) {
                if (!storageDir.exists()) {
                    return null
                }
            }

            videoFile = File.createTempFile(
                VIDEO_FILE_PREFIX
                        + System.currentTimeMillis() + "_",
                VIDEO_FILE_SUFFIX, storageDir
            )
        }
        return videoFile
    }

    fun getCurrentDate(): String {
        val c = Calendar.getInstance().time
        println("Current time => $c")

        val df =
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = df.format(c)


        return formattedDate
    }

    fun getCurrentServerDate(): String {
        val c = Calendar.getInstance().time
        println("Current time => $c")

        val df =
            SimpleDateFormat(Constants.DATE_FORMAT_SERVER, Locale.getDefault())
        val formattedDate = df.format(c)


        return formattedDate
    }

    /** Helper function used to create a timestamped file */
    internal fun createFile(
        context: Context,
        mMediaType: Int
    ): File {
        val filePrefix = when (mMediaType) {
            MEDIA_TYPE_IMAGE -> JPEG_FILE_PREFIX
            else -> {
                VIDEO_FILE_PREFIX
            }
        }

        val fileSuffix = when (mMediaType) {
            MEDIA_TYPE_IMAGE -> JPEG_FILE_SUFFIX
            else -> {
                VIDEO_FILE_SUFFIX
            }
        }
        return File(
            getOutputDirectory(context), filePrefix + SimpleDateFormat(
                FILENAME_FORMAT,
                Locale.US
            ).format(System.currentTimeMillis()) + fileSuffix
        )
    }

    fun getOutputDirectory(context: Context): File {
        val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
            File(it, context.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else context.filesDir
    }

    @Throws(IOException::class)
    fun setUpVideoThumbFile(directory: String): File? {
        var videoThumbFile: File? = null
        if (Environment.MEDIA_MOUNTED == Environment
                .getExternalStorageState()
        ) {
            val storageDir = File(directory)
            if (!storageDir.mkdirs()) {
                if (!storageDir.exists()) {
                    return null
                }
            }

            videoThumbFile = File.createTempFile(
                VIDEO_THUMB_FILE_PREFIX
                        + System.currentTimeMillis() + "_",
                VIDEO_THUMB_FILE_SUFFIX, storageDir
            )
        }
        return videoThumbFile
    }

    internal fun getDateFromMillis(
        dateInMillis: Long?,
        requiredFormat: String = Constants.DATE_FORMAT_DISPLAY
    ): String {
        try {
            return SimpleDateFormat(requiredFormat, Locale.US).format(Date(dateInMillis!!))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }


    fun generateRandomString(randomStringLength: Int): String {
        val buffer = StringBuffer()
        val charactersLength = ALPHA_NUMERIC_CHARS.length
        for (i in 0 until randomStringLength) {
            val index = Math.random() * charactersLength
            buffer.append(ALPHA_NUMERIC_CHARS.get(index.toInt()))
        }
        return buffer.toString()
    }

    fun isValidEmail(target: CharSequence?): Boolean {
        return null != target && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    fun isValidPassword(password: String): Boolean {
        return password.length in MIN_PASSWORD_LENGTH..MAX_PASSWORD_LENGTH
    }


    fun getResizedUserImage(imageUrl: String, imageWidth: Int = 0, imageHeight: Int = 0): String {
//        return when {
//            imageUrl.contains("graph.facebook.com") -> "$imageUrl?width=$imageWidth&height=$imageHeight"
//            imageUrl.contains("googleusercontent") -> imageUrl + "sz=" + imageWidth
//            else -> imageUrl + (if (0 != imageWidth) "/$imageWidth" else "") +
//                    if (0 != imageHeight) "/$imageHeight" else ""
//        }
        return imageUrl + (if (0 != imageWidth) "/$imageWidth" else "") +
                if (0 != imageHeight) "/$imageHeight" else ""
    }

    fun getResizedImage(imageUrl: String, imageWidth: Int, imageHeight: Int): String {
        return imageUrl + (if (0 != imageWidth) "/$imageWidth" else "") +
                if (0 != imageHeight) "/$imageHeight" else ""
    }

    fun getLocalImageFile(file: File): String {
        return "file://$file"
    }

    internal fun getLocalMediaFile(context: Context, mediaName: String): File {
        return File("${getOutputDirectory(context)}/$mediaName")
    }

    fun changeDateFormat(dob: String, currentFormat: String, requiredFormat: String): String {
        try {
            return SimpleDateFormat(requiredFormat, Locale.US)
                .format(SimpleDateFormat(currentFormat, Locale.US).parse(dob))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    fun isValidSelection(value: String, placeHolder: String): Boolean {
        return value != placeHolder
    }

    fun isBelow18(dob: String): Boolean {
        return false
    }

    fun calculateAge(dob: String): Int {
        try {
            val currentDate = Calendar.getInstance()

            val dateOfBirth = Calendar.getInstance()
            dateOfBirth.time =
                SimpleDateFormat(Constants.DATE_FORMAT_SERVER, Locale.US).parse(dob) ?: Date()

            var age = currentDate.get(Calendar.YEAR) - dateOfBirth[Calendar.YEAR]

            if (currentDate.get(Calendar.MONTH) < dateOfBirth[Calendar.MONTH] ||
                (currentDate.get(Calendar.MONTH) == dateOfBirth[Calendar.MONTH] &&
                        currentDate.get(Calendar.DAY_OF_MONTH) < dateOfBirth[Calendar.DAY_OF_MONTH])
            ) {
                age--
            }

            return age
        } catch (e: Exception) {
            e.printStackTrace()
            return 0
        }
    }

    internal fun getAddress(mLatitide: Double, mLongitude: Double, context: Context): String {
        var mLocationName = ""
        val geoCoder = Geocoder(context, Locale.getDefault())
        try {
            val addresses: List<Address> = geoCoder.getFromLocation(
                mLatitide, mLongitude,
                1
            )
            val obj: Address = addresses[0]
            val add = obj.getAddressLine(0)
            mLocationName = add.toString()

        } catch (e: Exception) {

        }
        return mLocationName
    }

    internal  fun getReverseGeoCoding(placeName:String,context:Context):List<Double>{
        //Get lat long from reverse geocoding
        val coordinates= mutableListOf<Double>()
        val coder = Geocoder(context)
        try {
            val address: ArrayList<Address> =
                coder.getFromLocationName(placeName, 50) as ArrayList<Address>
            for (add in address) {
                coordinates.add(add.latitude)
                coordinates.add(add.longitude) }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return coordinates
    }

    fun isRemoteImage(image: String): Boolean {
        return -1 == image.indexOf("/")
    }

//    fun getFileNameFromS3Url(media: String): String {
//        return media.substring(
//            media.lastIndexOf("/") + 1,
//            media.length
//        )
//    }

    fun getUserImage(image: String): String {
        return AmazonS3.S3_BUCKET_BASE_URL_FOR_USER_PHOTOS + image
    }

    fun getUserQuestionMedia(media: String): String {
        return AmazonS3.S3_BUCKET_BASE_URL_FOR_USER_QUESTIONS + media
    }

    fun stringToDate(mDate: String, format: String = DATE_FORMAT_SERVER_ISO): Date {
        val format = SimpleDateFormat(format)
        var date = Date()
        try {
            date = format.parse(mDate)
            println(date)
            return date
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return date
    }

    fun saveToSDCard(resourceID: Int, finalName: String, mContext: Context): String? {
        val createdFile = StringBuffer()
        val resourceImage = BitmapFactory.decodeResource(mContext.resources, resourceID)
        val externalStorageFile =
            File(
                mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                finalName
            )
        val bytes = ByteArrayOutputStream()
        resourceImage.compress(Bitmap.CompressFormat.JPEG, 60, bytes)
        val b: ByteArray = bytes.toByteArray()
        try {
            externalStorageFile.createNewFile()
            val filoutputStream: OutputStream = FileOutputStream(externalStorageFile)
            filoutputStream.write(b)
            filoutputStream.flush()
            filoutputStream.close()
            createdFile.append(externalStorageFile.absolutePath)
        } catch (e: IOException) {
        }
        return createdFile.toString()
    }

    fun bitmapToFile(bitmap: Bitmap, context: Context): File? { // File name like "image.png"
        //create a file to write bitmap data
        var file: File? = null
        return try {
            file = File(
                getOutputDirectory(context), JPEG_FILE_PREFIX + SimpleDateFormat(
                    FILENAME_FORMAT,
                    Locale.US
                ).format(System.currentTimeMillis()) + JPEG_FILE_SUFFIX
            )
            file.createNewFile()

            //Convert bitmap to byte array
            val bos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
            val bitmapdata = bos.toByteArray()

            //write the bytes in file
            val fos = FileOutputStream(file)
            fos.write(bitmapdata)
            fos.flush()
            fos.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            file // it will return null
        }
    }

    fun viewAddressOnMap(context: Context, latitude: Double, longitude: Double) {
        val uri =
            String.format(Locale.ENGLISH, "geo:%f,%f", latitude, longitude)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        context.startActivity(intent)
    }

    fun changeUtcToLocal(
        date: String, currentFormat: String = Constants.DATE_FORMAT_SERVER_ISO,
        displayFormat: String = Constants.DATE_FORMAT_DISPLAY1
    ): String {
        val df = SimpleDateFormat(currentFormat, Locale.ENGLISH)
        df.timeZone = TimeZone.getTimeZone("UTC")
        val date = df.parse(date)
        df.timeZone = TimeZone.getDefault()
        val formattedDate = df.format(date)

        return SimpleDateFormat(displayFormat, Locale.US)
            .format(SimpleDateFormat(currentFormat, Locale.US).parse(formattedDate))
    }

    fun getRealPathFromURI(context: Context, contentUri: Uri?): String? {
        var cursor: Cursor? = null
        return try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = context.contentResolver.query(contentUri!!, proj, null, null, null)
            val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            cursor.getString(column_index)
        } finally {
            cursor?.close()
        }
    }

    fun getStaticMap(mLatitude: Double, mLongitude: Double): String {
        var url = "https://maps.googleapis.com/maps/api/staticmap?"
        url += "&zoom=12"
        url += "&size=430x230"
        url += "&maptype=roadmap"
        url += """&markers=color:green%7Clabel:G%7C$mLatitude, $mLongitude"""
        url += "&key=AIzaSyC9GvyjT81OMWatHLczAlDI1B7ICtG_pEg"
        return url
    }


    fun isInternetConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false

            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true

                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        } else {
            // if the android version is below M
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }

    suspend fun compressMedia(context: Context, filePath: File): String {
        val compressedImageFile = Compressor.compress(context, filePath) {
            resolution(1280, 720)
            quality(70)
            format(Bitmap.CompressFormat.JPEG)
            size(2_097_152) // 2 MB
        }
        return compressedImageFile.absolutePath
    }

    fun phoneCall(number: String, context: Context) {
        val i = Intent(Intent.ACTION_DIAL)
        i.data = Uri.parse("tel:${number}")
        context.startActivity(i)

    }

    fun email(email: String, context: Context) {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:$email") // only email apps should handle this
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        }
    }

    fun covertTimeToText(dataDate: String?): String? {
        var convTime: String? = null
        val prefix = ""
        val suffix = "Ago"
        try {
            val dateFormat = SimpleDateFormat(Constants.DATE_FORMAT_DISPLAY1)
            val pasTime = dateFormat.parse(dataDate)
            val nowTime = Date()
            val dateDiff = nowTime.time - pasTime.time
            val second: Long = TimeUnit.MILLISECONDS.toSeconds(dateDiff)
            val minute: Long = TimeUnit.MILLISECONDS.toMinutes(dateDiff)
            val hour: Long = TimeUnit.MILLISECONDS.toHours(dateDiff)
            val day: Long = TimeUnit.MILLISECONDS.toDays(dateDiff)
            if (second < 60) {
                convTime = "$second Seconds $suffix"
            } else if (minute < 60) {
                convTime = "$minute Minutes $suffix"
            } else if (hour < 24) {
                convTime = "$hour Hours $suffix"
            } else if (day >= 7) {
                convTime = if (day > 360) {
                    (day / 360).toString() + " Years " + suffix
                } else if (day > 30) {
                    (day / 30).toString() + " Months " + suffix
                } else {
                    (day / 7).toString() + " Week " + suffix
                }
            } else if (day < 7) {
                convTime = "$day Days $suffix"
            }
        } catch (e: ParseException) {
            e.printStackTrace()
            Log.e("ConvTimeE", e.message!!)
        }
        return convTime
    }

}