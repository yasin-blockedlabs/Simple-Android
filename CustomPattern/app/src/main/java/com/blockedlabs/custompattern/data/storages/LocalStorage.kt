package com.blockedlabs.custompattern.data.storages

import android.content.Context
import android.content.SharedPreferences
import com.blockedlabs.custompattern.utills.SingleTonHelper

class LocalStorage(context: Context) {

    private val name = "LocalStorage"
    private lateinit var pref: SharedPreferences

    private val INTRO = "intro"
    private val USER = "user"

    var token: String?
    get() = getString("TOKEN")
    set(value) = setString("TOKEN", value)

    init {
        pref = context.getSharedPreferences(name, 0)
    }

    private fun contains(key: String): Boolean = pref.contains(key)

    private fun setBool(key: String, value: Boolean) = pref.edit().putBoolean(key, value).apply()

    private fun getBool(key: String, def: Boolean): Boolean = pref.getBoolean(key, def)

    private fun setString(key: String, value: String?) = pref.edit().putString(key, value).apply()

    private fun getString(key: String): String? = pref.getString(key, null)

    fun clearAll(){
        pref.edit().clear().apply()
    }

    companion object: SingleTonHelper<LocalStorage, Context>(::LocalStorage)

}