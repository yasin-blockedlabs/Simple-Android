package com.blockedlabs.custompattern.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.blockedlabs.custompattern.R
import com.blockedlabs.custompattern.retrofit.ApiRequest
import com.blockedlabs.custompattern.retrofit.ApiResponse

class MainActivity : SuperActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        return

        // toast
        shortToast("Short Toast")
        toast("Toast")
        debugToast("Debug Toast")

        // Log
        infoLog("Info Log")
        errorLog("Error Log")

        // Progress Dialog
        showProgress()
        isProgressShowing()
        dismissProgress()

        // Shared Preferences
        localStorage().token = "123"

        // Activity Helper

        /* close current activity and open New Activity */
        goToActivity(MainActivity::class.java, "TOKEN" to localStorage().token!!)

        /* opens new Activity in Stack, Also open as a NewTask */
        openActivity(MainActivity::class.java, false)

        // API using Retrofit

        val apiRequest = ApiRequest().also {
            it.username = "admin"
            it.password = "admin123"
        }

        callApi(1, apiRequest, apiService().login(apiRequest))

        // Broadcast

        sendBroadcast("TOKEN")

        registerReceiver("TOKEN"){
            // can receiver if broadcast sent!
            // broadcast can be received in overall app
        }
    }

    override fun onResponse(requestCode: Int, request: ApiRequest?, response: ApiResponse) {
        if (requestCode == 1){
            // Receive Login Response Here
            if (response.status == 1){
                localStorage().token = response.token
            }else{
                toast(response.message ?: "Login Error!")
            }
        }
    }
}