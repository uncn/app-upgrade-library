package com.uncn.upgrade.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.uncn.upgrade.AppUpgradeManager
import com.uncn.upgrade.AppUpgradeLogger

class AppInstallReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_INSTALL = "com.uncn.upgrade.action.INSTALL"
        const val EXTRA_APK_PATH = "apk_path"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) {
            AppUpgradeLogger.w("Received null context or intent")
            return
        }

        when (intent.action) {
            ACTION_INSTALL -> {
                val apkPath = intent.getStringExtra(EXTRA_APK_PATH)
                if (apkPath != null) {
                    AppUpgradeLogger.d("Installing APK: $apkPath")
                    val manager = AppUpgradeManager(context)
                    manager.installApk(apkPath)
                    manager.release()
                } else {
                    AppUpgradeLogger.w("APK path is null")
                }
            }
            Intent.ACTION_PACKAGE_ADDED -> {
                val packageName = intent.data?.schemeSpecificPart
                AppUpgradeLogger.d("Package installed: $packageName")
            }
        }
    }
}