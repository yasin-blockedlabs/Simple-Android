package com.blockedlabs.custompattern.utills

import android.util.Log
import com.blockedlabs.custompattern.BuildConfig

object LogUtills {

    fun infoLog(any: Any, message: String?){
        if (BuildConfig.DEBUG)
            message?.let { Log.i(any.javaClass.simpleName, it) }
    }

    fun errorLog(any: Any, message: String?){
        if (BuildConfig.DEBUG)
            message?.let { Log.e(any.javaClass.simpleName, it) }
    }
}