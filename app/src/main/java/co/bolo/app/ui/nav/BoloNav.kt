package co.bolo.app.ui.nav

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import co.bolo.app.data.seed.Seed
import co.bolo.app.ui.attendance.AddStudentScreen
import co.bolo.app.ui.attendance.AttendanceScreen
import co.bolo.app.ui.cohort.NewCohortScreen
import co.bolo.app.ui.dashboard.DashboardScreen
import co.bolo.app.ui.enrollment.EnrollmentScreen
import co.bolo.app.ui.firstrun.CohortSetupScreen
import co.bolo.app.ui.firstrun.ConsentScreen
import co.bolo.app.ui.firstrun.MicPermScreen
import co.bolo.app.ui.firstrun.PairScreen
import co.bolo.app.ui.firstrun.SplashScreen
import co.bolo.app.ui.history.HistoryScreen
import co.bolo.app.ui.history.SessionDetailScreen
import co.bolo.app.ui.home.HomeScreen
import co.bolo.app.ui.session.SessionScreen
import co.bolo.app.ui.settings.ForgetVoiceScreen
import co.bolo.app.ui.settings.SettingsScreen
import co.bolo.app.ui.settings.SyncScreen
import co.bolo.app.ui.summary.SummaryScreen
import co.bolo.app.ui.theme.BoloPalette
import co.bolo.app.util.Prefs

object Routes {
    const val SPLASH = "splash"
    const val PAIR = "pair"
    const val CONSENT = "consent"
    const val MIC_PERM = "mic_perm"
    const val COHORT_SETUP = "cohort_setup"

    const val HOME = "home"
    const val NEW_COHORT = "cohort/new"
    const val ENROLL = "enroll/{cohortId}/{studentId}"
    // Attendance is now the session entry-point — replaces the
    // ad-hoc participant-count and name-edit screens.
    const val ATTENDANCE_FOR_SESSION = "attendance/session/{cohortId}"
    const val ATTENDANCE_FOR_ROSTER = "attendance/roster/{cohortId}"
    const val ADD_STUDENT = "add_student/{cohortId}"

    const val SESSION = "session/{cohortId}"
    const val SUMMARY = "summary/{sessionId}"
    const val DASHBOARD = "dashboard/{studentId}"

    const val HISTORY = "history"
    const val SESSION_DETAIL = "session_detail/{id}"
    const val SETTINGS = "settings"
    const val FORGET_VOICE = "forget_voice"
    const val SYNC = "sync"

    fun enroll(cohortId: String, studentId: String) = "enroll/$cohortId/$studentId"
    fun attendanceForSession(cohortId: String) = "attendance/session/$cohortId"
    fun attendanceForRoster(cohortId: String) = "attendance/roster/$cohortId"
    fun addStudent(cohortId: String) = "add_student/$cohortId"
    fun session(cohortId: String) = "session/$cohortId"
    fun summary(sessionId: String) = "summary/$sessionId"
    fun dashboard(studentId: String) = "dashboard/$studentId"
    fun sessionDetail(id: String) = "session_detail/$id"
}

