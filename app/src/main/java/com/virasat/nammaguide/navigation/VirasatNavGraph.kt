package com.virasat.nammaguide.navigation

import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.virasat.nammaguide.VirasatNammaGuideApplication
import com.virasat.nammaguide.ui.audio.AudioGuidePlayer
import com.virasat.nammaguide.ui.scanner.CameraQrScanner
import com.virasat.nammaguide.ui.screens.HomeScreen
import com.virasat.nammaguide.ui.screens.PlaceDetailScreen
import com.virasat.nammaguide.ui.screens.QrScannerScreen
import com.virasat.nammaguide.ui.screens.SplashScreen
import com.virasat.nammaguide.ui.screens.TravelHistoryScreen
import com.virasat.nammaguide.viewmodel.GuideViewModel
import com.virasat.nammaguide.viewmodel.GuideViewModelFactory

@Composable
fun VirasatNavGraph(
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val app = context.applicationContext as VirasatNammaGuideApplication
    val viewModel: GuideViewModel = viewModel(
        factory = GuideViewModelFactory(app.repository)
    )
    val uiState by viewModel.uiState.collectAsState()
    val audioPlayer = remember {
        AudioGuidePlayer(
            context = context,
            onPlaybackFinished = viewModel::stopAudio
        )
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE || event == Lifecycle.Event.ON_STOP) {
                audioPlayer.stop()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            audioPlayer.release()
        }
    }

    NavHost(
        navController = navController,
        startDestination = VirasatRoutes.Splash
    ) {
        composable(VirasatRoutes.Splash) {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate(VirasatRoutes.Home) {
                        popUpTo(VirasatRoutes.Splash) { inclusive = true }
                    }
                }
            )
        }

        composable(VirasatRoutes.Home) {
            LaunchedEffect(Unit) {
                viewModel.clearSelectedPlace()
            }
            HomeScreen(
                places = uiState.places,
                onPlaceClick = { placeId ->
                    navController.navigate(VirasatRoutes.placeDetail(placeId))
                },
                onQrClick = {
                    navController.navigate(VirasatRoutes.QrScanner)
                },
                onHistoryClick = {
                    navController.navigate(VirasatRoutes.TravelHistory)
                }
            )
        }

        composable(VirasatRoutes.QrScanner) {
            val lifecycleOwner = LocalLifecycleOwner.current
            var scannedQrResult by remember { mutableStateOf<String?>(null) }
            val scanner = remember {
                CameraQrScanner(
                    context = context,
                    lifecycleOwner = lifecycleOwner,
                    onQrScanned = { rawValue, placeId ->
                        scannedQrResult = rawValue
                        if (placeId != null) {
                            viewModel.markQrScanResult(placeId)
                            navController.navigate(VirasatRoutes.placeDetail(placeId, fromQr = true)) {
                                popUpTo(VirasatRoutes.QrScanner) { inclusive = true }
                            }
                        }
                    }
                )
            }

            DisposableEffect(scanner) {
                onDispose {
                    scanner.close()
                }
            }

            QrScannerScreen(
                onBackClick = { navController.popBackStack() },
                onScannerReady = { previewView: PreviewView ->
                    scanner.reset()
                    scanner.bind(previewView)
                },
                scannedResult = scannedQrResult
            )
        }

        composable(
            route = VirasatRoutes.PlaceDetail,
            arguments = listOf(
                navArgument("placeId") { type = NavType.StringType },
                navArgument("fromQr") { type = NavType.BoolType }
            )
        ) { backStackEntry ->
            val placeId = backStackEntry.arguments?.getString("placeId").orEmpty()
            val fromQr = backStackEntry.arguments?.getBoolean("fromQr") ?: false

            LaunchedEffect(placeId, fromQr) {
                viewModel.selectPlace(placeId, revealHiddenFact = fromQr)
            }

            val place = uiState.selectedPlace ?: uiState.places.firstOrNull { it.id == placeId }
            if (place != null) {
                PlaceDetailScreen(
                    place = place,
                    isVisited = uiState.visitedPlaceIds.contains(place.id),
                    showHiddenFact = uiState.showHiddenFact,
                    isAudioPlaying = uiState.isAudioPlaying,
                    onBackClick = {
                        audioPlayer.stop()
                        viewModel.clearSelectedPlace()
                        navController.popBackStack()
                    },
                    onCheckInClick = { viewModel.checkInPlace(place.id) },
                    onAudioToggle = { narration ->
                        if (audioPlayer.toggle(uiState.isAudioPlaying, narration)) {
                            viewModel.toggleAudio()
                        }
                    }
                )
            } else {
                LaunchedEffect(Unit) {
                    navController.popBackStack()
                }
            }
        }

        composable(VirasatRoutes.TravelHistory) {
            TravelHistoryScreen(
                visitedPlaces = uiState.visitedPlaces,
                onBackClick = { navController.popBackStack() },
                onPlaceClick = { placeId ->
                    navController.navigate(VirasatRoutes.placeDetail(placeId))
                }
            )
        }
    }
}
