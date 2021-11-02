package com.example.pinboardassignment.network

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.pinboardassignment.model.PinBoardResponse
import com.example.pinboardassignment.utils.LoggerClass
import java.lang.Exception
import java.net.URL
import javax.net.ssl.HttpsURLConnection


/**
 * @author: SundravelS
 *
 * @desc: Below class is a repository class used to retrieve data from server
 */
class GenericRepository {

    suspend fun <T> callAPI(
        sUrl: String,
        tClass: Class<T>,
        bPostOrGET: Boolean = false,
        bJsonArray: Boolean = false
    ): HandleResponse<T> {
        return try {
            val service = APICallInterface.getRetrofit().create(APICallInterface::class.java)
            val response = when (bPostOrGET) {
                true -> service.postRequest(sUrl)
                else -> service.getResponse(sUrl)
            }
            HandleResponse.parseResponse(response, tClass = tClass, bJsonArry = bJsonArray)
        } catch (e: Exception) {

            HandleResponse.parseResponse(null, tClass = tClass)

        }


    }

    /**
     * @author: SundravelS on 31-10-2021
     *
     * @param sUrl:String
     * @param model:PinBoardResponse
     * @desc: Below Class download bitmap from server
     *
     */

    fun downloadBitmap(sUrl: String, model: PinBoardResponse): Bitmap? {
        LoggerClass.getLoggerClass().verbose(data = sUrl)
        var urlConnection: HttpsURLConnection? = null
        try {

            val uri = URL(sUrl)
            urlConnection = uri.openConnection() as HttpsURLConnection


            when {
                model.status -> {
                    urlConnection.disconnect()
                    return null
                }

            }
            val statusCode = urlConnection.responseCode

            if (statusCode != HttpsURLConnection.HTTP_OK) {
                return null
            }
            val inputStream = urlConnection.inputStream
            if (inputStream != null) {

                val bitmap = BitmapFactory.decodeStream(inputStream)

                return bitmap
            }
        } catch (e: Exception) {
            urlConnection?.disconnect()
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect()
            }

        }

        return null
    }

    companion object {
        val genericRepositoryInstance = GenericRepository()
    }

}