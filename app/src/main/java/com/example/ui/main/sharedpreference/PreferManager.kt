package com.example.ui.main.sharedpreference

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object PreferManager {

    private const val APP_PREF = "APP_PREFER"
    private const val LOGGED_IN = "LOGGED_IN"
    private const val THEME_MODE = "THEME_MODE"
    private const val ACCENT_COLOR = "ACCENT_COLOR"
    private const val TEXT_COLOR = "TEXT_COLOR"
    private const val BOLD_TEXT = "BOLD_TEXT"

    private val DEFAULT_ACCENT_COLOR = 0xFF3F52C8.toInt()
    private val DEFAULT_TEXT_COLOR = 0xFFFFFFFF.toInt()

    fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(APP_PREF, Context.MODE_PRIVATE)
    }

    fun saveLogin(context: Context, loggedIn: Boolean) {
        getSharedPreferences(context).edit {
            putBoolean(LOGGED_IN, loggedIn)
        }
    }

    fun getLogin(context: Context): Boolean {
        return getSharedPreferences(context).getBoolean(LOGGED_IN, false)
    }

    fun clearLogin(context: Context) {
        getSharedPreferences(context).edit {
            clear()
        }
    }

    // Persistent Appearance Settings
    fun saveThemeMode(context: Context, mode: String) {
        getSharedPreferences(context).edit {
            putString(THEME_MODE, mode)
        }
    }

    fun getThemeMode(context: Context): String {
        return getSharedPreferences(context).getString(THEME_MODE, "Dark") ?: "Dark"
    }

    fun saveAccentColor(context: Context, colorInt: Int) {
        getSharedPreferences(context).edit {
            putInt(ACCENT_COLOR, colorInt)
        }
    }

    fun getAccentColor(context: Context): Int {
        return try {
            getSharedPreferences(context).getInt(ACCENT_COLOR, DEFAULT_ACCENT_COLOR)
        } catch (e: Exception) {
            DEFAULT_ACCENT_COLOR
        }
    }

    fun saveTextColor(context: Context, colorInt: Int) {
        getSharedPreferences(context).edit {
            putInt(TEXT_COLOR, colorInt)
        }
    }

    fun getTextColor(context: Context): Int {
        return try {
            getSharedPreferences(context).getInt(TEXT_COLOR, DEFAULT_TEXT_COLOR)
        } catch (e: Exception) {
            DEFAULT_TEXT_COLOR
        }
    }

    fun saveBoldText(context: Context, isBold: Boolean) {
        getSharedPreferences(context).edit {
            putBoolean(BOLD_TEXT, isBold)
        }
    }

    fun getBoldText(context: Context): Boolean {
        return getSharedPreferences(context).getBoolean(BOLD_TEXT, false)
    }
}
