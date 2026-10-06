package com.example.infobahaya.navigation

sealed class AppRoute(val route: String) {
    data object Splash : AppRoute("splash")
    data object Onboarding : AppRoute("onboarding")
    data object Login : AppRoute("login")
    data object Register : AppRoute("register")
    data object ForgotPassword : AppRoute("forgot_password")
    
    // Main Container with Bottom Navigation
    data object Main : AppRoute("main")
    
    // Direct or Nested Flows
    data object CreateReport : AppRoute("create_report")
    data object ReportDetail : AppRoute("report_detail/{reportId}") {
        fun createRoute(reportId: String): String = "report_detail/$reportId"
    }
    data object ModeratorDashboard : AppRoute("moderator_dashboard")
    data object EditProfile : AppRoute("edit_profile")
    data object Settings : AppRoute("settings")
}

enum class BottomNavItem(
    val route: String,
    val title: String
) {
    HOME("tab_home", "Peta & Home"),
    TRACKING("tab_tracking", "Riwayat"),
    EMERGENCY("tab_emergency", "Darurat"),
    NOTIFICATIONS("tab_notifications", "Notifikasi"),
    PROFILE("tab_profile", "Profil")
}
