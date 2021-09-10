package com.blockedlabs.custompattern.utills

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.blockedlabs.custompattern.interfaces.SuperInterface

object ActivityHelper {

    fun go(context: Context, clazz: Class<*>, vararg pairs: Pair<String, Any>){
        activity(context, clazz, true, newTask = false, pairs = *pairs)
    }

    fun open(context: Context, clazz: Class<*>, newTask: Boolean, vararg pairs: Pair<String, Any>){
        activity(context, clazz, newTask = newTask, pairs = *pairs)
    }

    fun open(context: Context, clazz: Class<*>, vararg pairs: Pair<String, Any>){
        activity(context, clazz, false, pairs = *pairs)
    }

    private fun activity(context: Context, activityCls: Class<*>,
                         closeTree: Boolean = false, newTask: Boolean = false, vararg pairs: Pair<String, Any>){

        val i = Intent(context, activityCls)

        for (pair in pairs){
            when(pair.second){
                is Int -> i.putExtra(pair.first, pair.second as Int)
                is Long -> i.putExtra(pair.first, pair.second as Long)
                is Float -> i.putExtra(pair.first, pair.second as Float)
                is Double -> i.putExtra(pair.first, pair.second as Double)
                is Boolean -> i.putExtra(pair.first, pair.second as Boolean)
                is Char -> i.putExtra(pair.first, pair.second as Char)
                is String -> i.putExtra(pair.first, pair.second as String)
                else -> i.putExtra(pair.first, (context as SuperInterface).gson().toJson(pair.second))
            }
        }

        if(newTask){
            println("New Task: ${context.javaClass.simpleName}")
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        context.startActivity(i)
        if(closeTree)
            if(context is Activity){
                context.finishAffinity()
            }
    }
}