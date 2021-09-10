package com.blockedlabs.custompattern.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.blockedlabs.custompattern.MyApplication
import com.blockedlabs.custompattern.activities.SuperActivity
import com.blockedlabs.custompattern.data.storages.LocalStorage
import com.blockedlabs.custompattern.interfaces.SuperInterface
import com.blockedlabs.custompattern.retrofit.ApiRequest
import com.blockedlabs.custompattern.retrofit.ApiResponse
import com.blockedlabs.custompattern.retrofit.ApiService
import com.blockedlabs.custompattern.utills.ApiHelper
import com.blockedlabs.custompattern.utills.LogUtills
import com.blockedlabs.custompattern.utills.PermissionUtills
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.random.Random
import kotlin.random.nextInt

class SuperFragment: Fragment(), SuperInterface {

    private val activeApi: HashMap<Int, Call<ApiResponse>> by lazy { HashMap() }
    private val permissions: HashMap<Int, (permissionGranted: Boolean) -> Unit> by lazy { HashMap() }
    private val broadcastReceivers: HashMap<String, BroadcastReceiver> by lazy { HashMap() }

    private fun application(): MyApplication = requireActivity().application as MyApplication
    private fun activity(): SuperActivity = requireActivity() as SuperActivity

    override fun gson(): Gson = activity().gson()

    override fun localStorage(): LocalStorage = activity().localStorage()

    override fun goToActivity(cls: Class<*>, vararg params: Pair<String, Any>) {
        activity().goToActivity(cls, *params)
    }

    override fun openActivity(cls: Class<*>, vararg params: Pair<String, Any>) {
        activity().openActivity(cls, *params)
    }

    override fun openActivity(cls: Class<*>, newTask: Boolean, vararg params: Pair<String, Any>) {
        activity().openActivity(cls, newTask, *params)
    }

    override fun goToFragment(fragmentPos: Int, vararg params: Pair<String, Any>) {
        TODO("Not yet implemented")
    }

    override fun toast(message: String) {
        activity().toast(message)
    }

    override fun shortToast(message: String) {
        activity().shortToast(message)
    }

    override fun debugToast(message: String) {
        activity().debugToast(message)
    }

    override fun apiHelper(): ApiHelper = activity().apiHelper()

    override fun apiService(): ApiService = activity().apiService()

    override fun callApi(requestCode: Int, request: ApiRequest?, call: Call<ApiResponse>) {
        showProgress()
        addActiveApi(requestCode, call)
        call.enqueue(object : Callback<ApiResponse> {
            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                removeActiveApi(requestCode)
                dismissProgress()
                onError(requestCode, request, t)
            }

            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                removeActiveApi(requestCode)
                dismissProgress()
                val apiResponse = response.body()
                if (apiResponse != null) {
                    this@SuperFragment.onResponse(
                        requestCode, request, apiResponse
                    )
                } else {
                    this@SuperFragment.onError(
                        requestCode, request,
                        Throwable(response.message())
                    )
                }

            }
        })
    }

    override fun onResponse(requestCode: Int, request: ApiRequest?, response: ApiResponse) {
        TODO("Not yet implemented")
    }

    override fun onError(requestCode: Int, request: ApiRequest?, throwable: Throwable) {
        TODO("Not yet implemented")
    }

    override fun showProgress() {
        activity().showProgress()
    }

    override fun isProgressShowing(): Boolean = activity().isProgressShowing()

    override fun dismissProgress() {
        if (!isAnyActiveApi())
            activity().dismissProgress()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun requestPermission(
        vararg permissions: String,
        result: (permissionGranted: Boolean) -> Unit
    ) {
        var requestCode = 0
        do {
            requestCode = Random.nextInt(0..10000)
        } while (this.permissions.containsKey(requestCode))
        this.permissions[requestCode] = result
        PermissionUtills.requestPermission(activity(), requestCode, *permissions)
    }

    override fun registerReceiver(action: String, onReceive: (intent: Intent?) -> Unit) {
        if(broadcastReceivers.containsKey(action))
            throw Exception("${javaClass.simpleName}:" +
                    " Broadcast Receiver for action \"$action\" already found!")
        val receiver = object: BroadcastReceiver(){
            override fun onReceive(p0: Context?, p1: Intent?) {
                onReceive(p1)
            }
        }
        LocalBroadcastManager
            .getInstance(activity())
            .registerReceiver(receiver, IntentFilter(action))
        broadcastReceivers[action] = receiver
    }

    override fun sendBroadcast(action: String) {
        LocalBroadcastManager.getInstance(activity()).sendBroadcast(Intent(action))
    }

    override fun unregisterAllReceiver() {
        for (register
        in broadcastReceivers.values){
            LocalBroadcastManager
                .getInstance(activity())
                .unregisterReceiver(register)
        }
        broadcastReceivers.clear()
    }

    override fun infoLog(message: String?) {
        LogUtills.infoLog(this, message)
    }

    override fun errorLog(message: String?) {
        LogUtills.errorLog(this, message)
    }

    private fun addActiveApi(requestCode: Int, call: Call<ApiResponse>) {
        activeApi[requestCode] = call
    }

    private fun removeActiveApi(requestCode: Int) {
        activeApi.remove(requestCode)
    }

    private fun isActiveApi(requestCode: Int): Boolean = activeApi.containsKey(requestCode)

    private fun isAnyActiveApi(): Boolean = activeApi.isNotEmpty()

    override fun onDestroy() {
        unregisterAllReceiver()
        super.onDestroy()
    }

}