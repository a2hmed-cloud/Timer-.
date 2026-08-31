package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.data.entity.ColorAccent
import com.example.data.repository.AppThemeMode

private fun buildLightScheme(primary: Color, secondary: Color = SecondaryLight): androidx.compose.material3.ColorScheme {
    return lightColorScheme(
        primary = primary,
        onPrimary = OnPrimaryLight,
        primaryContainer = primary.copy(alpha = 0.12f),
        onPrimaryContainer = primary,
        secondary = secondary,
        onSecondary = OnSecondaryLight,
        secondaryContainer = secondary.copy(alpha = 0.15f),
        onSecondaryContainer = secondary,
        tertiary = TertiaryLight,
        onTertiary = OnTertiaryLight,
        tertiaryContainer = TertiaryContainerLight,
        onTertiaryContainer = OnTertiaryContainerLight,
        background = BackgroundLight,
        onBackground = OnBackgroundLight,
        surface = SurfaceLight,
        onSurface = OnSurfaceLight,
        surfaceVariant = SurfaceVariantLight,
        onSurfaceVariant = OnSurfaceVariantLight,
        outline = OutlineLight
    )
}

private fun buildDarkScheme(primary: Color, secondary: Color = SecondaryDark): androidx.compose.material3.ColorScheme {
    return darkColorScheme(
        primary = primary,
        onPrimary = Color(0xFF0F172A),
        primaryContainer = primary.copy(alpha = 0.25f),
        onPrimaryContainer = primary,
        secondary = secondary,
        onSecondary = OnSecondaryDark,
        secondaryContainer = secondary.copy(alpha = 0.25f),
        onSecondaryContainer = secondary,
        tertiary = TertiaryDark,
        onTertiary = OnTertiaryDark,
        tertiaryContainer = TertiaryContainerDark,
        onTertiaryContainer = OnTertiaryContainerDark,
        background = BackgroundDark,
        onBackground = OnBackgroundDark,
        surface = SurfaceDark,
        onSurface = OnSurfaceDark,
        surfaceVariant = SurfaceVariantDark,
        onSurfaceVariant = OnSurfaceVariantDark,
        outline = OutlineDark
    )
}

@Composable
fun StudyFlowTheme(
    themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    colorAccent: ColorAccent = ColorAccent.DYNAMIC,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        AppThemeMode.SYSTEM -> isSystemInDarkTheme()
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
    }

    val context = LocalContext.current

    val colorScheme = when {
        colorAccent == ColorAccent.DYNAMIC && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        colorAccent == ColorAccent.BLUE -> {
            if (darkTheme) buildDarkScheme(BluePrimaryDark) else buildLightScheme(BluePrimaryLight)
        }
        colorAccent == ColorAccent.GREEN -> {
            if (darkTheme) buildDarkScheme(GreenPrimaryDark) else buildLightScheme(GreenPrimaryLight)
        }
        colorAccent == ColorAccent.PURPLE -> {
            if (darkTheme) buildDarkScheme(PurplePrimaryDark) else buildLightScheme(PurplePrimaryLight)
        }
        colorAccent == ColorAccent.AMBER -> {
            if (darkTheme) buildDarkScheme(AmberPrimaryDark) else buildLightScheme(AmberPrimaryLight)
        }
        colorAccent == ColorAccent.ROSE -> {
            if (darkTheme) buildDarkScheme(RosePrimaryDark) else buildLightScheme(RosePrimaryLight)
        }
        else -> {
            if (darkTheme) buildDarkScheme(PrimaryDark) else buildLightScheme(PrimaryLight)
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
