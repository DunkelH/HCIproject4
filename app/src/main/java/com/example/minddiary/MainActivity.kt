package com.example.minddiary

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.minddiary.ui.common.BottomNavBar
import com.example.minddiary.ui.diary.DiaryViewModel
import com.example.minddiary.ui.diary.DiaryScreen
import com.example.minddiary.ui.diary.DiaryWriteScreen
import com.example.minddiary.ui.diary.DiaryLoadingScreen
import com.example.minddiary.ui.diary.DiaryWriteStep2Screen
import com.example.minddiary.ui.diary.DiaryCompleteScreen
import com.example.minddiary.ui.diary.DiaryDetailScreen
import com.example.minddiary.ui.diary.DiaryEditScreen
import com.example.minddiary.ui.home.HomeScreen
import com.example.minddiary.ui.onboarding.OnboardingScreen
import com.example.minddiary.ui.report.EmotionReportScreen
import com.example.minddiary.ui.settings.SettingsScreen
import com.example.minddiary.ui.theme.MindDiaryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MindDiaryTheme {
                MindDiaryApp()
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MindDiaryApp() {
    MindDiaryTheme {
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        val screenOrder = listOf("home", "report", "diary")
        
        // DiaryViewModel을 공유하여 일기 작성 플로우에서 데이터 유지
        val diaryViewModel: DiaryViewModel = viewModel()

            Scaffold(
            bottomBar = {
                if (currentRoute != "onboarding" && currentRoute != "settings" && currentRoute != "diaryWrite" && currentRoute != "diaryLoading" && currentRoute != "diaryWriteStep2" && currentRoute != "diaryComplete" && !(currentRoute?.startsWith("diaryDetail") == true) && !(currentRoute?.startsWith("diaryEdit") == true)) {
                    BottomNavBar(
                        navController = navController,
                        currentRoute = currentRoute ?: "home"
                    )
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "onboarding",
                modifier = Modifier.padding(innerPadding),
                enterTransition = {
                    val initialRoute = initialState.destination.route
                    val targetRoute = targetState.destination.route
                    val initialIndex = screenOrder.indexOf(initialRoute)
                    val targetIndex = screenOrder.indexOf(targetRoute)

                    if (initialIndex != -1 && targetIndex != -1) {
                        if (targetIndex > initialIndex) {
                            slideInHorizontally(initialOffsetX = { it })
                        } else {
                            slideInHorizontally(initialOffsetX = { -it })
                        }
                    } else {
                        slideInHorizontally(initialOffsetX = { it })
                    }
                },
                exitTransition = {
                    val initialRoute = initialState.destination.route
                    val targetRoute = targetState.destination.route
                    val initialIndex = screenOrder.indexOf(initialRoute)
                    val targetIndex = screenOrder.indexOf(targetRoute)

                    if (initialIndex != -1 && targetIndex != -1) {
                        if (targetIndex > initialIndex) {
                            slideOutHorizontally(targetOffsetX = { -it })
                        } else {
                            slideOutHorizontally(targetOffsetX = { it })
                        }
                    } else {
                        slideOutHorizontally(targetOffsetX = { -it })
                    }
                },
                popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) },
                popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) }
            ) {
                composable("onboarding") {
                    OnboardingScreen(
                        onFinished = { name ->
                            navController.navigate("home") {
                                popUpTo("onboarding") { inclusive = true }
                            }
                        }
                    )
                }

                composable("home") {
                    HomeScreen(
                        navController = navController,
                        viewModel = diaryViewModel
                    )
                }

                composable("report") {
                    EmotionReportScreen(viewModel = diaryViewModel)
                }

                composable("diary") {
                    DiaryScreen(
                        onSettingsClick = { navController.navigate("settings") },
                        onDiaryClick = { diaryId ->
                            navController.navigate("diaryDetail/$diaryId")
                        },
                        viewModel = diaryViewModel
                    )
                }

                composable("settings") {
                    SettingsScreen(navController = navController)
                }

                composable("diaryWrite") {
                    DiaryWriteScreen(
                        navController = navController,
                        viewModel = diaryViewModel
                    )
                }

                composable("diaryLoading") {
                    DiaryLoadingScreen(navController = navController)
                }

                composable("diaryWriteStep2") {
                    DiaryWriteStep2Screen(
                        navController = navController,
                        viewModel = diaryViewModel
                    )
                }

                composable("diaryComplete") {
                    DiaryCompleteScreen(
                        navController = navController,
                        viewModel = diaryViewModel
                    )
                }

                composable(
                    route = "diaryDetail/{diaryId}",
                    enterTransition = {
                        slideInHorizontally(
                            initialOffsetX = { it },
                            animationSpec = androidx.compose.animation.core.tween(300)
                        )
                    },
                    exitTransition = {
                        slideOutHorizontally(
                            targetOffsetX = { -it },
                            animationSpec = androidx.compose.animation.core.tween(300)
                        )
                    },
                    popEnterTransition = {
                        slideInHorizontally(
                            initialOffsetX = { -it },
                            animationSpec = androidx.compose.animation.core.tween(300)
                        )
                    },
                    popExitTransition = {
                        slideOutHorizontally(
                            targetOffsetX = { it },
                            animationSpec = androidx.compose.animation.core.tween(300)
                        )
                    }
                ) { backStackEntry ->
                    val diaryId = backStackEntry.arguments?.getString("diaryId")?.toLongOrNull() ?: 0L
                    DiaryDetailScreen(
                        navController = navController,
                        diaryId = diaryId,
                        viewModel = diaryViewModel
                    )
                }

                composable(
                    route = "diaryEdit/{diaryId}",
                    enterTransition = {
                        slideInHorizontally(
                            initialOffsetX = { it },
                            animationSpec = androidx.compose.animation.core.tween(300)
                        )
                    },
                    exitTransition = {
                        slideOutHorizontally(
                            targetOffsetX = { -it },
                            animationSpec = androidx.compose.animation.core.tween(300)
                        )
                    },
                    popEnterTransition = {
                        slideInHorizontally(
                            initialOffsetX = { -it },
                            animationSpec = androidx.compose.animation.core.tween(300)
                        )
                    },
                    popExitTransition = {
                        slideOutHorizontally(
                            targetOffsetX = { it },
                            animationSpec = androidx.compose.animation.core.tween(300)
                        )
                    }
                ) { backStackEntry ->
                    val diaryId = backStackEntry.arguments?.getString("diaryId")?.toLongOrNull() ?: 0L
                    DiaryEditScreen(
                        navController = navController,
                        diaryId = diaryId,
                        viewModel = diaryViewModel
                    )
                }
            }
        }
    }
}
