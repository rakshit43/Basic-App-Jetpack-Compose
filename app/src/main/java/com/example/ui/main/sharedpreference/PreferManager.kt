package com.example.ui.main.sharedpreference

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object PreferManager {

    private val APP_PREF = "APP_PREFER"
    private val LOGGED_IN = "LOGGED_IN"


     fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(APP_PREF, Context.MODE_PRIVATE)
    }

     fun saveLogin(context: Context, loggedIn: Boolean){
        getSharedPreferences(context).edit {
            putBoolean(LOGGED_IN, loggedIn)
        }
    }
    fun getLogin(context: Context): Boolean {
        return getSharedPreferences(context).getBoolean(LOGGED_IN, false)
    }

    fun clearLogin(context: Context){
        getSharedPreferences(context).edit {
            clear()
        }
    }
}