package com.example.model

enum class UserRole(val displayName: String, val badge: String) {
    ADMINISTRATOR("Administrator", "ADMIN"),
    LOGISTICS_OPERATOR("Logistics Operator", "LOGISTICS"),
    DRIVER("Driver", "DRIVER"),
    FIELD_OFFICER("Field Officer", "FIELD"),
    DISASTER_OFFICER("Disaster Management Officer", "DISASTER")
}

data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: UserRole,
    val department: String
)

enum class RiskLevel(val label: String) {
    LOW("LOW"),
    MEDIUM("MEDIUM"),
    HIGH("HIGH"),
    CRITICAL("CRITICAL")
}

enum class VehicleStatus(val label: String) {
    IN_TRANSIT("In Transit"),
    REROUTED("Rerouted"),
    IDLE("Idle"),
    DELAYED("Delayed"),
    MAINTENANCE("Maintenance")
}

data class GpsCoordinate(
    val latitude: Double,
    val longitude: Double,
    val locationName: String = ""
)

data class Vehicle(
    val id: String,
    val driverName: String,
    val driverPhone: String,
    val type: String,
    val currentLocation: String,
    val destination: String,
    val speedKmh: Int,
    val eta: String,
    val status: VehicleStatus,
    val routeRisk: RiskLevel,
    val currentCoordinate: GpsCoordinate,
    val progressPercent: Float,
    val cargo: String
)

data class RouteOption(
    val id: String,
    val name: String,
    val tag: String,
    val distanceKm: Int,
    val durationText: String,
    val riskLevel: RiskLevel,
    val riskScore: Int,
    val isRecommended: Boolean,
    val explanation: String,
    val waypoints: List<String>,
    val roadCondition: String,
    val weatherCondition: String
)

data class RiskFactor(
    val name: String,
    val score: Int,
    val maxScore: Int,
    val level: RiskLevel,
    val note: String
)

data class RiskPredictionResult(
    val source: String,
    val destination: String,
    val vehicleType: String,
    val weather: String,
    val overallScore: Int,
    val riskLevel: RiskLevel,
    val factors: List<RiskFactor>,
    val aiExplanation: String,
    val disruptionProbability: Int,
    val recommendation: String
)

enum class ReportType(val label: String) {
    LANDSLIDE("Landslide"),
    FLOOD("Flood"),
    ROAD_DAMAGE("Road Damage"),
    BRIDGE_DAMAGE("Bridge Damage"),
    ACCIDENT("Accident"),
    ROAD_BLOCKAGE("Road Blockage"),
    OTHER("Other")
}

data class FieldReport(
    val id: String,
    val type: ReportType,
    val locationName: String,
    val coordinates: GpsCoordinate,
    val description: String,
    val severity: RiskLevel,
    val officerName: String,
    val timestamp: String,
    val isSynced: Boolean = true,
    val hasPhoto: Boolean = true
)

data class AlertItem(
    val id: String,
    val severity: RiskLevel,
    val title: String,
    val description: String,
    val location: String,
    val timeAgo: String,
    val source: String,
    val recommendedAction: String,
    val isAcknowledged: Boolean = false,
    val relatedVehicleId: String? = null
)

enum class DeliveryPriority(val label: String) {
    NORMAL("Normal"),
    HIGH("High"),
    EMERGENCY("Emergency")
}

data class DeliveryItem(
    val id: String,
    val vehicleId: String,
    val origin: String,
    val destination: String,
    val goodsType: String,
    val priority: DeliveryPriority,
    val eta: String,
    val status: String,
    val riskLevel: RiskLevel,
    val riskScore: Int = 25,
    val startCoordinate: GpsCoordinate = GpsCoordinate(26.14, 91.73, origin),
    val destinationCoordinate: GpsCoordinate = GpsCoordinate(27.58, 91.86, destination),
    val waypoints: List<GpsCoordinate> = emptyList()
)

data class WeatherZone(
    val id: String,
    val regionName: String,
    val rainfallMmPerHour: Double,
    val temperatureCelsius: Int,
    val windKmh: Int,
    val floodWarning: Boolean,
    val landslideProbabilityPercent: Int,
    val roadCondition: String,
    val riskLevel: RiskLevel,
    val coordinates: GpsCoordinate = GpsCoordinate(26.2, 92.5, regionName)
) {
    val severity: RiskLevel get() = riskLevel
}

data class MapFilterState(
    val showVehicles: Boolean = true,
    val showWeather: Boolean = true,
    val showLandslides: Boolean = true,
    val showFloods: Boolean = true,
    val showRoadBlocks: Boolean = true,
    val showRiskZones: Boolean = true,
    val showFieldReports: Boolean = true
)

data class DemoScenarioStep(
    val stepNumber: Int,
    val title: String,
    val description: String,
    val vehicleLocation: String,
    val riskScore: Int,
    val riskLevel: RiskLevel,
    val alertGenerated: String?,
    val activeRouteName: String,
    val roadBlockedLocation: String? = null,
    val systemAction: String = "Automated Telemetry Update"
)
