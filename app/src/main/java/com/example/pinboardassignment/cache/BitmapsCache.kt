package com.example.pinboardassignment.cache

import android.graphics.Bitmap
import android.util.LruCache


/**
 * @author: SundravelS
 *
 * @param maxSize: Int
 *
 * @desc: Below class to used for setting up LRU Cache
 */

class BitmapsCache(private val maxSize: Int) : LruCache<String, Bitmap>(maxSize) {


    override fun sizeOf(key: String?, value: Bitmap?): Int {
        val kbOfBitmap = (value?.byteCount ?: 0) / 1024
        return kbOfBitmap
    }

}