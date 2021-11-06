package com.example.pinboardassignment.model

import android.graphics.Bitmap
import com.example.pinboardassignment.utils.AppUtils
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

/**
 * @author: SundravelS
 *
 * @desc: Below data is used for parsing pinboard response from server
 */


data class PinBoardResponse(
    @SerializedName("id") val id: String,
    @SerializedName("created_at") val created_at: String,
    @SerializedName("width") val width: Int,
    @SerializedName("height") val height: Int,
    @SerializedName("color") val color: String,
    @SerializedName("likes") val likes: Int,
    @SerializedName("liked_by_user") val liked_by_user: Boolean,
    @SerializedName("user") val user: User,
    @SerializedName("current_user_collections") val current_user_collections: ArrayList<String>,
    @SerializedName("urls") val urls: Urls,
    @SerializedName("categories") val categories: ArrayList<Categories>,
    @SerializedName("links") val links: Links
)

data class Categories(

    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("photo_count") val photo_count: Int,
    @SerializedName("links") val links: Links
)

data class Links(

    @SerializedName("self") val self: String,
    @SerializedName("html") val html: String,
    @SerializedName("download") val download: String
)

data class Profile_image(
    @SerializedName("small") val small: String,
    @SerializedName("medium") val medium: String,
    @SerializedName("large") val large: String
)

data class Urls(

    @SerializedName("raw") val raw: String,
    @SerializedName("full") val full: String,
    @SerializedName("regular") val regular: String,
    @SerializedName("small") val small: String,
    @SerializedName("thumb") val thumb: String
)


data class User(

    @SerializedName("id") val id: String,
    @SerializedName("username") val username: String,
    @SerializedName("name") val name: String,
    @SerializedName("profile_image") val profile_image: Profile_image,
    @SerializedName("links") val links: Links
)