@Composable
fun BoloNavHost() {
    val nav = rememberNavController()
    val context = LocalContext.current

    Scaffold(
        containerColor = BoloPalette.Bg,
        contentColor = BoloPalette.Ink
    ) { inner ->
        NavHost(
            navController = nav,
            startDestination = Routes.SPLASH,
            modifier = Modifier.padding(inner)
        ) {
            composable(Routes.SPLASH) {
                SplashScreen(onContinue = {
                    val paired = Prefs.isPaired(context)
                    val dest = if (paired) Routes.HOME else Routes.PAIR
                    nav.navigate(dest) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                })
            }
            composable(Routes.PAIR) {
                PairScreen(onContinue = { nav.navigate(Routes.CONSENT) })
            }
            composable(Routes.CONSENT) {
                ConsentScreen(onContinue = { nav.navigate(Routes.MIC_PERM) })
            }
            composable(Routes.MIC_PERM) {
                MicPermScreen(onAllow = { nav.navigate(Routes.COHORT_SETUP) })
            }
            composable(Routes.COHORT_SETUP) {
                CohortSetupScreen(onReady = {
                    Prefs.setPaired(context, true)
                    nav.navigate(Routes.HOME) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                })
            }

            composable(Routes.HOME) {
                HomeScreen(
                    onStartSession = { cohortId -> nav.navigate(Routes.attendanceForSession(cohortId)) },
                    onOpenDashboard = { studentId -> nav.navigate(Routes.dashboard(studentId)) },
                    onEnroll = { cohortId, studentId -> nav.navigate(Routes.enroll(cohortId, studentId)) },
                    onOpenSession = { sessionId -> nav.navigate(Routes.summary(sessionId)) },
                    onOpenHistory = { nav.navigate(Routes.HISTORY) },
                    onOpenSettings = { nav.navigate(Routes.SETTINGS) },
                    onNewCohort = { nav.navigate(Routes.NEW_COHORT) }
                )
            }

            composable(Routes.NEW_COHORT) {
                NewCohortScreen(
                    onBack = { nav.popBackStack() },
                    onCreated = { cohortId ->
                        // Land facilitator on the attendance screen for the
                        // new cohort so they can start adding students.
                        nav.navigate(Routes.attendanceForRoster(cohortId)) {
                            popUpTo(Routes.HOME) { inclusive = false }
                        }
                    }
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

            // Attendance · session-start mode.
            // Selecting "Start" jumps into the live session with the
            // tapped students as the present roster.
            composable(
                route = Routes.ATTENDANCE_FOR_SESSION,
                arguments = listOf(navArgument("cohortId") { type = NavType.StringType })
            ) { entry ->
                val cohortId = entry.arguments?.getString("cohortId").orEmpty()
                AttendanceScreen(
                    onBack = { nav.popBackStack() },
                    onAddStudent = { nav.navigate(Routes.addStudent(cohortId)) },
                    onStart = { presentIds ->
                        val route = if (presentIds.isEmpty()) Routes.session(cohortId)
                        else Routes.session(cohortId) + "?present=" + presentIds.joinToString(",")
                        nav.navigate(route)
                    }
                )
            }

            // Attendance · roster-management mode.
            // Same screen — just no "start session" exit.
            composable(
                route = Routes.ATTENDANCE_FOR_ROSTER,
                arguments = listOf(navArgument("cohortId") { type = NavType.StringType })
            ) { entry ->
                val cohortId = entry.arguments?.getString("cohortId").orEmpty()
                AttendanceScreen(
                    onBack = { nav.popBackStack() },
                    onAddStudent = { nav.navigate(Routes.addStudent(cohortId)) },
                    onStart = { _ -> nav.popBackStack() }
                )
            }

            composable(
                route = Routes.ADD_STUDENT,
                arguments = listOf(navArgument("cohortId") { type = NavType.StringType })
            ) { entry ->
                val cohortId = entry.arguments?.getString("cohortId").orEmpty()
                AddStudentScreen(
                    cohortId = cohortId,
                    onBack = { nav.popBackStack() },
                    onContinue = { _ -> nav.popBackStack() }
                )
            }

            composable(
                // Optional ?present= comma-joined student ids selected on Attendance.
                route = Routes.SESSION + "?present={present}",
                arguments = listOf(
                    navArgument("cohortId") { type = NavType.StringType },
                    navArgument("present") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { _ ->
                SessionScreen(
                    onEnd = { sessionId ->
                        nav.navigate(Routes.summary(sessionId)) {
                            popUpTo(Routes.HOME)
                        }
                    },
                    onCancel = { nav.popBackStack() }
                )
            }
            composable(
                route = Routes.SESSION,
                arguments = listOf(navArgument("cohortId") { type = NavType.StringType })
            ) { _ ->
                SessionScreen(
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

            composable(Routes.HISTORY) {
                HistoryScreen(
                    onBack = { nav.popBackStack() },
                    onOpenSession = { id -> nav.navigate(Routes.sessionDetail(id)) }
                )
            }
            composable(
                route = Routes.SESSION_DETAIL,
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) { entry ->
                SessionDetailScreen(
                    sessionId = entry.arguments?.getString("id").orEmpty(),
                    onBack = { nav.popBackStack() }
                )
            }
            composable(Routes.SETTINGS) {
                SettingsScreen(
                    onBack = { nav.popBackStack() },
                    onManageStudents = { nav.navigate(Routes.attendanceForRoster(Seed.DEFAULT_COHORT_ID)) },
                    onOpenSync = { nav.navigate(Routes.SYNC) },
                    onForgetVoice = { nav.navigate(Routes.FORGET_VOICE) }
                )
            }
            composable(Routes.FORGET_VOICE) {
                ForgetVoiceScreen(
                    onBack = { nav.popBackStack() },
                    onDone = { nav.popBackStack(Routes.HOME, inclusive = false) }
                )
            }
            composable(Routes.SYNC) {
                SyncScreen(onBack = { nav.popBackStack() })
            }
        }
    }
}
