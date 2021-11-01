package com.example.pinboardassignment.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pinboardassignment.model.PinBoardResponse
import com.example.pinboardassignment.network.GenericRepository
import com.example.pinboardassignment.network.HandleResponse
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch


/**
 * @author: SundravelS
 *
 * @desc: Below View model is used to hold pin board response
 */

class PinBoardViewModel: ViewModel() {

    private val _arrPinBoardResponse = MutableLiveData<HandleResponse<PinBoardResponse>>()
    var arrPinBoardResponse: LiveData<HandleResponse<PinBoardResponse>> = _arrPinBoardResponse


    //Exception handler to catch any exception thrown while calling API
    private val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        _arrPinBoardResponse.value = HandleResponse.parseResponse(null,tClass = PinBoardResponse::class.java)
    }


    fun callAPI(sUrl:String){
        viewModelScope.launch(exceptionHandler) {
            _arrPinBoardResponse.value = GenericRepository.genericRepositoryInstance.callAPI(sUrl=sUrl,tClass = PinBoardResponse::class.java,bJsonArray = true)
        }
    }


}