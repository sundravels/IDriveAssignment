package com.example.pinboardassignment.utils

import android.app.ActivityManager
import android.content.Context
import android.graphics.Bitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.pinboardassignment.R
import com.google.android.material.imageview.ShapeableImageView
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.json.JSONArray


/**
 * @author: SundravelS
 *
 * @desc: Utility class
 */


object AppUtils {


    const val sErrorMessage = "Error Occurred"
     val jobId = CoroutineScope(Job()+ Dispatchers.Main)

    /**
     * @author: SundravelS on 31-10-2021
     *
     * @param tClass:Class
     * @param sResponse:String
     * @desc: Below function parses JSON Array and returns ArrayList
     *
     */

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

    /**
     * @author: SundravelS on 31-10-2021
     *
     * @param bitmap:Bitmap
     * @param view:ShapeableImageView
     * @desc: Below function download bitmap from server
     *
     */

    fun populateGlide(context: Context, bitmap: Bitmap?, view: ShapeableImageView) {
        Glide.with(context).asBitmap().load(when(bitmap){
            null -> R.drawable.image_placeholder
            else -> bitmap
        }).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)
            .placeholder(R.drawable.image_placeholder)
            .error(R.drawable.image_placeholder)
            .into(view)
    }

    /**
     * @author: SundravelS on 31-10-2021
     *
     * @param context:Context
     * @param bitmap:Bitmap
     * @param view:ShapeableImageView
     * @desc: Below function download bitmap from server
     *
     */

    fun populateGlideWithDiskCache(context: Context, sUrl: String, view: ShapeableImageView) {
        Glide.with(context).asBitmap().load(sUrl)
            .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true)
            .placeholder(R.drawable.image_placeholder)
            .error(R.drawable.image_placeholder)
            .into(view)

    }

    /**
     * @author: SundravelS on 31-10-2021
     *
     * @param context:Context
     *
     * @desc: Below function returns maz size for cache
     *
     */

    fun getMaxSize(context: Context): Int {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
        return (am?.memoryClass ?: 0) * 1024 * 1024
    }

}