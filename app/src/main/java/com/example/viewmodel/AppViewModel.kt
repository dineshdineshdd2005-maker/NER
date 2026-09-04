package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.MockDataProvider
import com.example.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AppScreen(val title: String, val routeName: String) {
    LOGIN("Login", "login"),
    DASHBOARD("Dashboard", "dashboard"),
    LIVE_TRACKING("Live Tracking", "live_tracking"),
    ROUTE_OPTIMIZER("Route Optimizer", "route_optimizer"),
    AI_RISK_PREDICTION("AI Risk Prediction", "risk_prediction"),
    WEATHER_DISASTER("Weather & Disaster", "weather_disaster"),
    FIELD_REPORTS("Field Reports", "field_reports"),
    DELIVERIES("Deliveries", "deliveries"),
    GIS_MAP("GIS Map", "gis_map"),
    ANALYTICS("Analytics", "analytics"),
    ALERTS("Alerts Center", "alerts"),
    ADMIN_PANEL("Admin Panel", "admin_panel"),
    LIVE_DEMO("Live Demo Scenario", "live_demo"),
    AI_COPILOT("AI Dispatch Copilot", "ai_copilot")
}

data class AppUiState(
    val currentUser: User = MockDataProvider.demoUsers[0],
    val currentScreen: AppScreen = AppScreen.DASHBOARD,
    val isUserLoggedIn: Boolean = true,
    val vehicles: List<Vehicle> = MockDataProvider.initialVehicles,
    val selectedVehicleId: String? = "NER-TRUCK-104",
    val isSimulatingMovement: Boolean = false,
    val alerts: List<AlertItem> = MockDataProvider.initialAlerts,
    val fieldReports: List<FieldReport> = MockDataProvider.initialFieldReports,
    val deliveries: List<DeliveryItem> = MockDataProvider.initialDeliveries,
    val selectedDeliveryId: String = "DEL-NER-5501",
    val weatherZones: List<WeatherZone> = MockDataProvider.weatherZones,
    val mapFilters: MapFilterState = MapFilterState(),
    val isRoadBlocked: Boolean = true,
    val roadBlockLocation: String = "NH-13 Bomdila Pass Km 142",
    // Offline Field Reporting Demo
    val isOfflineMode: Boolean = false,
    val pendingOfflineReports: List<FieldReport> = emptyList(),
    val isSyncingReports: Boolean = false,
    val syncSuccessMessage: String? = null,
    // AI Risk Prediction Form State
    val predictionSource: String = "Guwahati Central Depot",
    val predictionDestination: String = "Tawang Military Hospital",
    val predictionVehicleType: String = "All-Terrain Freight Truck (16T)",
    val predictionWeather: String = "Heavy Monsoonal Rain (65 mm/h)",
    val activePredictionResult: RiskPredictionResult = createInitialPredictionResult(),
    // Route Optimizer State
    val optimizerSource: String = "Guwahati",
    val optimizerDestination: String = "Tawang",
    val routeOptions: List<RouteOption> = MockDataProvider.sampleRouteOptions,
    val selectedRouteId: String = "RT-B",
    // Live Demo Scenario State
    val demoCurrentStepIndex: Int = 0,
    val isDemoRunning: Boolean = false,
    val isDemoAutoPlaying: Boolean = false,
    val activeBannerNotification: String? = null
) {
    val isLoggedIn: Boolean get() = isUserLoggedIn
    val isOffline: Boolean get() = isOfflineMode
    val isSimulatingGps: Boolean get() = isSimulatingMovement
    val currentSource: String get() = predictionSource
    val currentDestination: String get() = predictionDestination
    val currentVehicleType: String get() = predictionVehicleType
    val currentWeather: String get() = predictionWeather
    val riskPrediction: RiskPredictionResult get() = activePredictionResult
    val isSyncing: Boolean get() = isSyncingReports
    val currentDemoStepIndex: Int get() = demoCurrentStepIndex
    val isAutoPlayingDemo: Boolean get() = isDemoAutoPlaying
    val activeDelivery: DeliveryItem? get() = deliveries.find { it.id == selectedDeliveryId } ?: deliveries.firstOrNull()
}

