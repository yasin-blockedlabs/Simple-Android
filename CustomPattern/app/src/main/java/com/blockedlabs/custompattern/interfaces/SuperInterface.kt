package com.blockedlabs.custompattern.interfaces

import android.content.Intent
import com.blockedlabs.custompattern.data.storages.LocalStorage
import com.blockedlabs.custompattern.retrofit.ApiRequest
import com.blockedlabs.custompattern.retrofit.ApiResponse
import com.blockedlabs.custompattern.retrofit.ApiService
import com.blockedlabs.custompattern.utills.ApiHelper
import com.google.gson.Gson
import retrofit2.Call

interface SuperInterface {

    fun gson(): Gson
    fun localStorage(): LocalStorage
    fun goToActivity(cls: Class<*>, vararg params: Pair<String, Any>)
    fun openActivity(cls: Class<*>, vararg params: Pair<String, Any>)
    fun openActivity(cls: Class<*>, newTask: Boolean, vararg params: Pair<String, Any>)
    fun goToFragment(fragmentPos: Int, vararg params: Pair<String, Any>)
    fun toast(message: String)
    fun shortToast(message: String)
    fun debugToast(message: String)
    fun apiHelper(): ApiHelper
    fun apiService(): ApiService
    fun callApi(requestCode: Int, request: ApiRequest? = null, call: Call<ApiResponse>)
    fun onResponse(requestCode: Int, request: ApiRequest?, response: ApiResponse)
    fun onError(requestCode: Int, request: ApiRequest?, throwable: Throwable)
    fun showProgress()
    fun isProgressShowing(): Boolean
    fun dismissProgress()
    fun requestPermission(vararg permissions: String, result: (permissionGranted: Boolean) -> Unit)
    fun registerReceiver(action: String, onReceive: (intent: Intent?) -> Unit)
    fun sendBroadcast(action: String)
    fun unregisterAllReceiver()
    fun infoLog(message: String?)
    fun errorLog(message: String?)

}