package com.blockedlabs.custompattern.utills

import android.content.Context
import android.widget.Toast
import com.blockedlabs.custompattern.BuildConfig

object ToastUtills {
    
    fun toast(context: Context, message: String){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
    
    fun shortToast(context: Context, message: String){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
    
    fun debugToast(context: Context, message: String){
        if (BuildConfig.DEBUG)
            Toast.makeText(context, "#DEBUG:\n$message", Toast.LENGTH_LONG).show()
    }
    
}