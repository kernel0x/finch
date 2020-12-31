package com.kernel.finch.sample

import android.app.Application
import com.kernel.finch.Finch
import com.kernel.finch.common.models.Configuration
import com.kernel.finch.components.Divider
import com.kernel.finch.components.Label
import com.kernel.finch.components.Padding
import com.kernel.finch.components.special.*
import com.kernel.finch.log.FinchLogger
import com.kernel.finch.networklog.okhttp.FinchOkHttpLogger

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        Finch.initialize(
            application = this,
            configuration = Configuration(
                logger = FinchLogger,
                networkLoggers = listOf(FinchOkHttpLogger)
            ),
            components = arrayOf(
                Header(
                    title = getString(R.string.app_name),
                    subtitle = BuildConfig.APPLICATION_ID,
                    text = "${BuildConfig.BUILD_TYPE} v${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
                ),
                Padding(),
                Label("Tools", Label.Type.HEADER),
                DesignOverlay(),
                AnimationSpeed(),
                ScreenCaptureToolbox(),
                Divider(),
                Label("Logs", Label.Type.HEADER),
                LifecycleLogs(),
                NetworkLogs(),
                Logs(),
                Divider(),
                Label("Other", Label.Type.HEADER),
                DeviceInfo(),
                AppInfo(),
                DeveloperOptions(),
                ForceCrash()
            )
        )
    }
}
