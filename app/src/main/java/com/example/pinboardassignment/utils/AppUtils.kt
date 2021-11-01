package com.example.pinboardassignment.utils

import com.google.gson.GsonBuilder
import org.json.JSONArray


/**
 * @author: SundravelS
 *
 * @desc: Utility class
 */


object AppUtils {


    const val sErrorMessage = "Error Occurred"

    //for parsing JsonArray
    fun <T> getJSONArray(tClass: Class<T>, sResponse: String): ArrayList<T> {

        val gsonBuilder = GsonBuilder()
        val gson = gsonBuilder.create()

        val list = ArrayList<T>()
        val jsonArray = JSONArray(sResponse)

        for (i in 0 until jsonArray.length()) {
            list.add(gson.fromJson(jsonArray.getJSONObject(i).toString(), tClass))

        }
        return list

    }

}