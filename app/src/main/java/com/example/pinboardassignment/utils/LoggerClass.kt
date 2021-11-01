package com.example.pinboardassignment.utils

import android.util.Log


/**
 * @author: SundravelS
 *
 * @desc: Below is generic class used for printing Logs throughout the app.
 */


class LoggerClass {

    fun <T> verbose(sTAG: String = "TAG", data: T) {
        Log.v(sTAG, "${data}")
    }

    companion object {
        fun getLoggerClass() = LoggerClass()

    }

}