fun createInitialPredictionResult(): RiskPredictionResult {
    return RiskPredictionResult(
        source = "Guwahati",
        destination = "Tawang",
        vehicleType = "All-Terrain Heavy Truck",
        weather = "Heavy Monsoonal Rain (65mm/h)",
        overallScore = 78,
        riskLevel = RiskLevel.HIGH,
        factors = listOf(
            RiskFactor("Rainfall Intensity (65mm/h)", 25, 30, RiskLevel.CRITICAL, "Exceeds 50mm soil saturation threshold"),
            RiskFactor("Terrain Slope (West Kameng 42°)", 20, 25, RiskLevel.HIGH, "Steep incline prone to mass wasting"),
            RiskFactor("Historical Landslide Vulnerability", 18, 20, RiskLevel.HIGH, "14 recorded slips in sector during monsoon"),
            RiskFactor("Road Surface Condition", 10, 15, RiskLevel.MEDIUM, "Potholes and cracked asphalt on NH-13"),
            RiskFactor("Flash Flood Probability", 5, 10, RiskLevel.LOW, "Drainage channels near mountain base"),
            RiskFactor("Cellular / GPS Connectivity", 2, 5, RiskLevel.LOW, "Satellite telemetry link operational")
        ),
        aiExplanation = "Heavy rainfall and steep terrain have increased the predicted landslide risk along this route.",
        disruptionProbability = 82,
        recommendation = "Recommend switching to Route B (Kalaktang-Rupa Bypass) to avoid vulnerable mountain slips."
    )
}

class AppViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    private var simulationJob: Job? = null
    private var demoAutoPlayJob: Job? = null

    init {
        // Start simulated telemetry fluctuations
    }

    fun navigateTo(screen: AppScreen) {
        _uiState.update { it.copy(currentScreen = screen) }
    }

    fun loginAs(user: User) {
        viewModelScope.launch {
            com.example.service.FirebaseService.saveUserToFirestore(user)
        }
        _uiState.update {
            it.copy(
                currentUser = user,
                isUserLoggedIn = true,
                currentScreen = if (user.role == UserRole.FIELD_OFFICER) AppScreen.FIELD_REPORTS else AppScreen.DASHBOARD
            )
        }
    }

    fun logout() {
        _uiState.update {
            it.copy(
                isUserLoggedIn = false,
                currentScreen = AppScreen.LOGIN
            )
        }
    }

    fun selectVehicle(vehicleId: String) {
        _uiState.update { it.copy(selectedVehicleId = vehicleId) }
    }

    // Toggle real-time vehicle movement simulation
    fun toggleVehicleMovementSimulation() {
        val currentSimulating = _uiState.value.isSimulatingMovement
        if (currentSimulating) {
            simulationJob?.cancel()
            _uiState.update { it.copy(isSimulatingMovement = false) }
        } else {
            _uiState.update { it.copy(isSimulatingMovement = true) }
            simulationJob = viewModelScope.launch {
                while (true) {
                    delay(1500)
                    _uiState.update { state ->
                        val updatedVehicles = state.vehicles.map { vehicle ->
                            val newProgress = (vehicle.progressPercent + 0.02f).let {
                                if (it > 0.98f) 0.15f else it
                            }
                            val speedJitter = (-3..4).random()
                            val newSpeed = (vehicle.speedKmh + speedJitter).coerceIn(25, 65)

                            // Interpolate latitude and longitude slightly to show moving GPS marker
                            val newLat = vehicle.currentCoordinate.latitude + (0.005 * (if (newProgress > 0.5) -1 else 1))
                            val newLng = vehicle.currentCoordinate.longitude + (0.008 * (if (newProgress > 0.5) 1 else -1))

                            vehicle.copy(
                                progressPercent = newProgress,
                                speedKmh = newSpeed,
                                currentCoordinate = vehicle.currentCoordinate.copy(
                                    latitude = newLat,
                                    longitude = newLng
                                )
                            )
                        }
                        state.copy(vehicles = updatedVehicles)
                    }
                }
            }
        }
    }

    // Filter toggles for GIS Map
    fun toggleMapFilter(filterKey: String) {
        _uiState.update { state ->
            val filters = state.mapFilters
            val newFilters = when (filterKey) {
                "vehicles" -> filters.copy(showVehicles = !filters.showVehicles)
                "weather" -> filters.copy(showWeather = !filters.showWeather)
                "landslides" -> filters.copy(showLandslides = !filters.showLandslides)
                "floods" -> filters.copy(showFloods = !filters.showFloods)
                "roadblocks" -> filters.copy(showRoadBlocks = !filters.showRoadBlocks)
                "riskzones" -> filters.copy(showRiskZones = !filters.showRiskZones)
                "fieldreports" -> filters.copy(showFieldReports = !filters.showFieldReports)
                else -> filters
            }
            state.copy(mapFilters = newFilters)
        }
    }

    // Recalculate AI Risk Prediction based on parameters
    fun updatePredictionInputs(
        source: String = _uiState.value.predictionSource,
        destination: String = _uiState.value.predictionDestination,
        vehicleType: String = _uiState.value.predictionVehicleType,
        weather: String = _uiState.value.predictionWeather
    ) {
        val isRainy = weather.contains("Rain", ignoreCase = true) || weather.contains("Monsoonal", ignoreCase = true)
        val isFoggy = weather.contains("Fog", ignoreCase = true)
        val isMountainDest = destination.contains("Tawang", ignoreCase = true) || destination.contains("Ziro", ignoreCase = true)
        val isHeavyTruck = vehicleType.contains("Heavy", ignoreCase = true) || vehicleType.contains("16T", ignoreCase = true)

        val rainScore = if (isRainy) 25 else 8
        val slopeScore = if (isMountainDest) 20 else 7
        val landslideScore = if (isMountainDest && isRainy) 18 else 5
        val roadScore = if (isHeavyTruck) 10 else 6
        val floodScore = if (isRainy && !isMountainDest) 10 else 5
        val connScore = if (isMountainDest) 3 else 1

        val totalScore = rainScore + slopeScore + landslideScore + roadScore + floodScore + connScore
        val riskLevel = when {
            totalScore >= 70 -> RiskLevel.HIGH
            totalScore >= 45 -> RiskLevel.MEDIUM
            else -> RiskLevel.LOW
        }

        val explanation = if (totalScore >= 70) {
            "Heavy rainfall and steep terrain have increased the predicted landslide risk along this route."
        } else if (totalScore >= 45) {
            "Moderate precipitation and variable road surfacing create localized transport delays."
        } else {
            "Optimal weather conditions and clear mountain corridors provide safe transit parameters."
        }

        val factors = listOf(
            RiskFactor("Rainfall Intensity", rainScore, 30, if (rainScore > 18) RiskLevel.CRITICAL else RiskLevel.LOW, "Hydrological saturation index"),
            RiskFactor("Terrain Slope", slopeScore, 25, if (slopeScore > 15) RiskLevel.HIGH else RiskLevel.LOW, "Elevation gradient & contour"),
            RiskFactor("Historical Landslides", landslideScore, 20, if (landslideScore > 12) RiskLevel.HIGH else RiskLevel.LOW, "Regional geological survey data"),
            RiskFactor("Road Surface Condition", roadScore, 15, RiskLevel.MEDIUM, "Pavement quality & width"),
            RiskFactor("Flood Probability", floodScore, 10, RiskLevel.LOW, "River catchment telemetry"),
            RiskFactor("Cellular / GPS Link", connScore, 5, RiskLevel.LOW, "Emergency satellite relay")
        )

        val newResult = RiskPredictionResult(
            source = source,
            destination = destination,
            vehicleType = vehicleType,
            weather = weather,
            overallScore = totalScore,
            riskLevel = riskLevel,
            factors = factors,
            aiExplanation = explanation,
            disruptionProbability = (totalScore * 0.95).toInt().coerceIn(10, 95),
            recommendation = if (totalScore >= 70) {
                "Recommend switching to Route B (Kalaktang-Rupa Bypass) to avoid vulnerable mountain slips."
            } else {
                "Primary route verified safe for scheduled transport."
            }
        )

        _uiState.update {
            it.copy(
                predictionSource = source,
                predictionDestination = destination,
                predictionVehicleType = vehicleType,
                predictionWeather = weather,
                activePredictionResult = newResult
            )
        }
    }

    // Select Route in Optimizer
    fun selectRoute(routeId: String) {
        _uiState.update { it.copy(selectedRouteId = routeId) }
    }

    // Reroute specific vehicle (e.g. from alert or optimizer)
    fun rerouteVehicle(vehicleId: String, newRouteId: String = "RT-B") {
        _uiState.update { state ->
            val updatedVehicles = state.vehicles.map { v ->
                if (v.id == vehicleId) {
                    v.copy(
                        status = VehicleStatus.REROUTED,
                        routeRisk = RiskLevel.LOW,
                        eta = "3h 45m (via Safe Bypass)"
                    )
                } else v
            }
            val updatedDeliveries = state.deliveries.map { d ->
                if (d.vehicleId == vehicleId) {
                    d.copy(
                        status = "Rerouted (Bypass)",
                        riskLevel = RiskLevel.LOW,
                        riskScore = 24,
                        waypoints = listOf(
                            GpsCoordinate(26.65, 92.79, "Tezpur Transit"),
                            GpsCoordinate(27.05, 92.25, "Kalaktang Safe Bypass"),
                            GpsCoordinate(27.35, 92.24, "Dirang Valley")
                        )
                    )
                } else d
            }
            state.copy(
                vehicles = updatedVehicles,
                deliveries = updatedDeliveries,
                selectedRouteId = newRouteId,
                activeBannerNotification = "Vehicle $vehicleId dynamically rerouted to AI-Recommended Route B (Safe Bypass)."
            )
        }
    }

    // Select Active Delivery to focus polyline on map
    fun selectDelivery(deliveryId: String) {
        _uiState.update { it.copy(selectedDeliveryId = deliveryId) }
    }

    // Acknowledge alert
    fun acknowledgeAlert(alertId: String) {
        _uiState.update { state ->
            val updatedAlerts = state.alerts.map { alert ->
                if (alert.id == alertId) alert.copy(isAcknowledged = true) else alert
            }
            state.copy(alerts = updatedAlerts)
        }
    }

    // Priority delivery routing toggle
    fun toggleEmergencyDelivery(deliveryId: String) {
        _uiState.update { state ->
            val updated = state.deliveries.map { d ->
                if (d.id == deliveryId) {
                    val newPriority = if (d.priority == DeliveryPriority.EMERGENCY) DeliveryPriority.HIGH else DeliveryPriority.EMERGENCY
                    d.copy(priority = newPriority, status = "Priority Clearance Granted")
                } else d
            }
            state.copy(
                deliveries = updated,
                activeBannerNotification = "Emergency priority clearance granted. Corridor cleared for delivery $deliveryId."
            )
        }
    }

    // Submit Field Report
    fun submitFieldReport(
        type: ReportType,
        locationName: String,
        latitude: Double,
        longitude: Double,
        description: String,
        severity: RiskLevel,
        hasPhoto: Boolean
    ) {
        val report = FieldReport(
            id = "REP-${(704..999).random()}",
            type = type,
            locationName = locationName,
            coordinates = GpsCoordinate(latitude, longitude, locationName),
            description = description,
            severity = severity,
            officerName = _uiState.value.currentUser.name,
            timestamp = "Just now",
            isSynced = !_uiState.value.isOfflineMode,
            hasPhoto = hasPhoto
        )

        if (_uiState.value.isOfflineMode) {
            _uiState.update { state ->
                state.copy(
                    pendingOfflineReports = state.pendingOfflineReports + report,
                    activeBannerNotification = "Offline — Report saved locally in offline storage queue."
                )
            }
        } else {
            val newAlert = AlertItem(
                id = "ALT-${(905..999).random()}",
                severity = severity,
                title = "${type.label} Reported: $locationName",
                description = description,
                location = locationName,
                timeAgo = "Just now",
                source = "Field Officer: ${_uiState.value.currentUser.name}",
                recommendedAction = "Inspect corridor and alert approaching vehicles."
            )

            viewModelScope.launch {
                com.example.service.FirebaseService.persistFieldReportToFirestore(report)
            }
            _uiState.update { state ->
                state.copy(
                    fieldReports = listOf(report) + state.fieldReports,
                    alerts = listOf(newAlert) + state.alerts,
                    activeBannerNotification = "Report received successfully! Live dashboard and GIS map updated."
                )
            }
        }
    }

    // Offline mode simulation toggle
    fun toggleOfflineMode() {
        val currentlyOffline = _uiState.value.isOfflineMode
        if (currentlyOffline) {
            // Reconnecting: trigger sync animation
            _uiState.update { it.copy(isOfflineMode = false, isSyncingReports = true, syncSuccessMessage = null) }
            viewModelScope.launch {
                val pendingCount = _uiState.value.pendingOfflineReports.size
                delay(1800) // Simulate network sync
                val pending = _uiState.value.pendingOfflineReports.map { it.copy(isSynced = true) }
                _uiState.update { state ->
                    state.copy(
                        isSyncingReports = false,
                        fieldReports = pending + state.fieldReports,
                        pendingOfflineReports = emptyList(),
                        syncSuccessMessage = "All $pendingCount reports synchronized successfully to central cloud!",
                        activeBannerNotification = "Sync Complete: Central GIS database updated."
                    )
                }
            }
        } else {
            // Going offline
            _uiState.update {
                it.copy(
                    isOfflineMode = true,
                    syncSuccessMessage = null,
                    activeBannerNotification = "Network Disconnected — Operating in Offline Mode."
                )
            }
        }
    }

    // Dismiss active banner
    fun dismissBanner() {
        _uiState.update { it.copy(activeBannerNotification = null) }
    }

    // Live Demo Scenario Controls
    fun startLiveDemo() {
        _uiState.update {
            it.copy(
                currentScreen = AppScreen.LIVE_DEMO,
                demoCurrentStepIndex = 0,
                isDemoRunning = true,
                isDemoAutoPlaying = false
            )
        }
        applyDemoStep(0)
    }

    fun nextDemoStep() {
        val nextIndex = (_uiState.value.demoCurrentStepIndex + 1).coerceAtMost(MockDataProvider.demoScenarioSteps.size - 1)
        applyDemoStep(nextIndex)
    }

    fun previousDemoStep() {
        val prevIndex = (_uiState.value.demoCurrentStepIndex - 1).coerceAtLeast(0)
        applyDemoStep(prevIndex)
    }

    fun resetDemo() {
        demoAutoPlayJob?.cancel()
        _uiState.update {
            it.copy(
                demoCurrentStepIndex = 0,
                isDemoAutoPlaying = false,
                isDemoRunning = false
            )
        }
        applyDemoStep(0)
    }

    fun toggleDemoAutoPlay() {
        val currentPlaying = _uiState.value.isDemoAutoPlaying
        if (currentPlaying) {
            demoAutoPlayJob?.cancel()
            _uiState.update { it.copy(isDemoAutoPlaying = false) }
        } else {
            _uiState.update { it.copy(isDemoAutoPlaying = true) }
            demoAutoPlayJob = viewModelScope.launch {
                while (_uiState.value.demoCurrentStepIndex < MockDataProvider.demoScenarioSteps.size - 1) {
                    delay(3500)
                    nextDemoStep()
                }
                _uiState.update { it.copy(isDemoAutoPlaying = false) }
            }
        }
    }

    fun applyDemoStep(stepIndex: Int) {
        val step = MockDataProvider.demoScenarioSteps[stepIndex]
        _uiState.update { state ->
            val updatedVehicles = state.vehicles.map { v ->
                if (v.id == "NER-TRUCK-104") {
                    v.copy(
                        currentLocation = step.vehicleLocation,
                        routeRisk = step.riskLevel,
                        status = if (stepIndex >= 6) VehicleStatus.REROUTED else VehicleStatus.IN_TRANSIT,
                        progressPercent = (stepIndex + 1) / 11f
                    )
                } else v
            }

            val updatedDeliveries = state.deliveries.map { d ->
                if (d.vehicleId == "NER-TRUCK-104") {
                    d.copy(
                        status = if (stepIndex == 10) "Delivered" else if (stepIndex >= 6) "Rerouted (Bypass)" else "In Transit",
                        riskLevel = step.riskLevel
                    )
                } else d
            }

            state.copy(
                demoCurrentStepIndex = stepIndex,
                vehicles = updatedVehicles,
                deliveries = updatedDeliveries,
                isRoadBlocked = stepIndex in 3..9,
                selectedRouteId = if (stepIndex >= 5) "RT-B" else "RT-A",
                activeBannerNotification = step.alertGenerated ?: "Step ${step.stepNumber}: ${step.title}"
            )
        }
    }

    fun login(user: User) = loginAs(user)
    fun startDemo() = startLiveDemo()
    fun toggleGpsSimulation() = toggleVehicleMovementSimulation()
    fun calculateRisk(source: String, destination: String, vehicleType: String, weather: String) =
        updatePredictionInputs(source, destination, vehicleType, weather)
    fun emergencyPriorityRoute(deliveryId: String) = toggleEmergencyDelivery(deliveryId)
    fun toggleAutoPlayDemo() = toggleDemoAutoPlay()
    fun jumpToDemoStep(stepIndex: Int) = applyDemoStep(stepIndex)

    fun switchUserRole(user: User) {
        _uiState.update { it.copy(currentUser = user) }
    }

    fun triggerDisasterAlert() {
        val alert = AlertItem(
            id = "ALT-SURGE-${(100..999).random()}",
            severity = RiskLevel.CRITICAL,
            title = "CRITICAL MONSOON SURGE DETECTED",
            description = "Rainfall exceeded 65 mm/h in West Kameng sector. Landslide probability elevated to 86%.",
            location = "NH-13 Bomdila Sector",
            timeAgo = "Just now",
            source = "IMD Radar Telemetry",
            recommendedAction = "Immediate reroute to Route B required for approaching trucks.",
            relatedVehicleId = "NER-TRUCK-104"
        )
        _uiState.update { state ->
            state.copy(
                alerts = listOf(alert) + state.alerts,
                isRoadBlocked = true,
                activeBannerNotification = "CRITICAL: Monsoon cloudburst triggering automated reroute warnings!"
            )
        }
    }
}
