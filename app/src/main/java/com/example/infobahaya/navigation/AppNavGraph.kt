package com.example.infobahaya.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.infobahaya.presentation.auth.ForgotPasswordScreen
import com.example.infobahaya.presentation.auth.LoginScreen
import com.example.infobahaya.presentation.auth.OnboardingScreen
import com.example.infobahaya.presentation.auth.RegisterScreen
import com.example.infobahaya.presentation.auth.SplashScreen
import com.example.infobahaya.presentation.main.MainContainerScreen
import com.example.infobahaya.presentation.moderator.ModeratorDashboardScreen
import com.example.infobahaya.presentation.profile.EditProfileScreen
import com.example.infobahaya.presentation.profile.SettingsScreen
import com.example.infobahaya.presentation.report.CreateReportScreen
import com.example.infobahaya.presentation.tracking.ReportDetailScreen

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = AppRoute.Splash.route
    ) {
        // Splash Screen
        composable(AppRoute.Splash.route) {
            SplashScreen(
                onNavigate = { destinationRoute ->
                    navController.navigate(destinationRoute) {
                        popUpTo(AppRoute.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // Onboarding Screen
        composable(AppRoute.Onboarding.route) {
            OnboardingScreen(
                onFinishOnboarding = {
                    navController.navigate(AppRoute.Login.route) {
                        popUpTo(AppRoute.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        // Login Screen
        composable(AppRoute.Login.route) {
            LoginScreen(
                onNavigateToHome = {
                    navController.navigate(AppRoute.Main.route) {
                        popUpTo(AppRoute.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(AppRoute.Register.route)
                },
                onNavigateToForgotPassword = {
                    navController.navigate(AppRoute.ForgotPassword.route)
                }
            )
        }

        // Register Screen
        composable(AppRoute.Register.route) {
            RegisterScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHome = {
                    navController.navigate(AppRoute.Main.route) {
                        popUpTo(AppRoute.Login.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        // Forgot Password Screen
        composable(AppRoute.ForgotPassword.route) {
            ForgotPasswordScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Main App Container (Bottom Tabs: Home, Tracking, Emergency, Notif, Profile)
        composable(AppRoute.Main.route) {
            MainContainerScreen(
                onNavigateToReportDetail = { reportId ->
                    navController.navigate(AppRoute.ReportDetail.createRoute(reportId))
                },
                onNavigateToCreateReport = {
                    navController.navigate(AppRoute.CreateReport.route)
                },
                onNavigateToEditProfile = {
                    navController.navigate(AppRoute.EditProfile.route)
                },
                onNavigateToSettings = {
                    navController.navigate(AppRoute.Settings.route)
                },
                onNavigateToModeratorDashboard = {
                    navController.navigate(AppRoute.ModeratorDashboard.route)
                },
                onNavigateToLogin = {
                    navController.navigate(AppRoute.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Multi-Step Create Report Flow
        composable(AppRoute.CreateReport.route) {
            CreateReportScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHome = {
                    navController.navigate(AppRoute.Main.route) {
                        popUpTo(AppRoute.Main.route) { inclusive = true }
                    }
                },
                onNavigateToTracking = {
                    navController.navigate(AppRoute.Main.route) {
                        popUpTo(AppRoute.Main.route) { inclusive = true }
                    }
                }
            )
        }

        // Report Detail Screen
        composable(
            route = AppRoute.ReportDetail.route,
            arguments = listOf(navArgument("reportId") { type = NavType.StringType })
        ) { backStackEntry ->
            val reportId = backStackEntry.arguments?.getString("reportId").orEmpty()
            ReportDetailScreen(
                reportId = reportId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEmergency = {
                    navController.navigate(AppRoute.Main.route)
                }
            )
        }

        // Moderator Dashboard & Verification Queue
        composable(AppRoute.ModeratorDashboard.route) {
            ModeratorDashboardScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToReportDetail = { reportId ->
                    navController.navigate(AppRoute.ReportDetail.createRoute(reportId))
                }
            )
        }

        // Edit Profile Screen
        composable(AppRoute.EditProfile.route) {
            EditProfileScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Settings Screen
        composable(AppRoute.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
