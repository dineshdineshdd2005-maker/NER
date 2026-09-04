package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AppSidebarDrawerContent
import com.example.ui.components.TopNavBar
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NavyDark
import com.example.ui.theme.SlateBackground
import com.example.viewmodel.AppScreen
import com.example.viewmodel.AppViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                NerLogixApp()
            }
        }
    }
}

@Composable
fun NerLogixApp(
    viewModel: AppViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // If user is not logged in, display the Secure Login screen
    if (!uiState.isLoggedIn || uiState.currentUser == null) {
        LoginScreen(
            onLoginSuccess = { user ->
                viewModel.login(user)
            }
        )
        return
    }

    val currentUser = uiState.currentUser!!

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppSidebarDrawerContent(
                currentScreen = uiState.currentScreen,
                currentUser = currentUser,
                onSelectScreen = { screen ->
                    viewModel.navigateTo(screen)
                    coroutineScope.launch { drawerState.close() }
                },
                onLogout = {
                    viewModel.logout()
                    coroutineScope.launch { drawerState.close() }
                },
                onSwitchRole = { role ->
                    // Handled inside Admin panel
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopNavBar(
                    currentUser = currentUser,
                    currentScreen = uiState.currentScreen,
                    onMenuClick = {
                        coroutineScope.launch {
                            if (drawerState.isOpen) drawerState.close() else drawerState.open()
                        }
                    },
                    onSearchClick = {
                        viewModel.navigateTo(AppScreen.GIS_MAP)
                    },
                    onNotificationsClick = {
                        viewModel.navigateTo(AppScreen.ALERTS)
                    },
                    onProfileClick = {
                        viewModel.navigateTo(AppScreen.ADMIN_PANEL)
                    },
                    onStartDemoClick = {
                        viewModel.navigateTo(AppScreen.LIVE_DEMO)
                        viewModel.startDemo()
                    },
                    onAiCopilotClick = {
                        viewModel.navigateTo(AppScreen.AI_COPILOT)
                    },
                    unreadAlertCount = uiState.alerts.count { !it.isAcknowledged },
                    isOffline = uiState.isOffline
                )
            },
            bottomBar = {
                // Persistent Landing Page Brand Banner
                Surface(
                    color = NavyDark,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "NER-LOGIX: Predict. Reroute. Track. Respond. • AI Logistics Intelligence for NER India",
                            fontSize = 9.sp,
                            color = Color(0xFF94A3B8),
                            maxLines = 1
                        )
                    }
                }
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            modifier = Modifier
                .fillMaxSize()
                .background(SlateBackground)
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (uiState.currentScreen) {
                    AppScreen.LOGIN -> {
                        LoginScreen(
                            onLoginSuccess = { user -> viewModel.login(user) }
                        )
                    }
                    AppScreen.DASHBOARD -> {
                        DashboardScreen(
                            vehicles = uiState.vehicles,
                            alerts = uiState.alerts,
                            fieldReports = uiState.fieldReports,
                            mapFilters = uiState.mapFilters,
                            isRoadBlocked = uiState.isRoadBlocked,
                            selectedRouteId = uiState.selectedRouteId,
                            weatherZones = uiState.weatherZones,
                            deliveries = uiState.deliveries,
                            activeDeliveryId = uiState.selectedDeliveryId,
                            onSelectDelivery = { deliveryId -> viewModel.selectDelivery(deliveryId) },
                            onFilterToggle = { filterKey -> viewModel.toggleMapFilter(filterKey) },
                            onAcknowledgeAlert = { alertId ->
                                viewModel.acknowledgeAlert(alertId)
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Alert acknowledged.")
                                }
                            },
                            onRerouteVehicle = { vehicleId ->
                                viewModel.rerouteVehicle(vehicleId, "RT-B")
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Vehicle $vehicleId rerouted safely via Route B.")
                                }
                            },
                            onNavigateToOptimizer = { viewModel.navigateTo(AppScreen.ROUTE_OPTIMIZER) },
                            onNavigateToTracking = { viewModel.navigateTo(AppScreen.LIVE_TRACKING) },
                            onNavigateToDemo = {
                                viewModel.navigateTo(AppScreen.LIVE_DEMO)
                                viewModel.startDemo()
                            }
                        )
                    }
                    AppScreen.LIVE_TRACKING -> {
                        LiveTrackingScreen(
                            vehicles = uiState.vehicles,
                            selectedVehicleId = uiState.selectedVehicleId,
                            isSimulating = uiState.isSimulatingGps,
                            onToggleSimulation = { viewModel.toggleGpsSimulation() },
                            onSelectVehicle = { id -> viewModel.selectVehicle(id) },
                            onRerouteVehicle = { id -> viewModel.rerouteVehicle(id, "RT-B") }
                        )
                    }
                    AppScreen.ROUTE_OPTIMIZER -> {
                        RouteOptimizerScreen(
                            source = uiState.currentSource,
                            destination = uiState.currentDestination,
                            routeOptions = uiState.routeOptions,
                            selectedRouteId = uiState.selectedRouteId,
                            onSelectRoute = { routeId -> viewModel.selectRoute(routeId) },
                            onRerouteFleet = {
                                viewModel.selectRoute("RT-B")
                                viewModel.rerouteVehicle("NER-TRUCK-104", "RT-B")
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Fleet dispatched on AI Recommended Route B (Kalaktang Bypass)!")
                                }
                            },
                            onNavigateToTracking = { viewModel.navigateTo(AppScreen.LIVE_TRACKING) }
                        )
                    }
                    AppScreen.AI_RISK_PREDICTION -> {
                        AiRiskPredictionScreen(
                            currentSource = uiState.currentSource,
                            currentDestination = uiState.currentDestination,
                            currentVehicleType = uiState.currentVehicleType,
                            currentWeather = uiState.currentWeather,
                            predictionResult = uiState.riskPrediction,
                            onCalculateRisk = { src, dst, veh, wtr ->
                                viewModel.calculateRisk(src, dst, veh, wtr)
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("AI Model recalculated route risk!")
                                }
                            },
                            onNavigateToOptimizer = { viewModel.navigateTo(AppScreen.ROUTE_OPTIMIZER) }
                        )
                    }
                    AppScreen.WEATHER_DISASTER -> {
                        WeatherDisasterScreen(
                            weatherZones = uiState.weatherZones,
                            onTriggerDisasterSimulation = {
                                viewModel.triggerDisasterAlert()
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Disaster alert triggered! Rainfall surge: 65 mm/h")
                                }
                            }
                        )
                    }
                    AppScreen.FIELD_REPORTS -> {
                        FieldReportingScreen(
                            fieldReports = uiState.fieldReports,
                            isOffline = uiState.isOffline,
                            pendingOfflineReports = uiState.pendingOfflineReports,
                            isSyncing = uiState.isSyncing,
                            syncSuccessMessage = uiState.syncSuccessMessage,
                            onToggleOffline = {
                                viewModel.toggleOfflineMode()
                                coroutineScope.launch {
                                    val msg = if (!uiState.isOffline) "Offline mode simulated. Reports will queue locally." else "Connecting to network & syncing queued reports..."
                                    snackbarHostState.showSnackbar(msg)
                                }
                            },
                            onSubmitReport = { type, loc, lat, lng, desc, sev, photo ->
                                viewModel.submitFieldReport(type, loc, lat, lng, desc, sev, photo)
                                coroutineScope.launch {
                                    val msg = if (uiState.isOffline) "Offline — Report saved locally in SQLite queue" else "Report received successfully"
                                    snackbarHostState.showSnackbar(msg)
                                }
                            }
                        )
                    }
                    AppScreen.DELIVERIES -> {
                        DeliveryTrackingScreen(
                            deliveries = uiState.deliveries,
                            onEmergencyPriorityRouting = { deliveryId ->
                                viewModel.emergencyPriorityRoute(deliveryId)
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Emergency priority clearance activated for consignment $deliveryId!")
                                }
                            }
                        )
                    }
                    AppScreen.GIS_MAP -> {
                        GisMapScreen(
                            vehicles = uiState.vehicles,
                            alerts = uiState.alerts,
                            fieldReports = uiState.fieldReports,
                            filters = uiState.mapFilters,
                            isRoadBlocked = uiState.isRoadBlocked,
                            selectedRouteId = uiState.selectedRouteId,
                            onToggleFilter = { key -> viewModel.toggleMapFilter(key) }
                        )
                    }
                    AppScreen.ANALYTICS -> {
                        AnalyticsScreen()
                    }
                    AppScreen.ALERTS -> {
                        AlertsCenterScreen(
                            alerts = uiState.alerts,
                            onAcknowledge = { alertId ->
                                viewModel.acknowledgeAlert(alertId)
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Alert acknowledged.")
                                }
                            },
                            onReroute = { vehicleId ->
                                viewModel.rerouteVehicle(vehicleId, "RT-B")
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Vehicle $vehicleId rerouted safely via Route B.")
                                }
                            },
                            onViewRoute = { viewModel.navigateTo(AppScreen.ROUTE_OPTIMIZER) }
                        )
                    }
                    AppScreen.ADMIN_PANEL -> {
                        AdminPanelScreen(
                            currentUser = currentUser,
                            onSwitchUser = { newUser ->
                                viewModel.switchUserRole(newUser)
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Switched profile to ${newUser.name} (${newUser.role.displayName})")
                                }
                            }
                        )
                    }
                    AppScreen.LIVE_DEMO -> {
                        LiveDemoScreen(
                            currentStepIndex = uiState.currentDemoStepIndex,
                            isAutoPlaying = uiState.isAutoPlayingDemo,
                            vehicles = uiState.vehicles,
                            alerts = uiState.alerts,
                            fieldReports = uiState.fieldReports,
                            mapFilters = uiState.mapFilters,
                            isRoadBlocked = uiState.isRoadBlocked,
                            selectedRouteId = uiState.selectedRouteId,
                            onStartDemo = { viewModel.startDemo() },
                            onNextStep = { viewModel.nextDemoStep() },
                            onAutoPlay = { viewModel.toggleAutoPlayDemo() },
                            onResetDemo = { viewModel.resetDemo() },
                            onJumpToStep = { stepIdx -> viewModel.jumpToDemoStep(stepIdx) },
                            onNavigateToDashboard = { viewModel.navigateTo(AppScreen.DASHBOARD) },
                            onNavigateToOptimizer = { viewModel.navigateTo(AppScreen.ROUTE_OPTIMIZER) }
                        )
                    }
                    AppScreen.AI_COPILOT -> {
                        GeminiChatScreen(
                            onNavigateBack = { viewModel.navigateTo(AppScreen.DASHBOARD) }
                        )
                    }
                }
            }
        }
    }
}
