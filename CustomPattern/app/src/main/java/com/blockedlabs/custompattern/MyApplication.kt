package com.blockedlabs.custompattern

import android.app.Application
import com.blockedlabs.custompattern.data.storages.LocalStorage
import com.blockedlabs.custompattern.retrofit.ApiService
import com.blockedlabs.custompattern.retrofit.Retrofit
import com.blockedlabs.custompattern.utills.ApiHelper
import com.google.gson.Gson

class MyApplication(): Application() {

    lateinit var localStorage: LocalStorage
    lateinit var gson: Gson
    lateinit var apiService: ApiService
    lateinit var apiHelper: ApiHelper

    override fun onCreate() {
        super.onCreate()

        instance = this
        localStorage = LocalStorage.getInstance(this)
        gson = Gson()
        apiService = Retrofit.getInstance().getService()
        apiHelper = ApiHelper.getInstance(this)

    }

    companion object {

        lateinit var instance: MyApplication

    }

}