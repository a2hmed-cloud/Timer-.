package com.example

import android.Manifest
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.entity.ColorAccent
import com.example.data.repository.AppLanguage
import com.example.data.repository.UserPreferences
import com.example.presentation.navigation.StudyFlowApp
import com.example.ui.theme.StudyFlowTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.util.Locale

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as StudyFlowApplication

        setContent {
            val prefs by app.userPreferencesRepository.userPreferencesFlow
                .collectAsStateWithLifecycle(initialValue = UserPreferences())

            // Request Notification Permission on Android 13+ (TIRAMISU) if enabled
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && prefs.notificationsEnabled) {
                val notificationPermissionState = rememberPermissionState(
                    permission = Manifest.permission.POST_NOTIFICATIONS
                )
                LaunchedEffect(Unit) {
                    if (!notificationPermissionState.status.isGranted) {
                        notificationPermissionState.launchPermissionRequest()
                    }
                }
            }

            // Determine Layout Direction (Arabic = RTL, English = LTR, System = default)
            val layoutDirection = when (prefs.language) {
                AppLanguage.ARABIC -> LayoutDirection.Rtl
                AppLanguage.ENGLISH -> LayoutDirection.Ltr
                AppLanguage.SYSTEM -> {
                    val systemLocale = Locale.getDefault()
                    if (systemLocale.language == "ar") LayoutDirection.Rtl else LayoutDirection.Ltr
                }
            }

            CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                StudyFlowTheme(
                    themeMode = prefs.themeMode,
                    colorAccent = prefs.colorAccent
                ) {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        StudyFlowApp(onboardingCompleted = prefs.onboardingCompleted)
                    }
                }
            }
        }
    }
}
