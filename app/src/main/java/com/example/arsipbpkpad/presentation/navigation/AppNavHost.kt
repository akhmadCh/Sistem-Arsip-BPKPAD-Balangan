package com.example.arsipbpkpad.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.arsipbpkpad.presentation.analytics.AnalyticsScreen
import com.example.arsipbpkpad.presentation.archive.add.manual.RapidInputScreen
import com.example.arsipbpkpad.presentation.archive.add.manual.RapidInputUiEvent
import com.example.arsipbpkpad.presentation.archive.add.manual.RapidInputViewModel
import com.example.arsipbpkpad.presentation.archive.add.manual.StagingBoxListScreen
import com.example.arsipbpkpad.presentation.archive.detail.ArchiveDetailScreen
import com.example.arsipbpkpad.presentation.archive.list.ArchiveListScreen
import com.example.arsipbpkpad.presentation.home.screen.HomeScreen
import com.example.arsipbpkpad.presentation.scan.ScanScreen
import com.example.arsipbpkpad.presentation.components.BottomNavItem

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Home.route
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToArchiveList = {
                    navController.navigate("archive_flow")
                },
                onNavigateToStagingBoxList = {
                    navController.navigate(Screen.StagingBoxList.route)
                },
                onNavigateToDetail = { archiveId ->
                    navController.navigate(Screen.ArchiveDetail.createRoute(archiveId))
                },
                onNavigateToRapidInput = { sessionId ->
                    navController.navigate(Screen.RapidInput.createRoute(sessionId))
                },
                onNavigateToAnalytics = {
                    navController.navigate(Screen.Analytics.route)
                },
                onNavigateToScan = {
                    navController.navigate(Screen.Scan.route)
                }
            )
        }

        navigation(
            startDestination = Screen.ArchiveList.route,
            route = "archive_flow"
        ) {
            composable(Screen.ArchiveList.route) { entry ->
                val flowEntry = remember(entry) { navController.getBackStackEntry("archive_flow") }
                val rapidViewModel: RapidInputViewModel = hiltViewModel(flowEntry)

                ArchiveListScreen(
                    onNavigateToDetail = { archiveId ->
                        navController.navigate(Screen.ArchiveDetail.createRoute(archiveId))
                    },
                    onNavigateToRapidInput = {
                        val sessionId = rapidViewModel.uiState.value.currentSessionId ?: ""
                        navController.navigate(Screen.RapidInput.createRoute(sessionId))
                    },
                    onNavigateToScan = {
                        navController.navigate(Screen.Scan.route)
                    },
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToBottomNav = { item: BottomNavItem ->
                        when (item.route) {
                            "home" -> navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Home.route) { inclusive = true }
                            }
                            "archive" -> { /* Already here */ }
                            "add" -> navController.navigate(Screen.StagingBoxList.route)
                            "analytics" -> navController.navigate(Screen.Analytics.route)
                        }
                    }
                )
            }

            composable(Screen.StagingBoxList.route) { entry ->
                val flowEntry = remember(entry) { navController.getBackStackEntry("archive_flow") }
                val rapidViewModel: RapidInputViewModel = hiltViewModel(flowEntry)

                StagingBoxListScreen(
                    viewModel = rapidViewModel,
                    onNavigateToRapidInput = { sessionId ->
                        navController.navigate(Screen.RapidInput.createRoute(sessionId))
                    },
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToBottomNav = { item: BottomNavItem ->
                        when (item.route) {
                            "home" -> navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Home.route) { inclusive = true }
                            }
                            "archive" -> navController.navigate("archive_flow") {
                                popUpTo(Screen.Home.route)
                            }
                            "add" -> { /* Already here */ }
                            "analytics" -> navController.navigate(Screen.Analytics.route)
                        }
                    }
                )
            }
            
            composable(Screen.RapidInput.route) { entry ->
                val sessionId = entry.arguments?.getString("sessionId") ?: ""
                val flowEntry = remember(entry) { navController.getBackStackEntry("archive_flow") }
                val rapidViewModel: RapidInputViewModel = hiltViewModel(flowEntry)
                
                // Observe OCR results from multiple possible handles
                val ocrResult by entry.savedStateHandle.getStateFlow<com.example.arsipbpkpad.domain.model.ParsedMetadata?>("ocr_result", null).collectAsStateWithLifecycle()
                val flowOcrResult by flowEntry.savedStateHandle.getStateFlow<com.example.arsipbpkpad.domain.model.ParsedMetadata?>("ocr_result", null).collectAsStateWithLifecycle()

                LaunchedEffect(ocrResult) {
                    ocrResult?.let {
                        android.util.Log.e("AppNavHost", "UI: OCR result detected on entry handle: $it")
                        rapidViewModel.onEvent(RapidInputUiEvent.OnOcrResultReceived(it))
                        entry.savedStateHandle["ocr_result"] = null
                    }
                }
                
                LaunchedEffect(flowOcrResult) {
                    flowOcrResult?.let {
                        android.util.Log.e("AppNavHost", "UI: OCR result detected on flow handle: $it")
                        rapidViewModel.onEvent(RapidInputUiEvent.OnOcrResultReceived(it))
                        flowEntry.savedStateHandle["ocr_result"] = null
                    }
                }

                RapidInputScreen(
                    sessionId = sessionId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToScan = { navController.navigate(Screen.Scan.route) },
                    viewModel = rapidViewModel,
                    onNavigateToBottomNav = { item: BottomNavItem ->
                        when (item.route) {
                            "home" -> navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Home.route) { inclusive = true }
                            }
                            "archive" -> navController.navigate("archive_flow") {
                                popUpTo(Screen.Home.route)
                            }
                            "add" -> navController.navigate(Screen.StagingBoxList.route)
                            "analytics" -> navController.navigate(Screen.Analytics.route)
                        }
                    }
                )
            }
        }

        composable(Screen.ArchiveDetail.route) { backStackEntry ->
            val archiveId = backStackEntry.arguments?.getString("archiveId") ?: ""
            ArchiveDetailScreen(
                archiveId = archiveId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Scan.route) {
            android.util.Log.e("AppNavHost", "NAV: Navigating to Scan Screen")
            ScanScreen(
                onNavigateBack = { navController.popBackStack() },
                onResultDispatched = { metadata ->
                    android.util.Log.e("AppNavHost", "NAV: Result received from Scan: $metadata")
                    
                    // Set on BOTH possible locations to be 100% sure
                    navController.previousBackStackEntry?.savedStateHandle?.set("ocr_result", metadata)
                    
                    try {
                        val flowEntry = navController.getBackStackEntry("archive_flow")
                        flowEntry.savedStateHandle.set("ocr_result", metadata)
                        android.util.Log.e("AppNavHost", "NAV: Result set on archive_flow handle")
                    } catch (e: Exception) {
                        android.util.Log.e("AppNavHost", "NAV: archive_flow not found")
                    }

                    // ALSO try setting it on the specific RapidInput destination entry if visible
                    try {
                        navController.getBackStackEntry(Screen.RapidInput.route)?.savedStateHandle?.set("ocr_result", metadata)
                    } catch (e: Exception) {}

                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Analytics.route) {
            AnalyticsScreen(
                onNavigateToBottomNav = { item ->
                    when (item.route) {
                        "home" -> navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                        "archive" -> navController.navigate("archive_flow") {
                            popUpTo(Screen.Home.route)
                        }
                        "add" -> navController.navigate(Screen.StagingBoxList.route)
                        "analytics" -> { /* Already here */ }
                    }
                }
            )
        }
    }
}
