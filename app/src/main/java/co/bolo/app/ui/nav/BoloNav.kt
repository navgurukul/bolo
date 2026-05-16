package co.bolo.app.ui.nav

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import co.bolo.app.ui.session.NameEditingScreen
import co.bolo.app.ui.session.ParticipantCountScreen
import co.bolo.app.ui.session.SessionScreen
import co.bolo.app.ui.session.SessionViewModel
import co.bolo.app.ui.settings.ForgetVoiceScreen
import co.bolo.app.ui.settings.SettingsScreen
import co.bolo.app.ui.settings.SyncScreen
import co.bolo.app.ui.summary.SummaryScreen
import co.bolo.app.ui.theme.BoloPalette
import co.bolo.app.util.Prefs
import androidx.hilt.navigation.compose.hiltViewModel

object Routes {
    const val SPLASH = "splash"
    const val PAIR = "pair"
    const val CONSENT = "consent"
    const val MIC_PERM = "mic_perm"
    const val COHORT_SETUP = "cohort_setup"

    const val HOME = "home"
    const val ENROLL = "enroll/{cohortId}/{studentId}"
    const val SETUP_COUNT = "setup/count/{cohortId}"
    const val SETUP_NAMES = "setup/names/{cohortId}"
    const val SESSION = "session/{cohortId}"
    const val SUMMARY = "summary/{sessionId}"
    const val DASHBOARD = "dashboard/{studentId}"

    const val ATTENDANCE = "attendance/{cohortId}"
    const val ADD_STUDENT = "add_student/{cohortId}"
    const val HISTORY = "history"
    const val SESSION_DETAIL = "session_detail/{id}"
    const val SETTINGS = "settings"
    const val FORGET_VOICE = "forget_voice"
    const val SYNC = "sync"

    fun enroll(cohortId: String, studentId: String) = "enroll/$cohortId/$studentId"
    fun setupCount(cohortId: String) = "setup/count/$cohortId"
    fun setupNames(cohortId: String) = "setup/names/$cohortId"
    fun session(cohortId: String) = "session/$cohortId"
    fun summary(sessionId: String) = "summary/$sessionId"
    fun dashboard(studentId: String) = "dashboard/$studentId"
    fun attendance(cohortId: String) = "attendance/$cohortId"
    fun addStudent(cohortId: String) = "add_student/$cohortId"
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
                    onStartSession = { cohortId -> nav.navigate(Routes.setupCount(cohortId)) },
                    onOpenDashboard = { studentId -> nav.navigate(Routes.dashboard(studentId)) },
                    onEnroll = { cohortId, studentId -> nav.navigate(Routes.enroll(cohortId, studentId)) },
                    onOpenSession = { sessionId -> nav.navigate(Routes.summary(sessionId)) },
                    onOpenHistory = { nav.navigate(Routes.HISTORY) },
                    onOpenSettings = { nav.navigate(Routes.SETTINGS) }
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
                route = Routes.SETUP_COUNT,
                arguments = listOf(navArgument("cohortId") { type = NavType.StringType })
            ) { entry ->
                val parentEntry = remember(entry) { nav.getBackStackEntry(Routes.SETUP_COUNT) }
                val vm: SessionViewModel = hiltViewModel(parentEntry)
                ParticipantCountScreen(
                    vm = vm,
                    onNext = { _ -> nav.navigate(Routes.setupNames(entry.arguments?.getString("cohortId")!!)) },
                    onBack = { nav.popBackStack() }
                )
            }
            composable(
                route = Routes.SETUP_NAMES,
                arguments = listOf(navArgument("cohortId") { type = NavType.StringType })
            ) { entry ->
                val parentEntry = remember(entry) { nav.getBackStackEntry(Routes.SETUP_COUNT) }
                val vm: SessionViewModel = hiltViewModel(parentEntry)
                NameEditingScreen(
                    vm = vm,
                    onStartSession = { nav.navigate(Routes.session(entry.arguments?.getString("cohortId")!!)) },
                    onBack = { nav.popBackStack() }
                )
            }
            composable(
                route = Routes.SESSION,
                arguments = listOf(navArgument("cohortId") { type = NavType.StringType })
            ) { _ ->
                val setupEntry = try { nav.getBackStackEntry(Routes.SETUP_COUNT) } catch (e: Exception) { null }
                val vm: SessionViewModel = if (setupEntry != null) hiltViewModel(setupEntry) else hiltViewModel()

                SessionScreen(
                    vm = vm,
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

            // ── new presentational screens ──
            composable(
                route = Routes.ATTENDANCE,
                arguments = listOf(navArgument("cohortId") { type = NavType.StringType })
            ) { _ ->
                AttendanceScreen(
                    onBack = { nav.popBackStack() },
                    onAddStudent = {
                        nav.navigate(Routes.addStudent(Seed.DEFAULT_COHORT_ID))
                    },
                    onStart = { _ -> nav.popBackStack() }
                )
            }
            composable(
                route = Routes.ADD_STUDENT,
                arguments = listOf(navArgument("cohortId") { type = NavType.StringType })
            ) { _ ->
                AddStudentScreen(
                    onBack = { nav.popBackStack() },
                    onContinue = { _ -> nav.popBackStack() }
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
                    onManageStudents = { nav.navigate(Routes.attendance(Seed.DEFAULT_COHORT_ID)) },
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
