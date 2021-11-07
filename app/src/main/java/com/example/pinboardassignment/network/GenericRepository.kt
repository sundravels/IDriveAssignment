package com.example.pinboardassignment.network

import android.graphics.Bitmap
import android.graphics.BitmapFactory


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

    //download files
    suspend fun downloadFile(
        sUrl: String,
        bPostOrGET: Boolean = false,
    ): Bitmap? {
        return try {
            val service = APICallInterface.getRetrofit().create(APICallInterface::class.java)
            val response = when (bPostOrGET) {
                true -> service.postRequest(sUrl)
                else -> service.getResponse(sUrl)
            }

            val inputStream = response.body()?.byteStream()
            return BitmapFactory.decodeStream(inputStream)


        } catch (e: Exception) {
            null
        }

    }

    companion object {
        val genericRepositoryInstance = GenericRepository()
    }

}