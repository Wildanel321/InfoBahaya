package com.example.infobahaya.core.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlueLight,
    onPrimary = Color.White,
    primaryContainer = SafetyNavy800,
    onPrimaryContainer = PrimaryBlueLight,
    secondary = AccentCyan,
    onSecondary = Color.Black,
    secondaryContainer = SafetyNavy700,
    onSecondaryContainer = AccentCyan,
    tertiary = SeverityEmergency,
    onTertiary = Color.White,
    background = SurfaceDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceCardDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = SafetyNavy800,
    onSurfaceVariant = TextSecondaryDark,
    outline = SurfaceBorderDark,
    outlineVariant = SafetyNavy700,
    error = SeverityEmergency,
    onError = Color.White,
    errorContainer = SeverityEmergencyContainer,
    onErrorContainer = SeverityEmergencyText
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = Color.White,
    primaryContainer = PrimaryBlueContainer,
    onPrimaryContainer = PrimaryBlueDark,
    secondary = AccentCyanDark,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0F2FE),
    onSecondaryContainer = Color(0xFF0369A1),
    tertiary = SeverityEmergency,
    onTertiary = Color.White,
    background = SurfaceLight,
    onBackground = TextPrimaryLight,
    surface = SurfaceCardLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = TextSecondaryLight,
    outline = SurfaceBorderLight,
    outlineVariant = Color(0xFFE2E8F0),
    error = SeverityEmergency,
    onError = Color.White,
    errorContainer = SeverityEmergencyContainer,
    onErrorContainer = SeverityEmergencyText
)

object InfoBahayaThemeTokens {
    val spacing: InfoBahayaSpacing
        @Composable
        @ReadOnlyComposable
        get() = LocalSpacing.current

    val shapes: InfoBahayaShapes
        @Composable
        @ReadOnlyComposable
        get() = LocalCustomShapes.current
}

@Composable
fun InfoBahayaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.background.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(
        LocalSpacing provides InfoBahayaSpacing(),
        LocalCustomShapes provides InfoBahayaShapes()
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            shapes = MaterialShapes,
            content = content
        )
    }
}
