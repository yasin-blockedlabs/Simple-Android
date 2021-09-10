package com.blockedlabs.custompattern.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.blockedlabs.custompattern.MyApplication
import com.blockedlabs.custompattern.data.storages.LocalStorage
import com.blockedlabs.custompattern.dialogs.LoadingDialog
import com.blockedlabs.custompattern.interfaces.SuperInterface
import com.blockedlabs.custompattern.retrofit.ApiRequest
import com.blockedlabs.custompattern.retrofit.ApiResponse
import com.blockedlabs.custompattern.retrofit.ApiService
import com.blockedlabs.custompattern.utills.*
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.random.Random
import kotlin.random.nextInt

open class SuperActivity: AppCompatActivity(), SuperInterface {

    private val progressDialog: LoadingDialog by lazy { LoadingDialog(this) }
    private val activeApi: HashMap<Int, Call<ApiResponse>> by lazy { HashMap() }
    private val permissions: HashMap<Int, (permissionGranted: Boolean) -> Unit> by lazy { HashMap() }
    private val broadcastReceivers: HashMap<String, BroadcastReceiver> by lazy { HashMap() }

    override fun gson(): Gson = MyApplication.instance.gson

    override fun localStorage(): LocalStorage = MyApplication.instance.localStorage

    override fun goToActivity(cls: Class<*>, vararg params: Pair<String, Any>) {
        ActivityHelper.go(this, cls, *params)
    }

    override fun openActivity(cls: Class<*>, vararg params: Pair<String, Any>) {
        ActivityHelper.open(this, cls, *params)
    }

    override fun openActivity(cls: Class<*>, newTask: Boolean, vararg params: Pair<String, Any>) {
        ActivityHelper.open(this, cls, newTask, *params)
    }

    override fun goToFragment(fragmentPos: Int, vararg params: Pair<String, Any>) {
//        TODO("Not yet implemented")
    }

    override fun toast(message: String) { ToastUtills.toast(this, message) }

    override fun shortToast(message: String) { ToastUtills.shortToast(this, message) }

    override fun debugToast(message: String) { ToastUtills.debugToast(this, message) }

    override fun apiHelper(): ApiHelper = MyApplication.instance.apiHelper

    override fun apiService(): ApiService = MyApplication.instance.apiService

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
                    this@SuperActivity.onResponse(
                        requestCode, request, apiResponse
                    )
                } else {
                    this@SuperActivity.onError(
                        requestCode, request,
                        Throwable(response.message())
                    )
                }

            }
        })
    }

    override fun onResponse(requestCode: Int, request: ApiRequest?, response: ApiResponse) {

    }

    override fun onError(requestCode: Int, request: ApiRequest?, throwable: Throwable) {

    }

    override fun showProgress() {
        progressDialog.show()
    }

    override fun isProgressShowing(): Boolean = progressDialog.isShowing()

    override fun dismissProgress() {
        if (!isAnyActiveApi())
            progressDialog.dismiss()
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
        PermissionUtills.requestPermission(this, requestCode, *permissions)
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
            .getInstance(this)
            .registerReceiver(receiver, IntentFilter(action))
        broadcastReceivers[action] = receiver
    }

    override fun sendBroadcast(action: String) {
        LocalBroadcastManager.getInstance(this).sendBroadcast(Intent(action))
    }

    override fun unregisterAllReceiver() {
        for (register
        in broadcastReceivers.values){
            LocalBroadcastManager
                .getInstance(this)
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