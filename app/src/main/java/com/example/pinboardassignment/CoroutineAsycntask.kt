package com.imagepreviewer.idriveassignment

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import com.example.pinboardassignment.model.PinBoardResponse
import com.example.pinboardassignment.utils.AppUtils
import com.example.pinboardassignment.utils.LoggerClass
import com.google.android.material.imageview.ShapeableImageView

import kotlinx.coroutines.*


/**
 * @author: SundravelS on 31-10-2021
 * @desc: Below Class is used to perform background task using coroutines
 *
 */
abstract class CoroutineAsynctask<Params, Progress, Result> {

    private var job = CoroutineScope(Job() + Dispatchers.Main)


    /**
     * Below coroutines exception handler is used to capture any exceptions thrown,
     * and to cancel job inside exception scope
     *
     */

    private val coroutineExceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            throwable.localizedMessage?.toString()?.let {

                //job.cancel("")
                LoggerClass.getLoggerClass().verbose(data = "${throwable}")

                // job.cancel(it)
                onCancelled(it)
            }

        }

    @WorkerThread
    abstract suspend fun doInBackground(vararg params: Params): Result?


    @MainThread
    open fun onPostExecute(result: Result?, view: ShapeableImageView) {
    }

    @MainThread
    open fun onPreExecute(model: PinBoardResponse, job: CoroutineScope) {
    }

    open fun onCancelled(sErrorMessage: String) {}


    //launch task
    fun execute(
        vararg params: Params,
        model: PinBoardResponse,
        shapeableImageView: ShapeableImageView
    ) {
        if (!job.isActive) {
            job = CoroutineScope(Dispatchers.Main)
        }

        launch(params, model, job, shapeableImageView)
    }

    //cancel task
    fun cancelDownload(job: CoroutineScope) {

        job.let {
            //if job is active cancel it
            when (it.isActive) {
                true -> {
                    job.cancel()
                    onCancelled("")
                }
            }
        }


    }


    private fun launch(
        params: Array<out Params>,
        model: PinBoardResponse,
        job: CoroutineScope,
        shapeableImageView: ShapeableImageView
    ) {
        job.launch(coroutineExceptionHandler) {
            onPreExecute(model, job)
            withContext(Dispatchers.IO) {
                val result = doInBackground(*params)
                withContext(Dispatchers.Main) {
                    if (result != null) {
                        onPostExecute(result, shapeableImageView)
                    } else {
                        job.cancel("")
                        onCancelled(AppUtils.sErrorMessage)
                    }
                }
            }
        }

    }


}