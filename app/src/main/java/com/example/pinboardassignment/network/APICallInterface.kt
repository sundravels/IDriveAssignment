package com.example.pinboardassignment.network

import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url


/**
 * @author: SundravelS
 *
 * @desc: Below interface is used for calling API.
 */

interface APICallInterface {
    @GET
    suspend fun getResponse(@Url sUrl: String): Response<ResponseBody>

    @POST
    suspend fun postRequest(@Url sUrl: String): Response<ResponseBody>


    companion object {


        const val sBaseUrl = "https://pastebin.com/"
        const val sPinBoardResponseUrl = "https://pastebin.com/raw/wgkJgazE"

        //getting instance of OKHttpClient
        fun getClient(): OkHttpClient {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            return OkHttpClient().newBuilder().addInterceptor(interceptor).build()
        }

        //getting instance of Retrofit
        fun getRetrofit(): Retrofit {
            return Retrofit.Builder().baseUrl(sBaseUrl).client(getClient()).build()
        }
    }


}