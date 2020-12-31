package com.kernel.finch.implementation

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.kernel.finch.FinchCore
import com.kernel.finch.R
import com.kernel.finch.common.models.Inset
import com.kernel.finch.core.presentation.InternalDebugMenuView
import com.kernel.finch.utils.extensions.colorResource
import com.kernel.finch.utils.extensions.tintedDrawable

internal class DebugMenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        FinchCore.implementation.configuration.themeResourceId?.let { setTheme(it) }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.finch_activity_debug_menu)
        supportActionBar?.hide()
        findViewById<Toolbar>(R.id.finch_toolbar).apply {
            setNavigationOnClickListener { onBackPressed() }
            navigationIcon = tintedDrawable(
                R.drawable.finch_ic_close,
                colorResource(android.R.attr.textColorPrimary)
            )
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            val debugMenu = findViewById<InternalDebugMenuView>(R.id.finch_debug_menu)
            val bottomNavigationOverlay = findViewById<View>(R.id.finch_bottom_navigation_overlay)
            bottomNavigationOverlay.setBackgroundColor(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    window.navigationBarColor
                } else {
                    Color.BLACK
                }
            )
            window.decorView.run {
                setOnApplyWindowInsetsListener { _, insets ->
                    onApplyWindowInsets(insets).also {
                        val input = Inset(
                            left = it.systemWindowInsetLeft,
                            top = it.systemWindowInsetTop,
                            right = it.systemWindowInsetRight,
                            bottom = it.systemWindowInsetBottom
                        )
                        val output =
                            FinchCore.implementation.configuration.applyInsets?.invoke(input)
                                ?: Inset(
                                    left = it.systemWindowInsetLeft,
                                    top = 0,
                                    right = it.systemWindowInsetRight,
                                    bottom = it.systemWindowInsetBottom
                                )
                        debugMenu.applyInsets(output.left, output.top, output.right, output.bottom)
                        bottomNavigationOverlay.run {
                            layoutParams = layoutParams.apply { height = output.bottom }
                        }
                    }
                }
                requestApplyInsets()
            }
        }
        if (savedInstanceState == null) {
            FinchCore.implementation.notifyVisibilityListenersOnShow()
        }
    }

    override fun onStart() {
        super.onStart()
        (FinchCore.implementation.uiManager as ActivityUiManager).debugMenuActivity = this
    }

    override fun onStop() {
        (FinchCore.implementation.uiManager as ActivityUiManager).let {
            if (it.debugMenuActivity == this) {
                it.debugMenuActivity = null
                if (isFinishing) {
                    FinchCore.implementation.notifyVisibilityListenersOnHide()
                }
            }
        }
        super.onStop()
    }
}
