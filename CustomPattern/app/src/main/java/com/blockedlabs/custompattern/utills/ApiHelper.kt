package com.blockedlabs.custompattern.utills

import android.content.Context
import android.util.Log
import com.blockedlabs.custompattern.MyApplication
import com.blockedlabs.custompattern.interfaces.SuperInterface
import com.blockedlabs.custompattern.retrofit.ApiResponse
import com.blockedlabs.custompattern.retrofit.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ApiHelper(context: Context) {

    private val apiService: ApiService = MyApplication.instance.apiService

    private var apiQueue: ArrayList<String> = arrayListOf()
    private var apiCache: HashMap<String, ApiResponse> = hashMapOf()

    private var pendingResult: HashMap<String, ArrayList<(response: ApiResponse?) -> Unit>> = hashMapOf()

    fun get(
        api: String,
        call: Call<ApiResponse>,
        superInterface: SuperInterface,
        showLoader: Boolean = true,
        reload: Boolean = false,
        result: (response: ApiResponse?) -> Unit){

        if(reload){
            if(isCacheFound(api)){
                removeCache(api)
            }
        }

        if(isCacheFound(api)){
            provideResult(api, result)
            return
        }

        addPendingResult(api, result)

        if(isLoading(api)){
            return
        }

        if(showLoader)
            superInterface.showProgress()

        callApi(api, call){
            if(showLoader)
                superInterface.dismissProgress()
            if(it != null){
                addCache(api, it)
            }
            provideAllResult(api)
        }
    }

    private fun callApi(api: String, call: Call<ApiResponse>, result: (response: ApiResponse?) -> Unit){
        call.enqueue(object : Callback<ApiResponse> {
            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Log.i(javaClass.simpleName, "\n$api: Response Error!")
                        result(null)
            }

            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                Log.i(javaClass.simpleName, "\n$api: Response\n${response}")
                result(response.body())
            }
        })

    }

    private fun provideAllResult(api: String){
        val results = pendingResult[api]
        results?.forEach { provideResult(api, it) }
        results?.clear()
    }

    private fun provideResult(api: String, result: (response: ApiResponse?) -> Unit){
        result(getCache(api))
    }

    private fun addPendingResult(api: String, result: (response: ApiResponse?) -> Unit){
        var list = pendingResult[api]
        if(list != null){
            list.add(result)
        }else{
            list = arrayListOf(result)
            pendingResult[api] = list
        }
    }

    private fun isLoading(api: String): Boolean = apiQueue.contains(api)
    private fun isCacheFound(api: String): Boolean = apiCache.containsKey(api)

    private fun addQueue(api: String) = apiQueue.add(api)
    private fun addCache(api: String, response: ApiResponse) = apiCache.put(api, response)

    private fun getCache(api: String): ApiResponse? = apiCache[api]

    private fun removeQueue(api: String) = apiQueue.remove(api)
    private fun removeCache(api: String) = apiCache.remove(api)

    companion object: SingleTonHelper<ApiHelper, Context>(::ApiHelper)

}