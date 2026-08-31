package com.example.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.example.R
import com.example.StudyFlowApplication
import com.example.presentation.focus.FocusScreen
import com.example.presentation.focus.FocusViewModel
import com.example.presentation.more.MoreScreen
import com.example.presentation.news.NewsScreen
import com.example.presentation.news.NewsViewModel
import com.example.presentation.onboarding.OnboardingScreen
import com.example.presentation.onboarding.OnboardingViewModel
import com.example.presentation.reminders.AddEditReminderScreen
import com.example.presentation.reminders.AddEditReminderViewModel
import com.example.presentation.reminders.RemindersScreen
import com.example.presentation.reminders.RemindersViewModel
import com.example.presentation.schedule.ScheduleScreen
import com.example.presentation.schedule.ScheduleViewModel
import com.example.presentation.settings.SettingsScreen
import com.example.presentation.settings.SettingsViewModel
import com.example.presentation.subjects.SubjectsScreen
import com.example.presentation.subjects.SubjectsViewModel
import com.example.presentation.tasks.AddEditTaskScreen
import com.example.presentation.tasks.AddEditTaskViewModel
import com.example.presentation.tasks.TaskDetailsScreen
import com.example.presentation.tasks.TaskDetailsViewModel
import com.example.presentation.tasks.TasksScreen
import com.example.presentation.tasks.TasksViewModel
import com.example.presentation.today.TodayScreen
import com.example.presentation.today.TodayViewModel
import com.example.presentation.weeklyreview.WeeklyReviewScreen
import com.example.presentation.weeklyreview.WeeklyReviewViewModel

sealed class Screen(val route: String, val titleRes: Int, val icon: ImageVector) {
    data object Today : Screen("today", R.string.nav_today, Icons.Default.CalendarToday)
    data object Tasks : Screen("tasks", R.string.nav_tasks, Icons.Default.Checklist)
    data object Focus : Screen("focus_tab", R.string.focus_timer_title, Icons.Default.Timer)
    data object Schedule : Screen("schedule", R.string.nav_schedule, Icons.Default.Schedule)
    data object More : Screen("more", R.string.nav_more, Icons.Default.MoreHoriz)

    companion object {
        val bottomNavItems = listOf(Today, Tasks, Focus, Schedule, More)
    }
}

