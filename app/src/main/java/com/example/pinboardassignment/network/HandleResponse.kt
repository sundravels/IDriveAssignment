package com.example.pinboardassignment.network

import com.example.pinboardassignment.utils.AppUtils
import com.google.gson.Gson
import okhttp3.ResponseBody
import java.lang.Exception


/**
 * @author: SundravelS
 *
 * @desc: Below sealed is used for handling server response
 */

sealed class HandleResponse<T> {


    companion object {
        fun <T> parseResponse(
            responseBody: retrofit2.Response<ResponseBody>?,
            tClass: Class<T>,
            bJsonArry: Boolean = false
        ): HandleResponse<T> {
            val gson = Gson()

            return when (responseBody) {
                null -> ErrorResponse("")
                else -> {
                    try {
                        when (bJsonArry) {
                            true -> {
                                JsonArrayResponse(AppUtils.getJSONArray(tClass,(responseBody.body() as ResponseBody).string()))
                            }
                            else -> JsonObjectResponse(
                                gson.fromJson(
                                    (responseBody.body() as ResponseBody).string(),
                                    tClass
                                )
                            )
                        }

                    } catch (e: Exception) {
                        ErrorResponse(e.localizedMessage)
                    }
                }
            }

        }
    }

}

//JSONObject
class JsonObjectResponse<T>(val data: T) : HandleResponse<T>()

//JsonArrayResponse
class JsonArrayResponse<T>(val data: ArrayList<T>) : HandleResponse<T>()

//ErrorResponse
class ErrorResponse<T>(val sData: String) : HandleResponse<T>()
