package co.bolo.app.ui.nav

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import co.bolo.app.ui.dashboard.DashboardScreen
import co.bolo.app.ui.enrollment.EnrollmentScreen
import co.bolo.app.ui.home.HomeScreen
import co.bolo.app.ui.session.SessionScreen
import co.bolo.app.ui.summary.SummaryScreen
import co.bolo.app.ui.theme.BoloPalette

object Routes {
    const val HOME = "home"
    const val ENROLL = "enroll/{cohortId}/{studentId}"
    const val SESSION = "session/{cohortId}"
    const val SUMMARY = "summary/{sessionId}"
    const val DASHBOARD = "dashboard/{studentId}"

    fun enroll(cohortId: String, studentId: String) = "enroll/$cohortId/$studentId"
    fun session(cohortId: String) = "session/$cohortId"
    fun summary(sessionId: String) = "summary/$sessionId"
    fun dashboard(studentId: String) = "dashboard/$studentId"
}

@Composable
fun BoloNavHost() {
    val nav = rememberNavController()

    Scaffold(
        containerColor = BoloPalette.Bg,
        contentColor = BoloPalette.Ink
    ) { inner ->
        NavHost(
            navController = nav,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(inner)
        ) {
            composable(Routes.HOME) {
                HomeScreen(
                    onStartSession = { cohortId -> nav.navigate(Routes.session(cohortId)) },
                    onOpenDashboard = { studentId -> nav.navigate(Routes.dashboard(studentId)) },
                    onEnroll = { cohortId, studentId -> nav.navigate(Routes.enroll(cohortId, studentId)) },
                    onOpenSession = { sessionId -> nav.navigate(Routes.summary(sessionId)) }
                )
            }
            composable(
                route = Routes.ENROLL,
                arguments = listOf(
                    navArgument("cohortId") { type = NavType.StringType },
                    navArgument("studentId") { type = NavType.StringType }
                )
            ) { entry ->
                EnrollmentScreen(
                    cohortId = entry.arguments?.getString("cohortId").orEmpty(),
                    studentId = entry.arguments?.getString("studentId").orEmpty(),
                    onDone = { nav.popBackStack() },
                    onCancel = { nav.popBackStack() }
                )
            }
            composable(
                route = Routes.SESSION,
                arguments = listOf(navArgument("cohortId") { type = NavType.StringType })
            ) { entry ->
                SessionScreen(
                    cohortId = entry.arguments?.getString("cohortId").orEmpty(),
                    onEnd = { sessionId ->
                        nav.navigate(Routes.summary(sessionId)) {
                            popUpTo(Routes.HOME)
                        }
                    },
                    onCancel = { nav.popBackStack() }
                )
            }
            composable(
                route = Routes.SUMMARY,
                arguments = listOf(navArgument("sessionId") { type = NavType.StringType })
            ) { entry ->
                SummaryScreen(
                    sessionId = entry.arguments?.getString("sessionId").orEmpty(),
                    onDone = { nav.popBackStack(Routes.HOME, inclusive = false) },
                    onOpenStudent = { studentId -> nav.navigate(Routes.dashboard(studentId)) }
                )
            }
            composable(
                route = Routes.DASHBOARD,
                arguments = listOf(navArgument("studentId") { type = NavType.StringType })
            ) { entry ->
                DashboardScreen(
                    studentId = entry.arguments?.getString("studentId").orEmpty(),
                    onBack = { nav.popBackStack() }
                )
            }
        }
    }
}