@Composable
fun StudyFlowApp(
    onboardingCompleted: Boolean,
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current.applicationContext as StudyFlowApplication
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isTopLevelDestination = Screen.bottomNavItems.any { it.route == currentRoute }

    val startDestination = if (onboardingCompleted) Screen.Today.route else "onboarding"

    Scaffold(
        bottomBar = {
            if (isTopLevelDestination) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    Screen.bottomNavItems.forEach { screen ->
                        val selected = currentRoute == screen.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (currentRoute != screen.route) {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = { Icon(imageVector = screen.icon, contentDescription = stringResource(screen.titleRes)) },
                            label = { Text(stringResource(screen.titleRes)) },
                            modifier = Modifier.testTag("nav_item_${screen.route}"),
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (currentRoute == Screen.Today.route || currentRoute == Screen.Tasks.route) {
                FloatingActionButton(
                    onClick = { navController.navigate("add_edit_task?taskId=-1") },
                    shape = RoundedCornerShape(16.dp),
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.testTag("global_add_task_fab")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.action_add_task)
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            // 0. Onboarding
            composable(route = "onboarding") {
                val viewModel: OnboardingViewModel = viewModel(
                    factory = OnboardingViewModel.Factory(
                        context.userProfileRepository,
                        context.subjectRepository,
                        context.userPreferencesRepository
                    )
                )
                OnboardingScreen(
                    viewModel = viewModel,
                    onComplete = { openAddTask ->
                        navController.navigate(Screen.Today.route) {
                            popUpTo("onboarding") { inclusive = true }
                        }
                        if (openAddTask) {
                            navController.navigate("add_edit_task?taskId=-1")
                        }
                    }
                )
            }

            // 1. Today Screen
            composable(
                route = Screen.Today.route,
                deepLinks = listOf(navDeepLink { uriPattern = "studyflow://today" })
            ) {
                val viewModel: TodayViewModel = viewModel(
                    factory = TodayViewModel.Factory(
                        context.taskRepository,
                        context.subjectRepository,
                        context.userProfileRepository,
                        context.studySessionRepository,
                        context.taskReminderScheduler
                    )
                )
                TodayScreen(
                    viewModel = viewModel,
                    onNavigateToTaskDetails = { taskId ->
                        navController.navigate("task_details/$taskId")
                    },
                    onNavigateToAddTask = {
                        navController.navigate("add_edit_task?taskId=-1")
                    },
                    onStartFocus = { taskId ->
                        navController.navigate("focus/$taskId")
                    },
                    onNavigateToSchedule = {
                        navController.navigate(Screen.Schedule.route)
                    },
                    onNavigateToReminders = {
                        navController.navigate("reminders")
                    }
                )
            }

            // 2. Tasks Screen
            composable(
                route = Screen.Tasks.route,
                deepLinks = listOf(navDeepLink { uriPattern = "studyflow://tasks" })
            ) {
                val viewModel: TasksViewModel = viewModel(
                    factory = TasksViewModel.Factory(
                        context.taskRepository,
                        context.subjectRepository,
                        context.taskReminderScheduler
                    )
                )
                TasksScreen(
                    viewModel = viewModel,
                    onNavigateToTaskDetails = { taskId ->
                        navController.navigate("task_details/$taskId")
                    },
                    onNavigateToAddTask = {
                        navController.navigate("add_edit_task?taskId=-1")
                    }
                )
            }

            // 3. Focus (as a Bottom Nav Tab)
            composable(route = Screen.Focus.route) {
                val viewModel: FocusViewModel = viewModel(
                    factory = FocusViewModel.Factory(
                        0L,
                        context.taskRepository,
                        context.studySessionRepository,
                        context.userPreferencesRepository
                    )
                )
                FocusScreen(
                    viewModel = viewModel,
                    onNavigateBack = {
                        navController.navigate(Screen.Today.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

            // 4. Schedule Screen
            composable(
                route = Screen.Schedule.route,
                deepLinks = listOf(navDeepLink { uriPattern = "studyflow://schedule" })
            ) {
                val viewModel: ScheduleViewModel = viewModel(
                    factory = ScheduleViewModel.Factory(
                        context.taskRepository,
                        context.subjectRepository
                    )
                )
                ScheduleScreen(
                    viewModel = viewModel,
                    onNavigateToTaskDetails = { taskId ->
                        navController.navigate("task_details/$taskId")
                    },
                    onNavigateToAddTask = {
                        navController.navigate("add_edit_task?taskId=-1")
                    },
                    onStartFocus = { taskId ->
                        navController.navigate("focus/$taskId")
                    }
                )
            }

            // 5. More Screen
            composable(route = Screen.More.route) {
                MoreScreen(
                    onNavigateToSubjects = { navController.navigate("subjects") },
                    onNavigateToWeeklyReview = { navController.navigate("weekly_review") },
                    onNavigateToNews = { navController.navigate("news") },
                    onNavigateToSettings = { navController.navigate("settings") }
                )
            }

            // Add/Edit Task
            composable(
                route = "add_edit_task?taskId={taskId}",
                arguments = listOf(navArgument("taskId") {
                    type = NavType.LongType
                    defaultValue = -1L
                })
            ) { backStackEntry ->
                val rawTaskId = backStackEntry.arguments?.getLong("taskId") ?: -1L
                val taskId = if (rawTaskId > 0) rawTaskId else null

                val viewModel: AddEditTaskViewModel = viewModel(
                    factory = AddEditTaskViewModel.Factory(
                        taskId,
                        context.taskRepository,
                        context.subjectRepository,
                        context.userPreferencesRepository,
                        context.taskReminderScheduler
                    )
                )
                AddEditTaskScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Task Details
            composable(
                route = "task_details/{taskId}",
                arguments = listOf(navArgument("taskId") { type = NavType.LongType }),
                deepLinks = listOf(navDeepLink { uriPattern = "studyflow://task_details/{taskId}" })
            ) { backStackEntry ->
                val taskId = backStackEntry.arguments?.getLong("taskId") ?: 0L
                val viewModel: TaskDetailsViewModel = viewModel(
                    factory = TaskDetailsViewModel.Factory(
                        taskId,
                        context.taskRepository,
                        context.subjectRepository,
                        context.taskReminderScheduler
                    )
                )
                TaskDetailsScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToEditTask = { id ->
                        navController.navigate("add_edit_task?taskId=$id")
                    },
                    onStartFocus = { id ->
                        navController.navigate("focus/$id")
                    }
                )
            }

            // Focus Screen (Navigated with taskId)
            composable(
                route = "focus/{taskId}",
                arguments = listOf(navArgument("taskId") { type = NavType.LongType })
            ) { backStackEntry ->
                val taskId = backStackEntry.arguments?.getLong("taskId") ?: 0L
                val viewModel: FocusViewModel = viewModel(
                    factory = FocusViewModel.Factory(
                        taskId,
                        context.taskRepository,
                        context.studySessionRepository,
                        context.userPreferencesRepository
                    )
                )
                FocusScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Reminders Screen
            composable(
                route = "reminders",
                deepLinks = listOf(navDeepLink { uriPattern = "studyflow://reminders" })
            ) {
                val viewModel: RemindersViewModel = viewModel(
                    factory = RemindersViewModel.Factory(
                        context.recurringReminderRepository,
                        context.recurringReminderScheduler
                    )
                )
                RemindersScreen(
                    viewModel = viewModel,
                    onNavigateToAddReminder = {
                        navController.navigate("add_edit_reminder?reminderId=-1")
                    },
                    onNavigateToEditReminder = { reminderId ->
                        navController.navigate("add_edit_reminder?reminderId=$reminderId")
                    }
                )
            }

            // Add/Edit Reminder
            composable(
                route = "add_edit_reminder?reminderId={reminderId}",
                arguments = listOf(navArgument("reminderId") {
                    type = NavType.LongType
                    defaultValue = -1L
                })
            ) { backStackEntry ->
                val rawId = backStackEntry.arguments?.getLong("reminderId") ?: -1L
                val reminderId = if (rawId > 0) rawId else null

                val viewModel: AddEditReminderViewModel = viewModel(
                    factory = AddEditReminderViewModel.Factory(
                        reminderId,
                        context.recurringReminderRepository,
                        context.recurringReminderScheduler
                    )
                )
                AddEditReminderScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Subjects
            composable(route = "subjects") {
                val viewModel: SubjectsViewModel = viewModel(
                    factory = SubjectsViewModel.Factory(context.subjectRepository)
                )
                SubjectsScreen(viewModel = viewModel)
            }

            // Weekly Review
            composable(route = "weekly_review") {
                val viewModel: WeeklyReviewViewModel = viewModel(
                    factory = WeeklyReviewViewModel.Factory(
                        context.taskRepository,
                        context.studySessionRepository
                    )
                )
                WeeklyReviewScreen(viewModel = viewModel)
            }

            // News
            composable(route = "news") {
                val viewModel: NewsViewModel = viewModel(
                    factory = NewsViewModel.Factory(context.newsRepository)
                )
                NewsScreen(viewModel = viewModel)
            }

            // Settings
            composable(route = "settings") {
                val viewModel: SettingsViewModel = viewModel(
                    factory = SettingsViewModel.Factory(
                        context.userPreferencesRepository,
                        context.userProfileRepository
                    )
                )
                SettingsScreen(viewModel = viewModel)
            }
        }
    }
}
