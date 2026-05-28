package com.uncn.upgrade

import android.util.Log

object AppUpgradeLogger {
    private const val TAG = "AppUpgrade"
    private var isDebugMode = true

    fun setDebugMode(debug: Boolean) {
        isDebugMode = debug
    }

    fun d(message: String) {
        if (isDebugMode) Log.d(TAG, message)
    }

    fun i(message: String) = Log.i(TAG, message)
    fun w(message: String) = Log.w(TAG, message)
    fun e(message: String, throwable: Throwable? = null) {
        if (throwable != null) Log.e(TAG, message, throwable) else Log.e(TAG, message)
    }
}